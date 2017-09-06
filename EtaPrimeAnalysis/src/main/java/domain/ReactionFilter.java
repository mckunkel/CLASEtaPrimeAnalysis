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
import java.util.Iterator;
import java.util.List;

import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.clas.physics.Particle;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class ReactionFilter {

	private List<Particle> particleList = null;
	private List<String> reactionList = null;

	public ReactionFilter() {

	}

	private List<Particle> compareList() {

		List<String> reactionListCopy = getReactionCopy();
		List<Particle> particleListCopy = getParticleCopy();

		List<Particle> aList = new ArrayList<>();
		if (reactionList.size() <= particleList.size()) {

			Iterator<Particle> particleIterator = particleListCopy.iterator();
			while (particleIterator.hasNext()) {
				Iterator<String> stringIterator = reactionListCopy.iterator();
				Particle particle = particleIterator.next();
				while (stringIterator.hasNext()) {
					String string = stringIterator.next();
					if (PDGDatabase.getParticleByName(string).equals(PDGDatabase.getParticleById(particle.pid()))) {
						aList.add(particle);
						particleIterator.remove();
						stringIterator.remove();
						break;
					}
				}
			}
		}
		if (aList.size() == reactionList.size()) {
			// printList(aList);
			return aList;
		}

		return new ArrayList<>();
	}

	private void printList(List<Particle> aList) {
		System.out.println("######################");
		for (Particle particle : aList) {
			System.out.println(particle);
		}
	}

	private List<String> getReactionCopy() {
		List<String> reactionListCopy = new ArrayList<>();
		reactionListCopy.addAll(reactionList);

		return reactionListCopy;
	}

	private List<Particle> getParticleCopy() {
		List<Particle> aListCopy = new ArrayList<>();
		aListCopy.addAll(particleList);

		return aListCopy;
	}

	public void setParticleList(List<Particle> particleList) {
		this.particleList = particleList;
	}

	public void setReactionList(List<String> reactionList) {
		this.reactionList = reactionList;
	}

	public Multimap<Integer, Particle> reactionMap() {
		Multimap<Integer, Particle> aMap = ArrayListMultimap.create();

		// Map<Integer, Particle> aMap = new HashMap<>();
		List<Particle> aList = runComparision();
		for (Particle particle : aList) {
			aMap.put(particle.pid(), particle);
		}

		return aMap;

	}

	public List<Particle> reactionList() {

		return runComparision();

	}

	public List<Particle> runComparision() {
		// return compareList();
		try {
			return compareList();
		} catch (Exception e) {
			System.err.println("Particle List or Reaction List not set");
			return new ArrayList<>();
		}
	}

}
