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

import java.util.EventObject;

/**
 * Event of DMap modifications in state.
 * 
 * @author jfaleiro
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public class EntryEvent<K, V> extends EventObject {

	private static final long serialVersionUID = 1L;

	public enum Type {
		CREATED, UPDATED, CREATED_OR_UPDATED, REMOVED, CLEARED;
	}

	private final Type type;
	private final K key;
	private final V value;
	private final V previousValue;

	public EntryEvent(Object source, Type type, K key, V value, V previousValue) {
		super(source);
		this.type = type;
		this.key = key;
		this.value = value;
		this.previousValue = previousValue;
	}

	public Type getType() {
		return type;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	public V getPreviousValue() {
		return previousValue;
	}

}
