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
package tapestry;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * DMap delegate to a proxy map.
 * 
 * @author jfaleiro
 *
 * @param <K>
 * @param <V>
 */
public abstract class ConcurrentMapDelegate<K, V> implements ConcurrentMap<K, V> {

	private final ConcurrentMap<K, V> proxy;

	public ConcurrentMapDelegate(ConcurrentMap<K, V> proxy) {
		this.proxy = proxy;
	}

	public ConcurrentMap<K, V> getProxy() {
		return proxy;
	}

	public V putIfAbsent(K key, V value) {
		return proxy.putIfAbsent(key, value);
	}

	public boolean remove(Object key, Object value) {
		return proxy.remove(key, value);
	}

	public int size() {
		return proxy.size();
	}

	public boolean replace(K key, V oldValue, V newValue) {
		return proxy.replace(key, oldValue, newValue);
	}

	public boolean isEmpty() {
		return proxy.isEmpty();
	}

	public boolean containsKey(Object key) {
		return proxy.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return proxy.containsValue(value);
	}

	public V replace(K key, V value) {
		return proxy.replace(key, value);
	}

	public V get(Object key) {
		return proxy.get(key);
	}

	public V put(K key, V value) {
		return proxy.put(key, value);
	}

	public V remove(Object key) {
		return proxy.remove(key);
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		proxy.putAll(m);
	}

	public void clear() {
		proxy.clear();
	}

	public Set<K> keySet() {
		return proxy.keySet();
	}

	public Collection<V> values() {
		return proxy.values();
	}

	public Set<Entry<K, V>> entrySet() {
		return proxy.entrySet();
	}

	public boolean equals(Object o) {
		return proxy.equals(o);
	}

	public int hashCode() {
		return proxy.hashCode();
	}

}
