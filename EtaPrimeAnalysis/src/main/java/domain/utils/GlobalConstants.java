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
package domain.utils;

import domain.Coordinate;

public class GlobalConstants {

	private GlobalConstants() {

	}

	public static int SCREENWIDTH = 1200;
	public static int SCREENHEIGHT = 800;

	public static String coordinateToString(Coordinate aCoordinate) {
		String sb = new String();
		for (int i = 0; i < aCoordinate.getStrSize(); i++) {
			sb += aCoordinate.getStrings()[i];

		}
		String my_new_str = sb.replaceAll("p", "P");

		my_new_str = my_new_str.replaceAll("e\\+", "Ep");
		my_new_str = my_new_str.replaceAll("e\\-", "Em");
		my_new_str = my_new_str.replaceAll("pi\\-", "Pim");
		my_new_str = my_new_str.replaceAll("pi\\+", "Pip");
		my_new_str = my_new_str.replaceAll("K\\-", "Km");
		my_new_str = my_new_str.replaceAll("K\\+", "Kp");

		return my_new_str;
	}

	public static String coordinateToString(String aString) {
		aString = aString.replaceAll("p", "P");
		aString = aString.replaceAll("e\\+", "Ep");
		aString = aString.replaceAll("e\\-", "Em");
		aString = aString.replaceAll("pi\\-", "Pim");
		aString = aString.replaceAll("pi\\+", "Pip");
		aString = aString.replaceAll("K\\-", "Km");
		aString = aString.replaceAll("K\\+", "Kp");

		return aString;
	}

}
