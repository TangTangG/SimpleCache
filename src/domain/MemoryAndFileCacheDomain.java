package domain;

import model.CacheModel;

import java.util.List;

public class MemoryAndFileCacheDomain implements CacheDomain {

    private FileCacheDomain fileCache;
    private MemoryCacheDomain memoryCache;
    private final boolean sync;

    MemoryAndFileCacheDomain(int features, boolean sync, long sizeLimit, String filePath) {
        long maxFileSize = sizeLimit * 3;
        if (maxFileSize < 0) {
            //over flow,1Gb
            maxFileSize = 1024 * 1024 * 1024;
        }
        fileCache = new FileCacheDomain(features, maxFileSize, filePath);
        memoryCache = new MemoryCacheDomain(features, sizeLimit);
        //First time sync data to memory.
        if (fileCache.size() > 0) {
            List<CacheModel> all = fileCache.getAll();
            for (CacheModel m : all) {
                memoryCache.put(m.storeKey, m.data);
            }
        }
        this.sync = sync;
    }

    @Override
    public void put(String key, Object data) {
        memoryCache.put(key, data);
        if (sync) {
            fileCache.put(key, data);
        }
    }

    @Override
    public List<CacheModel> getAll() {
        return memoryCache.getAll();
    }

    @Override
    public Object get(String key) {
        Object result = memoryCache.get(key);
        if (result == null) {
            result = fileCache.get(key);
            if (result != null) {
                memoryCache.put(key, result);
            }
        }
        return result;
    }

    @Override
    public boolean remove(String key) {
        boolean result = memoryCache.remove(key);
        if (sync) {
            fileCache.remove(key);
        }
        return result;
    }

    @Override
    public CacheModel removeByPolicy() {
        CacheModel model = memoryCache.removeByPolicy();
        if (model != null && sync) {
            fileCache.remove(model.storeKey);
        }
        return model;
    }

    @Override
    public void clear() {
        memoryCache.clear();
        if (sync) {
            fileCache.clear();
        }
    }

    @Override
    public void timeOut(long offset) {
        memoryCache.timeOut(offset);
    }

    @Override
    public void destroy() {
        if (!sync) {
            fileCache.clear();
            List<CacheModel> all = memoryCache.getAll();
            for (CacheModel model : all) {
                fileCache.putMD5(model.storeKey, model.data);
            }
            memoryCache.clear();
        }
    }

    @Override
    public int size() {
        return memoryCache.size();
    }

    @Override
    public boolean clean() {

        return memoryCache.clean();
    }
}
