package model;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class LinkListContainer implements CacheModelContainer {

    private final Comparator<CacheModel> comparator;

    private static class CacheNode {
        CacheModel data;
        CacheNode next;

        static CacheNode obtain(CacheModel model) {
            CacheNode node = new CacheNode();
            node.data = model;
            node.next = null;
            return node;
        }

        @Override
        public int hashCode() {
            return data.hashCode();
        }
    }

    public LinkListContainer() {
        comparator = null;
    }

    public LinkListContainer(Comparator<CacheModel> comparator) {
        this.comparator = comparator;
    }

    private final Object linkrwLock = new Object();

    private CacheNode root;
    private AtomicInteger size = new AtomicInteger();

    @Override
    public void put(CacheModel model) {
        if (model == null) {
            return;
        }
        CacheNode node = CacheNode.obtain(model);
        synchronized (linkrwLock) {
            if (root == null) {
                root = node;
            } else {
                if (comparator != null) {
                    putWithComparator(node);
                } else {
                    CacheNode head = root;
                    while (head.next != null) {
                        head = head.next;
                    }
                    head.next = node;
                }
            }
        }
        model.accessUpdate();
        size.getAndIncrement();
    }

    private void putWithComparator(CacheNode node) {
        CacheNode head = root;
        while (head.next != null) {
            if (comparator.compare(head.next.data, node.data) < 0) {
                break;
            }
            head = head.next;
        }
        if (head.next == null) {
            head.next = node;
        } else {
            CacheNode oldNext = head.next;
            head.next = node;
            node.next = oldNext;
        }
    }

    @Override
    public CacheModel get(String key) {
        CacheNode head = root;
        synchronized (linkrwLock) {
            while (head != null) {
                if (compareNode(key, head)) {
                    break;
                }
                head = head.next;
            }
        }
        if (head != null) {
            head.data.accessUpdate();
            return head.data;
        }
        return null;
    }

    @Override
    public boolean remove(String key) {
        CacheNode head = root;
        if (head == null) {
            return false;
        }
        synchronized (linkrwLock) {
            if (compareNode(key, head)) {
                CacheNode old = root;
                root = root.next;
                old.data = null;
                old.next = null;
                size.getAndDecrement();
                return true;
            }
            while (head.next != null) {
                if (compareNode(key, head.next)) {
                    CacheNode old = head.next;
                    head.next = old.next;
                    old.data = null;
                    old.next = null;
                    size.getAndDecrement();
                    return true;
                }
                head = head.next;
            }
        }

        return false;
    }

    private boolean compareNode(String key, CacheNode head) {
        return head.hashCode() == key.hashCode();
    }

    @Override
    public void clear() {
        synchronized (linkrwLock) {
            CacheNode head = root;
            while (head != null) {
                head.data = null;
                head = head.next;
            }
            root = null;
            size.set(0);
        }
    }

    /**
     * Not thread safe.
     */
    @Override
    public void foreach(final Accept accept) {
        if (accept == null) {
            return;
        }
        CacheNode head = root;
        while (head != null) {
            if (accept.onModel(head.data)) {
                break;
            }
            head = head.next;
        }
    }

    @Override
    public boolean exist(String key) {
        CacheNode head = root;
        synchronized (linkrwLock) {
            while (head != null) {
                if (compareNode(key, head)) {
                    head.data.accessUpdate();
                    break;
                }
                head = head.next;
            }
        }
        return head != null;
    }

    @Override
    public int size() {
        return size.get();
    }

    @Override
    public CacheModel head() {
        if (root != null) {
            root.data.accessUpdate();
            return root.data;
        }
        return null;
    }

    @Override
    public CacheModel tail() {
        if (root == null) {
            return null;
        }
        CacheNode head = root;
        while (head.next != null) {
            head = head.next;
        }
        head.data.accessUpdate();
        return head.data;
    }
}
