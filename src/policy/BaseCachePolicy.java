package policy;

import model.CacheModel;
import model.CacheModelContainer;

public abstract class BaseCachePolicy implements CachePolicy{
    @Override
    public boolean modelCheck(CacheModel model, CacheModelContainer container) {
        return false;
    }
}
