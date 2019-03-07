package domain;

import model.CacheModel;
import model.CacheModelContainer;
import policy.CachePolicy;
import policy.TimeOutCachePolicy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class BaseCacheDomain implements CacheDomain {

    final CacheModelContainer container;
    final List<CachePolicy> policies;
    final long MAX_MEMORY_SIZE;

    BaseCacheDomain(int features, long memoryLimit) {
        policies = CachePolicy.Factory.create().test(features);
        Comparator<CacheModel> c = null;
        for (CachePolicy p : policies) {
            if ((c = p.generateComparator()) != null) {
                break;
            }
        }
        container = CacheModelContainer.Factory.create().test(features, c);
        MAX_MEMORY_SIZE = memoryLimit;
    }

    @Override
    public void put(String key, Object data) {
        if (container.exist(key)) {
            container.remove(key);
        }
        CacheModel cacheModel = new CacheModel();
        cacheModel.key = key;
        cacheModel.data = data;
        container.put(cacheModel);
    }

    @Override
    public List<CacheModel> getAll() {
        final List<CacheModel> models = new ArrayList<>();
        container.foreach(new CacheModelContainer.Accept() {
            @Override
            public boolean onModel(CacheModel model) {
                models.add(model);
                return false;
            }
        });
        return models;
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

    @Override
    public int size() {
        return container.size();
    }

    @Override
    public boolean clean() {
        long memoryCount = container.memorySize();
        if (memoryCount < MAX_MEMORY_SIZE) {
            return false;
        }
        CacheModel model = removeByPolicy();
        if (model == null) {
            //always be false,for logic.
            CacheModel tail = container.tail();
            if (tail != null) {
                container.remove(tail.key);
            }
        }
        //if remove one and memory still too large,clean again.
        if (clean()) {
            return clean();
        }
        return true;
    }
}
