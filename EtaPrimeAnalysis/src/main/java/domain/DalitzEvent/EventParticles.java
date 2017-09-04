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
package domain.DalitzEvent;

import java.util.ArrayList;
import java.util.List;

import org.jlab.clas.physics.Particle;

public class EventParticles {

	private List<Particle> eventList = null;

	public EventParticles() {
		this.eventList = new ArrayList<>();
		setList();
	}

	private void setList() {
		this.eventList.add(EventList.beamParticle);
		this.eventList.add(EventList.targetParticle);
		this.eventList.add(EventList.recoilProton);
		this.eventList.add(EventList.firstElectron);
		this.eventList.add(EventList.secondElectron);
		this.eventList.add(EventList.dalitzPositron);

	}

	public List<Particle> getList() {
		return this.eventList;
	}

	public void resetList() {
		setList();
	}

	public static void main(String[] args) {
		EventParticles eventParticles = new EventParticles();

		List<Particle> thisList = eventParticles.getList();

		// thisList.set(2, thisList.get(2).setVector(nvec, nvert);)

	}

}
