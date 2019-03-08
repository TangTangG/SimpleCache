package domain;

import model.CacheModel;
import model.CacheModelContainer;
import policy.CachePolicy;
import util.CacheFileWriter;
import util.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileCacheDomain extends BaseCacheDomain {

    private CacheFileWriter mWriter;

    FileCacheDomain(int features, long memoryLimit, String path) {
        super(features, memoryLimit);
        mWriter = new CacheFileWriter(path);
        mWriter.syncDomain(this);
    }

    public CacheModel addL2Cache(String key, File data) {
        CacheModel model = new CacheModel(key, data);
        container.put(model);
        model.accessUpdate();
        return model;
    }

    /**
     * key already md5.
     */
    public CacheModel removeL2Cache(String key) {
        CacheModel model = container.get(key);
        if (model != null) {
            container.remove(key);
        }
        return model;
    }

    @Override
    public void put(String key, Object data) {
        if (data == null) {
            return;
        }
        final String fileName = Util.MD5(key);
        String name = Util.formatFileName(fileName, "0");
        File cache = null;
        if (data instanceof InputStream) {
            cache = mWriter.writer2File(name, (InputStream) data);
        } else {
            byte[] bytes = data2ByteArray(data);
            if (bytes.length > 0) {
                cache = mWriter.writer2File(name, bytes);
            }
        }
        //File L2 cache
        if (cache != null) {
            cache.setLastModified(addL2Cache(key, cache).lastAccessTime);
        }
    }

    private byte[] data2ByteArray(Object data) {
        if (data instanceof String) {
            return ((String) data).getBytes();
        } else if (data instanceof byte[]) {
            return (byte[]) data;
        } else if (data instanceof Serializable) {
            Serializable value = (Serializable) data;
            ByteArrayOutputStream baos;
            ObjectOutputStream oos = null;
            try {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(value);
                return baos.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (oos != null) {
                        oos.close();
                    }
                } catch (IOException e) {
                }
            }
        }
        return new byte[0];
    }

    /**
     * All file cache return as byte[]
     */
    @Override
    public Object get(String key) {
        final String fileName = Util.MD5(key);
        CacheModel model = container.get(fileName);
        if (model == null) {
            String name = Util.formatFileName(fileName, "");
            mWriter.deleteFileStartWith(name);
            return null;
        }
        File file = (File) model.data;
        if (!file.exists()) {
            return null;
        }
        for (CachePolicy p : policies) {
            if (!p.modelCheck(model, container)) {
                file.delete();
                return null;
            }
        }
        model.accessUpdate();
        file.setLastModified(model.lastAccessTime);
        return file2ByteArray(file);
    }

    private byte[] file2ByteArray(File file) {
        if (file.exists()){
            RandomAccessFile raFile = null;
            try {
                raFile = new RandomAccessFile(file,"r");
                byte[] bytes = new byte[(int) file.length()];
                raFile.read(bytes);
                return bytes;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (raFile != null){
                    try {
                        raFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return new byte[0];
    }

    @Override
    public boolean remove(String key) {
        String md5 = Util.MD5(key);
        boolean remove = container.remove(md5);
        mWriter.deleteFileStartWith(Util.formatFileName(md5, ""));
        return remove;
    }

    @Override
    public CacheModel removeByPolicy() {
        CacheModel remove = null;
        for (CachePolicy p : policies) {
            if ((remove = p.filter(container)) != null) {
                break;
            }
        }
        if (remove != null) {
            mWriter.deleteFileStartWith(Util.formatFileName(remove.storeKey, ""));
        }
        return remove;
    }

    @Override
    public void clear() {
        super.clear();
        mWriter.deleteAllUnderDir();
    }

    @Override
    public boolean clean() {
        long size = mWriter.countFileSize();
        if (size < MAX_SIZE) {
            return false;
        }
        CacheModel model = removeByPolicy();
        if (model == null) {
            //always be false,for logic.
            CacheModel tail = container.tail();
            if (tail != null) {
                container.remove(tail.storeKey);
            }
        }
        //if remove one and memory still too large,clean again.
        if (clean()) {
            return clean();
        }
        return true;
    }

    @Override
    public List<CacheModel> getAll() {
        final List<CacheModel> models = new ArrayList<>();
        container.foreach(new CacheModelContainer.Accept() {
            @Override
            public boolean onModel(CacheModel model) {
                model.data = file2ByteArray((File) model.data);
                models.add(model);
                return false;
            }
        });
        return models;
    }

    @Override
    public void destroy() {
        container.clear();
    }
}
