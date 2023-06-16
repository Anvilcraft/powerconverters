package covers1624.repack.cofh.lib.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.google.common.base.Objects;

public class LinkedHashList extends AbstractCollection implements Cloneable {
    protected transient Entry head;
    protected transient Entry tail;
    protected transient int size;
    protected transient int mask;
    protected transient Entry[] hashTable;
    protected transient int modCount;

    protected static int roundUpToPowerOf2(int number) {
        return number >= 1073741824
            ? 1073741824
            : (number > 2 ? Integer.highestOneBit(number - 1 << 1) : 2);
    }

    public LinkedHashList() {
        this.hashTable = new Entry[8];
        this.mask = 7;
    }

    public LinkedHashList(int size) {
        size = roundUpToPowerOf2(size);
        this.hashTable = new Entry[size];
        this.mask = size - 1;
    }

    public LinkedHashList(Collection col) {
        int size = roundUpToPowerOf2(col.size());
        this.hashTable = new Entry[size];
        this.mask = size - 1;
        this.addAll(col);
    }

    protected int hash(Object n) {
        int h = n == null ? 0 : n.hashCode();
        h ^= h >>> 20 ^ h >>> 12;
        return h ^ h >>> 7 ^ h >>> 4;
    }

    public int size() {
        return this.size;
    }

    public boolean add(Object e) {
        return this.push(e);
    }

    public Object get(int index) {
        this.checkElementIndex(index);
        return this.index(index).key;
    }

    public boolean push(Object obj) {
        int hash = this.hash(obj);
        if (this.seek(obj, hash) != null) {
            return false;
        } else {
            ++this.modCount;
            Entry e;
            this.insert(e = new Entry(obj, hash));
            this.rehashIfNecessary();
            e.prev = this.tail;
            e.next = null;
            if (this.tail != null) {
                this.tail.next = e;
            } else {
                this.head = e;
            }

            this.tail = e;
            return true;
        }
    }

    public Object pop() {
        Entry e = this.tail;
        if (e != null) {
            ++this.modCount;
            this.delete(e);
            this.tail = e.prev;
            e.prev = null;
            if (this.tail != null) {
                this.tail.next = null;
            } else {
                this.head = null;
            }

            return e.key;
        } else {
            return null;
        }
    }

    public Object peek() {
        return this.tail != null ? this.tail.key : null;
    }

    public Object poke() {
        return this.head != null ? this.head.key : null;
    }

    public boolean unshift(Object obj) {
        int hash = this.hash(obj);
        if (this.seek(obj, hash) != null) {
            return false;
        } else {
            ++this.modCount;
            Entry e;
            this.insert(e = new Entry(obj, hash));
            this.rehashIfNecessary();
            e.next = this.head;
            e.prev = null;
            if (this.head != null) {
                this.head.prev = e;
            } else {
                this.tail = e;
            }

            this.head = e;
            return true;
        }
    }

    public Object shift() {
        Entry e = this.head;
        if (e != null) {
            ++this.modCount;
            this.delete(e);
            this.head = e.next;
            e.next = null;
            if (this.head != null) {
                this.head.prev = null;
            } else {
                this.tail = null;
            }

            return e.key;
        } else {
            return null;
        }
    }

    public boolean contains(Object obj) {
        return this.seek(obj, this.hash(obj)) != null;
    }

    public boolean remove(Object obj) {
        Entry e = this.seek(obj, this.hash(obj));
        if (e == null) {
            return false;
        } else {
            this.unlink(e);
            return true;
        }
    }

    protected Entry index(int index) {
        Entry x;
        int i;
        if (index < this.size >> 1) {
            x = this.head;

            for (i = index; i-- > 0; x = x.next) {}
        } else {
            x = this.tail;

            for (i = this.size - 1; i-- > index; x = x.prev) {}
        }

        return x;
    }

    protected Entry seek(Object obj, int hash) {
        for (Entry entry = this.hashTable[hash & this.mask]; entry != null;
             entry = entry.nextInBucket) {
            if (hash == entry.hash && Objects.equal(obj, entry.key)) {
                return entry;
            }
        }

        return null;
    }

    protected void insert(Entry entry) {
        int bucket = entry.hash & this.mask;
        entry.nextInBucket = this.hashTable[bucket];
        this.hashTable[bucket] = entry;
        ++this.size;
    }

    protected boolean linkBefore(Object obj, Entry succ) {
        int hash = this.hash(obj);
        if (this.seek(obj, hash) != null) {
            return false;
        } else {
            Entry pred = succ.prev;
            Entry newNode = new Entry(obj, hash);
            ++this.modCount;
            this.insert(newNode);
            this.rehashIfNecessary();
            newNode.next = succ;
            newNode.prev = pred;
            succ.prev = newNode;
            if (pred == null) {
                this.head = newNode;
            } else {
                pred.next = newNode;
            }

            return true;
        }
    }

    protected void delete(Entry entry) {
        synchronized (this.hashTable) {
            int bucket = entry.hash & this.mask;
            Entry prev = null;
            Entry cur = this.hashTable[bucket];
            if (cur == entry) {
                this.hashTable[bucket] = cur.nextInBucket;
            } else {
                while (cur != entry) {
                    prev = cur;
                    cur = cur.nextInBucket;
                }

                prev.nextInBucket = entry.nextInBucket;
            }
        }

        --this.size;
    }

    protected Object unlink(Entry x) {
        Object element = x.key;
        Entry next = x.next;
        Entry prev = x.prev;
        if (prev == null) {
            this.head = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            this.tail = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        this.delete(x);
        ++this.modCount;
        return element;
    }

    protected void rehashIfNecessary() {
        Entry[] old = this.hashTable;
        if (this.size > old.length * 2 && old.length < 1073741824) {
            synchronized (this.hashTable) {
                int newTableSize = old.length * 2;
                int newMask = newTableSize - 1;
                Entry[] newTable = new Entry[newTableSize];
                this.mask = newMask;
                int bucket = old.length;

                Entry nextEntry;
                while (bucket-- > 0) {
                    for (Entry entry = old[bucket]; entry != null; entry = nextEntry) {
                        nextEntry = entry.nextInBucket;
                        int keyBucket = entry.hash & newMask;
                        entry.nextInBucket = newTable[keyBucket];
                        newTable[keyBucket] = entry;
                    }
                }

                this.hashTable = newTable;
            }
        }
    }

    public LinkedHashList clone() {
        return new LinkedHashList(this);
    }

    public Iterator iterator() {
        return this.listIterator();
    }

    public ListIterator listIterator() {
        return this.listIterator(0);
    }

    public ListIterator listIterator(int index) {
        this.checkPositionIndex(index);
        return new ListItr(index);
    }

    public Iterator descendingIterator() {
        return new DescendingIterator();
    }

    protected boolean isElementIndex(int index) {
        return index >= 0 && index < this.size;
    }

    protected boolean isPositionIndex(int index) {
        return index >= 0 && index <= this.size;
    }

    protected String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + this.size;
    }

    protected void checkElementIndex(int index) {
        if (!this.isElementIndex(index)) {
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(index));
        }
    }

    protected void checkPositionIndex(int index) {
        if (!this.isPositionIndex(index)) {
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(index));
        }
    }

    protected class DescendingIterator implements Iterator {
        protected final ListItr itr
            = LinkedHashList.this.new ListItr(LinkedHashList.this.size());

        public boolean hasNext() {
            return this.itr.hasPrevious();
        }

        public Object next() {
            return this.itr.previous();
        }

        public void remove() {
            this.itr.remove();
        }
    }

    protected class ListItr implements ListIterator {
        protected Entry lastReturned = null;
        protected Entry next;
        protected int nextIndex;
        protected int expectedModCount;

        protected ListItr(int index) {
            this.expectedModCount = LinkedHashList.this.modCount;
            this.next = index == LinkedHashList.this.size
                ? null
                : LinkedHashList.this.index(index);
            this.nextIndex = index;
        }

        public boolean hasNext() {
            return this.nextIndex < LinkedHashList.this.size;
        }

        public Object next() {
            this.checkForComodification();
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            } else {
                this.lastReturned = this.next;
                this.next = this.next.next;
                ++this.nextIndex;
                return this.lastReturned.key;
            }
        }

        public boolean hasPrevious() {
            return this.nextIndex > 0;
        }

        public Object previous() {
            this.checkForComodification();
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            } else {
                this.lastReturned = this.next
                    = this.next == null ? LinkedHashList.this.tail : this.next.prev;
                --this.nextIndex;
                return this.lastReturned.key;
            }
        }

        public int nextIndex() {
            return this.nextIndex;
        }

        public int previousIndex() {
            return this.nextIndex - 1;
        }

        public void remove() {
            this.checkForComodification();
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            } else {
                Entry lastNext = this.lastReturned.next;
                LinkedHashList.this.unlink(this.lastReturned);
                if (this.next == this.lastReturned) {
                    this.next = lastNext;
                } else {
                    --this.nextIndex;
                }

                this.lastReturned = null;
                ++this.expectedModCount;
            }
        }

        public void set(Object e) {
            this.checkForComodification();
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            } else {
                LinkedHashList.this.linkBefore(e, this.lastReturned);
                LinkedHashList.this.unlink(this.lastReturned);
                this.lastReturned
                    = this.next == null ? LinkedHashList.this.tail : this.next.prev;
                this.expectedModCount += 2;
            }
        }

        public void add(Object e) {
            this.checkForComodification();
            this.lastReturned = null;
            if (this.next == null) {
                LinkedHashList.this.push(e);
            } else {
                LinkedHashList.this.linkBefore(e, this.next);
            }

            ++this.nextIndex;
            ++this.expectedModCount;
        }

        protected final void checkForComodification() {
            if (LinkedHashList.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    protected static final class Entry {
        protected Entry next;
        protected Entry prev;
        protected final Object key;
        protected final int hash;
        protected Entry nextInBucket;

        protected Entry(Object key, int keyHash) {
            this.key = key;
            this.hash = keyHash;
        }
    }
}
