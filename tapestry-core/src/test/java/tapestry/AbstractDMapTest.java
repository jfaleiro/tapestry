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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static tapestry.Constraints.constrain;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * Abstract DMap tests.
 * 
 * @author jfaleiro
 *
 */
public abstract class AbstractDMapTest {

	@Test
	public final void testPut() throws Exception {
		final DMap<String, Integer> m = createDMap("test-put");
		try {
			m.addConstraintListener(new ConstraintListener<EntryEvent<String, Integer>>() {
				@Override
				public void onConstraintEvent(EntryEvent<String, Integer> event) {
					switch (event.getType()) {
					case CREATED:
					case UPDATED:
						constrain(event.getValue() >= 0,
								"cannot create/update negative");
						break;
					default:
						break;
					}
				}
			});

			final AtomicInteger i = new AtomicInteger(0);

			m.addBoundListener(new BoundListener<EntryEvent<String, Integer>>() {
				@Override
				public void onBoundEvent(EntryEvent<String, Integer> event) {
					i.incrementAndGet();
				}
			});

			m.put("A", 0);
			assertTrue(m.containsKey("A"));
			assertEquals(Integer.valueOf(0), m.get("A"));
			assertEquals(1, i.get());

			try {
				m.put("A", -1);
				fail();
			} catch (ConstraintException e) {
				assertTrue(m.containsKey("A"));
				assertEquals(Integer.valueOf(0), m.get("A"));
				assertEquals(1, i.get());
			}

			try {
				m.put("B", -1);
				fail();
			} catch (ConstraintException e) {
				assertFalse(m.containsKey("B"));
				assertEquals(Integer.valueOf(0), m.get("A"));
				assertEquals(1, i.get());
			}

			m.put("C", 1);
			assertTrue(m.containsKey("C"));
			assertEquals(Integer.valueOf(1), m.get("C"));
			assertEquals(2, i.get());
		} finally {
			destroyDMap(m);
		}

	}

	@Test
	public final void testRemove() throws Exception {
		final DMap<String, Integer> m = createDMap("test-remove");
		m.addConstraintListener(new ConstraintListener<EntryEvent<String, Integer>>() {
			@Override
			public void onConstraintEvent(EntryEvent<String, Integer> event) {
				switch (event.getType()) {
				case REMOVED:
					constrain(event.getValue() >= 0, "cannot remove positive");
					break;
				default:
					break;
				}
			}
		});

		final AtomicInteger insertions = new AtomicInteger(0);
		final AtomicInteger removals = new AtomicInteger(0);

		m.addBoundListener(new BoundListener<EntryEvent<String, Integer>>() {
			@Override
			public void onBoundEvent(EntryEvent<String, Integer> event) {
				System.out.println(event.getType());
				switch (event.getType()) {
				case CREATED:
				case CREATED_OR_UPDATED: // JGroups
					insertions.incrementAndGet();
					break;
				case REMOVED:
					removals.incrementAndGet();
					break;
				default:
					break;

				}
			}
		});

		m.put("A", -1);
		m.put("B", 0);
		m.put("C", 1);

		assertEquals(3, insertions.get());

		m.remove("C");
		assertEquals(1, removals.get());
		m.remove("B");
		assertEquals(2, removals.get());
		try {
			m.remove("A");
			fail();
		} catch (ConstraintException e) {
			assertEquals(2, removals.get());
		}

	}

	protected abstract DMap<String, Integer> createDMap(String name)
			throws Exception;

	protected abstract void destroyDMap(DMap<String, Integer> map)
			throws Exception;

}
