package policy;

import model.CacheModel;
import model.CacheModelContainer;

import java.util.Comparator;

/**
 * Least Recently Used
 */
public class LRUCachePolicy extends BaseCachePolicy {
    @Override
    public Comparator<CacheModel> generateComparator() {
        return new Comparator<CacheModel>() {
            @Override
            public int compare(CacheModel o1, CacheModel o2) {
                long t1 = o1 == null ? -1 : o1.lastAccessTime;
                long t2 = o2 == null ? -1 : o2.lastAccessTime;
                long l = t1 - t2;
                return l > 0 ? 1 : (l == 0 ? 0 : -1);
            }
        };
    }

    @Override
    public boolean filter(CacheModelContainer container) {
        if (container == null) {
            return false;
        }
        CacheModel least = container.tail();
        if (least == null) {
            return false;
        }
        container.remove(least.key);
        return true;
    }
}
