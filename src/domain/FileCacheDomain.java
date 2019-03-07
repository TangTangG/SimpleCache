package domain;

import model.CacheModel;

import java.util.List;

public class FileCacheDomain implements CacheDomain{



    @Override
    public void put(String key, Object data) {

    }

    @Override
    public List<CacheModel> getAll() {
        return null;
    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public boolean remove(String key) {
        return false;
    }

    @Override
    public CacheModel removeByPolicy() {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public void timeOut(long offset) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean clean() {
        return false;
    }
}
