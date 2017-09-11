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
	private double mean;
	private double sigmaRange;
	private double sigma;

	public Cuts(Coordinate topology, double mean, double sigmaRange) {
		this.topology = topology;
		this.mean = mean;
		this.sigmaRange = sigmaRange;
	}

	public Cuts(Coordinate topology, double mean, double sigma, double sigmaRange) {
		this.topology = topology;
		this.mean = mean;
		this.sigmaRange = sigmaRange;
		this.sigma = sigma;
	}

	public Coordinate getTopology() {
		return topology;
	}

	public double getMean() {
		return mean;
	}

	public double getSigmaRange() {
		return sigmaRange;
	}

	public double getSigma() {
		return this.sigma;
	}
}
