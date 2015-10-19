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

import tapestry.DMap;
import tapestry.Factory;

/**
 * Factory of DMapLocal.
 * 
 * @author jfaleiro
 *
 * @param <K>
 *            type of key
 * @param <V>
 *            type of value
 */
public class DMapLocalFactory<K, V> implements
		Factory<DMap<K, V>, RuntimeException> {

	@Override
	public DMap<K, V> create() {
		return new DMapLocal<K, V>();
	}

}
