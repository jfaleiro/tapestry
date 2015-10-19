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

import org.jgroups.JChannel;

import tapestry.Factory;

/**
 * Factory for JGroups backed DMaps.
 * 
 * @author jfaleiro
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public class DMapJgroupsFactory<K extends Serializable, V extends Serializable>
		implements Factory<DMapJgroups<K, V>, Exception> {

	final JChannel channel;

	final String cluster;

	public DMapJgroupsFactory(JChannel channel, String cluster) {
		this.channel = channel;
		this.cluster = cluster;
	}

	@Override
	public DMapJgroups<K, V> create() throws Exception {
		return new DMapJgroups<K, V>(this.channel, this.cluster);
	}
}
