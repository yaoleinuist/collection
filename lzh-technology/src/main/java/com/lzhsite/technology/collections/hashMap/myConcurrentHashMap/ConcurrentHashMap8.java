package com.lzhsite.technology.collections.hashMap.myConcurrentHashMap;

import java.io.*;
import java.lang.reflect.*;
import java.net.NetworkInterface;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.function.*;

public class ConcurrentHashMap8<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
	private static final long serialVersionUID = 7249069246763182397L;

	// 最大容量：2^30=1073741824
	private static final int MAXIMUM_CAPACITY = 1 << 30;

	// 默认初始值，必须是2的幕数
	private static final int DEFAULT_CAPACITY = 16;

	//
	static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	//
	private static final int DEFAULT_CONCURRENCY_LEVEL = 16;

	//
	private static final float LOAD_FACTOR = 0.75f;

	// 链表转红黑树阀值,> 8 链表转换为红黑树
	static final int TREEIFY_THRESHOLD = 8;

	// 树转链表阀值，小于等于6（tranfer时，lc、hc=0两个计数器分别++记录原bin、新binTreeNode数量，<=UNTREEIFY_THRESHOLD
	// 则untreeify(lo)）
	static final int UNTREEIFY_THRESHOLD = 6;

	//
	static final int MIN_TREEIFY_CAPACITY = 64;

	//
	private static final int MIN_TRANSFER_STRIDE = 16;

	//
	private static int RESIZE_STAMP_BITS = 16;

	// 2^15-1，help resize的最大线程数
	private static final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;

	// 32-16=16，sizeCtl中记录size大小的偏移量
	private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;

	// forwarding nodes的hash值
	static final int MOVED = -1;

	// 树根节点的hash值
	static final int TREEBIN = -2;

	// ReservationNode的hash值
	static final int RESERVED = -3;

	// 可用处理器数量
	static final int NCPU = Runtime.getRuntime().availableProcessors();

	static final int HASH_BITS = 0x7fffffff; // usable bits of normal node hash

	/** For serialization compatibility. */
	private static final ObjectStreamField[] serialPersistentFields = {
			new ObjectStreamField("segments", Segment[].class), new ObjectStreamField("segmentMask", Integer.TYPE),
			new ObjectStreamField("segmentShift", Integer.TYPE) };

	static class Node<K, V> implements Map.Entry<K, V> {
		final int hash;
		final K key;
		volatile V val;
		volatile Node<K, V> next;

		Node(int hash, K key, V val, Node<K, V> next) {
			this.hash = hash;
			this.key = key;
			this.val = val;
			this.next = next;
		}

		public final K getKey() {
			return key;
		}

		public final V getValue() {
			return val;
		}

		public final int hashCode() {
			return key.hashCode() ^ val.hashCode();
		}

		public final String toString() {
			return key + "=" + val;
		}

		public final V setValue(V value) {
			throw new UnsupportedOperationException();
		}

		public final boolean equals(Object o) {
			Object k, v, u;
			Map.Entry<?, ?> e;
			return ((o instanceof Map.Entry) && (k = (e = (Map.Entry<?, ?>) o).getKey()) != null
					&& (v = e.getValue()) != null && (k == key || k.equals(key)) && (v == (u = val) || v.equals(u)));
		}

		/**
		 * Virtualized support for map.get(); overridden in subclasses.
		 */
		Node<K, V> find(int h, Object k) {
			Node<K, V> e = this;
			if (k != null) {
				do {
					K ek;
					if (e.hash == h && ((ek = e.key) == k || (ek != null && k.equals(ek))))
						return e;
				} while ((e = e.next) != null);
			}
			return null;
		}
	}

	static final int spread(int h) {
		return (h ^ (h >>> 16)) & HASH_BITS;
	}

	private static final int tableSizeFor(int c) {
		int n = c - 1;
		n |= n >>> 1;
		n |= n >>> 2;
		n |= n >>> 4;
		n |= n >>> 8;
		n |= n >>> 16;
		return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
	}

	static Class<?> comparableClassFor(Object x) {
		if (x instanceof Comparable) {
			Class<?> c;
			Type[] ts, as;
			Type t;
			ParameterizedType p;
			if ((c = x.getClass()) == String.class) // bypass checks
				return c;
			if ((ts = c.getGenericInterfaces()) != null) {
				for (int i = 0; i < ts.length; ++i) {
					if (((t = ts[i]) instanceof ParameterizedType)
							&& ((p = (ParameterizedType) t).getRawType() == Comparable.class)
							&& (as = p.getActualTypeArguments()) != null && as.length == 1 && as[0] == c) // type
																											// arg
																											// is
																											// c
						return c;
				}
			}
		}
		return null;
	}

	/**
	 * Returns k.compareTo(x) if x matches kc (k's screened comparable class),
	 * else 0.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" }) // for cast to Comparable
	static int compareComparables(Class<?> kc, Object k, Object x) {
		return (x == null || x.getClass() != kc ? 0 : ((Comparable) k).compareTo(x));
	}

	@SuppressWarnings("unchecked")
	static final <K, V> Node<K, V> tabAt(Node<K, V>[] tab, int i) {
		return (Node<K, V>) U.getObjectVolatile(tab, ((long) i << ASHIFT) + ABASE);
	}

	static final <K, V> boolean casTabAt(Node<K, V>[] tab, int i, Node<K, V> c, Node<K, V> v) {
		return U.compareAndSwapObject(tab, ((long) i << ASHIFT) + ABASE, c, v);
	}

	static final <K, V> void setTabAt(Node<K, V>[] tab, int i, Node<K, V> v) {
		U.putObjectVolatile(tab, ((long) i << ASHIFT) + ABASE, v);
	}

	/* ---------------- Fields -------------- */

	// 用来存放Node节点数据的，默认为null，默认大小为16的数组，每次扩容时大小总是2的幂次方；
	transient volatile Node<K, V>[] table;

	// 扩容时新生成的数据，数组为table的两倍；
	private transient volatile Node<K, V>[] nextTable;

	/**
	 * Base counter value, used mainly when there is no contention, but also as
	 * a fallback during table initialization races. Updated via CAS.
	 */
	private transient volatile long baseCount;

	/**
	 * T控制标识符，用来控制table初始化和扩容操作的，在不同的地方有不同的用途，
	 * 其值也不同，所代表的含义也不同 负数代表正在进行初始化或扩容操作
	 * -1代表正在初始化 -N 表示有N-1个线程正在进行扩容操作 正数或0代表hash表
	 * 还没有被初始化，这个数值表示初始化或下一次进行扩容的大小
	 * 
	 */
	private transient volatile int sizeCtl;

	/**
	 * The next table index (plus one) to split while resizing.
	 */
	private transient volatile int transferIndex;

	/**
	 * Spinlock (locked via CAS) used when resizing and/or creating
	 * CounterCells.
	 */
	private transient volatile int cellsBusy;

	/**
	 * Table of counter cells. When non-null, size is a power of 2.
	 */
	private transient volatile CounterCell[] counterCells;

	// views
	private transient KeySetView8<K, V> keySet;
	private transient ValuesView<K, V> values;
	private transient EntrySetView<K, V> entrySet;

	/* ---------------- Public operations -------------- */

	/**
	 * Creates a new, empty map with the default initial table size (16).
	 */
	public ConcurrentHashMap8() {
	}

	public ConcurrentHashMap8(int initialCapacity) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException();
		int cap = ((initialCapacity >= (MAXIMUM_CAPACITY >>> 1)) ? MAXIMUM_CAPACITY
				: tableSizeFor(initialCapacity + (initialCapacity >>> 1) + 1));
		this.sizeCtl = cap;
	}

	/**
	 * Creates a new map with the same mappings as the given map.
	 *
	 * @param m
	 *            the map
	 */
	public ConcurrentHashMap8(Map<? extends K, ? extends V> m) {
		this.sizeCtl = DEFAULT_CAPACITY;
		putAll(m);
	}

	public ConcurrentHashMap8(int initialCapacity, float loadFactor) {
		this(initialCapacity, loadFactor, 1);
	}

	public ConcurrentHashMap8(int initialCapacity, float loadFactor, int concurrencyLevel) {
		if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0)
			throw new IllegalArgumentException();
		if (initialCapacity < concurrencyLevel) // Use at least as many bins
			initialCapacity = concurrencyLevel; // as estimated threads
		long size = (long) (1.0 + (long) initialCapacity / loadFactor);
		int cap = (size >= (long) MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : tableSizeFor((int) size);
		this.sizeCtl = cap;
	}

	// Original (since JDK1.2) Map methods

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		long n = sumCount();
		return ((n < 0L) ? 0 : (n > (long) Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) n);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty() {
		return sumCount() <= 0L; // ignore transient negative values
	}

	public V get(Object key) {
		Node<K, V>[] tab;
		Node<K, V> e, p;
		int n, eh;
		K ek;
		int h = spread(key.hashCode());
		if ((tab = table) != null && (n = tab.length) > 0 && (e = tabAt(tab, (n - 1) & h)) != null) {
			if ((eh = e.hash) == h) {
				if ((ek = e.key) == key || (ek != null && key.equals(ek)))
					return e.val;
			} else if (eh < 0)
				return (p = e.find(h, key)) != null ? p.val : null;
			while ((e = e.next) != null) {
				if (e.hash == h && ((ek = e.key) == key || (ek != null && key.equals(ek))))
					return e.val;
			}
		}
		return null;
	}

 
	public boolean containsKey(Object key) {
		return get(key) != null;
	}

	 
	public boolean containsValue(Object value) {
		if (value == null)
			throw new NullPointerException();
		Node<K, V>[] t;
		if ((t = table) != null) {
			Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
			for (Node<K, V> p; (p = it.advance()) != null;) {
				V v;
				if ((v = p.val) == value || (v != null && value.equals(v)))
					return true;
			}
		}
		return false;
	}
 
	public V put(K key, V value) {
		return putVal(key, value, false);
	}

	/** Implementation for put and putIfAbsent */
	final V putVal(K key, V value, boolean onlyIfAbsent) {
		if (key == null || value == null)
			throw new NullPointerException();
		int hash = spread(key.hashCode());
		int binCount = 0;
		for (Node<K, V>[] tab = table;;) {
			Node<K, V> f;
			int n, i, fh;
			if (tab == null || (n = tab.length) == 0)
				tab = initTable();
			else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
				//1、如果相应位置的Node还未初始化，则通过CAS插入相应的数据；
				if (casTabAt(tab, i, null, new Node<K, V>(hash, key, value, null)))
					break; // no lock when adding to empty bin
			} else if ((fh = f.hash) == MOVED)
				tab = helpTransfer(tab, f);
			else {
				//2、如果相应位置的Node不为空，且当前该节点不处于移动状态，
			    //则对该节点加synchronized锁，如果该节点的hash不小于0，则遍历链表更新节点或插入新节点；
				V oldVal = null;
				synchronized (f) {
					if (tabAt(tab, i) == f) {
						
						if (fh >= 0) {
							binCount = 1;
							for (Node<K, V> e = f;; ++binCount) {
								K ek;
								if (e.hash == hash && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
									oldVal = e.val;
									if (!onlyIfAbsent)
										e.val = value;
									break;
								}
								Node<K, V> pred = e;
								if ((e = e.next) == null) {
									pred.next = new Node<K, V>(hash, key, value, null);
									break;
								}
							}
						 //3.如果该节点是TreeBin类型的节点，说明是红黑树结构，则通过putTreeVal方法往红黑树中插入节点；
						} else if (f instanceof TreeBin) {
							Node<K, V> p;
							binCount = 2;
							if ((p = ((TreeBin<K, V>) f).putTreeVal(hash, key, value)) != null) {
								oldVal = p.val;
								if (!onlyIfAbsent)
									p.val = value;
							}
						}
					}
				}
				//4、如果binCount不为0，说明put操作对数据产生了影响，如果当前链表的个数达到8个，
				//则通过treeifyBin方法转化为红黑树，如果oldVal不为空，
				//说明是一次更新操作，没有对元素个数产生影响，则直接返回旧值；
				if (binCount != 0) {
					if (binCount >= TREEIFY_THRESHOLD)
						treeifyBin(tab, i);
					if (oldVal != null)
						return oldVal;
					break;
				}
			}
		}
		//5、如果插入的是一个新节点，则执行addCount()方法尝试更新元素个数baseCount；
		addCount(1L, binCount);
		return null;
	}

	/**
	 * Copies all of the mappings from the specified map to this one. These
	 * mappings replace any mappings that this map had for any of the keys
	 * currently in the specified map.
	 *
	 * @param m
	 *            mappings to be stored in this map
	 */
	public void putAll(Map<? extends K, ? extends V> m) {
		tryPresize(m.size());
		for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
			putVal(e.getKey(), e.getValue(), false);
	}

	/**
	 * Removes the key (and its corresponding value) from this map. This method
	 * does nothing if the key is not in the map.
	 *
	 * @param key
	 *            the key that needs to be removed
	 * @return the previous value associated with {@code key}, or {@code null}
	 *         if there was no mapping for {@code key}
	 * @throws NullPointerException
	 *             if the specified key is null
	 */
	public V remove(Object key) {
		return replaceNode(key, null, null);
	}

	/**
	 * Implementation for the four public remove/replace methods: Replaces node
	 * value with v, conditional upon match of cv if non-null. If resulting
	 * value is null, delete.
	 */
	final V replaceNode(Object key, V value, Object cv) {
		int hash = spread(key.hashCode());
		for (Node<K, V>[] tab = table;;) {
			Node<K, V> f;
			int n, i, fh;
			if (tab == null || (n = tab.length) == 0 || (f = tabAt(tab, i = (n - 1) & hash)) == null)
				break;
			else if ((fh = f.hash) == MOVED)
				tab = helpTransfer(tab, f);
			else {
				V oldVal = null;
				boolean validated = false;
				synchronized (f) {
					if (tabAt(tab, i) == f) {
						if (fh >= 0) {
							validated = true;
							for (Node<K, V> e = f, pred = null;;) {
								K ek;
								if (e.hash == hash && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
									V ev = e.val;
									if (cv == null || cv == ev || (ev != null && cv.equals(ev))) {
										oldVal = ev;
										if (value != null)
											e.val = value;
										else if (pred != null)
											pred.next = e.next;
										else
											setTabAt(tab, i, e.next);
									}
									break;
								}
								pred = e;
								if ((e = e.next) == null)
									break;
							}
						} else if (f instanceof TreeBin) {
							validated = true;
							TreeBin<K, V> t = (TreeBin<K, V>) f;
							TreeNode<K, V> r, p;
							if ((r = t.root) != null && (p = r.findTreeNode(hash, key, null)) != null) {
								V pv = p.val;
								if (cv == null || cv == pv || (pv != null && cv.equals(pv))) {
									oldVal = pv;
									if (value != null)
										p.val = value;
									else if (t.removeTreeNode(p))
										setTabAt(tab, i, untreeify(t.first));
								}
							}
						}
					}
				}
				if (validated) {
					if (oldVal != null) {
						if (value == null)
							addCount(-1L, -1);
						return oldVal;
					}
					break;
				}
			}
		}
		return null;
	}

	/**
	 * Removes all of the mappings from this map.
	 */
	public void clear() {
		long delta = 0L; // negative number of deletions
		int i = 0;
		Node<K, V>[] tab = table;
		while (tab != null && i < tab.length) {
			int fh;
			Node<K, V> f = tabAt(tab, i);
			if (f == null)
				++i;
			else if ((fh = f.hash) == MOVED) {
				tab = helpTransfer(tab, f);
				i = 0; // restart
			} else {
				synchronized (f) {
					if (tabAt(tab, i) == f) {
						Node<K, V> p = (fh >= 0 ? f : (f instanceof TreeBin) ? ((TreeBin<K, V>) f).first : null);
						while (p != null) {
							--delta;
							p = p.next;
						}
						setTabAt(tab, i++, null);
					}
				}
			}
		}
		if (delta != 0L)
			addCount(delta, -1);
	}

	/**
	 * Returns a {@link Set} view of the keys contained in this map. The set is
	 * backed by the map, so changes to the map are reflected in the set, and
	 * vice-versa. The set supports element removal, which removes the
	 * corresponding mapping from this map, via the {@code Iterator.remove},
	 * {@code Set.remove}, {@code removeAll}, {@code retainAll}, and
	 * {@code clear} operations. It does not support the {@code add} or
	 * {@code addAll} operations.
	 *
	 * <p>
	 * The view's iterators and spliterators are
	 * <a href="package-summary.html#Weakly"><i>weakly consistent</i></a>.
	 *
	 * <p>
	 * The view's {@code spliterator} reports {@link Spliterator#CONCURRENT},
	 * {@link Spliterator#DISTINCT}, and {@link Spliterator#NONNULL}.
	 *
	 * @return the set view
	 */
	public KeySetView8<K, V> keySet() {
		KeySetView8<K, V> ks;
		return (ks = keySet) != null ? ks : (keySet = new KeySetView8<K, V>(this, null));
	}

	/**
	 * Returns a {@link Collection} view of the values contained in this map.
	 * The collection is backed by the map, so changes to the map are reflected
	 * in the collection, and vice-versa. The collection supports element
	 * removal, which removes the corresponding mapping from this map, via the
	 * {@code Iterator.remove}, {@code Collection.remove}, {@code removeAll},
	 * {@code retainAll}, and {@code clear} operations. It does not support the
	 * {@code add} or {@code addAll} operations.
	 *
	 * <p>
	 * The view's iterators and spliterators are
	 * <a href="package-summary.html#Weakly"><i>weakly consistent</i></a>.
	 *
	 * <p>
	 * The view's {@code spliterator} reports {@link Spliterator#CONCURRENT} and
	 * {@link Spliterator#NONNULL}.
	 *
	 * @return the collection view
	 */
	public Collection<V> values() {
		ValuesView<K, V> vs;
		return (vs = values) != null ? vs : (values = new ValuesView<K, V>(this));
	}

	public Set<Map.Entry<K, V>> entrySet() {
		EntrySetView<K, V> es;
		return (es = entrySet) != null ? es : (entrySet = new EntrySetView<K, V>(this));
	}

	/**
	 * Returns the hash code value for this {@link Map}, i.e., the sum of, for
	 * each key-value pair in the map,
	 * {@code key.hashCode() ^ value.hashCode()}.
	 *
	 * @return the hash code value for this map
	 */
	public int hashCode() {
		int h = 0;
		Node<K, V>[] t;
		if ((t = table) != null) {
			Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
			for (Node<K, V> p; (p = it.advance()) != null;)
				h += p.key.hashCode() ^ p.val.hashCode();
		}
		return h;
	}

	/**
	 * Returns a string representation of this map. The string representation
	 * consists of a list of key-value mappings (in no particular order)
	 * enclosed in braces ("{@code {}}"). Adjacent mappings are separated by the
	 * characters {@code ", "} (comma and space). Each key-value mapping is
	 * rendered as the key followed by an equals sign ("{@code =}") followed by
	 * the associated value.
	 *
	 * @return a string representation of this map
	 */
	public String toString() {
		Node<K, V>[] t;
		int f = (t = table) == null ? 0 : t.length;
		Traverser<K, V> it = new Traverser<K, V>(t, f, 0, f);
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		Node<K, V> p;
		if ((p = it.advance()) != null) {
			for (;;) {
				K k = p.key;
				V v = p.val;
				sb.append(k == this ? "(this Map)" : k);
				sb.append('=');
				sb.append(v == this ? "(this Map)" : v);
				if ((p = it.advance()) == null)
					break;
				sb.append(',').append(' ');
			}
		}
		return sb.append('}').toString();
	}

	/**
	 * Compares the specified object with this map for equality. Returns
	 * {@code true} if the given object is a map with the same mappings as this
	 * map. This operation may return misleading results if either map is
	 * concurrently modified during execution of this method.
	 *
	 * @param o
	 *            object to be compared for equality with this map
	 * @return {@code true} if the specified object is equal to this map
	 */
	public boolean equals(Object o) {
		if (o != this) {
			if (!(o instanceof Map))
				return false;
			Map<?, ?> m = (Map<?, ?>) o;
			Node<K, V>[] t;
			int f = (t = table) == null ? 0 : t.length;
			Traverser<K, V> it = new Traverser<K, V>(t, f, 0, f);
			for (Node<K, V> p; (p = it.advance()) != null;) {
				V val = p.val;
				Object v = m.get(p.key);
				if (v == null || (v != val && !v.equals(val)))
					return false;
			}
			for (Map.Entry<?, ?> e : m.entrySet()) {
				Object mk, mv, v;
				if ((mk = e.getKey()) == null || (mv = e.getValue()) == null || (v = get(mk)) == null
						|| (mv != v && !mv.equals(v)))
					return false;
			}
		}
		return true;
	}

	/**
	 * Stripped-down version of helper class used in previous version, declared
	 * for the sake of serialization compatibility
	 */
	static class Segment<K, V> extends ReentrantLock implements Serializable {
		private static final long serialVersionUID = 2249069246763182397L;
		final float loadFactor;

		Segment(float lf) {
			this.loadFactor = lf;
		}
	}

	/**
	 * Saves the state of the {@code ConcurrentHashMap} instance to a stream
	 * (i.e., serializes it).
	 * 
	 * @param s
	 *            the stream
	 * @throws java.io.IOException
	 *             if an I/O error occurs
	 * @serialData the key (Object) and value (Object) for each key-value
	 *             mapping, followed by a null pair. The key-value mappings are
	 *             emitted in no particular order.
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		// For serialization compatibility
		// Emulate segment calculation from previous version of this class
		int sshift = 0;
		int ssize = 1;
		while (ssize < DEFAULT_CONCURRENCY_LEVEL) {
			++sshift;
			ssize <<= 1;
		}
		int segmentShift = 32 - sshift;
		int segmentMask = ssize - 1;
		@SuppressWarnings("unchecked")
		Segment<K, V>[] segments = (Segment<K, V>[]) new Segment<?, ?>[DEFAULT_CONCURRENCY_LEVEL];
		for (int i = 0; i < segments.length; ++i)
			segments[i] = new Segment<K, V>(LOAD_FACTOR);
		s.putFields().put("segments", segments);
		s.putFields().put("segmentShift", segmentShift);
		s.putFields().put("segmentMask", segmentMask);
		s.writeFields();

		Node<K, V>[] t;
		if ((t = table) != null) {
			Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
			for (Node<K, V> p; (p = it.advance()) != null;) {
				s.writeObject(p.key);
				s.writeObject(p.val);
			}
		}
		s.writeObject(null);
		s.writeObject(null);
		segments = null; // throw away
	}

	/**
	 * Reconstitutes the instance from a stream (that is, deserializes it).
	 * 
	 * @param s
	 *            the stream
	 * @throws ClassNotFoundException
	 *             if the class of a serialized object could not be found
	 * @throws java.io.IOException
	 *             if an I/O error occurs
	 */
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		/*
		 * To improve performance in typical cases, we create nodes while
		 * reading, then place in table once size is known. However, we must
		 * also validate uniqueness and deal with overpopulated bins while doing
		 * so, which requires specialized versions of putVal mechanics.
		 */
		sizeCtl = -1; // force exclusion for table construction
		s.defaultReadObject();
		long size = 0L;
		Node<K, V> p = null;
		for (;;) {
			@SuppressWarnings("unchecked")
			K k = (K) s.readObject();
			@SuppressWarnings("unchecked")
			V v = (V) s.readObject();
			if (k != null && v != null) {
				p = new Node<K, V>(spread(k.hashCode()), k, v, p);
				++size;
			} else
				break;
		}
		if (size == 0L)
			sizeCtl = 0;
		else {
			int n;
			if (size >= (long) (MAXIMUM_CAPACITY >>> 1))
				n = MAXIMUM_CAPACITY;
			else {
				int sz = (int) size;
				n = tableSizeFor(sz + (sz >>> 1) + 1);
			}
			@SuppressWarnings("unchecked")
			Node<K, V>[] tab = (Node<K, V>[]) new Node<?, ?>[n];
			int mask = n - 1;
			long added = 0L;
			while (p != null) {
				boolean insertAtFront;
				Node<K, V> next = p.next, first;
				int h = p.hash, j = h & mask;
				if ((first = tabAt(tab, j)) == null)
					insertAtFront = true;
				else {
					K k = p.key;
					if (first.hash < 0) {
						TreeBin<K, V> t = (TreeBin<K, V>) first;
						if (t.putTreeVal(h, k, p.val) == null)
							++added;
						insertAtFront = false;
					} else {
						int binCount = 0;
						insertAtFront = true;
						Node<K, V> q;
						K qk;
						for (q = first; q != null; q = q.next) {
							if (q.hash == h && ((qk = q.key) == k || (qk != null && k.equals(qk)))) {
								insertAtFront = false;
								break;
							}
							++binCount;
						}
						if (insertAtFront && binCount >= TREEIFY_THRESHOLD) {
							insertAtFront = false;
							++added;
							p.next = first;
							TreeNode<K, V> hd = null, tl = null;
							for (q = p; q != null; q = q.next) {
								TreeNode<K, V> t = new TreeNode<K, V>(q.hash, q.key, q.val, null, null);
								if ((t.prev = tl) == null)
									hd = t;
								else
									tl.next = t;
								tl = t;
							}
							setTabAt(tab, j, new TreeBin<K, V>(hd));
						}
					}
				}
				if (insertAtFront) {
					++added;
					p.next = first;
					setTabAt(tab, j, p);
				}
				p = next;
			}
			table = tab;
			sizeCtl = n - (n >>> 2);
			baseCount = added;
		}
	}

	// ConcurrentMap methods

	/**
	 * {@inheritDoc}
	 *
	 * @return the previous value associated with the specified key, or
	 *         {@code null} if there was no mapping for the key
	 * @throws NullPointerException
	 *             if the specified key or value is null
	 */
	public V putIfAbsent(K key, V value) {
		return putVal(key, value, true);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws NullPointerException
	 *             if the specified key is null
	 */
	public boolean remove(Object key, Object value) {
		if (key == null)
			throw new NullPointerException();
		return value != null && replaceNode(key, null, value) != null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws NullPointerException
	 *             if any of the arguments are null
	 */
	public boolean replace(K key, V oldValue, V newValue) {
		if (key == null || oldValue == null || newValue == null)
			throw new NullPointerException();
		return replaceNode(key, newValue, oldValue) != null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return the previous value associated with the specified key, or
	 *         {@code null} if there was no mapping for the key
	 * @throws NullPointerException
	 *             if the specified key or value is null
	 */
	public V replace(K key, V value) {
		if (key == null || value == null)
			throw new NullPointerException();
		return replaceNode(key, value, null);
	}

	// Overrides of JDK8+ Map extension method defaults

	/**
	 * Returns the value to which the specified key is mapped, or the given
	 * default value if this map contains no mapping for the key.
	 *
	 * @param key
	 *            the key whose associated value is to be returned
	 * @param defaultValue
	 *            the value to return if this map contains no mapping for the
	 *            given key
	 * @return the mapping for the key, if present; else the default value
	 * @throws NullPointerException
	 *             if the specified key is null
	 */
	public V getOrDefault(Object key, V defaultValue) {
		V v;
		return (v = get(key)) == null ? defaultValue : v;
	}

	public void forEach(BiConsumer<? super K, ? super V> action) {
		if (action == null)
			throw new NullPointerException();
		Node<K, V>[] t;
		if ((t = table) != null) {
			Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
			for (Node<K, V> p; (p = it.advance()) != null;) {
				action.accept(p.key, p.val);
			}
		}
	}

	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		if (function == null)
			throw new NullPointerException();
		Node<K, V>[] t;
		if ((t = table) != null) {
			Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
			for (Node<K, V> p; (p = it.advance()) != null;) {
				V oldValue = p.val;
				for (K key = p.key;;) {
					V newValue = function.apply(key, oldValue);
					if (newValue == null)
						throw new NullPointerException();
					if (replaceNode(key, newValue, oldValue) != null || (oldValue = get(key)) == null)
						break;
				}
			}
		}
	}

	/**
	 * If the specified key is not already associated with a value, attempts to
	 * compute its value using the given mapping function and enters it into
	 * this map unless {@code null}. The entire method invocation is performed
	 * atomically, so the function is applied at most once per key. Some
	 * attempted update operations on this map by other threads may be blocked
	 * while computation is in progress, so the computation should be short and
	 * simple, and must not attempt to update any other mappings of this map.
	 *
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param mappingFunction
	 *            the function to compute a value
	 * @return the current (existing or computed) value associated with the
	 *         specified key, or null if the computed value is null
	 * @throws NullPointerException
	 *             if the specified key or mappingFunction is null
	 * @throws IllegalStateException
	 *             if the computation detectably attempts a recursive update to
	 *             this map that would otherwise never complete
	 * @throws RuntimeException
	 *             or Error if the mappingFunction does so, in which case the
	 *             mapping is left unestablished
	 */
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		if (key == null || mappingFunction == null)
			throw new NullPointerException();
		int h = spread(key.hashCode());
		V val = null;
		int binCount = 0;
		for (Node<K, V>[] tab = table;;) {
			Node<K, V> f;
			int n, i, fh;
			if (tab == null || (n = tab.length) == 0)
				tab = initTable();
			else if ((f = tabAt(tab, i = (n - 1) & h)) == null) {
				Node<K, V> r = new ReservationNode<K, V>();
				synchronized (r) {
					if (casTabAt(tab, i, null, r)) {
						binCount = 1;
						Node<K, V> node = null;
						try {
							if ((val = mappingFunction.apply(key)) != null)
								node = new Node<K, V>(h, key, val, null);
						} finally {
							setTabAt(tab, i, node);
						}
					}
				}
				if (binCount != 0)
					break;
			} else if ((fh = f.hash) == MOVED)
				tab = helpTransfer(tab, f);
			else {
				boolean added = false;
				synchronized (f) {
					if (tabAt(tab, i) == f) {
						if (fh >= 0) {
							binCount = 1;
							for (Node<K, V> e = f;; ++binCount) {
								K ek;
								V ev;
								if (e.hash == h && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
									val = e.val;
									break;
								}
								Node<K, V> pred = e;
								if ((e = e.next) == null) {
									if ((val = mappingFunction.apply(key)) != null) {
										added = true;
										pred.next = new Node<K, V>(h, key, val, null);
									}
									break;
								}
							}
						} else if (f instanceof TreeBin) {
							binCount = 2;
							TreeBin<K, V> t = (TreeBin<K, V>) f;
							TreeNode<K, V> r, p;
							if ((r = t.root) != null && (p = r.findTreeNode(h, key, null)) != null)
								val = p.val;
							else if ((val = mappingFunction.apply(key)) != null) {
								added = true;
								t.putTreeVal(h, key, val);
							}
						}
					}
				}
				if (binCount != 0) {
					if (binCount >= TREEIFY_THRESHOLD)
						treeifyBin(tab, i);
					if (!added)
						return val;
					break;
				}
			}
		}
		if (val != null)
			addCount(1L, binCount);
		return val;
	}

	/**
	 * If the value for the specified key is present, attempts to compute a new
	 * mapping given the key and its current mapped value. The entire method
	 * invocation is performed atomically. Some attempted update operations on
	 * this map by other threads may be blocked while computation is in
	 * progress, so the computation should be short and simple, and must not
	 * attempt to update any other mappings of this map.
	 *
	 * @param key
	 *            key with which a value may be associated
	 * @param remappingFunction
	 *            the function to compute a value
	 * @return the new value associated with the specified key, or null if none
	 * @throws NullPointerException
	 *             if the specified key or remappingFunction is null
	 * @throws IllegalStateException
	 *             if the computation detectably attempts a recursive update to
	 *             this map that would otherwise never complete
	 * @throws RuntimeException
	 *             or Error if the remappingFunction does so, in which case the
	 *             mapping is unchanged
	 */
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		if (key == null || remappingFunction == null)
			throw new NullPointerException();
		int h = spread(key.hashCode());
		V val = null;
		int delta = 0;
		int binCount = 0;
		for (Node<K, V>[] tab = table;;) {
			Node<K, V> f;
			int n, i, fh;
			if (tab == null || (n = tab.length) == 0)
				tab = initTable();
			else if ((f = tabAt(tab, i = (n - 1) & h)) == null)
				break;
			else if ((fh = f.hash) == MOVED)
				tab = helpTransfer(tab, f);
			else {
				synchronized (f) {
					if (tabAt(tab, i) == f) {
						if (fh >= 0) {
							binCount = 1;
							for (Node<K, V> e = f, pred = null;; ++binCount) {
								K ek;
								if (e.hash == h && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
									val = remappingFunction.apply(key, e.val);
									if (val != null)
										e.val = val;
									else {
										delta = -1;
										Node<K, V> en = e.next;
										if (pred != null)
											pred.next = en;
										else
											setTabAt(tab, i, en);
									}
									break;
								}
								pred = e;
								if ((e = e.next) == null)
									break;
							}
						} else if (f instanceof TreeBin) {
							binCount = 2;
							TreeBin<K, V> t = (TreeBin<K, V>) f;
							TreeNode<K, V> r, p;
							if ((r = t.root) != null && (p = r.findTreeNode(h, key, null)) != null) {
								val = remappingFunction.apply(key, p.val);
								if (val != null)
									p.val = val;
								else {
									delta = -1;
									if (t.removeTreeNode(p))
										setTabAt(tab, i, untreeify(t.first));
								}
							}
						}
					}
				}
				if (binCount != 0)
					break;
			}
		}
		if (delta != 0)
			addCount((long) delta, binCount);
		return val;
	}

	/**
	 * Attempts to compute a mapping for the specified key and its current
	 * mapped value (or {@code null} if there is no current mapping). The entire
	 * method invocation is performed atomically. Some attempted update
	 * operations on this map by other threads may be blocked while computation
	 * is in progress, so the computation should be short and simple, and must
	 * not attempt to update any other mappings of this Map.
	 *
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param remappingFunction
	 *            the function to compute a value
	 * @return the new value associated with the specified key, or null if none
	 * @throws NullPointerException
	 *             if the specified key or remappingFunction is null
	 * @throws IllegalStateException
	 *             if the computation detectably attempts a recursive update to
	 *             this map that would otherwise never complete
	 * @throws RuntimeException
	 *             or Error if the remappingFunction does so, in which case the
	 *             mapping is unchanged
	 */
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		if (key == null || remappingFunction == null)
			throw new NullPointerException();
		int h = spread(key.hashCode());
		V val = null;
		int delta = 0;
		int binCount = 0;
		for (Node<K, V>[] tab = table;;) {
			Node<K, V> f;
			int n, i, fh;
			if (tab == null || (n = tab.length) == 0)
				tab = initTable();
			else if ((f = tabAt(tab, i = (n - 1) & h)) == null) {
				Node<K, V> r = new ReservationNode<K, V>();
				synchronized (r) {
					if (casTabAt(tab, i, null, r)) {
						binCount = 1;
						Node<K, V> node = null;
						try {
							if ((val = remappingFunction.apply(key, null)) != null) {
								delta = 1;
								node = new Node<K, V>(h, key, val, null);
							}
						} finally {
							setTabAt(tab, i, node);
						}
					}
				}
				if (binCount != 0)
					break;
			} else if ((fh = f.hash) == MOVED)
				tab = helpTransfer(tab, f);
			else {
				synchronized (f) {
					if (tabAt(tab, i) == f) {
						if (fh >= 0) {
							binCount = 1;
							for (Node<K, V> e = f, pred = null;; ++binCount) {
								K ek;
								if (e.hash == h && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
									val = remappingFunction.apply(key, e.val);
									if (val != null)
										e.val = val;
									else {
										delta = -1;
										Node<K, V> en = e.next;
										if (pred != null)
											pred.next = en;
										else
											setTabAt(tab, i, en);
									}
									break;
								}
								pred = e;
								if ((e = e.next) == null) {
									val = remappingFunction.apply(key, null);
									if (val != null) {
										delta = 1;
										pred.next = new Node<K, V>(h, key, val, null);
									}
									break;
								}
							}
						} else if (f instanceof TreeBin) {
							binCount = 1;
							TreeBin<K, V> t = (TreeBin<K, V>) f;
							TreeNode<K, V> r, p;
							if ((r = t.root) != null)
								p = r.findTreeNode(h, key, null);
							else
								p = null;
							V pv = (p == null) ? null : p.val;
							val = remappingFunction.apply(key, pv);
							if (val != null) {
								if (p != null)
									p.val = val;
								else {
									delta = 1;
									t.putTreeVal(h, key, val);
								}
							} else if (p != null) {
								delta = -1;
								if (t.removeTreeNode(p))
									setTabAt(tab, i, untreeify(t.first));
							}
						}
					}
				}
				if (binCount != 0) {
					if (binCount >= TREEIFY_THRESHOLD)
						treeifyBin(tab, i);
					break;
				}
			}
		}
		if (delta != 0)
			addCount((long) delta, binCount);
		return val;
	}

	/**
	 * If the specified key is not already associated with a (non-null) value,
	 * associates it with the given value. Otherwise, replaces the value with
	 * the results of the given remapping function, or removes if {@code null}.
	 * The entire method invocation is performed atomically. Some attempted
	 * update operations on this map by other threads may be blocked while
	 * computation is in progress, so the computation should be short and
	 * simple, and must not attempt to update any other mappings of this Map.
	 *
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            the value to use if absent
	 * @param remappingFunction
	 *            the function to recompute a value if present
	 * @return the new value associated with the specified key, or null if none
	 * @throws NullPointerException
	 *             if the specified key or the remappingFunction is null
	 * @throws RuntimeException
	 *             or Error if the remappingFunction does so, in which case the
	 *             mapping is unchanged
	 */
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		if (key == null || value == null || remappingFunction == null)
			throw new NullPointerException();
		int h = spread(key.hashCode());
		V val = null;
		int delta = 0;
		int binCount = 0;
		for (Node<K, V>[] tab = table;;) {
			Node<K, V> f;
			int n, i, fh;
			if (tab == null || (n = tab.length) == 0)
				tab = initTable();
			else if ((f = tabAt(tab, i = (n - 1) & h)) == null) {
				if (casTabAt(tab, i, null, new Node<K, V>(h, key, value, null))) {
					delta = 1;
					val = value;
					break;
				}
			} else if ((fh = f.hash) == MOVED)
				tab = helpTransfer(tab, f);
			else {
				synchronized (f) {
					if (tabAt(tab, i) == f) {
						if (fh >= 0) {
							binCount = 1;
							for (Node<K, V> e = f, pred = null;; ++binCount) {
								K ek;
								if (e.hash == h && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
									val = remappingFunction.apply(e.val, value);
									if (val != null)
										e.val = val;
									else {
										delta = -1;
										Node<K, V> en = e.next;
										if (pred != null)
											pred.next = en;
										else
											setTabAt(tab, i, en);
									}
									break;
								}
								pred = e;
								if ((e = e.next) == null) {
									delta = 1;
									val = value;
									pred.next = new Node<K, V>(h, key, val, null);
									break;
								}
							}
						} else if (f instanceof TreeBin) {
							binCount = 2;
							TreeBin<K, V> t = (TreeBin<K, V>) f;
							TreeNode<K, V> r = t.root;
							TreeNode<K, V> p = (r == null) ? null : r.findTreeNode(h, key, null);
							val = (p == null) ? value : remappingFunction.apply(p.val, value);
							if (val != null) {
								if (p != null)
									p.val = val;
								else {
									delta = 1;
									t.putTreeVal(h, key, val);
								}
							} else if (p != null) {
								delta = -1;
								if (t.removeTreeNode(p))
									setTabAt(tab, i, untreeify(t.first));
							}
						}
					}
				}
				if (binCount != 0) {
					if (binCount >= TREEIFY_THRESHOLD)
						treeifyBin(tab, i);
					break;
				}
			}
		}
		if (delta != 0)
			addCount((long) delta, binCount);
		return val;
	}

	// Hashtable legacy methods

	/**
	 * Legacy method testing if some key maps into the specified value in this
	 * table. This method is identical in functionality to
	 * {@link #containsValue(Object)}, and exists solely to ensure full
	 * compatibility with class {@link java.util.Hashtable}, which supported
	 * this method prior to introduction of the Java Collections framework.
	 *
	 * @param value
	 *            a value to search for
	 * @return {@code true} if and only if some key maps to the {@code value}
	 *         argument in this table as determined by the {@code equals}
	 *         method; {@code false} otherwise
	 * @throws NullPointerException
	 *             if the specified value is null
	 */
	public boolean contains(Object value) {
		return containsValue(value);
	}

	/**
	 * Returns an enumeration of the keys in this table.
	 *
	 * @return an enumeration of the keys in this table
	 * @see #keySet()
	 */
	public Enumeration<K> keys() {
		Node<K, V>[] t;
		int f = (t = table) == null ? 0 : t.length;
		return new KeyIterator8<K, V>(t, f, 0, f, this);
	}

	/**
	 * Returns an enumeration of the values in this table.
	 *
	 * @return an enumeration of the values in this table
	 * @see #values()
	 */
	public Enumeration<V> elements() {
		Node<K, V>[] t;
		int f = (t = table) == null ? 0 : t.length;
		return new ValueIterator8<K, V>(t, f, 0, f, this);
	}

	// ConcurrentHashMap-only methods

	/**
	 * Returns the number of mappings. This method should be used instead of
	 * {@link #size} because a ConcurrentHashMap may contain more mappings than
	 * can be represented as an int. The value returned is an estimate; the
	 * actual count may differ if there are concurrent insertions or removals.
	 *
	 * @return the number of mappings
	 * @since 1.8
	 */
	public long mappingCount() {
		long n = sumCount();
		return (n < 0L) ? 0L : n; // ignore transient negative values
	}

	/**
	 * Creates a new {@link Set} backed by a ConcurrentHashMap from the given
	 * type to {@code Boolean.TRUE}.
	 *
	 * @param <K>
	 *            the element type of the returned set
	 * @return the new set
	 * @since 1.8
	 */
	public static <K> KeySetView8<K, Boolean> newKeySet() {
		return new KeySetView8<K, Boolean>(new ConcurrentHashMap8<K, Boolean>(), Boolean.TRUE);
	}

	public static <K> KeySetView8<K, Boolean> newKeySet(int initialCapacity) {
		return new KeySetView8<K, Boolean>(new ConcurrentHashMap8<K, Boolean>(initialCapacity), Boolean.TRUE);
	}

	public KeySetView8<K, V> keySet(V mappedValue) {
		if (mappedValue == null)
			throw new NullPointerException();
		return new KeySetView8<K, V>(this, mappedValue);
	}

	/* ---------------- Special Nodes -------------- */

	/**
	 * 一个特殊的Node节点，hash值为-1，其中存储nextTable的引用。
	 * 只有table发生扩容的时候，ForwardingNode才会发挥作用， 作为一个占位符放在table中表示当前节点为null或则已经被移动
	 */
	static final class ForwardingNode<K, V> extends Node<K, V> {
		final Node<K, V>[] nextTable;

		ForwardingNode(Node<K, V>[] tab) {
			super(MOVED, null, null, null);
			this.nextTable = tab;
		}

		Node<K, V> find(int h, Object k) {
			// loop to avoid arbitrarily deep recursion on forwarding nodes
			outer: for (Node<K, V>[] tab = nextTable;;) {
				Node<K, V> e;
				int n;
				if (k == null || tab == null || (n = tab.length) == 0 || (e = tabAt(tab, (n - 1) & h)) == null)
					return null;
				for (;;) {
					int eh;
					K ek;
					if ((eh = e.hash) == h && ((ek = e.key) == k || (ek != null && k.equals(ek))))
						return e;
					if (eh < 0) {
						if (e instanceof ForwardingNode) {
							tab = ((ForwardingNode<K, V>) e).nextTable;
							continue outer;
						} else
							return e.find(h, k);
					}
					if ((e = e.next) == null)
						return null;
				}
			}
		}
	}

	/**
	 * A place-holder node used in computeIfAbsent and compute
	 */
	static final class ReservationNode<K, V> extends Node<K, V> {
		ReservationNode() {
			super(RESERVED, null, null, null);
		}

		Node<K, V> find(int h, Object k) {
			return null;
		}
	}

	static final int resizeStamp(int n) {
		return Integer.numberOfLeadingZeros(n) | (1 << (RESIZE_STAMP_BITS - 1));
	}

	/**
	 * 只有在执行第一次put方法时才会调用initTable()初始化Node数组 
	 */
	private final Node<K, V>[] initTable() {
		Node<K, V>[] tab;
		int sc;
		while ((tab = table) == null || tab.length == 0) {
			if ((sc = sizeCtl) < 0)
				Thread.yield(); // lost initialization race; just spin
			else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
				try {
					if ((tab = table) == null || tab.length == 0) {
						int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
						@SuppressWarnings("unchecked")
						Node<K, V>[] nt = (Node<K, V>[]) new Node<?, ?>[n];
						table = tab = nt;
						sc = n - (n >>> 2);
					}
				} finally {
					sizeCtl = sc;
				}
				break;
			}
		}
		return tab;
	}

	/**
	 * Adds to count, and if table is too small and not already resizing,
	 * initiates transfer. If already resizing, helps perform transfer if work
	 * is available. Rechecks occupancy after a transfer to see if another
	 * resize is already needed because resizings are lagging additions.
	 *
	 * @param x
	 *            the count to add
	 * @param check
	 *            if <0, don't check resize, if <= 1 only check if uncontended
	 */
	private final void addCount(long x, int check) {
		CounterCell[] as;
		long b, s;
		if ((as = counterCells) != null || !U.compareAndSwapLong(this, BASECOUNT, b = baseCount, s = b + x)) {
			CounterCell a;
			long v;
			int m;
			boolean uncontended = true;
			if (as == null || (m = as.length - 1) < 0 || (a = as[getProbe() & m]) == null
					|| !(uncontended = U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))) {
				fullAddCount(x, uncontended);
				return;
			}
			if (check <= 1)
				return;
			s = sumCount();
		}
		if (check >= 0) {
			Node<K, V>[] tab, nt;
			int n, sc;
			while (s >= (long) (sc = sizeCtl) && (tab = table) != null && (n = tab.length) < MAXIMUM_CAPACITY) {
				int rs = resizeStamp(n);
				if (sc < 0) {
					if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 || sc == rs + MAX_RESIZERS
							|| (nt = nextTable) == null || transferIndex <= 0)
						break;
					if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1))
						transfer(tab, nt);
				} else if (U.compareAndSwapInt(this, SIZECTL, sc, (rs << RESIZE_STAMP_SHIFT) + 2))
					transfer(tab, null);
				s = sumCount();
			}
		}
	}

	/**
	 * Helps transfer if a resize is in progress.
	 */
	final Node<K, V>[] helpTransfer(Node<K, V>[] tab, Node<K, V> f) {
		Node<K, V>[] nextTab;
		int sc;
		if (tab != null && (f instanceof ForwardingNode) && (nextTab = ((ForwardingNode<K, V>) f).nextTable) != null) {
			int rs = resizeStamp(tab.length);
			while (nextTab == nextTable && table == tab && (sc = sizeCtl) < 0) {
				if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 || sc == rs + MAX_RESIZERS || transferIndex <= 0)
					break;
				if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) {
					transfer(tab, nextTab);
					break;
				}
			}
			return nextTab;
		}
		return table;
	}

	/**
	 * Tries to presize table to accommodate the given number of elements.
	 *
	 * @param size
	 *            number of elements (doesn't need to be perfectly accurate)
	 */
	private final void tryPresize(int size) {
		int c = (size >= (MAXIMUM_CAPACITY >>> 1)) ? MAXIMUM_CAPACITY : tableSizeFor(size + (size >>> 1) + 1);
		int sc;
		while ((sc = sizeCtl) >= 0) {
			Node<K, V>[] tab = table;
			int n;
			if (tab == null || (n = tab.length) == 0) {
				n = (sc > c) ? sc : c;
				if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
					try {
						if (table == tab) {
							@SuppressWarnings("unchecked")
							Node<K, V>[] nt = (Node<K, V>[]) new Node<?, ?>[n];
							table = nt;
							sc = n - (n >>> 2);
						}
					} finally {
						sizeCtl = sc;
					}
				}
			} else if (c <= sc || n >= MAXIMUM_CAPACITY)
				break;
			else if (tab == table) {
				int rs = resizeStamp(n);
				if (sc < 0) {
					Node<K, V>[] nt;
					if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 || sc == rs + MAX_RESIZERS
							|| (nt = nextTable) == null || transferIndex <= 0)
						break;
					if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1))
						transfer(tab, nt);
				} else if (U.compareAndSwapInt(this, SIZECTL, sc, (rs << RESIZE_STAMP_SHIFT) + 2))
					transfer(tab, null);
			}
		}
	}

	/**
	 * Moves and/or copies the nodes in each bin to new table. See above for
	 * explanation.
	 */
	private final void transfer(Node<K, V>[] tab, Node<K, V>[] nextTab) {
		int n = tab.length, stride;
		if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
			stride = MIN_TRANSFER_STRIDE; // subdivide range
		if (nextTab == null) { // initiating
			try {
				@SuppressWarnings("unchecked")
				Node<K, V>[] nt = (Node<K, V>[]) new Node<?, ?>[n << 1];
				nextTab = nt;
			} catch (Throwable ex) { // try to cope with OOME
				sizeCtl = Integer.MAX_VALUE;
				return;
			}
			nextTable = nextTab;
			transferIndex = n;
		}
		int nextn = nextTab.length;
		ForwardingNode<K, V> fwd = new ForwardingNode<K, V>(nextTab);
		boolean advance = true;
		boolean finishing = false; // to ensure sweep before committing nextTab
		for (int i = 0, bound = 0;;) {
			Node<K, V> f;
			int fh;
			while (advance) {
				int nextIndex, nextBound;
				if (--i >= bound || finishing)
					advance = false;
				else if ((nextIndex = transferIndex) <= 0) {
					i = -1;
					advance = false;
				} else if (U.compareAndSwapInt(this, TRANSFERINDEX, nextIndex,
						nextBound = (nextIndex > stride ? nextIndex - stride : 0))) {
					bound = nextBound;
					i = nextIndex - 1;
					advance = false;
				}
			}
			if (i < 0 || i >= n || i + n >= nextn) {
				int sc;
				if (finishing) {
					nextTable = null;
					table = nextTab;
					sizeCtl = (n << 1) - (n >>> 1);
					return;
				}
				if (U.compareAndSwapInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
					if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
						return;
					finishing = advance = true;
					i = n; // recheck before commit
				}
			} else if ((f = tabAt(tab, i)) == null)
				advance = casTabAt(tab, i, null, fwd);
			else if ((fh = f.hash) == MOVED)
				advance = true; // already processed
			else {
				synchronized (f) {
					if (tabAt(tab, i) == f) {
						Node<K, V> ln, hn;
						if (fh >= 0) {
							int runBit = fh & n;
							Node<K, V> lastRun = f;
							for (Node<K, V> p = f.next; p != null; p = p.next) {
								int b = p.hash & n;
								if (b != runBit) {
									runBit = b;
									lastRun = p;
								}
							}
							if (runBit == 0) {
								ln = lastRun;
								hn = null;
							} else {
								hn = lastRun;
								ln = null;
							}
							for (Node<K, V> p = f; p != lastRun; p = p.next) {
								int ph = p.hash;
								K pk = p.key;
								V pv = p.val;
								if ((ph & n) == 0)
									ln = new Node<K, V>(ph, pk, pv, ln);
								else
									hn = new Node<K, V>(ph, pk, pv, hn);
							}
							setTabAt(nextTab, i, ln);
							setTabAt(nextTab, i + n, hn);
							setTabAt(tab, i, fwd);
							advance = true;
						} else if (f instanceof TreeBin) {
							TreeBin<K, V> t = (TreeBin<K, V>) f;
							TreeNode<K, V> lo = null, loTail = null;
							TreeNode<K, V> hi = null, hiTail = null;
							int lc = 0, hc = 0;
							for (Node<K, V> e = t.first; e != null; e = e.next) {
								int h = e.hash;
								TreeNode<K, V> p = new TreeNode<K, V>(h, e.key, e.val, null, null);
								if ((h & n) == 0) {
									if ((p.prev = loTail) == null)
										lo = p;
									else
										loTail.next = p;
									loTail = p;
									++lc;
								} else {
									if ((p.prev = hiTail) == null)
										hi = p;
									else
										hiTail.next = p;
									hiTail = p;
									++hc;
								}
							}
							ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) : (hc != 0) ? new TreeBin<K, V>(lo) : t;
							hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) : (lc != 0) ? new TreeBin<K, V>(hi) : t;
							setTabAt(nextTab, i, ln);
							setTabAt(nextTab, i + n, hn);
							setTabAt(tab, i, fwd);
							advance = true;
						}
					}
				}
			}
		}
	}

	/* ---------------- Counter support -------------- */

	/**
	 * A padded cell for distributing counts. Adapted from LongAdder and
	 * Striped64. See their internal docs for explanation.
	 */
	@sun.misc.Contended
	static final class CounterCell {
		volatile long value;

		CounterCell(long x) {
			value = x;
		}
	}

	final long sumCount() {
		CounterCell[] as = counterCells;
		CounterCell a;
		long sum = baseCount;
		if (as != null) {
			for (int i = 0; i < as.length; ++i) {
				if ((a = as[i]) != null)
					sum += a.value;
			}
		}
		return sum;
	}

	// See LongAdder version for explanation
	private final void fullAddCount(long x, boolean wasUncontended) {
		int h;
		if ((h = getProbe()) == 0) {
			localInit(); // force initialization
			h = getProbe();
			wasUncontended = true;
		}
		boolean collide = false; // True if last slot nonempty
		for (;;) {
			CounterCell[] as;
			CounterCell a;
			int n;
			long v;
			if ((as = counterCells) != null && (n = as.length) > 0) {
				if ((a = as[(n - 1) & h]) == null) {
					if (cellsBusy == 0) { // Try to attach new Cell
						CounterCell r = new CounterCell(x); // Optimistic create
						if (cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
							boolean created = false;
							try { // Recheck under lock
								CounterCell[] rs;
								int m, j;
								if ((rs = counterCells) != null && (m = rs.length) > 0 && rs[j = (m - 1) & h] == null) {
									rs[j] = r;
									created = true;
								}
							} finally {
								cellsBusy = 0;
							}
							if (created)
								break;
							continue; // Slot is now non-empty
						}
					}
					collide = false;
				} else if (!wasUncontended) // CAS already known to fail
					wasUncontended = true; // Continue after rehash
				else if (U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))
					break;
				else if (counterCells != as || n >= NCPU)
					collide = false; // At max size or stale
				else if (!collide)
					collide = true;
				else if (cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
					try {
						if (counterCells == as) {// Expand table unless stale
							CounterCell[] rs = new CounterCell[n << 1];
							for (int i = 0; i < n; ++i)
								rs[i] = as[i];
							counterCells = rs;
						}
					} finally {
						cellsBusy = 0;
					}
					collide = false;
					continue; // Retry with expanded table
				}
				h = advanceProbe(h);
			} else if (cellsBusy == 0 && counterCells == as && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
				boolean init = false;
				try { // Initialize table
					if (counterCells == as) {
						CounterCell[] rs = new CounterCell[2];
						rs[h & 1] = new CounterCell(x);
						counterCells = rs;
						init = true;
					}
				} finally {
					cellsBusy = 0;
				}
				if (init)
					break;
			} else if (U.compareAndSwapLong(this, BASECOUNT, v = baseCount, v + x))
				break; // Fall back on using base
		}
	}

	/* ---------------- Conversion from/to TreeBins -------------- */

	/**
	 * Replaces all linked nodes in bin at given index unless table is too
	 * small, in which case resizes instead.
	 */
	private final void treeifyBin(Node<K, V>[] tab, int index) {
		Node<K, V> b;
		int n, sc;
		if (tab != null) {
			if ((n = tab.length) < MIN_TREEIFY_CAPACITY)
				tryPresize(n << 1);
			else if ((b = tabAt(tab, index)) != null && b.hash >= 0) {
				synchronized (b) {
					if (tabAt(tab, index) == b) {
						TreeNode<K, V> hd = null, tl = null;
						for (Node<K, V> e = b; e != null; e = e.next) {
							TreeNode<K, V> p = new TreeNode<K, V>(e.hash, e.key, e.val, null, null);
							if ((p.prev = tl) == null)
								hd = p;
							else
								tl.next = p;
							tl = p;
						}
						setTabAt(tab, index, new TreeBin<K, V>(hd));
					}
				}
			}
		}
	}

	/**
	 * Returns a list on non-TreeNodes replacing those in given list.
	 */
	static <K, V> Node<K, V> untreeify(Node<K, V> b) {
		Node<K, V> hd = null, tl = null;
		for (Node<K, V> q = b; q != null; q = q.next) {
			Node<K, V> p = new Node<K, V>(q.hash, q.key, q.val, null);
			if (tl == null)
				hd = p;
			else
				tl.next = p;
			tl = p;
		}
		return hd;
	}

	/* ---------------- TreeNodes -------------- */

	/**
	 * Nodes for use in TreeBins
	 */
	static final class TreeNode<K, V> extends Node<K, V> {
		TreeNode<K, V> parent; // red-black tree links
		TreeNode<K, V> left;
		TreeNode<K, V> right;
		TreeNode<K, V> prev; // needed to unlink next upon deletion
		boolean red;

		TreeNode(int hash, K key, V val, Node<K, V> next, TreeNode<K, V> parent) {
			super(hash, key, val, next);
			this.parent = parent;
		}

		Node<K, V> find(int h, Object k) {
			return findTreeNode(h, k, null);
		}

		/**
		 *  查找hash为h，key为k的节点
		 */
		final TreeNode<K, V> findTreeNode(int h, Object k, Class<?> kc) {
			if (k != null) {
				TreeNode<K, V> p = this;
				do {
					int ph, dir;
					K pk;
					TreeNode<K, V> q;
					TreeNode<K, V> pl = p.left, pr = p.right;
					if ((ph = p.hash) > h)
						p = pl;
					else if (ph < h)
						p = pr;
					else if ((pk = p.key) == k || (pk != null && k.equals(pk)))
						return p;
					else if (pl == null)
						p = pr;
					else if (pr == null)
						p = pl;
					else if ((kc != null || (kc = comparableClassFor(k)) != null)
							&& (dir = compareComparables(kc, k, pk)) != 0)
						p = (dir < 0) ? pl : pr;
					else if ((q = pr.findTreeNode(h, k, kc)) != null)
						return q;
					else
						p = pl;
				} while (p != null);
			}
			return null;
		}
	}

 
	/**
	 * 该类并不负责key-value的键值对包装，它用于在链表转换为红黑树时包装TreeNode节点，
	 * 也就是说ConcurrentHashMap红黑树存放是TreeBin，不是TreeNode。该类封装了一系列的方法，
	 * 包括putTreeVal、lookRoot、UNlookRoot、remove、balanceInsetion、balanceDeletion。
	 * @author lzhcode
	 *
	 * @param <K>
	 * @param <V>
	 */
	static final class TreeBin<K, V> extends Node<K, V> {
		TreeNode<K, V> root;
		volatile TreeNode<K, V> first;
		volatile Thread waiter;
		volatile int lockState;
		// values for lockState
		static final int WRITER = 1; // set while holding write lock
		static final int WAITER = 2; // set when waiting for write lock
		static final int READER = 4; // increment value for setting read lock
 
		static int tieBreakOrder(Object a, Object b) {
			int d;
			if (a == null || b == null || (d = a.getClass().getName().compareTo(b.getClass().getName())) == 0)
				d = (System.identityHashCode(a) <= System.identityHashCode(b) ? -1 : 1);
			return d;
		}

		/**
		 *  构造方法就是在构造一个红黑树的过程。
		 */
		TreeBin(TreeNode<K, V> b) {
			super(TREEBIN, null, null, null);
			this.first = b;
			TreeNode<K, V> r = null;
			for (TreeNode<K, V> x = b, next; x != null; x = next) {
				next = (TreeNode<K, V>) x.next;
				x.left = x.right = null;
				if (r == null) {
					x.parent = null;
					x.red = false;
					r = x;
				} else {
					K k = x.key;
					int h = x.hash;
					Class<?> kc = null;
					for (TreeNode<K, V> p = r;;) {
						int dir, ph;
						K pk = p.key;
						if ((ph = p.hash) > h)
							dir = -1;
						else if (ph < h)
							dir = 1;
						else if ((kc == null && (kc = comparableClassFor(k)) == null)
								|| (dir = compareComparables(kc, k, pk)) == 0)
							dir = tieBreakOrder(k, pk);
						TreeNode<K, V> xp = p;
						if ((p = (dir <= 0) ? p.left : p.right) == null) {
							x.parent = xp;
							if (dir <= 0)
								xp.left = x;
							else
								xp.right = x;
							r = balanceInsertion(r, x);
							break;
						}
					}
				}
			}
			this.root = r;
			assert checkInvariants(root);
		}

		/**
		 * Acquires write lock for tree restructuring.
		 */
		private final void lockRoot() {
			if (!U.compareAndSwapInt(this, LOCKSTATE, 0, WRITER))
				contendedLock(); // offload to separate method
		}

		/**
		 * Releases write lock for tree restructuring.
		 */
		private final void unlockRoot() {
			lockState = 0;
		}

		/**
		 * Possibly blocks awaiting root lock.
		 */
		private final void contendedLock() {
			boolean waiting = false;
			for (int s;;) {
				if (((s = lockState) & ~WAITER) == 0) {
					if (U.compareAndSwapInt(this, LOCKSTATE, s, WRITER)) {
						if (waiting)
							waiter = null;
						return;
					}
				} else if ((s & WAITER) == 0) {
					if (U.compareAndSwapInt(this, LOCKSTATE, s, s | WAITER)) {
						waiting = true;
						waiter = Thread.currentThread();
					}
				} else if (waiting)
					LockSupport.park(this);
			}
		}

		/**
		 * Returns matching node or null if none. Tries to search using tree
		 * comparisons from root, but continues linear search when lock not
		 * available.
		 */
		final Node<K, V> find(int h, Object k) {
			if (k != null) {
				for (Node<K, V> e = first; e != null;) {
					int s;
					K ek;
					if (((s = lockState) & (WAITER | WRITER)) != 0) {
						if (e.hash == h && ((ek = e.key) == k || (ek != null && k.equals(ek))))
							return e;
						e = e.next;
					} else if (U.compareAndSwapInt(this, LOCKSTATE, s, s + READER)) {
						TreeNode<K, V> r, p;
						try {
							p = ((r = root) == null ? null : r.findTreeNode(h, k, null));
						} finally {
							Thread w;
							if (U.getAndAddInt(this, LOCKSTATE, -READER) == (READER | WAITER) && (w = waiter) != null)
								LockSupport.unpark(w);
						}
						return p;
					}
				}
			}
			return null;
		}

		/**
		 * Finds or adds a node.
		 * 
		 * @return null if added
		 */
		final TreeNode<K, V> putTreeVal(int h, K k, V v) {
			Class<?> kc = null;
			boolean searched = false;
			for (TreeNode<K, V> p = root;;) {
				int dir, ph;
				K pk;
				if (p == null) {
					first = root = new TreeNode<K, V>(h, k, v, null, null);
					break;
				} else if ((ph = p.hash) > h)
					dir = -1;
				else if (ph < h)
					dir = 1;
				else if ((pk = p.key) == k || (pk != null && k.equals(pk)))
					return p;
				else if ((kc == null && (kc = comparableClassFor(k)) == null)
						|| (dir = compareComparables(kc, k, pk)) == 0) {
					if (!searched) {
						TreeNode<K, V> q, ch;
						searched = true;
						if (((ch = p.left) != null && (q = ch.findTreeNode(h, k, kc)) != null)
								|| ((ch = p.right) != null && (q = ch.findTreeNode(h, k, kc)) != null))
							return q;
					}
					dir = tieBreakOrder(k, pk);
				}

				TreeNode<K, V> xp = p;
				if ((p = (dir <= 0) ? p.left : p.right) == null) {
					TreeNode<K, V> x, f = first;
					first = x = new TreeNode<K, V>(h, k, v, f, xp);
					if (f != null)
						f.prev = x;
					if (dir <= 0)
						xp.left = x;
					else
						xp.right = x;
					if (!xp.red)
						x.red = true;
					else {
						lockRoot();
						try {
							root = balanceInsertion(root, x);
						} finally {
							unlockRoot();
						}
					}
					break;
				}
			}
			assert checkInvariants(root);
			return null;
		}

	 
		final boolean removeTreeNode(TreeNode<K, V> p) {
			TreeNode<K, V> next = (TreeNode<K, V>) p.next;
			TreeNode<K, V> pred = p.prev; // unlink traversal pointers
			TreeNode<K, V> r, rl;
			if (pred == null)
				first = next;
			else
				pred.next = next;
			if (next != null)
				next.prev = pred;
			if (first == null) {
				root = null;
				return true;
			}
			if ((r = root) == null || r.right == null || // too small
					(rl = r.left) == null || rl.left == null)
				return true;
			lockRoot();
			try {
				TreeNode<K, V> replacement;
				TreeNode<K, V> pl = p.left;
				TreeNode<K, V> pr = p.right;
				if (pl != null && pr != null) {
					TreeNode<K, V> s = pr, sl;
					while ((sl = s.left) != null) // find successor
						s = sl;
					boolean c = s.red;
					s.red = p.red;
					p.red = c; // swap colors
					TreeNode<K, V> sr = s.right;
					TreeNode<K, V> pp = p.parent;
					if (s == pr) { // p was s's direct parent
						p.parent = s;
						s.right = p;
					} else {
						TreeNode<K, V> sp = s.parent;
						if ((p.parent = sp) != null) {
							if (s == sp.left)
								sp.left = p;
							else
								sp.right = p;
						}
						if ((s.right = pr) != null)
							pr.parent = s;
					}
					p.left = null;
					if ((p.right = sr) != null)
						sr.parent = p;
					if ((s.left = pl) != null)
						pl.parent = s;
					if ((s.parent = pp) == null)
						r = s;
					else if (p == pp.left)
						pp.left = s;
					else
						pp.right = s;
					if (sr != null)
						replacement = sr;
					else
						replacement = p;
				} else if (pl != null)
					replacement = pl;
				else if (pr != null)
					replacement = pr;
				else
					replacement = p;
				if (replacement != p) {
					TreeNode<K, V> pp = replacement.parent = p.parent;
					if (pp == null)
						r = replacement;
					else if (p == pp.left)
						pp.left = replacement;
					else
						pp.right = replacement;
					p.left = p.right = p.parent = null;
				}

				root = (p.red) ? r : balanceDeletion(r, replacement);

				if (p == replacement) { // detach pointers
					TreeNode<K, V> pp;
					if ((pp = p.parent) != null) {
						if (p == pp.left)
							pp.left = null;
						else if (p == pp.right)
							pp.right = null;
						p.parent = null;
					}
				}
			} finally {
				unlockRoot();
			}
			assert checkInvariants(root);
			return false;
		}

		/* ------------------------------------------------------------ */
		// Red-black tree methods, all adapted from CLR

		static <K, V> TreeNode<K, V> rotateLeft(TreeNode<K, V> root, TreeNode<K, V> p) {
			TreeNode<K, V> r, pp, rl;
			if (p != null && (r = p.right) != null) {
				if ((rl = p.right = r.left) != null)
					rl.parent = p;
				if ((pp = r.parent = p.parent) == null)
					(root = r).red = false;
				else if (pp.left == p)
					pp.left = r;
				else
					pp.right = r;
				r.left = p;
				p.parent = r;
			}
			return root;
		}

		static <K, V> TreeNode<K, V> rotateRight(TreeNode<K, V> root, TreeNode<K, V> p) {
			TreeNode<K, V> l, pp, lr;
			if (p != null && (l = p.left) != null) {
				if ((lr = p.left = l.right) != null)
					lr.parent = p;
				if ((pp = l.parent = p.parent) == null)
					(root = l).red = false;
				else if (pp.right == p)
					pp.right = l;
				else
					pp.left = l;
				l.right = p;
				p.parent = l;
			}
			return root;
		}

		static <K, V> TreeNode<K, V> balanceInsertion(TreeNode<K, V> root, TreeNode<K, V> x) {
			x.red = true;
			for (TreeNode<K, V> xp, xpp, xppl, xppr;;) {
				if ((xp = x.parent) == null) {
					x.red = false;
					return x;
				} else if (!xp.red || (xpp = xp.parent) == null)
					return root;
				if (xp == (xppl = xpp.left)) {
					if ((xppr = xpp.right) != null && xppr.red) {
						xppr.red = false;
						xp.red = false;
						xpp.red = true;
						x = xpp;
					} else {
						if (x == xp.right) {
							root = rotateLeft(root, x = xp);
							xpp = (xp = x.parent) == null ? null : xp.parent;
						}
						if (xp != null) {
							xp.red = false;
							if (xpp != null) {
								xpp.red = true;
								root = rotateRight(root, xpp);
							}
						}
					}
				} else {
					if (xppl != null && xppl.red) {
						xppl.red = false;
						xp.red = false;
						xpp.red = true;
						x = xpp;
					} else {
						if (x == xp.left) {
							root = rotateRight(root, x = xp);
							xpp = (xp = x.parent) == null ? null : xp.parent;
						}
						if (xp != null) {
							xp.red = false;
							if (xpp != null) {
								xpp.red = true;
								root = rotateLeft(root, xpp);
							}
						}
					}
				}
			}
		}

		static <K, V> TreeNode<K, V> balanceDeletion(TreeNode<K, V> root, TreeNode<K, V> x) {
			for (TreeNode<K, V> xp, xpl, xpr;;) {
				if (x == null || x == root)
					return root;
				else if ((xp = x.parent) == null) {
					x.red = false;
					return x;
				} else if (x.red) {
					x.red = false;
					return root;
				} else if ((xpl = xp.left) == x) {
					if ((xpr = xp.right) != null && xpr.red) {
						xpr.red = false;
						xp.red = true;
						root = rotateLeft(root, xp);
						xpr = (xp = x.parent) == null ? null : xp.right;
					}
					if (xpr == null)
						x = xp;
					else {
						TreeNode<K, V> sl = xpr.left, sr = xpr.right;
						if ((sr == null || !sr.red) && (sl == null || !sl.red)) {
							xpr.red = true;
							x = xp;
						} else {
							if (sr == null || !sr.red) {
								if (sl != null)
									sl.red = false;
								xpr.red = true;
								root = rotateRight(root, xpr);
								xpr = (xp = x.parent) == null ? null : xp.right;
							}
							if (xpr != null) {
								xpr.red = (xp == null) ? false : xp.red;
								if ((sr = xpr.right) != null)
									sr.red = false;
							}
							if (xp != null) {
								xp.red = false;
								root = rotateLeft(root, xp);
							}
							x = root;
						}
					}
				} else { // symmetric
					if (xpl != null && xpl.red) {
						xpl.red = false;
						xp.red = true;
						root = rotateRight(root, xp);
						xpl = (xp = x.parent) == null ? null : xp.left;
					}
					if (xpl == null)
						x = xp;
					else {
						TreeNode<K, V> sl = xpl.left, sr = xpl.right;
						if ((sl == null || !sl.red) && (sr == null || !sr.red)) {
							xpl.red = true;
							x = xp;
						} else {
							if (sl == null || !sl.red) {
								if (sr != null)
									sr.red = false;
								xpl.red = true;
								root = rotateLeft(root, xpl);
								xpl = (xp = x.parent) == null ? null : xp.left;
							}
							if (xpl != null) {
								xpl.red = (xp == null) ? false : xp.red;
								if ((sl = xpl.left) != null)
									sl.red = false;
							}
							if (xp != null) {
								xp.red = false;
								root = rotateRight(root, xp);
							}
							x = root;
						}
					}
				}
			}
		}

		/**
		 * Recursive invariant check
		 */
		static <K, V> boolean checkInvariants(TreeNode<K, V> t) {
			TreeNode<K, V> tp = t.parent, tl = t.left, tr = t.right, tb = t.prev, tn = (TreeNode<K, V>) t.next;
			if (tb != null && tb.next != t)
				return false;
			if (tn != null && tn.prev != t)
				return false;
			if (tp != null && t != tp.left && t != tp.right)
				return false;
			if (tl != null && (tl.parent != t || tl.hash > t.hash))
				return false;
			if (tr != null && (tr.parent != t || tr.hash < t.hash))
				return false;
			if (t.red && tl != null && tl.red && tr != null && tr.red)
				return false;
			if (tl != null && !checkInvariants(tl))
				return false;
			if (tr != null && !checkInvariants(tr))
				return false;
			return true;
		}

		private static final sun.misc.Unsafe U;
		private static final long LOCKSTATE;
		static {
			try {
				U = sun.misc.Unsafe.getUnsafe();
				Class<?> k = TreeBin.class;
				LOCKSTATE = U.objectFieldOffset(k.getDeclaredField("lockState"));
			} catch (Exception e) {
				throw new Error(e);
			}
		}
	}

	/* ----------------Table Traversal -------------- */

	/**
	 * Records the table, its length, and current traversal index for a
	 * traverser that must process a region of a forwarded table before
	 * proceeding with current table.
	 */
	static final class TableStack<K, V> {
		int length;
		int index;
		Node<K, V>[] tab;
		TableStack<K, V> next;
	}

	/**
	 * Encapsulates traversal for methods such as containsValue; also serves as
	 * a base class for other iterators and spliterators.
	 *
	 * Method advance visits once each still-valid node that was reachable upon
	 * iterator construction. It might miss some that were added to a bin after
	 * the bin was visited, which is OK wrt consistency guarantees. Maintaining
	 * this property in the face of possible ongoing resizes requires a fair
	 * amount of bookkeeping state that is difficult to optimize away amidst
	 * volatile accesses. Even so, traversal maintains reasonable throughput.
	 *
	 * Normally, iteration proceeds bin-by-bin traversing lists. However, if the
	 * table has been resized, then all future steps must traverse both the bin
	 * at the current index as well as at (index + baseSize); and so on for
	 * further resizings. To paranoically cope with potential sharing by users
	 * of iterators across threads, iteration terminates if a bounds checks
	 * fails for a table read.
	 */
	static class Traverser<K, V> {
		Node<K, V>[] tab; // current table; updated if resized
		Node<K, V> next; // the next entry to use
		TableStack<K, V> stack, spare; // to save/restore on ForwardingNodes
		int index; // index of bin to use next
		int baseIndex; // current index of initial table
		int baseLimit; // index bound for initial table
		final int baseSize; // initial table size

		Traverser(Node<K, V>[] tab, int size, int index, int limit) {
			this.tab = tab;
			this.baseSize = size;
			this.baseIndex = this.index = index;
			this.baseLimit = limit;
			this.next = null;
		}

		/**
		 * Advances if possible, returning next valid node, or null if none.
		 */
		final Node<K, V> advance() {
			Node<K, V> e;
			if ((e = next) != null)
				e = e.next;
			for (;;) {
				Node<K, V>[] t;
				int i, n; // must use locals in checks
				if (e != null)
					return next = e;
				if (baseIndex >= baseLimit || (t = tab) == null || (n = t.length) <= (i = index) || i < 0)
					return next = null;
				if ((e = tabAt(t, i)) != null && e.hash < 0) {
					if (e instanceof ForwardingNode) {
						tab = ((ForwardingNode<K, V>) e).nextTable;
						e = null;
						pushState(t, i, n);
						continue;
					} else if (e instanceof TreeBin)
						e = ((TreeBin<K, V>) e).first;
					else
						e = null;
				}
				if (stack != null)
					recoverState(n);
				else if ((index = i + baseSize) >= n)
					index = ++baseIndex; // visit upper slots if present
			}
		}

		/**
		 * Saves traversal state upon encountering a forwarding node.
		 */
		private void pushState(Node<K, V>[] t, int i, int n) {
			TableStack<K, V> s = spare; // reuse if possible
			if (s != null)
				spare = s.next;
			else
				s = new TableStack<K, V>();
			s.tab = t;
			s.length = n;
			s.index = i;
			s.next = stack;
			stack = s;
		}

		/**
		 * Possibly pops traversal state.
		 *
		 * @param n
		 *            length of current table
		 */
		private void recoverState(int n) {
			TableStack<K, V> s;
			int len;
			while ((s = stack) != null && (index += (len = s.length)) >= n) {
				n = len;
				index = s.index;
				tab = s.tab;
				s.tab = null;
				TableStack<K, V> next = s.next;
				s.next = spare; // save for reuse
				stack = next;
				spare = s;
			}
			if (s == null && (index += baseSize) >= n)
				index = ++baseIndex;
		}
	}

	/**
	 * Base of key, value, and entry Iterators. Adds fields to Traverser to
	 * support iterator.remove.
	 */
	static class BaseIterator8<K, V> extends Traverser<K, V> {
		final ConcurrentHashMap8<K, V> map;
		Node<K, V> lastReturned;

		BaseIterator8(Node<K, V>[] tab, int size, int index, int limit, ConcurrentHashMap8<K, V> map) {
			super(tab, size, index, limit);
			this.map = map;
			advance();
		}

		public final boolean hasNext() {
			return next != null;
		}

		public final boolean hasMoreElements() {
			return next != null;
		}

		public final void remove() {
			Node<K, V> p;
			if ((p = lastReturned) == null)
				throw new IllegalStateException();
			lastReturned = null;
			map.replaceNode(p.key, null, null);
		}
	}

	static final class KeyIterator8<K, V> extends BaseIterator8<K, V> implements Iterator<K>, Enumeration<K> {
		KeyIterator8(Node<K, V>[] tab, int index, int size, int limit, ConcurrentHashMap8<K, V> map) {
			super(tab, index, size, limit, map);
		}

		public final K next() {
			Node<K, V> p;
			if ((p = next) == null)
				throw new NoSuchElementException();
			K k = p.key;
			lastReturned = p;
			advance();
			return k;
		}

		public final K nextElement() {
			return next();
		}
	}

	static final class ValueIterator8<K, V> extends BaseIterator8<K, V> implements Iterator<V>, Enumeration<V> {
		ValueIterator8(Node<K, V>[] tab, int index, int size, int limit, ConcurrentHashMap8<K, V> map) {
			super(tab, index, size, limit, map);
		}

		public final V next() {
			Node<K, V> p;
			if ((p = next) == null)
				throw new NoSuchElementException();
			V v = p.val;
			lastReturned = p;
			advance();
			return v;
		}

		public final V nextElement() {
			return next();
		}
	}

	static final class EntryIterator8<K, V> extends BaseIterator8<K, V> implements Iterator<Map.Entry<K, V>> {
		EntryIterator8(Node<K, V>[] tab, int index, int size, int limit, ConcurrentHashMap8<K, V> map) {
			super(tab, index, size, limit, map);
		}

		public final Map.Entry<K, V> next() {
			Node<K, V> p;
			if ((p = next) == null)
				throw new NoSuchElementException();
			K k = p.key;
			V v = p.val;
			lastReturned = p;
			advance();
			return new MapEntry<K, V>(k, v, map);
		}
	}

	/**
	 * Exported Entry for EntryIterator
	 */
	static final class MapEntry<K, V> implements Map.Entry<K, V> {
		final K key; // non-null
		V val; // non-null
		final ConcurrentHashMap8<K, V> map;

		MapEntry(K key, V val, ConcurrentHashMap8<K, V> map) {
			this.key = key;
			this.val = val;
			this.map = map;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return val;
		}

		public int hashCode() {
			return key.hashCode() ^ val.hashCode();
		}

		public String toString() {
			return key + "=" + val;
		}

		public boolean equals(Object o) {
			Object k, v;
			Map.Entry<?, ?> e;
			return ((o instanceof Map.Entry) && (k = (e = (Map.Entry<?, ?>) o).getKey()) != null
					&& (v = e.getValue()) != null && (k == key || k.equals(key)) && (v == val || v.equals(val)));
		}

		/**
		 * Sets our entry's value and writes through to the map. The value to
		 * return is somewhat arbitrary here. Since we do not necessarily track
		 * asynchronous changes, the most recent "previous" value could be
		 * different from what we return (or could even have been removed, in
		 * which case the put will re-establish). We do not and cannot guarantee
		 * more.
		 */
		public V setValue(V value) {
			if (value == null)
				throw new NullPointerException();
			V v = val;
			val = value;
			map.put(key, value);
			return v;
		}
	}

	static final class KeySpliterator<K, V> extends Traverser<K, V> implements Spliterator<K> {
		long est; // size estimate

		KeySpliterator(Node<K, V>[] tab, int size, int index, int limit, long est) {
			super(tab, size, index, limit);
			this.est = est;
		}

		public Spliterator<K> trySplit() {
			int i, f, h;
			return (h = ((i = baseIndex) + (f = baseLimit)) >>> 1) <= i ? null
					: new KeySpliterator<K, V>(tab, baseSize, baseLimit = h, f, est >>>= 1);
		}

		public void forEachRemaining(Consumer<? super K> action) {
			if (action == null)
				throw new NullPointerException();
			for (Node<K, V> p; (p = advance()) != null;)
				action.accept(p.key);
		}

		public boolean tryAdvance(Consumer<? super K> action) {
			if (action == null)
				throw new NullPointerException();
			Node<K, V> p;
			if ((p = advance()) == null)
				return false;
			action.accept(p.key);
			return true;
		}

		public long estimateSize() {
			return est;
		}

		public int characteristics() {
			return Spliterator.DISTINCT | Spliterator.CONCURRENT | Spliterator.NONNULL;
		}
	}

	static final class ValueSpliterator<K, V> extends Traverser<K, V> implements Spliterator<V> {
		long est; // size estimate

		ValueSpliterator(Node<K, V>[] tab, int size, int index, int limit, long est) {
			super(tab, size, index, limit);
			this.est = est;
		}

		public Spliterator<V> trySplit() {
			int i, f, h;
			return (h = ((i = baseIndex) + (f = baseLimit)) >>> 1) <= i ? null
					: new ValueSpliterator<K, V>(tab, baseSize, baseLimit = h, f, est >>>= 1);
		}

		public void forEachRemaining(Consumer<? super V> action) {
			if (action == null)
				throw new NullPointerException();
			for (Node<K, V> p; (p = advance()) != null;)
				action.accept(p.val);
		}

		public boolean tryAdvance(Consumer<? super V> action) {
			if (action == null)
				throw new NullPointerException();
			Node<K, V> p;
			if ((p = advance()) == null)
				return false;
			action.accept(p.val);
			return true;
		}

		public long estimateSize() {
			return est;
		}

		public int characteristics() {
			return Spliterator.CONCURRENT | Spliterator.NONNULL;
		}
	}

	static final class EntrySpliterator<K, V> extends Traverser<K, V> implements Spliterator<Map.Entry<K, V>> {
		final ConcurrentHashMap8<K, V> map; // To export MapEntry
		long est; // size estimate

		EntrySpliterator(Node<K, V>[] tab, int size, int index, int limit, long est, ConcurrentHashMap8<K, V> map) {
			super(tab, size, index, limit);
			this.map = map;
			this.est = est;
		}

		public Spliterator<Map.Entry<K, V>> trySplit() {
			int i, f, h;
			return (h = ((i = baseIndex) + (f = baseLimit)) >>> 1) <= i ? null
					: new EntrySpliterator<K, V>(tab, baseSize, baseLimit = h, f, est >>>= 1, map);
		}

		public void forEachRemaining(Consumer<? super Map.Entry<K, V>> action) {
			if (action == null)
				throw new NullPointerException();
			for (Node<K, V> p; (p = advance()) != null;)
				action.accept(new MapEntry<K, V>(p.key, p.val, map));
		}

		public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> action) {
			if (action == null)
				throw new NullPointerException();
			Node<K, V> p;
			if ((p = advance()) == null)
				return false;
			action.accept(new MapEntry<K, V>(p.key, p.val, map));
			return true;
		}

		public long estimateSize() {
			return est;
		}

		public int characteristics() {
			return Spliterator.DISTINCT | Spliterator.CONCURRENT | Spliterator.NONNULL;
		}
	}

	// Parallel bulk operations

	/**
	 * Computes initial batch value for bulk tasks. The returned value is
	 * approximately exp2 of the number of times (minus one) to split task by
	 * two before executing leaf action. This value is faster to compute and
	 * more convenient to use as a guide to splitting than is the depth, since
	 * it is used while dividing by two anyway.
	 */
	final int batchFor(long b) {
		long n;
		if (b == Long.MAX_VALUE || (n = sumCount()) <= 1L || n < b)
			return 0;
		int sp = ForkJoinPool.getCommonPoolParallelism() << 2; // slack of 4
		return (b <= 0L || (n /= b) >= sp) ? sp : (int) n;
	}

	/**
	 * Performs the given action for each (key, value).
	 *
	 * @param parallelismThreshold
	 *            the (estimated) number of elements needed for this operation
	 *            to be executed in parallel
	 * @param action
	 *            the action
	 * @since 1.8
	 */
	public void forEach(long parallelismThreshold, BiConsumer<? super K, ? super V> action) {
		if (action == null)
			throw new NullPointerException();
		new ForEachMappingTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, action).invoke();
	}

	public <U> void forEach(long parallelismThreshold, BiFunction<? super K, ? super V, ? extends U> transformer,
			Consumer<? super U> action) {
		if (transformer == null || action == null)
			throw new NullPointerException();
		new ForEachTransformedMappingTask8<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, transformer,
				action).invoke();
	}

	public <U> U search(long parallelismThreshold, BiFunction<? super K, ? super V, ? extends U> searchFunction) {
		if (searchFunction == null)
			throw new NullPointerException();
		return new SearchMappingsTask8<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, searchFunction,
				new AtomicReference<U>()).invoke();
	}

	public <U> U reduce(long parallelismThreshold, BiFunction<? super K, ? super V, ? extends U> transformer,
			BiFunction<? super U, ? super U, ? extends U> reducer) {
		if (transformer == null || reducer == null)
			throw new NullPointerException();
		return new MapReduceMappingsTask8<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, null, transformer,
				reducer).invoke();
	}

	public double reduceToDouble(long parallelismThreshold, ToDoubleBiFunction<? super K, ? super V> transformer,
			double basis, DoubleBinaryOperator reducer) {
		if (transformer == null || reducer == null)
			throw new NullPointerException();
		return new MapReduceMappingsToDoubleTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null,
				transformer, basis, reducer).invoke();
	}

	public long reduceToLong(long parallelismThreshold, ToLongBiFunction<? super K, ? super V> transformer, long basis,
			LongBinaryOperator reducer) {
		if (transformer == null || reducer == null)
			throw new NullPointerException();
		return new MapReduceMappingsToLongTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null,
				transformer, basis, reducer).invoke();
	}

	public int reduceToInt(long parallelismThreshold, ToIntBiFunction<? super K, ? super V> transformer, int basis,
			IntBinaryOperator reducer) {
		if (transformer == null || reducer == null)
			throw new NullPointerException();
		return new MapReduceMappingsToIntTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null,
				transformer, basis, reducer).invoke();
	}

	public void forEachKey(long parallelismThreshold, Consumer<? super K> action) {
		if (action == null)
			throw new NullPointerException();
		new ForEachKeyTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, action).invoke();
	}

	public <U> void forEachKey(long parallelismThreshold, Function<? super K, ? extends U> transformer,
			Consumer<? super U> action) {
		if (transformer == null || action == null)
			throw new NullPointerException();
		new ForEachTransformedKeyTask8<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, transformer, action)
				.invoke();
	}

	public <U> U searchKeys(long parallelismThreshold, Function<? super K, ? extends U> searchFunction) {
		if (searchFunction == null)
			throw new NullPointerException();
		return new SearchKeysTask8<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, searchFunction,
				new AtomicReference<U>()).invoke();
	}

	public K reduceKeys(long parallelismThreshold, BiFunction<? super K, ? super K, ? extends K> reducer) {
		if (reducer == null)
			throw new NullPointerException();
		return new ReduceKeysTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null, reducer).invoke();
	}

	public <U> U reduceKeys(long parallelismThreshold, Function<? super K, ? extends U> transformer,
			BiFunction<? super U, ? super U, ? extends U> reducer) {
		if (transformer == null || reducer == null)
			throw new NullPointerException();
		return new MapReduceKeysTask8<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, null, transformer,
				reducer).invoke();
	}

	public double reduceKeysToDouble(long parallelismThreshold, ToDoubleFunction<? super K> transformer, double basis,
			DoubleBinaryOperator reducer) {
		if (transformer == null || reducer == null)
			throw new NullPointerException();
		return new MapReduceKeysToDoubleTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null,
				transformer, basis, reducer).invoke();
	}

	public long reduceKeysToLong(long parallelismThreshold, ToLongFunction<? super K> transformer, long basis,
			LongBinaryOperator reducer) {
		if (transformer == null || reducer == null)
			throw new NullPointerException();
		return new MapReduceKeysToLongTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null, transformer,
				basis, reducer).invoke();
	}

	public int reduceKeysToInt(long parallelismThreshold, ToIntFunction<? super K> transformer, int basis,
			IntBinaryOperator reducer) {
		if (transformer == null || reducer == null)
			throw new NullPointerException();
		return new MapReduceKeysToIntTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null, transformer,
				basis, reducer).invoke();
	}

	public void forEachValue(long parallelismThreshold, Consumer<? super V> action) {
		if (action == null)
			throw new NullPointerException();
		new ForEachValueTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, action).invoke();
	}

	public <U> void forEachValue(long parallelismThreshold, Function<? super V, ? extends U> transformer,
			Consumer<? super U> action) {
		if (transformer == null || action == null)
			throw new NullPointerException();
		new ForEachTransformedValueTask8<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, transformer,
				action).invoke();
	}

	public <U> U searchValues(long parallelismThreshold, Function<? super V, ? extends U> searchFunction) {
		if (searchFunction == null)
			throw new NullPointerException();
		return new SearchValuesTask8<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, searchFunction,
				new AtomicReference<U>()).invoke();
	}

	public V reduceValues(long parallelismThreshold, BiFunction<? super V, ? super V, ? extends V> reducer) {
		if (reducer == null)
			throw new NullPointerException();
		return new ReduceValuesTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null, reducer).invoke();
	}

	public <U> U reduceValues(long parallelismThreshold, Function<? super V, ? extends U> transformer,
			BiFunction<? super U, ? super U, ? extends U> reducer) {
		if (transformer == null || reducer == null)
			throw new NullPointerException();
		return new MapReduceValuesTask8<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, null, transformer,
				reducer).invoke();
	}

	public double reduceValuesToDouble(long parallelismThreshold, ToDoubleFunction<? super V> transformer, double basis,
			DoubleBinaryOperator reducer) {
		if (transformer == null || reducer == null)
			throw new NullPointerException();
		return new MapReduceValuesToDoubleTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null,
				transformer, basis, reducer).invoke();
	}

	public long reduceValuesToLong(long parallelismThreshold, ToLongFunction<? super V> transformer, long basis,
			LongBinaryOperator reducer) {
		if (transformer == null || reducer == null)
			throw new NullPointerException();
		return new MapReduceValuesToLongTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null,
				transformer, basis, reducer).invoke();
	}

	public int reduceValuesToInt(long parallelismThreshold, ToIntFunction<? super V> transformer, int basis,
			IntBinaryOperator reducer) {
		if (transformer == null || reducer == null)
			throw new NullPointerException();
		return new MapReduceValuesToIntTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null, transformer,
				basis, reducer).invoke();
	}

	public void forEachEntry(long parallelismThreshold, Consumer<? super Map.Entry<K, V>> action) {
		if (action == null)
			throw new NullPointerException();
		new ForEachEntryTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, action).invoke();
	}

	public <U> void forEachEntry(long parallelismThreshold, Function<Map.Entry<K, V>, ? extends U> transformer,
			Consumer<? super U> action) {
		if (transformer == null || action == null)
			throw new NullPointerException();
		new ForEachTransformedEntryTask8<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, transformer,
				action).invoke();
	}

	public <U> U searchEntries(long parallelismThreshold, Function<Map.Entry<K, V>, ? extends U> searchFunction) {
		if (searchFunction == null)
			throw new NullPointerException();
		return new SearchEntriesTask8<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, searchFunction,
				new AtomicReference<U>()).invoke();
	}

	public Map.Entry<K, V> reduceEntries(long parallelismThreshold,
			BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer) {
		if (reducer == null)
			throw new NullPointerException();
		return new ReduceEntriesTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null, reducer).invoke();
	}

	public <U> U reduceEntries(long parallelismThreshold, Function<Map.Entry<K, V>, ? extends U> transformer,
			BiFunction<? super U, ? super U, ? extends U> reducer) {
		if (transformer == null || reducer == null)
			throw new NullPointerException();
		return new MapReduceEntriesTask8<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, null, transformer,
				reducer).invoke();
	}

	public double reduceEntriesToDouble(long parallelismThreshold, ToDoubleFunction<Map.Entry<K, V>> transformer,
			double basis, DoubleBinaryOperator reducer) {
		if (transformer == null || reducer == null)
			throw new NullPointerException();
		return new MapReduceEntriesToDoubleTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null,
				transformer, basis, reducer).invoke();
	}

	public long reduceEntriesToLong(long parallelismThreshold, ToLongFunction<Map.Entry<K, V>> transformer, long basis,
			LongBinaryOperator reducer) {
		if (transformer == null || reducer == null)
			throw new NullPointerException();
		return new MapReduceEntriesToLongTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null,
				transformer, basis, reducer).invoke();
	}

	public int reduceEntriesToInt(long parallelismThreshold, ToIntFunction<Map.Entry<K, V>> transformer, int basis,
			IntBinaryOperator reducer) {
		if (transformer == null || reducer == null)
			throw new NullPointerException();
		return new MapReduceEntriesToIntTask8<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null,
				transformer, basis, reducer).invoke();
	}

	abstract static class CollectionView<K, V, E> implements Collection<E>, java.io.Serializable {
		private static final long serialVersionUID = 7249069246763182397L;
		final ConcurrentHashMap8<K, V> map;

		CollectionView(ConcurrentHashMap8<K, V> map) {
			this.map = map;
		}

		public ConcurrentHashMap8<K, V> getMap() {
			return map;
		}

		public final void clear() {
			map.clear();
		}

		public final int size() {
			return map.size();
		}

		public final boolean isEmpty() {
			return map.isEmpty();
		}

		public abstract Iterator<E> iterator();

		public abstract boolean contains(Object o);

		public abstract boolean remove(Object o);

		private static final String oomeMsg = "Required array size too large";

		public final Object[] toArray() {
			long sz = map.mappingCount();
			if (sz > MAX_ARRAY_SIZE)
				throw new OutOfMemoryError(oomeMsg);
			int n = (int) sz;
			Object[] r = new Object[n];
			int i = 0;
			for (E e : this) {
				if (i == n) {
					if (n >= MAX_ARRAY_SIZE)
						throw new OutOfMemoryError(oomeMsg);
					if (n >= MAX_ARRAY_SIZE - (MAX_ARRAY_SIZE >>> 1) - 1)
						n = MAX_ARRAY_SIZE;
					else
						n += (n >>> 1) + 1;
					r = Arrays.copyOf(r, n);
				}
				r[i++] = e;
			}
			return (i == n) ? r : Arrays.copyOf(r, i);
		}

		@SuppressWarnings("unchecked")
		public final <T> T[] toArray(T[] a) {
			long sz = map.mappingCount();
			if (sz > MAX_ARRAY_SIZE)
				throw new OutOfMemoryError(oomeMsg);
			int m = (int) sz;
			T[] r = (a.length >= m) ? a : (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), m);
			int n = r.length;
			int i = 0;
			for (E e : this) {
				if (i == n) {
					if (n >= MAX_ARRAY_SIZE)
						throw new OutOfMemoryError(oomeMsg);
					if (n >= MAX_ARRAY_SIZE - (MAX_ARRAY_SIZE >>> 1) - 1)
						n = MAX_ARRAY_SIZE;
					else
						n += (n >>> 1) + 1;
					r = Arrays.copyOf(r, n);
				}
				r[i++] = (T) e;
			}
			if (a == r && i < n) {
				r[i] = null; // null-terminate
				return r;
			}
			return (i == n) ? r : Arrays.copyOf(r, i);
		}

		public final String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			Iterator<E> it = iterator();
			if (it.hasNext()) {
				for (;;) {
					Object e = it.next();
					sb.append(e == this ? "(this Collection)" : e);
					if (!it.hasNext())
						break;
					sb.append(',').append(' ');
				}
			}
			return sb.append(']').toString();
		}

		public final boolean containsAll(Collection<?> c) {
			if (c != this) {
				for (Object e : c) {
					if (e == null || !contains(e))
						return false;
				}
			}
			return true;
		}

		public final boolean removeAll(Collection<?> c) {
			if (c == null)
				throw new NullPointerException();
			boolean modified = false;
			for (Iterator<E> it = iterator(); it.hasNext();) {
				if (c.contains(it.next())) {
					it.remove();
					modified = true;
				}
			}
			return modified;
		}

		public final boolean retainAll(Collection<?> c) {
			if (c == null)
				throw new NullPointerException();
			boolean modified = false;
			for (Iterator<E> it = iterator(); it.hasNext();) {
				if (!c.contains(it.next())) {
					it.remove();
					modified = true;
				}
			}
			return modified;
		}

	}

	public static class KeySetView8<K, V> extends CollectionView<K, V, K> implements Set<K>, java.io.Serializable {
		private static final long serialVersionUID = 7249069246763182397L;
		private final V value;

		KeySetView8(ConcurrentHashMap8<K, V> map, V value) { // non-public
			super(map);
			this.value = value;
		}

		public V getMappedValue() {
			return value;
		}

		public boolean contains(Object o) {
			return map.containsKey(o);
		}

		public boolean remove(Object o) {
			return map.remove(o) != null;
		}

		/**
		 * @return an iterator over the keys of the backing map
		 */
		public Iterator<K> iterator() {
			Node<K, V>[] t;
			ConcurrentHashMap8<K, V> m = map;
			int f = (t = m.table) == null ? 0 : t.length;
			return new KeyIterator8<K, V>(t, f, 0, f, m);
		}

		public boolean add(K e) {
			V v;
			if ((v = value) == null)
				throw new UnsupportedOperationException();
			return map.putVal(e, v, true) == null;
		}

		public boolean addAll(Collection<? extends K> c) {
			boolean added = false;
			V v;
			if ((v = value) == null)
				throw new UnsupportedOperationException();
			for (K e : c) {
				if (map.putVal(e, v, true) == null)
					added = true;
			}
			return added;
		}

		public int hashCode() {
			int h = 0;
			for (K e : this)
				h += e.hashCode();
			return h;
		}

		public boolean equals(Object o) {
			Set<?> c;
			return ((o instanceof Set) && ((c = (Set<?>) o) == this || (containsAll(c) && c.containsAll(this))));
		}

		public Spliterator<K> spliterator() {
			Node<K, V>[] t;
			ConcurrentHashMap8<K, V> m = map;
			long n = m.sumCount();
			int f = (t = m.table) == null ? 0 : t.length;
			return new KeySpliterator<K, V>(t, f, 0, f, n < 0L ? 0L : n);
		}

		public void forEach(Consumer<? super K> action) {
			if (action == null)
				throw new NullPointerException();
			Node<K, V>[] t;
			if ((t = map.table) != null) {
				Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
				for (Node<K, V> p; (p = it.advance()) != null;)
					action.accept(p.key);
			}
		}
	}

	/**
	 * A view of a ConcurrentHashMap as a {@link Collection} of values, in which
	 * additions are disabled. This class cannot be directly instantiated. See
	 * {@link #values()}.
	 */
	static final class ValuesView<K, V> extends CollectionView<K, V, V> implements Collection<V>, java.io.Serializable {
		private static final long serialVersionUID = 2249069246763182397L;

		ValuesView(ConcurrentHashMap8<K, V> map) {
			super(map);
		}

		public final boolean contains(Object o) {
			return map.containsValue(o);
		}

		public final boolean remove(Object o) {
			if (o != null) {
				for (Iterator<V> it = iterator(); it.hasNext();) {
					if (o.equals(it.next())) {
						it.remove();
						return true;
					}
				}
			}
			return false;
		}

		public final Iterator<V> iterator() {
			ConcurrentHashMap8<K, V> m = map;
			Node<K, V>[] t;
			int f = (t = m.table) == null ? 0 : t.length;
			return new ValueIterator8<K, V>(t, f, 0, f, m);
		}

		public final boolean add(V e) {
			throw new UnsupportedOperationException();
		}

		public final boolean addAll(Collection<? extends V> c) {
			throw new UnsupportedOperationException();
		}

		public Spliterator<V> spliterator() {
			Node<K, V>[] t;
			ConcurrentHashMap8<K, V> m = map;
			long n = m.sumCount();
			int f = (t = m.table) == null ? 0 : t.length;
			return new ValueSpliterator<K, V>(t, f, 0, f, n < 0L ? 0L : n);
		}

		public void forEach(Consumer<? super V> action) {
			if (action == null)
				throw new NullPointerException();
			Node<K, V>[] t;
			if ((t = map.table) != null) {
				Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
				for (Node<K, V> p; (p = it.advance()) != null;)
					action.accept(p.val);
			}
		}
	}

	/**
	 * A view of a ConcurrentHashMap as a {@link Set} of (key, value) entries.
	 * This class cannot be directly instantiated. See {@link #entrySet()}.
	 */
	static final class EntrySetView<K, V> extends CollectionView<K, V, Map.Entry<K, V>>
			implements Set<Map.Entry<K, V>>, java.io.Serializable {
		private static final long serialVersionUID = 2249069246763182397L;

		EntrySetView(ConcurrentHashMap8<K, V> map) {
			super(map);
		}

		public boolean contains(Object o) {
			Object k, v, r;
			Map.Entry<?, ?> e;
			return ((o instanceof Map.Entry) && (k = (e = (Map.Entry<?, ?>) o).getKey()) != null
					&& (r = map.get(k)) != null && (v = e.getValue()) != null && (v == r || v.equals(r)));
		}

		public boolean remove(Object o) {
			Object k, v;
			Map.Entry<?, ?> e;
			return ((o instanceof Map.Entry) && (k = (e = (Map.Entry<?, ?>) o).getKey()) != null
					&& (v = e.getValue()) != null && map.remove(k, v));
		}

		/**
		 * @return an iterator over the entries of the backing map
		 */
		public Iterator<Map.Entry<K, V>> iterator() {
			ConcurrentHashMap8<K, V> m = map;
			Node<K, V>[] t;
			int f = (t = m.table) == null ? 0 : t.length;
			return new EntryIterator8<K, V>(t, f, 0, f, m);
		}

		public boolean add(Entry<K, V> e) {
			return map.putVal(e.getKey(), e.getValue(), false) == null;
		}

		public boolean addAll(Collection<? extends Entry<K, V>> c) {
			boolean added = false;
			for (Entry<K, V> e : c) {
				if (add(e))
					added = true;
			}
			return added;
		}

		public final int hashCode() {
			int h = 0;
			Node<K, V>[] t;
			if ((t = map.table) != null) {
				Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
				for (Node<K, V> p; (p = it.advance()) != null;) {
					h += p.hashCode();
				}
			}
			return h;
		}

		public final boolean equals(Object o) {
			Set<?> c;
			return ((o instanceof Set) && ((c = (Set<?>) o) == this || (containsAll(c) && c.containsAll(this))));
		}

		public Spliterator<Map.Entry<K, V>> spliterator() {
			Node<K, V>[] t;
			ConcurrentHashMap8<K, V> m = map;
			long n = m.sumCount();
			int f = (t = m.table) == null ? 0 : t.length;
			return new EntrySpliterator<K, V>(t, f, 0, f, n < 0L ? 0L : n, m);
		}

		public void forEach(Consumer<? super Map.Entry<K, V>> action) {
			if (action == null)
				throw new NullPointerException();
			Node<K, V>[] t;
			if ((t = map.table) != null) {
				Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
				for (Node<K, V> p; (p = it.advance()) != null;)
					action.accept(new MapEntry<K, V>(p.key, p.val, map));
			}
		}

	}

	// -------------------------------------------------------

	/**
	 * Base class for bulk tasks. Repeats some fields and code from class
	 * Traverser, because we need to subclass CountedCompleter.
	 */
	@SuppressWarnings("serial")
	abstract static class BulkTask8<K, V, R> extends CountedCompleter<R> {
		Node<K, V>[] tab; // same as Traverser
		Node<K, V> next;
		TableStack<K, V> stack, spare;
		int index;
		int baseIndex;
		int baseLimit;
		final int baseSize;
		int batch; // split control

		BulkTask8(BulkTask8<K, V, ?> par, int b, int i, int f, Node<K, V>[] t) {
			super(par);
			this.batch = b;
			this.index = this.baseIndex = i;
			if ((this.tab = t) == null)
				this.baseSize = this.baseLimit = 0;
			else if (par == null)
				this.baseSize = this.baseLimit = t.length;
			else {
				this.baseLimit = f;
				this.baseSize = par.baseSize;
			}
		}

		/**
		 * Same as Traverser version
		 */
		final Node<K, V> advance() {
			Node<K, V> e;
			if ((e = next) != null)
				e = e.next;
			for (;;) {
				Node<K, V>[] t;
				int i, n;
				if (e != null)
					return next = e;
				if (baseIndex >= baseLimit || (t = tab) == null || (n = t.length) <= (i = index) || i < 0)
					return next = null;
				if ((e = tabAt(t, i)) != null && e.hash < 0) {
					if (e instanceof ForwardingNode) {
						tab = ((ForwardingNode<K, V>) e).nextTable;
						e = null;
						pushState(t, i, n);
						continue;
					} else if (e instanceof TreeBin)
						e = ((TreeBin<K, V>) e).first;
					else
						e = null;
				}
				if (stack != null)
					recoverState(n);
				else if ((index = i + baseSize) >= n)
					index = ++baseIndex;
			}
		}

		private void pushState(Node<K, V>[] t, int i, int n) {
			TableStack<K, V> s = spare;
			if (s != null)
				spare = s.next;
			else
				s = new TableStack<K, V>();
			s.tab = t;
			s.length = n;
			s.index = i;
			s.next = stack;
			stack = s;
		}

		private void recoverState(int n) {
			TableStack<K, V> s;
			int len;
			while ((s = stack) != null && (index += (len = s.length)) >= n) {
				n = len;
				index = s.index;
				tab = s.tab;
				s.tab = null;
				TableStack<K, V> next = s.next;
				s.next = spare; // save for reuse
				stack = next;
				spare = s;
			}
			if (s == null && (index += baseSize) >= n)
				index = ++baseIndex;
		}
	}

	/*
	 * Task classes. Coded in a regular but ugly format/style to simplify checks
	 * that each variant differs in the right way from others. The null
	 * screenings exist because compilers cannot tell that we've already
	 * null-checked task arguments, so we force simplest hoisted bypass to help
	 * avoid convoluted traps.
	 */
	@SuppressWarnings("serial")
	static final class ForEachKeyTask8<K, V> extends BulkTask8<K, V, Void> {
		final Consumer<? super K> action;

		ForEachKeyTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, Consumer<? super K> action) {
			super(p, b, i, f, t);
			this.action = action;
		}

		public final void compute() {
			final Consumer<? super K> action;
			if ((action = this.action) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					new ForEachKeyTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, action).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;)
					action.accept(p.key);
				propagateCompletion();
			}
		}
	}

	@SuppressWarnings("serial")
	static final class ForEachValueTask8<K, V> extends BulkTask8<K, V, Void> {
		final Consumer<? super V> action;

		ForEachValueTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, Consumer<? super V> action) {
			super(p, b, i, f, t);
			this.action = action;
		}

		public final void compute() {
			final Consumer<? super V> action;
			if ((action = this.action) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					new ForEachValueTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, action).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;)
					action.accept(p.val);
				propagateCompletion();
			}
		}
	}

	@SuppressWarnings("serial")
	static final class ForEachEntryTask8<K, V> extends BulkTask8<K, V, Void> {
		final Consumer<? super Entry<K, V>> action;

		ForEachEntryTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				Consumer<? super Entry<K, V>> action) {
			super(p, b, i, f, t);
			this.action = action;
		}

		public final void compute() {
			final Consumer<? super Entry<K, V>> action;
			if ((action = this.action) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					new ForEachEntryTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, action).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;)
					action.accept(p);
				propagateCompletion();
			}
		}
	}

	@SuppressWarnings("serial")
	static final class ForEachMappingTask8<K, V> extends BulkTask8<K, V, Void> {
		final BiConsumer<? super K, ? super V> action;

		ForEachMappingTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				BiConsumer<? super K, ? super V> action) {
			super(p, b, i, f, t);
			this.action = action;
		}

		public final void compute() {
			final BiConsumer<? super K, ? super V> action;
			if ((action = this.action) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					new ForEachMappingTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, action).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;)
					action.accept(p.key, p.val);
				propagateCompletion();
			}
		}
	}

	@SuppressWarnings("serial")
	static final class ForEachTransformedKeyTask8<K, V, U> extends BulkTask8<K, V, Void> {
		final Function<? super K, ? extends U> transformer;
		final Consumer<? super U> action;

		ForEachTransformedKeyTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				Function<? super K, ? extends U> transformer, Consumer<? super U> action) {
			super(p, b, i, f, t);
			this.transformer = transformer;
			this.action = action;
		}

		public final void compute() {
			final Function<? super K, ? extends U> transformer;
			final Consumer<? super U> action;
			if ((transformer = this.transformer) != null && (action = this.action) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					new ForEachTransformedKeyTask8<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, transformer,
							action).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;) {
					U u;
					if ((u = transformer.apply(p.key)) != null)
						action.accept(u);
				}
				propagateCompletion();
			}
		}
	}

	@SuppressWarnings("serial")
	static final class ForEachTransformedValueTask8<K, V, U> extends BulkTask8<K, V, Void> {
		final Function<? super V, ? extends U> transformer;
		final Consumer<? super U> action;

		ForEachTransformedValueTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				Function<? super V, ? extends U> transformer, Consumer<? super U> action) {
			super(p, b, i, f, t);
			this.transformer = transformer;
			this.action = action;
		}

		public final void compute() {
			final Function<? super V, ? extends U> transformer;
			final Consumer<? super U> action;
			if ((transformer = this.transformer) != null && (action = this.action) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					new ForEachTransformedValueTask8<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, transformer,
							action).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;) {
					U u;
					if ((u = transformer.apply(p.val)) != null)
						action.accept(u);
				}
				propagateCompletion();
			}
		}
	}

	@SuppressWarnings("serial")
	static final class ForEachTransformedEntryTask8<K, V, U> extends BulkTask8<K, V, Void> {
		final Function<Map.Entry<K, V>, ? extends U> transformer;
		final Consumer<? super U> action;

		ForEachTransformedEntryTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				Function<Map.Entry<K, V>, ? extends U> transformer, Consumer<? super U> action) {
			super(p, b, i, f, t);
			this.transformer = transformer;
			this.action = action;
		}

		public final void compute() {
			final Function<Map.Entry<K, V>, ? extends U> transformer;
			final Consumer<? super U> action;
			if ((transformer = this.transformer) != null && (action = this.action) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					new ForEachTransformedEntryTask8<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, transformer,
							action).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;) {
					U u;
					if ((u = transformer.apply(p)) != null)
						action.accept(u);
				}
				propagateCompletion();
			}
		}
	}

	@SuppressWarnings("serial")
	static final class ForEachTransformedMappingTask8<K, V, U> extends BulkTask8<K, V, Void> {
		final BiFunction<? super K, ? super V, ? extends U> transformer;
		final Consumer<? super U> action;

		ForEachTransformedMappingTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				BiFunction<? super K, ? super V, ? extends U> transformer, Consumer<? super U> action) {
			super(p, b, i, f, t);
			this.transformer = transformer;
			this.action = action;
		}

		public final void compute() {
			final BiFunction<? super K, ? super V, ? extends U> transformer;
			final Consumer<? super U> action;
			if ((transformer = this.transformer) != null && (action = this.action) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					new ForEachTransformedMappingTask8<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, transformer,
							action).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;) {
					U u;
					if ((u = transformer.apply(p.key, p.val)) != null)
						action.accept(u);
				}
				propagateCompletion();
			}
		}
	}

	@SuppressWarnings("serial")
	static final class SearchKeysTask8<K, V, U> extends BulkTask8<K, V, U> {
		final Function<? super K, ? extends U> searchFunction;
		final AtomicReference<U> result;

		SearchKeysTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				Function<? super K, ? extends U> searchFunction, AtomicReference<U> result) {
			super(p, b, i, f, t);
			this.searchFunction = searchFunction;
			this.result = result;
		}

		public final U getRawResult() {
			return result.get();
		}

		public final void compute() {
			final Function<? super K, ? extends U> searchFunction;
			final AtomicReference<U> result;
			if ((searchFunction = this.searchFunction) != null && (result = this.result) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					if (result.get() != null)
						return;
					addToPendingCount(1);
					new SearchKeysTask8<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, searchFunction, result)
							.fork();
				}
				while (result.get() == null) {
					U u;
					Node<K, V> p;
					if ((p = advance()) == null) {
						propagateCompletion();
						break;
					}
					if ((u = searchFunction.apply(p.key)) != null) {
						if (result.compareAndSet(null, u))
							quietlyCompleteRoot();
						break;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class SearchValuesTask8<K, V, U> extends BulkTask8<K, V, U> {
		final Function<? super V, ? extends U> searchFunction;
		final AtomicReference<U> result;

		SearchValuesTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				Function<? super V, ? extends U> searchFunction, AtomicReference<U> result) {
			super(p, b, i, f, t);
			this.searchFunction = searchFunction;
			this.result = result;
		}

		public final U getRawResult() {
			return result.get();
		}

		public final void compute() {
			final Function<? super V, ? extends U> searchFunction;
			final AtomicReference<U> result;
			if ((searchFunction = this.searchFunction) != null && (result = this.result) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					if (result.get() != null)
						return;
					addToPendingCount(1);
					new SearchValuesTask8<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, searchFunction, result)
							.fork();
				}
				while (result.get() == null) {
					U u;
					Node<K, V> p;
					if ((p = advance()) == null) {
						propagateCompletion();
						break;
					}
					if ((u = searchFunction.apply(p.val)) != null) {
						if (result.compareAndSet(null, u))
							quietlyCompleteRoot();
						break;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class SearchEntriesTask8<K, V, U> extends BulkTask8<K, V, U> {
		final Function<Entry<K, V>, ? extends U> searchFunction;
		final AtomicReference<U> result;

		SearchEntriesTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				Function<Entry<K, V>, ? extends U> searchFunction, AtomicReference<U> result) {
			super(p, b, i, f, t);
			this.searchFunction = searchFunction;
			this.result = result;
		}

		public final U getRawResult() {
			return result.get();
		}

		public final void compute() {
			final Function<Entry<K, V>, ? extends U> searchFunction;
			final AtomicReference<U> result;
			if ((searchFunction = this.searchFunction) != null && (result = this.result) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					if (result.get() != null)
						return;
					addToPendingCount(1);
					new SearchEntriesTask8<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, searchFunction, result)
							.fork();
				}
				while (result.get() == null) {
					U u;
					Node<K, V> p;
					if ((p = advance()) == null) {
						propagateCompletion();
						break;
					}
					if ((u = searchFunction.apply(p)) != null) {
						if (result.compareAndSet(null, u))
							quietlyCompleteRoot();
						return;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class SearchMappingsTask8<K, V, U> extends BulkTask8<K, V, U> {
		final BiFunction<? super K, ? super V, ? extends U> searchFunction;
		final AtomicReference<U> result;

		SearchMappingsTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				BiFunction<? super K, ? super V, ? extends U> searchFunction, AtomicReference<U> result) {
			super(p, b, i, f, t);
			this.searchFunction = searchFunction;
			this.result = result;
		}

		public final U getRawResult() {
			return result.get();
		}

		public final void compute() {
			final BiFunction<? super K, ? super V, ? extends U> searchFunction;
			final AtomicReference<U> result;
			if ((searchFunction = this.searchFunction) != null && (result = this.result) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					if (result.get() != null)
						return;
					addToPendingCount(1);
					new SearchMappingsTask8<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, searchFunction, result)
							.fork();
				}
				while (result.get() == null) {
					U u;
					Node<K, V> p;
					if ((p = advance()) == null) {
						propagateCompletion();
						break;
					}
					if ((u = searchFunction.apply(p.key, p.val)) != null) {
						if (result.compareAndSet(null, u))
							quietlyCompleteRoot();
						break;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class ReduceKeysTask8<K, V> extends BulkTask8<K, V, K> {
		final BiFunction<? super K, ? super K, ? extends K> reducer;
		K result;
		ReduceKeysTask8<K, V> rights, nextRight;

		ReduceKeysTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, ReduceKeysTask8<K, V> nextRight,
				BiFunction<? super K, ? super K, ? extends K> reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.reducer = reducer;
		}

		public final K getRawResult() {
			return result;
		}

		public final void compute() {
			final BiFunction<? super K, ? super K, ? extends K> reducer;
			if ((reducer = this.reducer) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new ReduceKeysTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights, reducer))
							.fork();
				}
				K r = null;
				for (Node<K, V> p; (p = advance()) != null;) {
					K u = p.key;
					r = (r == null) ? u : u == null ? r : reducer.apply(r, u);
				}
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					ReduceKeysTask8<K, V> t = (ReduceKeysTask8<K, V>) c, s = t.rights;
					while (s != null) {
						K tr, sr;
						if ((sr = s.result) != null)
							t.result = (((tr = t.result) == null) ? sr : reducer.apply(tr, sr));
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class ReduceValuesTask8<K, V> extends BulkTask8<K, V, V> {
		final BiFunction<? super V, ? super V, ? extends V> reducer;
		V result;
		ReduceValuesTask8<K, V> rights, nextRight;

		ReduceValuesTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, ReduceValuesTask8<K, V> nextRight,
				BiFunction<? super V, ? super V, ? extends V> reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.reducer = reducer;
		}

		public final V getRawResult() {
			return result;
		}

		public final void compute() {
			final BiFunction<? super V, ? super V, ? extends V> reducer;
			if ((reducer = this.reducer) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new ReduceValuesTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights, reducer))
							.fork();
				}
				V r = null;
				for (Node<K, V> p; (p = advance()) != null;) {
					V v = p.val;
					r = (r == null) ? v : reducer.apply(r, v);
				}
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					ReduceValuesTask8<K, V> t = (ReduceValuesTask8<K, V>) c, s = t.rights;
					while (s != null) {
						V tr, sr;
						if ((sr = s.result) != null)
							t.result = (((tr = t.result) == null) ? sr : reducer.apply(tr, sr));
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class ReduceEntriesTask8<K, V> extends BulkTask8<K, V, Map.Entry<K, V>> {
		final BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer;
		Map.Entry<K, V> result;
		ReduceEntriesTask8<K, V> rights, nextRight;

		ReduceEntriesTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				ReduceEntriesTask8<K, V> nextRight,
				BiFunction<Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.reducer = reducer;
		}

		public final Map.Entry<K, V> getRawResult() {
			return result;
		}

		public final void compute() {
			final BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer;
			if ((reducer = this.reducer) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new ReduceEntriesTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights, reducer))
							.fork();
				}
				Map.Entry<K, V> r = null;
				for (Node<K, V> p; (p = advance()) != null;)
					r = (r == null) ? p : reducer.apply(r, p);
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					ReduceEntriesTask8<K, V> t = (ReduceEntriesTask8<K, V>) c, s = t.rights;
					while (s != null) {
						Map.Entry<K, V> tr, sr;
						if ((sr = s.result) != null)
							t.result = (((tr = t.result) == null) ? sr : reducer.apply(tr, sr));
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class MapReduceKeysTask8<K, V, U> extends BulkTask8<K, V, U> {
		final Function<? super K, ? extends U> transformer;
		final BiFunction<? super U, ? super U, ? extends U> reducer;
		U result;
		MapReduceKeysTask8<K, V, U> rights, nextRight;

		MapReduceKeysTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				MapReduceKeysTask8<K, V, U> nextRight, Function<? super K, ? extends U> transformer,
				BiFunction<? super U, ? super U, ? extends U> reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.transformer = transformer;
			this.reducer = reducer;
		}

		public final U getRawResult() {
			return result;
		}

		public final void compute() {
			final Function<? super K, ? extends U> transformer;
			final BiFunction<? super U, ? super U, ? extends U> reducer;
			if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new MapReduceKeysTask8<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
							transformer, reducer)).fork();
				}
				U r = null;
				for (Node<K, V> p; (p = advance()) != null;) {
					U u;
					if ((u = transformer.apply(p.key)) != null)
						r = (r == null) ? u : reducer.apply(r, u);
				}
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					MapReduceKeysTask8<K, V, U> t = (MapReduceKeysTask8<K, V, U>) c, s = t.rights;
					while (s != null) {
						U tr, sr;
						if ((sr = s.result) != null)
							t.result = (((tr = t.result) == null) ? sr : reducer.apply(tr, sr));
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class MapReduceValuesTask8<K, V, U> extends BulkTask8<K, V, U> {
		final Function<? super V, ? extends U> transformer;
		final BiFunction<? super U, ? super U, ? extends U> reducer;
		U result;
		MapReduceValuesTask8<K, V, U> rights, nextRight;

		MapReduceValuesTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				MapReduceValuesTask8<K, V, U> nextRight, Function<? super V, ? extends U> transformer,
				BiFunction<? super U, ? super U, ? extends U> reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.transformer = transformer;
			this.reducer = reducer;
		}

		public final U getRawResult() {
			return result;
		}

		public final void compute() {
			final Function<? super V, ? extends U> transformer;
			final BiFunction<? super U, ? super U, ? extends U> reducer;
			if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new MapReduceValuesTask8<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
							transformer, reducer)).fork();
				}
				U r = null;
				for (Node<K, V> p; (p = advance()) != null;) {
					U u;
					if ((u = transformer.apply(p.val)) != null)
						r = (r == null) ? u : reducer.apply(r, u);
				}
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					MapReduceValuesTask8<K, V, U> t = (MapReduceValuesTask8<K, V, U>) c, s = t.rights;
					while (s != null) {
						U tr, sr;
						if ((sr = s.result) != null)
							t.result = (((tr = t.result) == null) ? sr : reducer.apply(tr, sr));
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class MapReduceEntriesTask8<K, V, U> extends BulkTask8<K, V, U> {
		final Function<Map.Entry<K, V>, ? extends U> transformer;
		final BiFunction<? super U, ? super U, ? extends U> reducer;
		U result;
		MapReduceEntriesTask8<K, V, U> rights, nextRight;

		MapReduceEntriesTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				MapReduceEntriesTask8<K, V, U> nextRight, Function<Map.Entry<K, V>, ? extends U> transformer,
				BiFunction<? super U, ? super U, ? extends U> reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.transformer = transformer;
			this.reducer = reducer;
		}

		public final U getRawResult() {
			return result;
		}

		public final void compute() {
			final Function<Map.Entry<K, V>, ? extends U> transformer;
			final BiFunction<? super U, ? super U, ? extends U> reducer;
			if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new MapReduceEntriesTask8<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
							transformer, reducer)).fork();
				}
				U r = null;
				for (Node<K, V> p; (p = advance()) != null;) {
					U u;
					if ((u = transformer.apply(p)) != null)
						r = (r == null) ? u : reducer.apply(r, u);
				}
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					MapReduceEntriesTask8<K, V, U> t = (MapReduceEntriesTask8<K, V, U>) c, s = t.rights;
					while (s != null) {
						U tr, sr;
						if ((sr = s.result) != null)
							t.result = (((tr = t.result) == null) ? sr : reducer.apply(tr, sr));
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class MapReduceMappingsTask8<K, V, U> extends BulkTask8<K, V, U> {
		final BiFunction<? super K, ? super V, ? extends U> transformer;
		final BiFunction<? super U, ? super U, ? extends U> reducer;
		U result;
		MapReduceMappingsTask8<K, V, U> rights, nextRight;

		MapReduceMappingsTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				MapReduceMappingsTask8<K, V, U> nextRight, BiFunction<? super K, ? super V, ? extends U> transformer,
				BiFunction<? super U, ? super U, ? extends U> reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.transformer = transformer;
			this.reducer = reducer;
		}

		public final U getRawResult() {
			return result;
		}

		public final void compute() {
			final BiFunction<? super K, ? super V, ? extends U> transformer;
			final BiFunction<? super U, ? super U, ? extends U> reducer;
			if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new MapReduceMappingsTask8<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
							transformer, reducer)).fork();
				}
				U r = null;
				for (Node<K, V> p; (p = advance()) != null;) {
					U u;
					if ((u = transformer.apply(p.key, p.val)) != null)
						r = (r == null) ? u : reducer.apply(r, u);
				}
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					MapReduceMappingsTask8<K, V, U> t = (MapReduceMappingsTask8<K, V, U>) c, s = t.rights;
					while (s != null) {
						U tr, sr;
						if ((sr = s.result) != null)
							t.result = (((tr = t.result) == null) ? sr : reducer.apply(tr, sr));
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class MapReduceKeysToDoubleTask8<K, V> extends BulkTask8<K, V, Double> {
		final ToDoubleFunction<? super K> transformer;
		final DoubleBinaryOperator reducer;
		final double basis;
		double result;
		MapReduceKeysToDoubleTask8<K, V> rights, nextRight;

		MapReduceKeysToDoubleTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				MapReduceKeysToDoubleTask8<K, V> nextRight, ToDoubleFunction<? super K> transformer, double basis,
				DoubleBinaryOperator reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.transformer = transformer;
			this.basis = basis;
			this.reducer = reducer;
		}

		public final Double getRawResult() {
			return result;
		}

		public final void compute() {
			final ToDoubleFunction<? super K> transformer;
			final DoubleBinaryOperator reducer;
			if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
				double r = this.basis;
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new MapReduceKeysToDoubleTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
							transformer, r, reducer)).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;)
					r = reducer.applyAsDouble(r, transformer.applyAsDouble(p.key));
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					MapReduceKeysToDoubleTask8<K, V> t = (MapReduceKeysToDoubleTask8<K, V>) c, s = t.rights;
					while (s != null) {
						t.result = reducer.applyAsDouble(t.result, s.result);
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class MapReduceValuesToDoubleTask8<K, V> extends BulkTask8<K, V, Double> {
		final ToDoubleFunction<? super V> transformer;
		final DoubleBinaryOperator reducer;
		final double basis;
		double result;
		MapReduceValuesToDoubleTask8<K, V> rights, nextRight;

		MapReduceValuesToDoubleTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				MapReduceValuesToDoubleTask8<K, V> nextRight, ToDoubleFunction<? super V> transformer, double basis,
				DoubleBinaryOperator reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.transformer = transformer;
			this.basis = basis;
			this.reducer = reducer;
		}

		public final Double getRawResult() {
			return result;
		}

		public final void compute() {
			final ToDoubleFunction<? super V> transformer;
			final DoubleBinaryOperator reducer;
			if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
				double r = this.basis;
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new MapReduceValuesToDoubleTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
							transformer, r, reducer)).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;)
					r = reducer.applyAsDouble(r, transformer.applyAsDouble(p.val));
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					MapReduceValuesToDoubleTask8<K, V> t = (MapReduceValuesToDoubleTask8<K, V>) c, s = t.rights;
					while (s != null) {
						t.result = reducer.applyAsDouble(t.result, s.result);
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class MapReduceEntriesToDoubleTask8<K, V> extends BulkTask8<K, V, Double> {
		final ToDoubleFunction<Map.Entry<K, V>> transformer;
		final DoubleBinaryOperator reducer;
		final double basis;
		double result;
		MapReduceEntriesToDoubleTask8<K, V> rights, nextRight;

		MapReduceEntriesToDoubleTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				MapReduceEntriesToDoubleTask8<K, V> nextRight, ToDoubleFunction<Map.Entry<K, V>> transformer,
				double basis, DoubleBinaryOperator reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.transformer = transformer;
			this.basis = basis;
			this.reducer = reducer;
		}

		public final Double getRawResult() {
			return result;
		}

		public final void compute() {
			final ToDoubleFunction<Map.Entry<K, V>> transformer;
			final DoubleBinaryOperator reducer;
			if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
				double r = this.basis;
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new MapReduceEntriesToDoubleTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
							transformer, r, reducer)).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;)
					r = reducer.applyAsDouble(r, transformer.applyAsDouble(p));
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					MapReduceEntriesToDoubleTask8<K, V> t = (MapReduceEntriesToDoubleTask8<K, V>) c, s = t.rights;
					while (s != null) {
						t.result = reducer.applyAsDouble(t.result, s.result);
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class MapReduceMappingsToDoubleTask8<K, V> extends BulkTask8<K, V, Double> {
		final ToDoubleBiFunction<? super K, ? super V> transformer;
		final DoubleBinaryOperator reducer;
		final double basis;
		double result;
		MapReduceMappingsToDoubleTask8<K, V> rights, nextRight;

		MapReduceMappingsToDoubleTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				MapReduceMappingsToDoubleTask8<K, V> nextRight, ToDoubleBiFunction<? super K, ? super V> transformer,
				double basis, DoubleBinaryOperator reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.transformer = transformer;
			this.basis = basis;
			this.reducer = reducer;
		}

		public final Double getRawResult() {
			return result;
		}

		public final void compute() {
			final ToDoubleBiFunction<? super K, ? super V> transformer;
			final DoubleBinaryOperator reducer;
			if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
				double r = this.basis;
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new MapReduceMappingsToDoubleTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab,
							rights, transformer, r, reducer)).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;)
					r = reducer.applyAsDouble(r, transformer.applyAsDouble(p.key, p.val));
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					MapReduceMappingsToDoubleTask8<K, V> t = (MapReduceMappingsToDoubleTask8<K, V>) c, s = t.rights;
					while (s != null) {
						t.result = reducer.applyAsDouble(t.result, s.result);
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class MapReduceKeysToLongTask8<K, V> extends BulkTask8<K, V, Long> {
		final ToLongFunction<? super K> transformer;
		final LongBinaryOperator reducer;
		final long basis;
		long result;
		MapReduceKeysToLongTask8<K, V> rights, nextRight;

		MapReduceKeysToLongTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				MapReduceKeysToLongTask8<K, V> nextRight, ToLongFunction<? super K> transformer, long basis,
				LongBinaryOperator reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.transformer = transformer;
			this.basis = basis;
			this.reducer = reducer;
		}

		public final Long getRawResult() {
			return result;
		}

		public final void compute() {
			final ToLongFunction<? super K> transformer;
			final LongBinaryOperator reducer;
			if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
				long r = this.basis;
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new MapReduceKeysToLongTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
							transformer, r, reducer)).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;)
					r = reducer.applyAsLong(r, transformer.applyAsLong(p.key));
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					MapReduceKeysToLongTask8<K, V> t = (MapReduceKeysToLongTask8<K, V>) c, s = t.rights;
					while (s != null) {
						t.result = reducer.applyAsLong(t.result, s.result);
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class MapReduceValuesToLongTask8<K, V> extends BulkTask8<K, V, Long> {
		final ToLongFunction<? super V> transformer;
		final LongBinaryOperator reducer;
		final long basis;
		long result;
		MapReduceValuesToLongTask8<K, V> rights, nextRight;

		MapReduceValuesToLongTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				MapReduceValuesToLongTask8<K, V> nextRight, ToLongFunction<? super V> transformer, long basis,
				LongBinaryOperator reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.transformer = transformer;
			this.basis = basis;
			this.reducer = reducer;
		}

		public final Long getRawResult() {
			return result;
		}

		public final void compute() {
			final ToLongFunction<? super V> transformer;
			final LongBinaryOperator reducer;
			if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
				long r = this.basis;
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new MapReduceValuesToLongTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
							transformer, r, reducer)).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;)
					r = reducer.applyAsLong(r, transformer.applyAsLong(p.val));
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					MapReduceValuesToLongTask8<K, V> t = (MapReduceValuesToLongTask8<K, V>) c, s = t.rights;
					while (s != null) {
						t.result = reducer.applyAsLong(t.result, s.result);
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class MapReduceEntriesToLongTask8<K, V> extends BulkTask8<K, V, Long> {
		final ToLongFunction<Map.Entry<K, V>> transformer;
		final LongBinaryOperator reducer;
		final long basis;
		long result;
		MapReduceEntriesToLongTask8<K, V> rights, nextRight;

		MapReduceEntriesToLongTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				MapReduceEntriesToLongTask8<K, V> nextRight, ToLongFunction<Map.Entry<K, V>> transformer, long basis,
				LongBinaryOperator reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.transformer = transformer;
			this.basis = basis;
			this.reducer = reducer;
		}

		public final Long getRawResult() {
			return result;
		}

		public final void compute() {
			final ToLongFunction<Map.Entry<K, V>> transformer;
			final LongBinaryOperator reducer;
			if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
				long r = this.basis;
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new MapReduceEntriesToLongTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
							transformer, r, reducer)).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;)
					r = reducer.applyAsLong(r, transformer.applyAsLong(p));
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					MapReduceEntriesToLongTask8<K, V> t = (MapReduceEntriesToLongTask8<K, V>) c, s = t.rights;
					while (s != null) {
						t.result = reducer.applyAsLong(t.result, s.result);
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class MapReduceMappingsToLongTask8<K, V> extends BulkTask8<K, V, Long> {
		final ToLongBiFunction<? super K, ? super V> transformer;
		final LongBinaryOperator reducer;
		final long basis;
		long result;
		MapReduceMappingsToLongTask8<K, V> rights, nextRight;

		MapReduceMappingsToLongTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				MapReduceMappingsToLongTask8<K, V> nextRight, ToLongBiFunction<? super K, ? super V> transformer,
				long basis, LongBinaryOperator reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.transformer = transformer;
			this.basis = basis;
			this.reducer = reducer;
		}

		public final Long getRawResult() {
			return result;
		}

		public final void compute() {
			final ToLongBiFunction<? super K, ? super V> transformer;
			final LongBinaryOperator reducer;
			if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
				long r = this.basis;
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new MapReduceMappingsToLongTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
							transformer, r, reducer)).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;)
					r = reducer.applyAsLong(r, transformer.applyAsLong(p.key, p.val));
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					MapReduceMappingsToLongTask8<K, V> t = (MapReduceMappingsToLongTask8<K, V>) c, s = t.rights;
					while (s != null) {
						t.result = reducer.applyAsLong(t.result, s.result);
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class MapReduceKeysToIntTask8<K, V> extends BulkTask8<K, V, Integer> {
		final ToIntFunction<? super K> transformer;
		final IntBinaryOperator reducer;
		final int basis;
		int result;
		MapReduceKeysToIntTask8<K, V> rights, nextRight;

		MapReduceKeysToIntTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				MapReduceKeysToIntTask8<K, V> nextRight, ToIntFunction<? super K> transformer, int basis,
				IntBinaryOperator reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.transformer = transformer;
			this.basis = basis;
			this.reducer = reducer;
		}

		public final Integer getRawResult() {
			return result;
		}

		public final void compute() {
			final ToIntFunction<? super K> transformer;
			final IntBinaryOperator reducer;
			if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
				int r = this.basis;
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new MapReduceKeysToIntTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
							transformer, r, reducer)).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;)
					r = reducer.applyAsInt(r, transformer.applyAsInt(p.key));
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					MapReduceKeysToIntTask8<K, V> t = (MapReduceKeysToIntTask8<K, V>) c, s = t.rights;
					while (s != null) {
						t.result = reducer.applyAsInt(t.result, s.result);
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class MapReduceValuesToIntTask8<K, V> extends BulkTask8<K, V, Integer> {
		final ToIntFunction<? super V> transformer;
		final IntBinaryOperator reducer;
		final int basis;
		int result;
		MapReduceValuesToIntTask8<K, V> rights, nextRight;

		MapReduceValuesToIntTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				MapReduceValuesToIntTask8<K, V> nextRight, ToIntFunction<? super V> transformer, int basis,
				IntBinaryOperator reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.transformer = transformer;
			this.basis = basis;
			this.reducer = reducer;
		}

		public final Integer getRawResult() {
			return result;
		}

		public final void compute() {
			final ToIntFunction<? super V> transformer;
			final IntBinaryOperator reducer;
			if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
				int r = this.basis;
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new MapReduceValuesToIntTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
							transformer, r, reducer)).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;)
					r = reducer.applyAsInt(r, transformer.applyAsInt(p.val));
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					MapReduceValuesToIntTask8<K, V> t = (MapReduceValuesToIntTask8<K, V>) c, s = t.rights;
					while (s != null) {
						t.result = reducer.applyAsInt(t.result, s.result);
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	static final class MapReduceEntriesToIntTask8<K, V> extends BulkTask8<K, V, Integer> {
		final ToIntFunction<Map.Entry<K, V>> transformer;
		final IntBinaryOperator reducer;
		final int basis;
		int result;
		MapReduceEntriesToIntTask8<K, V> rights, nextRight;

		MapReduceEntriesToIntTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				MapReduceEntriesToIntTask8<K, V> nextRight, ToIntFunction<Map.Entry<K, V>> transformer, int basis,
				IntBinaryOperator reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.transformer = transformer;
			this.basis = basis;
			this.reducer = reducer;
		}

		public final Integer getRawResult() {
			return result;
		}

		public final void compute() {
			final ToIntFunction<Map.Entry<K, V>> transformer;
			final IntBinaryOperator reducer;
			if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
				int r = this.basis;
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new MapReduceEntriesToIntTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
							transformer, r, reducer)).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;)
					r = reducer.applyAsInt(r, transformer.applyAsInt(p));
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					MapReduceEntriesToIntTask8<K, V> t = (MapReduceEntriesToIntTask8<K, V>) c, s = t.rights;
					while (s != null) {
						t.result = reducer.applyAsInt(t.result, s.result);
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	static final class MapReduceMappingsToIntTask8<K, V> extends BulkTask8<K, V, Integer> {
		final ToIntBiFunction<? super K, ? super V> transformer;
		final IntBinaryOperator reducer;
		final int basis;
		int result;
		MapReduceMappingsToIntTask8<K, V> rights, nextRight;

		MapReduceMappingsToIntTask8(BulkTask8<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
				MapReduceMappingsToIntTask8<K, V> nextRight, ToIntBiFunction<? super K, ? super V> transformer,
				int basis, IntBinaryOperator reducer) {
			super(p, b, i, f, t);
			this.nextRight = nextRight;
			this.transformer = transformer;
			this.basis = basis;
			this.reducer = reducer;
		}

		public final Integer getRawResult() {
			return result;
		}

		public final void compute() {
			final ToIntBiFunction<? super K, ? super V> transformer;
			final IntBinaryOperator reducer;
			if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
				int r = this.basis;
				for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i;) {
					addToPendingCount(1);
					(rights = new MapReduceMappingsToIntTask8<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
							transformer, r, reducer)).fork();
				}
				for (Node<K, V> p; (p = advance()) != null;)
					r = reducer.applyAsInt(r, transformer.applyAsInt(p.key, p.val));
				result = r;
				CountedCompleter<?> c;
				for (c = firstComplete(); c != null; c = c.nextComplete()) {
					@SuppressWarnings("unchecked")
					MapReduceMappingsToIntTask8<K, V> t = (MapReduceMappingsToIntTask8<K, V>) c, s = t.rights;
					while (s != null) {
						t.result = reducer.applyAsInt(t.result, s.result);
						s = t.rights = s.nextRight;
					}
				}
			}
		}
	}

	////////// 下面这些逻辑可以不用关心////////////////////////
	// Unsafe mechanics
	private static final sun.misc.Unsafe U;
	private static final long SIZECTL;
	private static final long TRANSFERINDEX;
	private static final long BASECOUNT;
	private static final long CELLSBUSY;
	private static final long CELLVALUE;
	private static final long ABASE;
	private static final int ASHIFT;

	static {
		try {
			U = sun.misc.Unsafe.getUnsafe();
			Class<?> k = ConcurrentHashMap8.class;
			SIZECTL = U.objectFieldOffset(k.getDeclaredField("sizeCtl"));
			TRANSFERINDEX = U.objectFieldOffset(k.getDeclaredField("transferIndex"));
			BASECOUNT = U.objectFieldOffset(k.getDeclaredField("baseCount"));
			CELLSBUSY = U.objectFieldOffset(k.getDeclaredField("cellsBusy"));
			Class<?> ck = CounterCell.class;
			CELLVALUE = U.objectFieldOffset(ck.getDeclaredField("value"));
			Class<?> ak = Node[].class;
			ABASE = U.arrayBaseOffset(ak);
			int scale = U.arrayIndexScale(ak);
			if ((scale & (scale - 1)) != 0)
				throw new Error("data type scale not a power of two");
			ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	static final int getProbe() {
		return UNSAFE.getInt(Thread.currentThread(), PROBE);
	}

	static final int advanceProbe(int probe) {
		probe ^= probe << 13; // xorshift
		probe ^= probe >>> 17;
		probe ^= probe << 5;
		UNSAFE.putInt(Thread.currentThread(), PROBE, probe);
		return probe;
	}

	static final void localInit() {
		int p = probeGenerator.addAndGet(PROBE_INCREMENT);
		int probe = (p == 0) ? 1 : p; // skip 0
		long seed = mix64(seeder.getAndAdd(SEEDER_INCREMENT));
		Thread t = Thread.currentThread();
		UNSAFE.putLong(t, SEED, seed);
		UNSAFE.putInt(t, PROBE, probe);
	}

	private static final AtomicLong seeder = new AtomicLong(initialSeed());

	private static final AtomicInteger probeGenerator = new AtomicInteger();
	private static final sun.misc.Unsafe UNSAFE;
	private static final long SEED;
	private static final long PROBE;
	private static final long SECONDARY;
	private static final int PROBE_INCREMENT = 0x9e3779b9;
	private static final long SEEDER_INCREMENT = 0xbb67ae8584caa73bL;

	private static long initialSeed() {
		String pp = java.security.AccessController
				.doPrivileged(new sun.security.action.GetPropertyAction("java.util.secureRandomSeed"));
		if (pp != null && pp.equalsIgnoreCase("true")) {
			byte[] seedBytes = java.security.SecureRandom.getSeed(8);
			long s = (long) (seedBytes[0]) & 0xffL;
			for (int i = 1; i < 8; ++i)
				s = (s << 8) | ((long) (seedBytes[i]) & 0xffL);
			return s;
		}
		long h = 0L;
		try {
			Enumeration<NetworkInterface> ifcs = NetworkInterface.getNetworkInterfaces();
			boolean retry = false; // retry once if getHardwareAddress is null
			while (ifcs.hasMoreElements()) {
				NetworkInterface ifc = ifcs.nextElement();
				if (!ifc.isVirtual()) { // skip fake addresses
					byte[] bs = ifc.getHardwareAddress();
					if (bs != null) {
						int n = bs.length;
						int m = Math.min(n >>> 1, 4);
						for (int i = 0; i < m; ++i)
							h = (h << 16) ^ (bs[i] << 8) ^ bs[n - 1 - i];
						if (m < 4)
							h = (h << 8) ^ bs[n - 1 - m];
						h = mix64(h);
						break;
					} else if (!retry)
						retry = true;
					else
						break;
				}
			}
		} catch (Exception ignore) {
		}
		return (h ^ mix64(System.currentTimeMillis()) ^ mix64(System.nanoTime()));
	}

	private static long mix64(long z) {
		z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
		z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
		return z ^ (z >>> 33);
	}

	static {

		try {
			UNSAFE = sun.misc.Unsafe.getUnsafe();
			Class<?> tk = Thread.class;
			SEED = UNSAFE.objectFieldOffset(tk.getDeclaredField("threadLocalRandomSeed"));
			PROBE = UNSAFE.objectFieldOffset(tk.getDeclaredField("threadLocalRandomProbe"));
			SECONDARY = UNSAFE.objectFieldOffset(tk.getDeclaredField("threadLocalRandomSecondarySeed"));

		} catch (Exception e) {
			throw new Error(e);
		}
	}
}
