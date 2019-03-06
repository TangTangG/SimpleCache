package model;

public class CacheModel {
    /**
     * This is the key of model.
     */
    public String key;
    /**
     * The data hold in memory.
     */
    public Object data;

    /**
     * This is for Policy.
     */
    public volatile long lastAccessTime = 0L;

    public volatile int accessCount = 0;

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof CacheModel) && key != null && key.equals(((CacheModel) data).key);
    }

    @Override
    public int hashCode() {
        return key == null ? 0 : key.hashCode();
    }

    public synchronized void accessUpdate() {
        lastAccessTime = System.nanoTime();
        accessCount++;
    }

}
