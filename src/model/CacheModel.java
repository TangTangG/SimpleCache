package model;

import util.Util;

import java.io.File;

public class CacheModel {
    /**
     * This is the key of model.
     */
    public String key;
    /**
     * This is the MD5 key of model.Really store.
     */
    public String storeKey;
    /**
     * The data hold in memory.
     */
    public Object data;

    /**
     * This is for Policy.
     */
    public volatile long lastAccessTime = 0L;

    public volatile int accessCount = 0;

    public CacheModel(String key, Object data) {
        this.key = key;
        this.storeKey = Util.MD5(key);
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof CacheModel) && storeKey != null && storeKey.equals(((CacheModel) data).storeKey);
    }

    @Override
    public int hashCode() {
        return storeKey == null ? 0 : storeKey.hashCode();
    }

    public synchronized void accessUpdate() {
        lastAccessTime = System.nanoTime();
        accessCount++;
    }

    public long size() {
        if (data instanceof String) {
            return ((String) data).getBytes().length;
        } else if (data instanceof File) {
            return ((File) data).length();
        } else if (data instanceof byte[]) {
            return ((byte[]) data).length;
        } else {
            return 0L;
        }
    }

}
