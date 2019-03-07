package domain;

import model.CacheModel;
import policy.CachePolicy;
import util.CacheFileWriter;
import util.Util;

import java.io.*;
import java.util.List;

public class FileCacheDomain extends BaseCacheDomain {

    private CacheFileWriter mWriter;

    FileCacheDomain(int features, long memoryLimit, String path) {
        super(features, memoryLimit);
        mWriter = new CacheFileWriter(path);
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
        if (cache != null) {
            CacheModel model = new CacheModel(key, data);
            container.put(model);
            model.accessUpdate();
            cache.setLastModified(model.lastAccessTime);
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

    @Override
    public Object get(String key) {
        return super.get(key);
    }

    @Override
    public boolean remove(String key) {
        return super.remove(key);
    }

    @Override
    public CacheModel removeByPolicy() {
        CacheModel remove = null;
        for (CachePolicy p : policies) {
            if ((remove = p.filter(container)) != null) {
                break;
            }
        }
        return remove;

    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public boolean clean() {
        return super.clean();
    }

    @Override
    public List<CacheModel> getAll() {
        return super.getAll();
    }

    @Override
    public void destroy() {

    }
}
