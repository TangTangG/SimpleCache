package policy;

import model.CacheModel;
import model.CacheModelContainer;

import java.util.Comparator;

/**
 * Least Frequently Used
 */
public class LFUCachePolicy extends BaseCachePolicy {

    @Override
    public Comparator<CacheModel> generateComparator() {
        return new Comparator<CacheModel>() {
            @Override
            public int compare(CacheModel o1, CacheModel o2) {
                int i1 = o1 == null ? -1 : o1.accessCount;
                int i2 = o2 == null ? -1 : o2.accessCount;
                return i1 - i2;
            }
        };
    }

    @Override
    public CacheModel filter(CacheModelContainer container) {
        if (container == null) {
            return null;
        }
        CacheModel least = container.tail();
        if (least == null) {
            return null;
        }
        container.remove(least.key);
        return least;
    }
}
