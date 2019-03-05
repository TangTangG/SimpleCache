package model;

public interface CacheModelContainer {

    void put(CacheModel model);

    CacheModel get(String key);

    boolean remove(String key);

    void clear();

    void foreach(Accept accept);

    boolean exist(String key);

    int size();

    CacheModel head();

    CacheModel tail();

    interface Accept {
        boolean onModel(CacheModel model);
    }

}
