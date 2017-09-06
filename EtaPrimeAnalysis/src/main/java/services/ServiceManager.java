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
package services;

public enum ServiceManager {
	INSTANCE;
	private static MainService mainService = null;

	private static void getService() {
		mainService = new MainServiceImpl();
	}

	public static MainService getSession() {
		if (mainService == null) {
			getService();
		}
		return mainService;
	}
}
