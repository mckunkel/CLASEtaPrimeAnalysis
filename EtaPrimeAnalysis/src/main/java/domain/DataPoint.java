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

public class DataPoint implements Iterable<Double> {
	private Double[] size;

	public DataPoint(Double... size) {
		this.size = size;
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
		DataPoint other = (DataPoint) obj;
		if (!Arrays.equals(size, other.size))
			return false;
		return true;
	}

	public Double[] getDoubles() {
		return size;
	}

	public int getDoubleSize() {
		return size.length;
	}

	public DataPoint addDataPoint(DataPoint dp) {
		Double[] dList = new Double[this.getDoubleSize() + dp.getDoubleSize()];
		for (int i = 0; i < this.getDoubleSize(); i++) {
			dList[i] = size[i];
		}
		for (int i = 0; i < dp.getDoubleSize(); i++) {
			dList[this.getDoubleSize() + i] = dp.getDoubles()[i];
		}
		// for (Double double1 : dList) {
		// System.out.println(double1);
		// }
		return new DataPoint(dList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Double> iterator() {
		final List<Double> doubleList = new ArrayList<Double>();
		for (int i = 0; i < size.length; i++) {
			doubleList.add(size[i]);
		}
		return doubleList.iterator();
	}

	@Override
	public String toString() {
		return "DataPoint [size=" + Arrays.toString(size) + "]";
	}

	public static void main(String[] args) {
		DataPoint d8 = new DataPoint(10.2, 11.2);

		for (Double double1 : d8) {
			System.out.println(double1);
		}
		d8 = d8.addDataPoint(new DataPoint(10.0, 12.1));
		for (Double double1 : d8) {
			System.out.println(double1);
		}
		DataPoint dPoint = new DataPoint();

		DataPoint[] dataPoints = new DataPoint[10];
		for (int i = 0; i < dataPoints.length; i++) {
			dPoint.toString();
			dPoint = dPoint.addDataPoint(new DataPoint((double) i));
			dPoint.toString();

			// dataPoints[i].toString();
		}
		dPoint.toString();

		for (Double dataPoint : dPoint) {
			System.out.println(dataPoint);
		}

	}

}
