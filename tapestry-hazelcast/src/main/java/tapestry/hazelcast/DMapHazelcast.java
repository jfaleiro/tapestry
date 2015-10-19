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
package tapestry.hazelcast;

import tapestry.BoundListener;
import tapestry.ChangeSupport;
import tapestry.ConcurrentMapDelegateConstrainable;
import tapestry.DMap;
import tapestry.EntryEvent;
import tapestry.EntryEvent.Type;
import tapestry.Handler;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapClearedListener;

/**
 * Hazelcast backed DMap.
 * 
 * @author jfaleiro
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public class DMapHazelcast<K, V> extends ConcurrentMapDelegateConstrainable<K, V> implements
		DMap<K, V> {

	private final ChangeSupport<BoundListener<EntryEvent<K, V>>, EntryEvent<K, V>> boundSupport = new ChangeSupport<BoundListener<EntryEvent<K, V>>, EntryEvent<K, V>>(
			false) {
		@Override
		protected void apply(BoundListener<EntryEvent<K, V>> listener,
				EntryEvent<K, V> event) {
			listener.onBoundEvent(event);
		}
	};

	private final EntryEvent<K, V> toDmapEvent(Type type,
			com.hazelcast.core.EntryEvent<K, V> event) {
		return new EntryEvent<K, V>(this, type, event.getKey(),
				event.getValue(), event.getOldValue());
	}

	public DMapHazelcast(HazelcastInstance instance, String cluster)
			throws Exception {
		super(instance.getMap(cluster));
		final IMap<K, V> map = (IMap<K, V>) getProxy();
		map.addEntryListener(new EntryAddedListener<K, V>() {
			@Override
			public void entryAdded(com.hazelcast.core.EntryEvent<K, V> event) {
				boundSupport.notify(toDmapEvent(Type.CREATED, event));
			}
		}, true);
		map.addEntryListener(new EntryUpdatedListener<K, V>() {
			@Override
			public void entryUpdated(com.hazelcast.core.EntryEvent<K, V> event) {
				boundSupport.notify(toDmapEvent(Type.UPDATED, event));
			}
		}, true);
		map.addEntryListener(new EntryRemovedListener<K, V>() {
			@Override
			public void entryRemoved(com.hazelcast.core.EntryEvent<K, V> event) {
				boundSupport.notify(toDmapEvent(Type.REMOVED, event));
			}
		}, true);
		map.addEntryListener(new MapClearedListener() {
			@Override
			public void mapCleared(MapEvent event) {
				boundSupport.notify(new EntryEvent<K, V>(this, Type.CLEARED,
						null, null, null));
			}
		}, true);
	}

	@Override
	public Handler addBoundListener(BoundListener<EntryEvent<K, V>> listener) {
		return boundSupport.addListener(listener);
	}

	@Override
	public void removeBoundListener(Handler handler) {
		boundSupport.removeListener(handler);
	}

}
