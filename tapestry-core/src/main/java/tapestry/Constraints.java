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

import com.google.common.base.Preconditions;

/**
 * Constraint utilities.
 * 
 * @author jfaleiro
 *
 */
public class Constraints {

	/**
	 * Constraint exception when flag is <code>true</code>.
	 * 
	 * @param flag
	 *            boolean flag to be checked
	 * @param message
	 *            description of constraint
	 */
	public static final void constrain(boolean flag, String message) {
		try {
			Preconditions.checkArgument(flag, message);
		} catch (IllegalArgumentException e) {
			throw new ConstraintException(e);
		}
	}

}
