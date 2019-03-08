package cache;

import domain.CacheDomain;
import domain.CacheDomainBuilder;
import util.Util;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCache {


    private static final ConcurrentHashMap<Integer, CacheDomain> domainHolder = new ConcurrentHashMap<>();

    public static void onDomain(int tag, CacheDomain domain) {
        domainHolder.put(tag, domain);
    }

    public static CacheDomain getDomain(int domainTag) {
        return domainHolder.get(domainTag);
    }

    public static CacheDomainBuilder builder() {
        return new CacheDomainBuilder();
    }

    public static CacheDomain getDefault() {
        int defaultDomainFeatures = Util.addFlag(0, CacheDomain.POLICY_TIMEOUT
                | CacheDomain.POLICY_LRU
                | CacheDomain.MEMORY_ONLY);
        CacheDomain result = domainHolder.get(defaultDomainFeatures);
        if (result == null) {
            result = builder().policyTimeOut().policyLRU().memoryOnly().timeOut(1000 * 60 * 60).build();
            onDomain(defaultDomainFeatures, result);
        }
        return result;
    }

    public static void destroy() {
        Collection<CacheDomain> domains = domainHolder.values();
        for (CacheDomain d : domains) {
            d.destroy();
        }
    }

}
