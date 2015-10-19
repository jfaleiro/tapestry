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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import tapestry.BoundListener;
import tapestry.ChangeSupport;
import tapestry.ConcurrentMapDelegateConstrainable;
import tapestry.DMap;
import tapestry.EntryEvent;
import tapestry.EntryEvent.Type;
import tapestry.Handler;

/**
 * EHCache backed DMap.
 * 
 * @author jfaleiro
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public class DMapEhcache<K, V> extends ConcurrentMapDelegateConstrainable<K, V>
		implements DMap<K, V> {

	private final ChangeSupport<BoundListener<EntryEvent<K, V>>, EntryEvent<K, V>> boundSupport = new ChangeSupport<BoundListener<EntryEvent<K, V>>, EntryEvent<K, V>>(
			false) {
		@Override
		protected void apply(BoundListener<EntryEvent<K, V>> listener,
				EntryEvent<K, V> event) {
			listener.onBoundEvent(event);
		}
	};

	public DMapEhcache(Cache cache, String cluster) throws Exception {
		super(new ConcurrentMapAdapter<K, V>(cache));
		cache.getCacheEventNotificationService().registerListener(
				new CacheEventListener() {

					@Override
					public void notifyRemoveAll(Ehcache cache) {
						boundSupport.notify(new EntryEvent<K, V>(this,
								Type.CLEARED, null, null, null));
					}

					@SuppressWarnings("unchecked")
					@Override
					public void notifyElementUpdated(Ehcache cache,
							Element element) throws CacheException {
						boundSupport.notify(new EntryEvent<K, V>(this,
								Type.UPDATED, (K) element.getObjectKey(),
								(V) element.getObjectValue(), null));
					}

					@SuppressWarnings("unchecked")
					@Override
					public void notifyElementRemoved(Ehcache cache,
							Element element) throws CacheException {
						boundSupport.notify(new EntryEvent<K, V>(this,
								Type.REMOVED, (K) element.getObjectKey(),
								(V) element.getObjectValue(), null));
					}

					@SuppressWarnings("unchecked")
					@Override
					public void notifyElementPut(Ehcache cache, Element element)
							throws CacheException {
						boundSupport.notify(new EntryEvent<K, V>(this,
								Type.CREATED, (K) element.getObjectKey(),
								(V) element.getObjectValue(), null));
					}

					@SuppressWarnings("unchecked")
					@Override
					public void notifyElementExpired(Ehcache cache,
							Element element) {
						boundSupport.notify(new EntryEvent<K, V>(this,
								Type.REMOVED, (K) element.getObjectKey(),
								(V) element.getObjectValue(), null));
					}

					@SuppressWarnings("unchecked")
					@Override
					public void notifyElementEvicted(Ehcache cache,
							Element element) {
						boundSupport.notify(new EntryEvent<K, V>(this,
								Type.REMOVED, (K) element.getObjectKey(),
								(V) element.getObjectValue(), null));
					}

					@Override
					public void dispose() {
						// ND
					}

					@Override
					public Object clone() throws CloneNotSupportedException {
						return super.clone();
					}

				});
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
