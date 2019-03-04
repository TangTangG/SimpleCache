package policy;

import model.CacheModelContainer;

public interface CachePolicy {
    boolean filter(CacheModelContainer container);
}
