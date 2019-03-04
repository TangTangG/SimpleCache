package model;

public class CacheModel {
    /**
     * This is the key of model.
     */
    private String key;
    /**
     * The data hold in memory.
     */
    private Object data;

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof CacheModel) && key != null && key.equals(((CacheModel) data).key);
    }

    @Override
    public int hashCode() {
        return key == null ? 0 : key.hashCode();
    }

}
