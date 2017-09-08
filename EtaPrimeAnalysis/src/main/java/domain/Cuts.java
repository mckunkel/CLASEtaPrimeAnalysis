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

public class Cuts {

	private Coordinate topology = null;
	private int mean;
	private int sigmaRange;

	public Cuts(Coordinate topology, int mean, int sigmaRange) {
		this.topology = topology;
		this.mean = mean;
		this.sigmaRange = sigmaRange;
	}

	public Coordinate getTopology() {
		return topology;
	}

	public int getMean() {
		return mean;
	}

	public int getSigmaRange() {
		return sigmaRange;
	}

}
