package model;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapContainer implements CacheModelContainer {

    private final Comparator<CacheModel> comparator;
    private final Map<String, CacheModel> container;

    public MapContainer() {
        this.comparator = null;
        this.container = new LinkedHashMap<>(16);
    }

    public MapContainer(Comparator<CacheModel> comparator) {
        this.comparator = comparator;
        this.container = new LinkedHashMap<>(16);
    }

    @Override
    public void put(CacheModel model) {

    }

    @Override
    public CacheModel get(String key) {
        return null;
    }

    @Override
    public boolean remove(String key) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public void foreach(Accept accept) {

    }

    @Override
    public boolean exist(String key) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public CacheModel head() {
        return null;
    }

    @Override
    public CacheModel tail() {
        return null;
    }
}
