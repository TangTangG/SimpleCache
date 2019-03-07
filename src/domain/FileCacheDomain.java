package domain;

import model.CacheModel;
import util.FileWriter;

import java.util.List;

public class FileCacheDomain extends BaseCacheDomain {

    private FileWriter mWriter;

    FileCacheDomain(int features, long memoryLimit, String path) {
        super(features, memoryLimit);
        mWriter = new FileWriter(path);
    }

    @Override
    public void put(String key, Object data) {
        super.put(key, data);
    }

    @Override
    public Object get(String key) {
        return super.get(key);
    }

    @Override
    public boolean remove(String key) {
        return super.remove(key);
    }

    @Override
    public CacheModel removeByPolicy() {
        return super.removeByPolicy();
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void timeOut(long offset) {
        super.timeOut(offset);
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public boolean clean() {
        return super.clean();
    }

    @Override
    public List<CacheModel> getAll() {
        return super.getAll();
    }

    @Override
    public void destroy() {

    }
}
