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
import java.util.List;

import org.jlab.clas.physics.Particle;

public class ReactionFilter {

	private List<Particle> particleList = null;
	private List<String> reactionList = null;

	public ReactionFilter() {

	}

	public ReactionFilter(List<Particle> particleList, List<String> reactionList) {
		this.particleList = particleList;
		this.reactionList = reactionList;
		compareList();

	}

	private List<Particle> compareList() {
		List<Particle> aList = new ArrayList<>();
		System.out.println("######################");

		for (Particle particle : particleList) {
			System.out.println(particle.toLundString());

		}

		return aList;

	}

	public void setParticleList(List<Particle> particleList) {
		this.particleList = particleList;
	}

	public void setReactionList(List<String> reactionList) {
		this.reactionList = reactionList;
	}

	public void runComparision() {
		try {
			compareList();
		} catch (Exception e) {
			System.err.println("Particle List or Reaction List not set");
			// System.exit(1);
		}
		// if (this.particleList == null || this.reactionList == null) {
		// System.err.println("Particle List or Reaction List not set");
		// System.exit(1);
		// }

	}

}
