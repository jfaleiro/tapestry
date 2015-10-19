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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.function.Consumer;

/**
 * Generic support for changes and modifications.
 * 
 * @author jfaleiro
 *
 * @param <L>
 *            type of listener
 * @param <E>
 *            type of event
 */
public abstract class ChangeSupport<L, E> implements EventListener {

	private final List<L> listeners = Collections
			.synchronizedList(new ArrayList<>());

	private final boolean interruptOnException;

	public ChangeSupport(boolean interruptOnException) {
		this.interruptOnException = interruptOnException;
	}

	public static class IndexHandler implements Handler {
		private final int index;

		public IndexHandler(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

	}

	public synchronized Handler addListener(L listener) {
		listeners.add(listener);
		return new IndexHandler(listeners.size() - 1);
	}

	public synchronized void removeListener(Handler handler) {
		listeners.set(((IndexHandler) handler).getIndex(), null); // XXX leaves
																	// gaps
	}

	public synchronized void notify(final E event) {
		listeners.forEach(new Consumer<L>() {

			@Override
			public void accept(L listener) {
				try {
					apply(listener, event);
				} catch (Throwable e) {
					if (interruptOnException) {
						throw e;
					}
				}
			}

		});
	}

	protected abstract void apply(L listener, E event);

}
