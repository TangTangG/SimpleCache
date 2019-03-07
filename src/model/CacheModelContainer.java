package model;

import domain.CacheDomain;
import util.Util;

import java.util.Comparator;

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

    long memorySize();

    interface Accept {
        boolean onModel(CacheModel model);
    }

    class Factory {

        private Factory() {

        }

        public CacheModelContainer test(int tag, Comparator<CacheModel> c) {
            if (Util.containsFlag(tag, CacheDomain.POLICY_FIFO)) {
                return new QueueContainer(c);
            } else if (Util.containsFlag(tag, CacheDomain.POLICY_LFU)) {
                return new QueueContainer(c);
            } else if (Util.containsFlag(tag, CacheDomain.POLICY_LRU)) {
                return new LinkListContainer(c);
            } else if (Util.containsFlag(tag, CacheDomain.POLICY_TIMEOUT)) {
                return new MapContainer();
            } else {
                //default
                return new MapContainer();
            }
        }

        public static Factory create() {
            return new Factory();
        }


    }

}
