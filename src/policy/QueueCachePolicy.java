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
    public boolean filter(CacheModelContainer container) {
        if (container == null) {
            return false;
        }
        CacheModel firstIn = container.head();
        if (firstIn == null) {
            return false;
        }
        container.remove(firstIn.key);
        return true;
    }
}
