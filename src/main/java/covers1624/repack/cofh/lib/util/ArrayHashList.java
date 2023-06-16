package covers1624.repack.cofh.lib.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.google.common.base.Objects;

public class ArrayHashList
    extends AbstractCollection implements List, Cloneable, Serializable {
    private static final long serialVersionUID = 3230581060536180693L;
    private transient Object[] elementData;
    protected transient int size;
    protected transient int mask;
    protected transient Entry[] hashTable;
    protected transient int modCount;
    private static final int MAX_ARRAY_SIZE = 2147483639;

    protected static int hash(Object n) {
        int h = n == null ? 0 : n.hashCode();
        h ^= h >>> 20 ^ h >>> 12;
        return h ^ h >>> 7 ^ h >>> 4;
    }

    private static int roundUpToPowerOf2(int number) {
        return number >= 1073741824
            ? 1073741824
            : (number > 2 ? Integer.highestOneBit(number - 1 << 1) : 2);
    }

    public ArrayHashList() {
        this.elementData = new Object[10];
        this.hashTable = new Entry[8];
        this.mask = 7;
    }

    public ArrayHashList(int size) {
        this.elementData = new Object[size];
        size = roundUpToPowerOf2(size) >> 1;
        this.hashTable = new Entry[size];
        this.mask = size - 1;
    }

    public ArrayHashList(Collection col) {
        int size = col.size();
        this.elementData = new Object[size];
        size = roundUpToPowerOf2(size) >> 1;
        this.hashTable = new Entry[size];
        this.mask = size - 1;
        this.addAll(col);
    }

    public int size() {
        return this.size;
    }

    protected void add(Object obj, int hash) {
        this.ensureCapacityInternal(this.size + 1);
        this.elementData[this.size++] = obj;
        this.insert(new Entry(obj, hash));
        this.rehashIfNecessary();
    }

    public boolean add(Object obj) {
        int hash = hash(obj);
        if (this.seek(obj, hash) != null) {
            return false;
        } else {
            this.add(obj, hash);
            return true;
        }
    }

    public Object set(int index, Object obj) {
        this.checkElementIndex(index);
        int hash = hash(obj);
        if (this.seek(obj, hash) != null) {
            throw new IllegalArgumentException("Duplicate entries not allowed");
        } else {
            ++this.modCount;
            Entry e = this.seek(this.elementData[index], hash(this.elementData[index]));
            this.delete(e);
            this.elementData[index] = obj;
            this.insert(new Entry(obj, hash));
            return e.key;
        }
    }

    public void add(int index, Object obj) {
        this.checkPositionIndex(index);
        int hash = hash(obj);
        if (this.seek(obj, hash) != null) {
            throw new IllegalArgumentException("Duplicate entries not allowed");
        } else if (index == this.size) {
            this.add(obj, hash);
        } else {
            this.ensureCapacityInternal(++this.size);
            System.arraycopy(
                this.elementData,
                index,
                this.elementData,
                index + 1,
                this.size - index - 1
            );
            this.elementData[index] = obj;
            this.insert(new Entry(obj, hash));
            this.rehashIfNecessary();
        }
    }

    public boolean addAll(int index, Collection c) {
        if (c.size() == 0) {
            return false;
        } else {
            Iterator i$ = c.iterator();

            while (i$.hasNext()) {
                Object e = i$.next();
                this.add(index++, e);
            }

            return true;
        }
    }

    public Object get(int index) {
        this.checkElementIndex(index);
        return this.index(index);
    }

    public int indexOf(Object obj) {
        Entry e = this.seek(obj, hash(obj));
        if (e == null) {
            return -1;
        } else {
            Object o = e.key;
            Object[] data = this.elementData;
            int i = this.size;

            while (i-- > 0 && data[i] != o) {}

            return i;
        }
    }

    public int lastIndexOf(Object o) {
        return this.indexOf(o);
    }

    public boolean contains(Object obj) {
        return this.seek(obj, hash(obj)) != null;
    }

    public Object remove(int index) {
        this.checkElementIndex(index);
        Object oldValue = this.index(index);
        this.delete(this.seek(oldValue, hash(oldValue)));
        this.fastRemove(index);
        return oldValue;
    }

    public boolean remove(Object obj) {
        Entry e = this.seek(obj, hash(obj));
        if (e == null) {
            return false;
        } else {
            Object o = e.key;
            Object[] data = this.elementData;
            int i = this.size;

            while (i-- > 0) {
                if (data[i] == o) {
                    this.fastRemove(i);
                    break;
                }
            }

            this.delete(e);
            return true;
        }
    }

    private void fastRemove(int index) {
        ++this.modCount;
        int numMoved = this.size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(
                this.elementData, index + 1, this.elementData, index, numMoved
            );
        }

        this.elementData[--this.size] = null;
    }

    public void clear() {
        ++this.modCount;

        int i;
        for (i = 0; i < this.size; ++i) {
            this.elementData[i] = null;
        }

        for (i = this.hashTable.length; i-- > 0; this.hashTable[i] = null) {}

        this.size = 0;
    }

    public void trimToSize() {
        ++this.modCount;
        if (this.size < this.elementData.length) {
            this.elementData = Arrays.copyOf(this.elementData, this.size);
        }
    }

    public void ensureCapacity(int minCapacity) {
        if (minCapacity > 0) {
            this.ensureCapacityInternal(minCapacity);
        }
    }

    private void ensureCapacityInternal(int minCapacity) {
        ++this.modCount;
        if (minCapacity - this.elementData.length > 0) {
            this.grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {
        int oldCapacity = this.elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }

        if (newCapacity - 2147483639 > 0) {
            newCapacity = hugeCapacity(minCapacity);
        }

        this.elementData = Arrays.copyOf(this.elementData, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) {
            throw new OutOfMemoryError();
        } else {
            return minCapacity > 2147483639 ? Integer.MAX_VALUE : 2147483639;
        }
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        int expectedModCount = this.modCount;
        s.defaultWriteObject();
        s.writeInt(this.size);

        for (int i = 0; i < this.size; ++i) {
            s.writeObject(this.elementData[i]);
        }

        if (this.modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    private void readObject(ObjectInputStream s)
        throws ClassNotFoundException, IOException {
        this.elementData = new Object[10];
        this.hashTable = new Entry[8];
        this.mask = 7;
        s.defaultReadObject();
        int size = s.readInt();
        if (size > 0) {
            this.ensureCapacityInternal(size);

            for (int i = 0; i < size; ++i) {
                this.add(s.readObject());
            }
        }
    }

    Object index(int index) {
        return this.elementData[index];
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
    }

    protected void rehashIfNecessary() {
        Entry[] old = this.hashTable;
        if (this.size > old.length * 2 && old.length < 1073741824) {
            synchronized (this.hashTable) {
                int newTableSize = old.length * 2;
                int newMask = newTableSize - 1;
                Entry[] newTable = this.hashTable = new Entry[newTableSize];
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
            }
        }
    }

    public ArrayHashList clone() {
        return new ArrayHashList(this);
    }

    public List subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    public Iterator iterator() {
        return new Itr();
    }

    public ListIterator listIterator() {
        return this.listIterator(0);
    }

    public ListIterator listIterator(int index) {
        return new ListItr(index);
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

    private class ListItr extends Itr implements ListIterator {
        ListItr(int index) {
            super(null);
            super.cursor = index;
        }

        public boolean hasPrevious() {
            return super.cursor != 0;
        }

        public int nextIndex() {
            return super.cursor;
        }

        public int previousIndex() {
            return super.cursor - 1;
        }

        public Object previous() {
            this.checkForComodification();
            int i = super.cursor - 1;
            if (i < 0) {
                throw new NoSuchElementException();
            } else {
                Object[] elementData = ArrayHashList.this.elementData;
                if (i >= elementData.length) {
                    throw new ConcurrentModificationException();
                } else {
                    super.cursor = i;
                    return elementData[super.lastRet = i];
                }
            }
        }

        public void set(Object e) {
            if (super.lastRet < 0) {
                throw new IllegalStateException();
            } else {
                this.checkForComodification();

                try {
                    ArrayHashList.this.set(super.lastRet, e);
                } catch (IndexOutOfBoundsException var3) {
                    throw new ConcurrentModificationException();
                }
            }
        }

        public void add(Object e) {
            this.checkForComodification();

            try {
                int i = super.cursor;
                ArrayHashList.this.add(i, e);
                super.cursor = i + 1;
                super.lastRet = -1;
                super.expectedModCount = ArrayHashList.this.modCount;
            } catch (IndexOutOfBoundsException var3) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private class Itr implements Iterator {
        int cursor;
        int lastRet;
        int expectedModCount;

        private Itr() {
            this.lastRet = -1;
            this.expectedModCount = ArrayHashList.this.modCount;
        }

        public boolean hasNext() {
            return this.cursor != ArrayHashList.this.size;
        }

        public Object next() {
            this.checkForComodification();
            int i = this.cursor;
            if (i >= ArrayHashList.this.size) {
                throw new NoSuchElementException();
            } else {
                Object[] elementData = ArrayHashList.this.elementData;
                if (i >= elementData.length) {
                    throw new ConcurrentModificationException();
                } else {
                    this.cursor = i + 1;
                    return elementData[this.lastRet = i];
                }
            }
        }

        public void remove() {
            if (this.lastRet < 0) {
                throw new IllegalStateException();
            } else {
                this.checkForComodification();

                try {
                    ArrayHashList.this.remove(this.lastRet);
                    this.cursor = this.lastRet;
                    this.lastRet = -1;
                    this.expectedModCount = ArrayHashList.this.modCount;
                } catch (IndexOutOfBoundsException var2) {
                    throw new ConcurrentModificationException();
                }
            }
        }

        final void checkForComodification() {
            if (ArrayHashList.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        // $FF: synthetic method
        Itr(Object x1) {
            this();
        }
    }

    protected static final class Entry {
        protected final Object key;
        protected final int hash;
        protected Entry nextInBucket;

        protected Entry(Object key, int keyHash) {
            this.key = key;
            this.hash = keyHash;
        }
    }
}
