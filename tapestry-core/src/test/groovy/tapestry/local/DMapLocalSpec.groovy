/*
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
package tapestry.local

import spock.lang.*
import tapestry.BoundListener
import tapestry.EntryEvent


@Unroll
class DMapLocalSpec extends Specification {

	def dmap = new DMapLocal()

	def "does not accept a null key"() {
		when:
		dmap.put(null, "value")

		then:
		thrown(NullPointerException)
	}

	def "add and retrieve one item"() {
		when:
		dmap.put("A", 10)

		then:
		!dmap.empty
		dmap.size() == 1
		dmap.get("A") == 10
	}

	def "add and remove one item"() {
		when:
		dmap.put("A", 10)
		dmap.remove("A")

		then:
		// dmap.empty
		dmap.size() == 0
		dmap.get("A") == null
	}
	
	def "put modifications are published to all bound subscribers once"() {
		def s1 = Mock(BoundListener)
		def s2 = Mock(BoundListener)
		
		dmap.addBoundListener(s1)
		dmap.addBoundListener(s2)
		
		when:
		dmap.put("B", 2)
		
		then:
		1 * s1.onBoundEvent(_ as EntryEvent)
		1 * s2.onBoundEvent(_ as EntryEvent)
	}
	
	// Only @Shared and static variables can be accessed from within a where: block. 
	@Shared def m = new DMapLocal()
	
	def "put modifications should return the previous value or null if a key"() {
		expect:
		a == m.put(b, c)
		
		where:
		a	|	b	|	c
		null|	"a"	|	1
		1	|	"a"	|	2
		null|	"b"	|	1
	}

	def "maximum of 2 numbers"() {
		expect:
		Math.max(a, b) == c

		where:
		a << [3, 5, 9]
		b << [7, 4, 9]
		c << [7, 5, 9]
	}
}