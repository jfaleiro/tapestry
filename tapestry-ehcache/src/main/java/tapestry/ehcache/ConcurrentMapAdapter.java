/**
 * //
 * //    Tapestry- a really-really fast event driven distributed hash table
 * //
 * //    Copyright (C) 2006 Jorge M. Faleiro Jr.
 * //
 * //    This program is free software: you can redistribute it and/or modify
 * //    it under the terms of the GNU Affero General Public License as published
 * //    by the Free Software Foundation, either version 3 of the License, or
 * //    (at your option) any later version.
 * //
 * //    This program is distributed in the hope that it will be useful,
 * //    but WITHOUT ANY WARRANTY; without even the implied warranty of
 * //    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * //    GNU Affero General Public License for more details.
 * //
 * //    You should have received a copy of the GNU Affero General Public License
 * //    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * //
 */
package tapestry.ehcache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * A map facade to a cache instance.
 * 
 * @author jfaleiro
 *
 * @param <K>
 *            type of key
 * @param <V>
 *            type of value
 */
public class ConcurrentMapAdapter<K, V> implements ConcurrentMap<K, V> {
	@SuppressWarnings("unused")
	private final static Logger LOG = LoggerFactory
			.getLogger(ConcurrentMapAdapter.class);

	private final Cache ehCache;

	public ConcurrentMapAdapter(Cache ehCache) {
		super();
		this.ehCache = ehCache;
	}

	@Override
	public void clear() {
		ehCache.removeAll();
	}

	@Override
	public boolean containsKey(Object key) {
		return ehCache.isKeyInCache(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return ehCache.isValueInCache(value);
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		if (key == null)
			return null;
		Element element = ehCache.get(key);
		if (element == null)
			return null;
		return (V) element.getObjectValue();
	}

	@Override
	public boolean isEmpty() {
		return ehCache.getSize() == 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<K> keySet() {
		List<K> l = ehCache.getKeys();
		return Sets.newHashSet(l);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V put(K key, V value) {
		Object o = this.get(key);
		if (o != null)
			return (V) o;
		Element e = new Element(key, value);
		ehCache.put(e);
		return null;
	}

	@Override
	public V remove(Object key) {
		V retObj = null;
		if (this.containsKey(key)) {
			retObj = this.get(key);
		} // end if
		ehCache.remove(key);
		return retObj;
	}

	@Override
	public int size() {
		return ehCache.getSize();
	}

	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (K key : m.keySet()) {
			this.put(key, m.get(key));
		}
	}

	@Override
	public V putIfAbsent(K key, V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V replace(K key, V value) {
		throw new UnsupportedOperationException();
	}
}