package domain;

import util.Util;

public final class CacheDomainBuilder {

    private int policyTag = 0;
    private int policyTimeOut = 0;
    private int domainType = 0;
    private long timeOut = 0L;

    public CacheDomainBuilder timeOut(long offset) {
        timeOut = offset;
        return this;
    }

    private synchronized void updatePolicy(int tag) {
        if (policyTag == 0) {
            policyTag = tag;
        }
    }

    private synchronized void updateDomain(int tag) {
        if (domainType == 0) {
            domainType = tag;
        }
    }

    public CacheDomainBuilder policyType(int type) {
        updatePolicy(type);
        return this;
    }

    public CacheDomainBuilder policyLRU() {
        updatePolicy(CacheDomain.POLICY_LRU);
        return this;
    }

    public CacheDomainBuilder policyLFU() {
        updatePolicy(CacheDomain.POLICY_LFU);
        return this;
    }

    public CacheDomainBuilder policyFIFO() {
        updatePolicy(CacheDomain.POLICY_FIFO);
        return this;
    }

    public CacheDomainBuilder policyTimeOut() {
        policyTimeOut = CacheDomain.POLICY_TIMEOUT;
        return this;
    }

    public CacheDomainBuilder memoryOnly() {
        updateDomain(CacheDomain.MEMORY_ONLY);
        return this;
    }

    public CacheDomainBuilder fileOnly() {
        updateDomain(CacheDomain.FILE_ONLY);
        return this;
    }

    public CacheDomainBuilder memoryFileAsync() {
        updateDomain(CacheDomain.MEMORY_FILE_ASYNC);
        return this;
    }

    public CacheDomainBuilder memoryFileSync() {
        updateDomain(CacheDomain.MEMORY_FILE_SYNC);
        return this;
    }

    public CacheDomain build() {
        int tag = Util.addFlag(0, policyTag | policyTimeOut);
        CacheDomain domain;
        switch (domainType) {
            case CacheDomain.FILE_ONLY:
                domain = new FileCacheDomain(tag);
                break;
            case CacheDomain.MEMORY_FILE_ASYNC:
                domain = new MemoryAndFileCacheDomain(tag,false);
                break;
            case CacheDomain.MEMORY_FILE_SYNC:
                domain = new MemoryAndFileCacheDomain(tag,true);
                break;
            case CacheDomain.MEMORY_ONLY:
            default:
                domain = new MemoryCacheDomain(tag);
        }
        domain.timeOut(timeOut);
        return domain;
    }


}
