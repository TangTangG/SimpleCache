package policy;

import model.CacheModel;
import model.CacheModelContainer;

import java.util.Comparator;

/**
 * FIFO
 */
public class QueueCachePolicy extends BaseCachePolicy {
    @Override
    public Comparator<CacheModel> generateComparator() {
        return null;
    }

    @Override
    public CacheModel filter(CacheModelContainer container) {
        if (container == null) {
            return null;
        }
        CacheModel firstIn = container.head();
        if (firstIn == null) {
            return null;
        }
        container.remove(firstIn.key);
        return firstIn;
    }
}
