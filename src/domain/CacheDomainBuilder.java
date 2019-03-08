package domain;

import cache.SimpleCache;
import util.Util;

public final class CacheDomainBuilder {

    private int policyTag = 0;
    private int policyTimeOut = 0;
    private int domainType = 0;
    private long timeOut = 0L;
    /**
     * Default size is 32Mb;
     * The maximum file size is three times the size of the memory.
     */
    private long maxMemorySize = 1024 * 1024 * 32;

    private String filePath = "/data/cache";

    public CacheDomainBuilder timeOut(long offset) {
        timeOut = offset;
        return this;
    }

    public CacheDomainBuilder memoryMaxSize(long size) {
        maxMemorySize = size;
        return this;
    }

    public CacheDomainBuilder rootFilePath(String path) {
        filePath = path;
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
        if ((domain = SimpleCache.getDomain(tag)) != null) {
            return domain;
        }
        long maxFileSize = maxMemorySize * 3;
        if (maxFileSize < 0) {
            //over flow,1Gb
            maxFileSize = 1024 * 1024 * 1024;
        }
        switch (domainType) {
            case CacheDomain.FILE_ONLY:
                domain = new FileCacheDomain(tag, maxFileSize, filePath);
                break;
            case CacheDomain.MEMORY_FILE_ASYNC:
                domain = new MemoryAndFileCacheDomain(tag, false, maxMemorySize, filePath);
                break;
            case CacheDomain.MEMORY_FILE_SYNC:
                domain = new MemoryAndFileCacheDomain(tag, true, maxMemorySize, filePath);
                break;
            case CacheDomain.MEMORY_ONLY:
            default:
                domain = new MemoryCacheDomain(tag, maxMemorySize);
        }
        domain.timeOut(timeOut);
        SimpleCache.onDomain(tag, domain);
        return domain;
    }


}
