package policy;

import domain.CacheDomain;
import util.Util;

public final class CachePolicyFactory {

    static CachePolicy test(int tag) {
        if (Util.containsFlag(tag, CacheDomain.POLICY_FIFO)) {
            return new QueueCachePolicy();
        } else if (Util.containsFlag(tag, CacheDomain.POLICY_LFU)) {
            return new LFUCachePolicy();
        } else if (Util.containsFlag(tag, CacheDomain.POLICY_TIMEOUT)) {
            return new TimeOutCachePolicy();
        } else {
            //Util.containsFlag(tag,CacheDomain.POLICY_LRU)
            //default
            return new LRUCachePolicy();
        }
    }

}
