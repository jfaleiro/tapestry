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
package tapestry.jgroups;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.View;
import org.jgroups.blocks.ReplicatedHashMap;
import org.jgroups.blocks.ReplicatedHashMap.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tapestry.BoundListener;
import tapestry.ChangeSupport;
import tapestry.ConcurrentMapDelegateConstrainable;
import tapestry.DMap;
import tapestry.EntryEvent;
import tapestry.EntryEvent.Type;
import tapestry.Handler;

/**
 * JGroups backed DMap.
 * 
 * @author jfaleiro
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public class DMapJgroups<K extends Serializable, V extends Serializable>
		extends ConcurrentMapDelegateConstrainable<K, V> implements DMap<K, V> {

	private static final Logger LOG = LoggerFactory
			.getLogger(DMapJgroups.class);

	private final ChangeSupport<BoundListener<EntryEvent<K, V>>, EntryEvent<K, V>> boundSupport = new ChangeSupport<BoundListener<EntryEvent<K, V>>, EntryEvent<K, V>>(
			false) {
		@Override
		protected void apply(BoundListener<EntryEvent<K, V>> listener,
				EntryEvent<K, V> event) {
			listener.onBoundEvent(event);
		}
	};

	private final Channel channel;

	public DMapJgroups(Channel channel, String cluster) throws Exception {
		super(new ReplicatedHashMap<K, V>(channel));
		this.channel = channel;
		this.channel.connect(cluster);
		final ReplicatedHashMap<K, V> map = (ReplicatedHashMap<K, V>) getProxy();
		map.setBlockingUpdates(true);
		map.addNotifier(new Notification<K, V>() {

			@Override
			public void entrySet(K key, V value) {
				// XXX JGroups does not allow for notification of either an
				// update or create; at the time this notification is received
				// the state of the underlying map already changed.
				boundSupport.notify(new EntryEvent<K, V>(this,
						Type.CREATED_OR_UPDATED, key, value, null));
			}

			@Override
			public void entryRemoved(K key) {
				boundSupport.notify(new EntryEvent<K, V>(this, Type.REMOVED,
						key, null, null));
			}

			@Override
			public void contentsSet(Map<K, V> new_entries) {
				new_entries.forEach(new BiConsumer<K, V>() {
					@Override
					public void accept(K t, V u) {
						entrySet(t, u);
					}
				});
			}

			@Override
			public void contentsCleared() {
				boundSupport.notify(new EntryEvent<>(this, Type.CLEARED, null,
						null, null));
			}

			@Override
			public void viewChange(View view, List<Address> mbrs_joined,
					List<Address> mbrs_left) {
				LOG.info("inbound view change {}->{}", view, mbrs_joined);
			}

		});
	}

	public void close() {
		channel.close();
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
