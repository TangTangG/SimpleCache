package model;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueContainer implements CacheModelContainer {

    private final int MAX_CAPACITY = Integer.MAX_VALUE - 8;

    private final Object queueRWLock = new Object();
    private final Comparator<CacheModel> comparator;
    private CacheModel[] container;

    private AtomicInteger size = new AtomicInteger();
    private volatile int head = -1;

    public QueueContainer() {
        this.comparator = null;
        this.container = new CacheModel[16];
    }

    public QueueContainer(Comparator<CacheModel> comparator) {
        this.comparator = comparator;
        this.container = new CacheModel[16];
    }

    @Override
    public void put(CacheModel model) {
        if (model == null) {
            return;
        }
        synchronized (queueRWLock) {
            if (head < 0) {
                head = 0;
                container[0] = model;
            } else if (comparator != null) {
                putWithComparatorL(model);
            } else {
                capacityCheckL();
                container[size()] = model;
            }
            model.accessUpdate();
            size.getAndIncrement();
        }
    }

    private void capacityCheckL() {
        final int length = size.get();
        if (container.length == length && length < MAX_CAPACITY) {
            int newCapacity = length << 1;
            if (newCapacity < 0) {
                newCapacity = MAX_CAPACITY;
            }
            CacheModel[] newC = new CacheModel[newCapacity];
            System.arraycopy(container, 0, newC, 0, length);
            container = newC;
        }
    }

    private void putWithComparatorL(CacheModel model) {
        int length = size.get();
        int i = length;
        while (i < length - 1) {
            CacheModel cacheModel = container[i];
            if (comparator.compare(cacheModel, model) < 0) {
                break;
            }
            i++;
        }
        capacityCheckL();
        if (i < length - 1) {
            System.arraycopy(container, i, container, i + 1, length - i - 1);
        }
        container[i] = model;

    }

    @Override
    public CacheModel get(String key) {
        int position = position(key);
        if (position >= 0) {
            container[position].accessUpdate();
            return container[position];
        }
        return null;
    }

    private int position(String key) {
        int i = size();
        synchronized (queueRWLock) {
            while (i >= 0) {
                CacheModel cacheModel = container[i];
                if (cacheModel.hashCode() == key.hashCode()) {
                    break;
                }
                i--;
            }
        }
        return i;
    }

    @Override
    public boolean remove(String key) {
        int position = position(key);
        if (position < 0) {
            return false;
        }
        synchronized (queueRWLock) {
            int length = size.get();
            if (position == length - 1) {
                container[position] = null;
            } else {
                System.arraycopy(container, position + 1, container, position, length - position - 2);
                container[length - 1] = null;
            }
        }
        size.getAndDecrement();
        return true;
    }

    @Override
    public void clear() {
        int i = size();
        synchronized (queueRWLock) {
            while (i >= 0) {
                container[i] = null;
                i--;
            }
            container = new CacheModel[16];
            size.compareAndSet(0, 0);
        }
    }

    @Override
    public void foreach(Accept accept) {
        int i = size();
        synchronized (queueRWLock) {
            while (i >= 0) {
                if (accept.onModel(container[i])) {
                    container[i].accessUpdate();
                    break;
                }
                i--;
            }
        }
    }

    @Override
    public boolean exist(String key) {
        int position = position(key);
        if (position >= 0) {
            container[position].accessUpdate();
        }
        return position >= 0;
    }

    @Override
    public int size() {
        return size.get();
    }

    @Override
    public CacheModel head() {
        int size = size();
        if (size > 0) {
            container[0].accessUpdate();
            return container[0];
        }
        return null;
    }

    @Override
    public CacheModel tail() {
        int size = size();
        if (size > 0) {
            container[size - 1].accessUpdate();
            return container[size - 1];
        }
        return null;
    }

    @Override
    public long memorySize() {
        long memory = 0L;
        synchronized (queueRWLock) {
            int i = size();
            while (i >= 0) {
                memory += container[i].size();
                i--;
            }
        }
        return memory;
    }
}
