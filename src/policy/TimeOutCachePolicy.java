package policy;

import model.CacheModel;
import model.CacheModelContainer;

import java.util.Comparator;

public class TimeOutCachePolicy extends BaseCachePolicy {

    private long timeOut = 0L;

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    @Override
    public Comparator<CacheModel> generateComparator() {
        return null;
    }

    @Override
    public CacheModel filter(CacheModelContainer container) {
        return null;
    }

    @Override
    public boolean modelCheck(CacheModel model, CacheModelContainer container) {
        if (model == null || container == null) {
            return false;
        }
        if (model.lastAccessTime + timeOut < System.nanoTime()) {
            container.remove(model.storeKey);
            return false;
        }
        return true;
    }
}
