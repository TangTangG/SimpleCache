package domain;

public class MemoryCacheDomain extends BaseCacheDomain{

    MemoryCacheDomain(int features, long memoryLimit) {
        super(features, memoryLimit);
    }

    @Override
    public void destroy() {
        container.clear();
        policies.clear();
    }

}
