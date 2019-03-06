package model;


import util.Util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MapContainer implements CacheModelContainer {

    private final Object maprwLock = new Object();
    private final Map<String, CacheModel> container;

    public MapContainer() {
        this.container = new LinkedHashMap<>(16);
    }

    @Override
    public void put(CacheModel model) {
        if (model == null) {
            return;
        }
        synchronized (maprwLock) {
            model.accessUpdate();
            container.put(model.key, model);
        }
    }

    @Override
    public CacheModel get(String key) {
        if (Util.strIsEmpty(key)) {
            return null;
        }
        CacheModel result;
        synchronized (maprwLock) {
            result = container.get(key);
        }
        if (result != null){
            result.accessUpdate();
        }
        return result;
    }

    @Override
    public boolean remove(String key) {
        if (Util.strIsEmpty(key)) {
            return false;
        }
        CacheModel result;
        synchronized (maprwLock) {
            result = container.remove(key);
        }
        return result != null;
    }

    @Override
    public void clear() {
        synchronized (maprwLock) {
            container.clear();
        }
    }

    @Override
    public void foreach(Accept accept) {
        Set<Map.Entry<String, CacheModel>> entries = container.entrySet();
        for (Map.Entry<String, CacheModel> entry : entries) {
            if (accept.onModel(entry.getValue())) {
                entry.getValue().accessUpdate();
                break;
            }
        }
    }

    @Override
    public boolean exist(String key) {
        if (Util.strIsEmpty(key)) {
            return false;
        }
        CacheModel result;
        synchronized (maprwLock) {
            result = container.get(key);
        }
        if (result != null){
            result.accessUpdate();
        }
        return result != null;
    }

    @Override
    public int size() {
        return container.size();
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
