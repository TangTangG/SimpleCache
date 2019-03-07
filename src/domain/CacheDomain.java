package domain;

import model.CacheModel;

import java.util.List;

public interface CacheDomain {

    int POLICY_LRU = 1 << 1;
    int POLICY_LFU = 1 << 2;
    int POLICY_FIFO = 1 << 3;
    int POLICY_TIMEOUT = 1 << 4;

    int MEMORY_ONLY = 1 << 5;
    int FILE_ONLY = 1 << 6;
    int MEMORY_FILE_ASYNC = 1 << 7;
    int MEMORY_FILE_SYNC = 1 << 8;

    void put(String key, Object data);

    List<CacheModel> getAll();

    Object get(String key);

    boolean remove(String key);

    CacheModel removeByPolicy();

    void clear();

    void timeOut(long offset);

    void destroy();

    int size();

    boolean clean();

}
