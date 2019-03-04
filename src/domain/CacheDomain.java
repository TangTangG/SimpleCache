package domain;

public interface CacheDomain {

    int POLICY_LRU = 1 << 1;
    int POLICY_LFU = 1 << 2;
    int POLICY_FIFO = 1 << 3;
    int POLICY_TIMEOUT = 1 << 4;

    int MEMORY_ONLY = 1 << 5;
    int FILE_ONLY = 1 << 6;
    int MEMORY_FILE = 1 << 7;

    void put(String key, Object data);

    Object get(String key);

    void remove(String key);

    void clear();

}
