/*  +__^_________,_________,_____,________^-.-------------------,
 *  | |||||||||   `--------'     |          |                   O
 *  `+-------------USMC----------^----------|___________________|
 *    `\_,---------,---------,--------------'
 *      / X MK X /'|       /'
 *     / X MK X /  `\    /'
 *    / X MK X /`-------'
 *   / X MK X /
 *  / X MK X /
 * (________(                @author m.c.kunkel
 *  `------'
*/
package domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Coordinate implements Iterable<String> {
	private String[] size;
	private int[] ints;

	public Coordinate(String... size) {
		this.size = size;
	}

	public Coordinate(int... ints) {
		this.ints = ints;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(size);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (!Arrays.equals(size, other.size))
			return false;
		return true;
	}

	public String[] getStrings() {
		return size;
	}

	public int getSize() {
		return size.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<String> iterator() {
		final List<String> strList = new ArrayList<String>();
		for (int i = 0; i < size.length; i++) {
			strList.add(size[i]);
		}
		return strList.iterator();
	}

}
