package domain;

import model.CacheModel;
import model.CacheModelContainer;
import policy.CachePolicy;
import policy.TimeOutCachePolicy;

import java.util.List;

public abstract class BaseCacheDomain implements CacheDomain {

    private final CacheModelContainer container;
    private final List<CachePolicy> policies;

    BaseCacheDomain(int features) {
        container = CacheModelContainer.Factory.create().test(features);
        policies = CachePolicy.Factory.create().test(features);
    }

    @Override
    public void put(String key, Object data) {
        CacheModel cacheModel = new CacheModel();
        cacheModel.key = key;
        cacheModel.data = data;
        container.put(cacheModel);
    }

    @Override
    public Object get(String key) {
        CacheModel model = container.get(key);
        if (model != null) {
            for (CachePolicy p : policies) {
                if (!p.modelCheck(model, container)) {
                    return null;
                }
            }
        }
        return model;
    }

    @Override
    public boolean remove(String key) {
        return container.remove(key);
    }

    @Override
    public CacheModel removeByPolicy() {
        CacheModel remove = null;
        for (CachePolicy p : policies) {
            if ((remove = p.filter(container)) != null) {
                break;
            }
        }
        return remove;
    }

    @Override
    public void clear() {
        container.clear();
    }

    @Override
    public void timeOut(long offset) {
        if (offset <= 0L) {
            return;
        }
        TimeOutCachePolicy timeOutCachePolicy = null;
        for (CachePolicy p : policies) {
            if (p instanceof TimeOutCachePolicy) {
                timeOutCachePolicy = (TimeOutCachePolicy) p;
            }
        }
        if (timeOutCachePolicy == null) {
            timeOutCachePolicy = new TimeOutCachePolicy();
            policies.add(timeOutCachePolicy);
        }
        timeOutCachePolicy.setTimeOut(offset);
    }

}
