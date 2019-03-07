package policy;

import domain.CacheDomain;
import model.CacheModel;
import model.CacheModelContainer;
import util.Util;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface CachePolicy {

    Comparator<CacheModel> generateComparator();

    CacheModel filter(CacheModelContainer container);

    boolean modelCheck(CacheModel model, CacheModelContainer container);

    class Factory {

        private Factory() {

        }

        public List<CachePolicy> test(int tag) {
            List<CachePolicy> policies = new CopyOnWriteArrayList<>();
            if (Util.containsFlag(tag, CacheDomain.POLICY_FIFO)) {
                policies.add(new QueueCachePolicy());
            }
            if (Util.containsFlag(tag, CacheDomain.POLICY_LFU)) {
                policies.add(new LFUCachePolicy());
            }
            if (Util.containsFlag(tag, CacheDomain.POLICY_TIMEOUT)) {
                policies.add(new TimeOutCachePolicy());
            }
            if (policies.isEmpty() || Util.containsFlag(tag, CacheDomain.POLICY_LRU)) {
                policies.add(new LRUCachePolicy());
            }
            return policies;
        }

        public static Factory create() {
            return new Factory();
        }

    }

}
