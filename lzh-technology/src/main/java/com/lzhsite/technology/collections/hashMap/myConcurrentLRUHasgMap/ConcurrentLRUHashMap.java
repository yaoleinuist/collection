package com.lzhsite.technology.collections.hashMap.myConcurrentLRUHasgMap;
 

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于ConcurrentHashMap修改的LRUMap
 * 
 * @author noah
 * 
 * @param <K>
 * @param <V>
 */
public class ConcurrentLRUHashMap<K, V> extends AbstractMap<K, V> implements
                ConcurrentMap<K, V>, Serializable {
 
        private static final long serialVersionUID = -5031526786765467550L;

        /**
         * Segement默认最大数
         */
        static final int DEFAULT_SEGEMENT_MAX_CAPACITY = 100;
 
        static final float DEFAULT_LOAD_FACTOR = 0.75f;
 
        static final int DEFAULT_CONCURRENCY_LEVEL = 16;

    
        static final int MAXIMUM_CAPACITY = 1 << 30;

        /**
         * The maximum number of segments to allow; used to bound constructor
         * arguments.
         */
        static final int MAX_SEGMENTS = 1 << 16; // slightly conservative
 
        static final int RETRIES_BEFORE_LOCK = 2;
 
        final int segmentMask;

        /**
         * Shift value for indexing within segments.
         */
        final int segmentShift;
 
        final Segment<K, V>[] segments;

        transient Set<K> keySet;
        transient Set<Map.Entry<K, V>> entrySet;
        transient Collection<V> values;

    
        private static int hash(int h) {
                // Spread bits to regularize both segment and index locations,
                // using variant of single-word Wang/Jenkins hash.
                h += (h << 15) ^ 0xffffcd7d;
                h ^= (h >>> 10);
                h += (h << 3);
                h ^= (h >>> 6);
                h += (h << 2) + (h << 14);
                return h ^ (h >>> 16);
        }

 
        final Segment<K, V> segmentFor(int hash) {
                return segments[(hash >>> segmentShift) & segmentMask];
        }

        /* ---------------- Inner Classes -------------- */

        /**
         * 修改原HashEntry，
         */
        static final class HashEntry<K, V> {
                /**
                 * 键
                 */
                final K key;

                /**
                 * hash值
                 */
                final int hash;

                /**
                 * 值
                 */
                volatile V value;

                /**
                 * hash链指针
                 */
                final HashEntry<K, V> next;

                /**
                 * 双向链表的下一个节点
                 */
                HashEntry<K, V> linkNext;

                /**
                 * 双向链表的上一个节点
                 */
                HashEntry<K, V> linkPrev;

                /**
                 * 死亡标记
                 */
                AtomicBoolean dead;

                HashEntry(K key, int hash, HashEntry<K, V> next, V value) {
                        this.key = key;
                        this.hash = hash;
                        this.next = next;
                        this.value = value;
                        dead = new AtomicBoolean(false);
                }

                @SuppressWarnings("unchecked")
                static final <K, V> HashEntry<K, V>[] newArray(int i) {
                        return new HashEntry[i];
                }
        }

        /**
         * 基于原Segment修改，内部实现一个双向列表
         * 
         * @author noah
         * 
         * @param <K>
         * @param <V>
         */
        static final class Segment<K, V> extends ReentrantLock implements Serializable {
          
                private static final long serialVersionUID = 2249069246763182397L;
 
                transient volatile int count;

            
                transient int modCount;
 
                transient int threshold;
 
                transient volatile HashEntry<K, V>[] table;

            
                final float loadFactor;

                /**
                 * 头节点
                 */
                transient final HashEntry<K, V> header;
                
                /**
                 * Segement最大容量
                 */
                final int maxCapacity;

                Segment(int maxCapacity, float lf, ConcurrentLRUHashMap<K, V> lruMap) {
                        this.maxCapacity = maxCapacity;
                        loadFactor = lf;
                        setTable(HashEntry.<K, V> newArray(maxCapacity));
                        header = new HashEntry<K, V>(null, -1, null, null);
                        header.linkNext = header;
                        header.linkPrev = header;
                }

                @SuppressWarnings("unchecked")
                static final <K, V> Segment<K, V>[] newArray(int i) {
                        return new Segment[i];
                }

                /**
                 * Sets table to new HashEntry array. Call only while holding lock or in
                 * constructor.
                 */
                void setTable(HashEntry<K, V>[] newTable) {
                        threshold = (int) (newTable.length * loadFactor);
                        table = newTable;
                }

 
                HashEntry<K, V> getFirst(int hash) {
                        HashEntry<K, V>[] tab = table;
                        return tab[hash & (tab.length - 1)];
                }
 
                V readValueUnderLock(HashEntry<K, V> e) {
                        lock();
                        try {
                                return e.value;
                        } finally {
                                unlock();
                        }
                }

                /* Specialized implementations of map methods */

                V get(Object key, int hash) {
                        lock();
                        try {
                                if (count != 0) { // read-volatile
                                        HashEntry<K, V> e = getFirst(hash);
                                        while (e != null) {
                                                if (e.hash == hash && key.equals(e.key)) {
                                                        V v = e.value;
                                                        // 将节点移动到头节点之前
                                                        moveNodeToHeader(e);
                                                        if (v != null)
                                                                return v;
                                                        return readValueUnderLock(e); // recheck
                                                }
                                                e = e.next;
                                        }
                                }
                                return null;
                        } finally {
                                unlock();
                        }
                }

                /**
                 * 将节点移动到头节点之前
                 * 
                 * @param entry
                 */
                void moveNodeToHeader(HashEntry<K, V> entry) {
                        // 先移除，然后插入到头节点的前面
                        removeNode(entry);
                        addBefore(entry, header);
                }

                /**
                 * 将第一个参数代表的节点插入到第二个参数代表的节点之前
                 * 
                 * @param newEntry
                 *            需要插入的节点
                 * @param entry
                 *            被插入的节点
                 */
                void addBefore(HashEntry<K, V> newEntry, HashEntry<K, V> entry) {
                        newEntry.linkNext = entry;
                        newEntry.linkPrev = entry.linkPrev;
                        entry.linkPrev.linkNext = newEntry;
                        entry.linkPrev = newEntry;
                }

                /**
                 * 从双向链中删除该Entry
                 * 
                 * @param entry
                 */
                void removeNode(HashEntry<K, V> entry) {
                        entry.linkPrev.linkNext = entry.linkNext;
                        entry.linkNext.linkPrev = entry.linkPrev;
                }

                boolean containsKey(Object key, int hash) {
                        lock();
                        try {
                                if (count != 0) { // read-volatile
                                        HashEntry<K, V> e = getFirst(hash);
                                        while (e != null) {
                                                if (e.hash == hash && key.equals(e.key)) {
                                                        moveNodeToHeader(e);
                                                        return true;
                                                }

                                                e = e.next;
                                        }
                                }
                                return false;
                        } finally {
                                unlock();
                        }
                }

                boolean containsValue(Object value) {
                        lock();
                        try {
                                if (count != 0) { // read-volatile
                                        HashEntry<K, V>[] tab = table;
                                        int len = tab.length;
                                        for (int i = 0; i < len; i++) {
                                                for (HashEntry<K, V> e = tab[i]; e != null; e = e.next) {
                                                        V v = e.value;
                                                        if (v == null) // recheck
                                                                v = readValueUnderLock(e);
                                                        if (value.equals(v)) {
                                                                moveNodeToHeader(e);
                                                                return true;
                                                        }

                                                }
                                        }
                                }
                                return false;
                        } finally {
                                unlock();
                        }
                }

                boolean replace(K key, int hash, V oldValue, V newValue) {
                        lock();
                        try {
                                HashEntry<K, V> e = getFirst(hash);
                                while (e != null && (e.hash != hash || !key.equals(e.key)))
                                        e = e.next;

                                boolean replaced = false;
                                if (e != null && oldValue.equals(e.value)) {
                                        replaced = true;
                                        e.value = newValue;
                                        // 移动到头部
                                        moveNodeToHeader(e);
                                }
                                return replaced;
                        } finally {
                                unlock();
                        }
                }

                V replace(K key, int hash, V newValue) {
                        lock();
                        try {
                                HashEntry<K, V> e = getFirst(hash);
                                while (e != null && (e.hash != hash || !key.equals(e.key)))
                                        e = e.next;

                                V oldValue = null;
                                if (e != null) {
                                        oldValue = e.value;
                                        e.value = newValue;
                                        // 移动到头部
                                        moveNodeToHeader(e);
                                }
                                return oldValue;
                        } finally {
                                unlock();
                        }
                }

                V put(K key, int hash, V value, boolean onlyIfAbsent) {
                        lock();
                        try {
                                int c = count;
                                if (c++ > threshold) // ensure capacity
                                        rehash();
                                HashEntry<K, V>[] tab = table;
                                int index = hash & (tab.length - 1);
                                HashEntry<K, V> first = tab[index];
                                HashEntry<K, V> e = first;
                                while (e != null && (e.hash != hash || !key.equals(e.key)))
                                        e = e.next;

                                V oldValue = null;
                                if (e != null) {
                                        oldValue = e.value;
                                        if (!onlyIfAbsent) {
                                                e.value = value;
                                                // 移动到头部
                                                moveNodeToHeader(e);
                                        }
                                } else {
                                        oldValue = null;
                                        ++modCount;
                                        HashEntry<K, V> newEntry = new HashEntry<K, V>(key, hash, first, value);
                                        tab[index] = newEntry;
                                        count = c; // write-volatile
                                        // 添加到双向链
                                        addBefore(newEntry, header);
                                        // 判断是否达到最大值
                                        removeEldestEntry();
                                }
                                return oldValue;
                        } finally {
                                unlock();
                        }
                }

                void rehash() {
                        HashEntry<K, V>[] oldTable = table;
                        int oldCapacity = oldTable.length;
                        if (oldCapacity >= MAXIMUM_CAPACITY)
                                return;
 

                        HashEntry<K, V>[] newTable = HashEntry.newArray(oldCapacity << 1);
                        threshold = (int) (newTable.length * loadFactor);
                        int sizeMask = newTable.length - 1;
                        for (int i = 0; i < oldCapacity; i++) {
                                // We need to guarantee that any existing reads of old Map can
                                // proceed. So we cannot yet null out each bin.
                                HashEntry<K, V> e = oldTable[i];

                                if (e != null) {
                                        HashEntry<K, V> next = e.next;
                                        int idx = e.hash & sizeMask;

                                        // Single node on list
                                        if (next == null)
                                                newTable[idx] = e;

                                        else {
                                                // Reuse trailing consecutive sequence at same slot
                                                HashEntry<K, V> lastRun = e;
                                                int lastIdx = idx;
                                                for (HashEntry<K, V> last = next; last != null; last = last.next) {
                                                        int k = last.hash & sizeMask;
                                                        if (k != lastIdx) {
                                                                lastIdx = k;
                                                                lastRun = last;
                                                        }
                                                }
                                                newTable[lastIdx] = lastRun;

                                                // Clone all remaining nodes
                                                for (HashEntry<K, V> p = e; p != lastRun; p = p.next) {
                                                        int k = p.hash & sizeMask;
                                                        HashEntry<K, V> n = newTable[k];
                                                        HashEntry<K, V> newEntry = new HashEntry<K, V>(
                                                                        p.key, p.hash, n, p.value);
                                                        // update by Noah
                                                        newEntry.linkNext = p.linkNext;
                                                        newEntry.linkPrev = p.linkPrev;
                                                        newTable[k] = newEntry;
                                                }
                                        }
                                }
                        }
                        table = newTable;
                }

                /**
                 * Remove; match on key only if value null, else match both.
                 */
                V remove(Object key, int hash, Object value) {
                        lock();
                        try {
                                int c = count - 1;
                                HashEntry<K, V>[] tab = table;
                                int index = hash & (tab.length - 1);
                                HashEntry<K, V> first = tab[index];
                                HashEntry<K, V> e = first;
                                while (e != null && (e.hash != hash || !key.equals(e.key)))
                                        e = e.next;

                                V oldValue = null;
                                if (e != null) {
                                        V v = e.value;
                                        if (value == null || value.equals(v)) {
                                                oldValue = v;
                                                // All entries following removed node can stay
                                                // in list, but all preceding ones need to be
                                                // cloned.
                                                ++modCount;
                                                HashEntry<K, V> newFirst = e.next;
                                                for (HashEntry<K, V> p = first; p != e; p = p.next) {
                                                        newFirst = new HashEntry<K, V>(p.key, p.hash,
                                                                        newFirst, p.value);
                                                        newFirst.linkNext = p.linkNext;
                                                        newFirst.linkPrev = p.linkPrev;
                                                }
                                                tab[index] = newFirst;
                                                count = c; // write-volatile
                                                // 移除节点
                                                removeNode(e);
                                        }
                                }
                                return oldValue;
                        } finally {
                                unlock();
                        }
                }

                /**
                 * 移除最旧元素
                 */
                void removeEldestEntry() {
                        if (count > this.maxCapacity) {
                                HashEntry<K, V> eldest = header.linkNext;
                                remove(eldest.key, eldest.hash, null);
                        }
                }

                void clear() {
                        if (count != 0) {
                                lock();
                                try {
                                        HashEntry<K, V>[] tab = table;
                                        for (int i = 0; i < tab.length; i++)
                                                tab[i] = null;
                                        ++modCount;
                                        count = 0; // write-volatile
                                } finally {
                                        unlock();
                                }
                        }
                }
        }

        /**
         * 使用指定参数，创建一个ConcurrentLRUHashMap
         * 
         * @param segementCapacity
         *            Segement最大容量
         * @param loadFactor
         *            加载因子
         * @param concurrencyLevel
         *            并发级别
         */
        public ConcurrentLRUHashMap(int segementCapacity, float loadFactor,
                        int concurrencyLevel) {
                if (!(loadFactor > 0) || segementCapacity < 0 || concurrencyLevel <= 0)
                        throw new IllegalArgumentException();

                if (concurrencyLevel > MAX_SEGMENTS)
                        concurrencyLevel = MAX_SEGMENTS;

                // Find power-of-two sizes best matching arguments
                int sshift = 0;
                int ssize = 1;
                while (ssize < concurrencyLevel) {
                        ++sshift;
                        ssize <<= 1;
                }
                segmentShift = 32 - sshift;
                segmentMask = ssize - 1;
                this.segments = Segment.newArray(ssize);

                for (int i = 0; i < this.segments.length; ++i)
                        this.segments[i] = new Segment<K, V>(segementCapacity, loadFactor, this);
        }

        /**
         * 使用指定参数，创建一个ConcurrentLRUHashMap
         * 
         * @param segementCapacity
         *            Segement最大容量
         * @param loadFactor
         *            加载因子
         */
        public ConcurrentLRUHashMap(int segementCapacity, float loadFactor) {
                this(segementCapacity, loadFactor, DEFAULT_CONCURRENCY_LEVEL);
        }

        /**
         * 使用指定参数，创建一个ConcurrentLRUHashMap
         * 
         * @param segementCapacity
         *            Segement最大容量
         */
        public ConcurrentLRUHashMap(int segementCapacity) {
                this(segementCapacity, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL);
        }

        /**
         * 使用默认参数，创建一个ConcurrentLRUHashMap，存放元素最大数默认为1000， 加载因子为0.75，并发级别16
         */
        public ConcurrentLRUHashMap() {
                this(DEFAULT_SEGEMENT_MAX_CAPACITY, DEFAULT_LOAD_FACTOR,
                                DEFAULT_CONCURRENCY_LEVEL);
        }

        /**
         * Returns <tt>true</tt> if this map contains no key-value mappings.
         * 
         * @return <tt>true</tt> if this map contains no key-value mappings
         */
        public boolean isEmpty() {
                final Segment<K, V>[] segments = this.segments;
   
                int[] mc = new int[segments.length];
                int mcsum = 0;
                for (int i = 0; i < segments.length; ++i) {
                        if (segments[i].count != 0)
                                return false;
                        else
                                mcsum += mc[i] = segments[i].modCount;
                }
 
                if (mcsum != 0) {
                        for (int i = 0; i < segments.length; ++i) {
                                if (segments[i].count != 0 || mc[i] != segments[i].modCount)
                                        return false;
                        }
                }
                return true;
        }

 
        public int size() {
                final Segment<K, V>[] segments = this.segments;
                long sum = 0;
                long check = 0;
                int[] mc = new int[segments.length];
                // Try a few times to get accurate count. On failure due to
                // continuous async changes in table, resort to locking.
                for (int k = 0; k < RETRIES_BEFORE_LOCK; ++k) {
                        check = 0;
                        sum = 0;
                        int mcsum = 0;
                        for (int i = 0; i < segments.length; ++i) {
                                sum += segments[i].count;
                                mcsum += mc[i] = segments[i].modCount;
                        }
                        if (mcsum != 0) {
                                for (int i = 0; i < segments.length; ++i) {
                                        check += segments[i].count;
                                        if (mc[i] != segments[i].modCount) {
                                                check = -1; // force retry
                                                break;
                                        }
                                }
                        }
                        if (check == sum)
                                break;
                }
                if (check != sum) { // Resort to locking all segments
                        sum = 0;
                        for (int i = 0; i < segments.length; ++i)
                                segments[i].lock();
                        for (int i = 0; i < segments.length; ++i)
                                sum += segments[i].count;
                        for (int i = 0; i < segments.length; ++i)
                                segments[i].unlock();
                }
                if (sum > Integer.MAX_VALUE)
                        return Integer.MAX_VALUE;
                else
                        return (int) sum;

        }

 
        public V get(Object key) {
                int hash = hash(key.hashCode());
                return segmentFor(hash).get(key, hash);
        }

     
        public boolean containsKey(Object key) {
                int hash = hash(key.hashCode());
                return segmentFor(hash).containsKey(key, hash);
        }
 
        public boolean containsValue(Object value) {
                if (value == null)
                        throw new NullPointerException();

                // See explanation of modCount use above

                final Segment<K, V>[] segments = this.segments;
                int[] mc = new int[segments.length];

                // Try a few times without locking
                for (int k = 0; k < RETRIES_BEFORE_LOCK; ++k) {
                        int mcsum = 0;
                        for (int i = 0; i < segments.length; ++i) {
                                mcsum += mc[i] = segments[i].modCount;
                                if (segments[i].containsValue(value))
                                        return true;
                        }
                        boolean cleanSweep = true;
                        if (mcsum != 0) {
                                for (int i = 0; i < segments.length; ++i) {
                                        if (mc[i] != segments[i].modCount) {
                                                cleanSweep = false;
                                                break;
                                        }
                                }
                        }
                        if (cleanSweep)
                                return false;
                }
                // Resort to locking all segments
                for (int i = 0; i < segments.length; ++i)
                        segments[i].lock();
                boolean found = false;
                try {
                        for (int i = 0; i < segments.length; ++i) {
                                if (segments[i].containsValue(value)) {
                                        found = true;
                                        break;
                                }
                        }
                } finally {
                        for (int i = 0; i < segments.length; ++i)
                                segments[i].unlock();
                }
                return found;
        }

 
        public boolean contains(Object value) {
                return containsValue(value);
        }

        /**
         * Put一个键值，加Map锁
         */
        public V put(K key, V value) {
                if (value == null)
                        throw new NullPointerException();
                int hash = hash(key.hashCode());
                return segmentFor(hash).put(key, hash, value, false);
        }

        /**
         * Put一个键值，如果该Key不存在的话
         */
        public V putIfAbsent(K key, V value) {
                if (value == null)
                        throw new NullPointerException();
                int hash = hash(key.hashCode());
                return segmentFor(hash).put(key, hash, value, true);
        }

 
        public void putAll(Map<? extends K, ? extends V> m) {
                for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
                        put(e.getKey(), e.getValue());
        }

 
        public V remove(Object key) {
                int hash = hash(key.hashCode());
                return segmentFor(hash).remove(key, hash, null);
        }

 
        public boolean remove(Object key, Object value) {
                int hash = hash(key.hashCode());
                if (value == null)
                        return false;
                return segmentFor(hash).remove(key, hash, value) != null;
        }

 
        public boolean replace(K key, V oldValue, V newValue) {
                if (oldValue == null || newValue == null)
                        throw new NullPointerException();
                int hash = hash(key.hashCode());
                return segmentFor(hash).replace(key, hash, oldValue, newValue);
        }

 
        public V replace(K key, V value) {
                if (value == null)
                        throw new NullPointerException();
                int hash = hash(key.hashCode());
                return segmentFor(hash).replace(key, hash, value);
        }

        /**
         * Removes all of the mappings from this map.
         */
        public void clear() {
                for (int i = 0; i < segments.length; ++i)
                        segments[i].clear();
        }

 
        public Set<K> keySet() {
                Set<K> ks = keySet;
                return (ks != null) ? ks : (keySet = new KeySet());
        }

 
        public Collection<V> values() {
                Collection<V> vs = values;
                return (vs != null) ? vs : (values = new Values());
        }

 
        public Set<Map.Entry<K, V>> entrySet() {
                Set<Map.Entry<K, V>> es = entrySet;
                return (es != null) ? es : (entrySet = new EntrySet());
        }

  
        public Enumeration<K> keys() {
                return new KeyIterator();
        }
 
        public Enumeration<V> elements() {
                return new ValueIterator();
        }

        /* ---------------- Iterator Support -------------- */

        abstract class HashIterator {
                int nextSegmentIndex;
                int nextTableIndex;
                HashEntry<K, V>[] currentTable;
                HashEntry<K, V> nextEntry;
                HashEntry<K, V> lastReturned;

                HashIterator() {
                        nextSegmentIndex = segments.length - 1;
                        nextTableIndex = -1;
                        advance();
                }

                public boolean hasMoreElements() {
                        return hasNext();
                }

                final void advance() {
                        if (nextEntry != null && (nextEntry = nextEntry.next) != null)
                                return;

                        while (nextTableIndex >= 0) {
                                if ((nextEntry = currentTable[nextTableIndex--]) != null)
                                        return;
                        }

                        while (nextSegmentIndex >= 0) {
                                Segment<K, V> seg = segments[nextSegmentIndex--];
                                if (seg.count != 0) {
                                        currentTable = seg.table;
                                        for (int j = currentTable.length - 1; j >= 0; --j) {
                                                if ((nextEntry = currentTable[j]) != null) {
                                                        nextTableIndex = j - 1;
                                                        return;
                                                }
                                        }
                                }
                        }
                }

                public boolean hasNext() {
                        return nextEntry != null;
                }

                HashEntry<K, V> nextEntry() {
                        if (nextEntry == null)
                                throw new NoSuchElementException();
                        lastReturned = nextEntry;
                        advance();
                        return lastReturned;
                }

                public void remove() {
                        if (lastReturned == null)
                                throw new IllegalStateException();
                        ConcurrentLRUHashMap.this.remove(lastReturned.key);
                        lastReturned = null;
                }
        }

        final class KeyIterator extends HashIterator implements Iterator<K>,
                        Enumeration<K> {
                public K next() {
                        return super.nextEntry().key;
                }

                public K nextElement() {
                        return super.nextEntry().key;
                }
        }

        final class ValueIterator extends HashIterator implements Iterator<V>,
                        Enumeration<V> {
                public V next() {
                        return super.nextEntry().value;
                }

                public V nextElement() {
                        return super.nextEntry().value;
                }
        }
 
        final class WriteThroughEntry extends AbstractMap.SimpleEntry<K, V> {
                /**
                 * 
                 */
                private static final long serialVersionUID = -2545938966452012894L;

                WriteThroughEntry(K k, V v) {
                        super(k, v);
                }

  
                public V setValue(V value) {
                        if (value == null)
                                throw new NullPointerException();
                        V v = super.setValue(value);
                        ConcurrentLRUHashMap.this.put(getKey(), value);
                        return v;
                }
        }

        final class EntryIterator extends HashIterator implements
                        Iterator<Entry<K, V>> {
                public Map.Entry<K, V> next() {
                        HashEntry<K, V> e = super.nextEntry();
                        return new WriteThroughEntry(e.key, e.value);
                }
        }

        final class KeySet extends AbstractSet<K> {
                public Iterator<K> iterator() {
                        return new KeyIterator();
                }

                public int size() {
                        return ConcurrentLRUHashMap.this.size();
                }

                public boolean contains(Object o) {
                        return ConcurrentLRUHashMap.this.containsKey(o);
                }

                public boolean remove(Object o) {
                        return ConcurrentLRUHashMap.this.remove(o) != null;
                }

                public void clear() {
                        ConcurrentLRUHashMap.this.clear();
                }
        }

        final class Values extends AbstractCollection<V> {
                public Iterator<V> iterator() {
                        return new ValueIterator();
                }

                public int size() {
                        return ConcurrentLRUHashMap.this.size();
                }

                public boolean contains(Object o) {
                        return ConcurrentLRUHashMap.this.containsValue(o);
                }

                public void clear() {
                        ConcurrentLRUHashMap.this.clear();
                }
        }

        final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
                public Iterator<Map.Entry<K, V>> iterator() {
                        return new EntryIterator();
                }

                public boolean contains(Object o) {
                        if (!(o instanceof Map.Entry))
                                return false;
                        Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
                        V v = ConcurrentLRUHashMap.this.get(e.getKey());
                        return v != null && v.equals(e.getValue());
                }

                public boolean remove(Object o) {
                        if (!(o instanceof Map.Entry))
                                return false;
                        Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
                        return ConcurrentLRUHashMap.this.remove(e.getKey(), e.getValue());
                }

                public int size() {
                        return ConcurrentLRUHashMap.this.size();
                }

                public void clear() {
                        ConcurrentLRUHashMap.this.clear();
                }
        }

 
        private void writeObject(java.io.ObjectOutputStream s) throws IOException {
                s.defaultWriteObject();

                for (int k = 0; k < segments.length; ++k) {
                        Segment<K, V> seg = segments[k];
                        seg.lock();
                        try {
                                HashEntry<K, V>[] tab = seg.table;
                                for (int i = 0; i < tab.length; ++i) {
                                        for (HashEntry<K, V> e = tab[i]; e != null; e = e.next) {
                                                s.writeObject(e.key);
                                                s.writeObject(e.value);
                                        }
                                }
                        } finally {
                                seg.unlock();
                        }
                }
                s.writeObject(null);
                s.writeObject(null);
        }

 
        @SuppressWarnings("unchecked")
        private void readObject(java.io.ObjectInputStream s) throws IOException,
                        ClassNotFoundException {
                s.defaultReadObject();

                // Initialize each segment to be minimally sized, and let grow.
                for (int i = 0; i < segments.length; ++i) {
                        segments[i].setTable(new HashEntry[1]);
                }

                // Read the keys and values, and put the mappings in the table
                for (;;) {
                        K key = (K) s.readObject();
                        V value = (V) s.readObject();
                        if (key == null)
                                break;
                        put(key, value);
                }
        }
}