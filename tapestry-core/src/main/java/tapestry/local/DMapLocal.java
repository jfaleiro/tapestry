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
package tapestry.local;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import tapestry.BoundListener;
import tapestry.ChangeSupport;
import tapestry.ConstraintListener;
import tapestry.DMap;
import tapestry.EntryEvent;
import tapestry.EntryEvent.Type;
import tapestry.Handler;
import tapestry.UncheckedCallable;

public class DMapLocal<K, V> extends ConcurrentHashMap<K, V> implements
		DMap<K, V> {

	private static final long serialVersionUID = 1L;

	private ChangeSupport<ConstraintListener<EntryEvent<K, V>>, EntryEvent<K, V>> constraintSupport = new ChangeSupport<ConstraintListener<EntryEvent<K, V>>, EntryEvent<K, V>>(
			true) {
		@Override
		protected void apply(ConstraintListener<EntryEvent<K, V>> listener,
				EntryEvent<K, V> event) {
			listener.onConstraintEvent(event);
		}
	};

	private ChangeSupport<BoundListener<EntryEvent<K, V>>, EntryEvent<K, V>> boundSupport = new ChangeSupport<BoundListener<EntryEvent<K, V>>, EntryEvent<K, V>>(
			false) {
		@Override
		protected void apply(BoundListener<EntryEvent<K, V>> listener,
				EntryEvent<K, V> event) {
			listener.onBoundEvent(event);
		}
	};

	@Override
	public Handler addBoundListener(BoundListener<EntryEvent<K, V>> listener) {
		return boundSupport.addListener(listener);
	}

	@Override
	public void removeBoundListener(Handler handler) {
		boundSupport.removeListener(handler);
	}

	@Override
	public Handler addConstraintListener(
			ConstraintListener<EntryEvent<K, V>> listener) {
		return constraintSupport.addListener(listener);
	}

	@Override
	public void removeConstraintListener(Handler handler) {
		constraintSupport.removeListener(handler);
	}

	private <R> R changeAndNotify(final EntryEvent<K, V> event,
			final UncheckedCallable<R> function) {
		constraintSupport.notify(event);
		try {
			return function.call();
		} finally {
			boundSupport.notify(event);
		}
	}

	@Override
	public V put(K key, V value) {
		final Type t = this.containsKey(key) ? Type.UPDATED : Type.CREATED;
		return changeAndNotify(
				new EntryEvent<K, V>(this, t, key, value, this.get(key)),
				new UncheckedCallable<V>() {
					@Override
					public V call() {
						return DMapLocal.super.put(key, value);
					}
				});

	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		this.forEach(new BiConsumer<K, V>() {
			@Override
			public void accept(K t, V u) {
				put(t, u);
			}
		});
	}

	@Override
	public V remove(Object k) {
		@SuppressWarnings("unchecked")
		K key = (K) k;
		return changeAndNotify(new EntryEvent<K, V>(this, Type.REMOVED, key,
				this.get(key), null), new UncheckedCallable<V>() {
			@Override
			public V call() {
				return DMapLocal.super.remove(key);
			}
		});
	}

	@Override
	public void clear() {
		changeAndNotify(new EntryEvent<K, V>(this, Type.CLEARED, null, null,
				null), new UncheckedCallable<V>() {
			@Override
			public V call() {
				DMapLocal.super.clear();
				return null;
			}
		});
	}

	@Override
	public boolean remove(Object k, Object value) {
		@SuppressWarnings("unchecked")
		K key = (K) k;
		return changeAndNotify(new EntryEvent<K, V>(this, Type.REMOVED, key,
				this.get(key), null), new UncheckedCallable<Boolean>() {
			@SuppressWarnings("unchecked")
			@Override
			public Boolean call() {
				return DMapLocal.super.remove(key, (V) value);
			}
		});

	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return changeAndNotify(new EntryEvent<K, V>(this, Type.UPDATED, key,
				this.get(key), null), new UncheckedCallable<Boolean>() {
			@Override
			public Boolean call() {
				return DMapLocal.super.replace(key, oldValue, newValue);
			}
		});
	}

	@Override
	public V replace(K key, V value) {
		return changeAndNotify(new EntryEvent<K, V>(this, Type.UPDATED, key,
				this.get(key), null), new UncheckedCallable<V>() {
			@Override
			public V call() {
				return DMapLocal.super.replace(key, value);
			}
		});
	}

}
