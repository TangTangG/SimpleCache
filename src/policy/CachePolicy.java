package policy;

import model.CacheModel;
import model.CacheModelContainer;

import java.util.Comparator;

public interface CachePolicy {

    Comparator<CacheModel> generateComparator();

    boolean filter(CacheModelContainer container);

    boolean modelCheck(CacheModel model,CacheModelContainer container);

}
