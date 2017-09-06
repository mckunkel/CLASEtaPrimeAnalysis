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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.clas.physics.Particle;

import domain.DalitzEvent.EventList;
import services.MainService;
import services.ServiceManager;

public class MakePlots {

	private MainService mainService = null;
	private List<Particle> particles = null;
	private Map<String, List<Particle>> aMap = null;
	private String dataType = null;;

	public MakePlots(List<Particle> aList, String dataType) {
		this.particles = aList;
		this.dataType = dataType;

	}

	public void init() {
		if (this.particles.size() > 0) {

			this.mainService = ServiceManager.getSession();
			this.aMap = new HashMap<>();
			seperateList(this.mainService.getMissingMassList());
			seperateList(this.mainService.getInvariantList());

			if (getFlag() == false) {
				setFlag();
				setHists();
			}
			createHists(this.mainService.getMissingMassList(), "Mx");
			createHists(this.mainService.getInvariantList(), "M");
		}
	}

	private boolean getFlag() {
		if (dataType == "gen") {
			return this.mainService.getMCFlag();
		} else
			return this.mainService.getRECFlag();
	}

	private void setFlag() {
		if (dataType == "gen") {
			this.mainService.setMCFlag(true);
		} else
			this.mainService.setRECFlag(true);
	}

	private void seperateList(List<Coordinate> coordinates) {
		for (Coordinate aCoordinate : coordinates) {
			for (String string : aCoordinate) {
				for (Particle particle : particles) {
					if (PDGDatabase.getParticleByName(string).equals(PDGDatabase.getParticleById(particle.pid()))) {
						if (this.aMap.containsKey(string)) {
							if (!this.aMap.get(string).contains(particle)) {
								this.aMap.get(string).add(particle);
							}
						} else {
							List<Particle> aList = new ArrayList<>();
							aList.add(particle);
							this.aMap.put(string, aList);
						}
					}
				}
			}
		}
	}

	private void setHists() {
		Map<Coordinate, Integer> aMap = new HashMap<>();
		for (Coordinate aCoordinate : this.mainService.getMissingMassList()) {
			int totalSize = 0;
			for (String string : aCoordinate) {
				totalSize = totalSize + this.aMap.get(string).size();
			}
			int histsNeeded = totalSize - aCoordinate.getSize() + 1;
			if (!aMap.containsKey(aCoordinate)) {
				aMap.put(aCoordinate, histsNeeded);
			}
		}
		for (Coordinate aCoordinate : this.mainService.getInvariantList()) {
			int totalSize = 0;
			for (String string : aCoordinate) {
				totalSize = totalSize + this.aMap.get(string).size();
			}
			int histsNeeded = totalSize - aCoordinate.getSize() + 1;
			if (!aMap.containsKey(aCoordinate)) {
				aMap.put(aCoordinate, histsNeeded);
			}
		}
		this.mainService.setHists(aMap, dataType);

	}

	private void createHists(List<Coordinate> coordinates, String topologyType) {

		for (Coordinate aCoordinate : coordinates) {
			List<List<Particle>> aList = new ArrayList<>();
			for (String string : aCoordinate) {
				aList.add(aMap.get(string));
			}
			List<Particle> tempList = new ArrayList<>();
			Particle tempPart = new Particle(22, 0.0, 0.0, 0.0);
			tempList.add(tempPart);
			for (List<Particle> ic : aList) {
				tempList = MultArray(tempList, ic);
			}
			if (topologyType.equals("Mx")) {
				for (int i = 0; i < tempList.size(); i++) {
					Particle sum = new Particle();
					sum.copy(EventList.beamParticle);
					sum.combine(EventList.targetParticle, +1);
					sum.combine(tempList.get(i), -1);
					// System.out.println(sum.mass() + " blah method");
					this.mainService.getH1Map().get(makeHistogramCoordinate(aCoordinate, dataType + topologyType))
							.get(i).fill(sum.mass());
				}
			} else {
				for (int i = 0; i < tempList.size(); i++) {
					Particle sum = new Particle();
					sum.copy(tempList.get(i));
					// System.out.println(sum.mass() + " blah method");
					this.mainService.getH1Map().get(makeHistogramCoordinate(aCoordinate, dataType + topologyType))
							.get(i).fill(sum.mass());
				}
			}
		}
	}

	private List<Particle> MultArray(List<Particle> aList1, List<Particle> aList2) {
		List<Particle> returnList = new ArrayList<>();
		for (Particle p1 : aList1) {
			// System.out.println(" in p1 " + p1.mass() + " " + p1.p());
			for (Particle p2 : aList2) {
				// System.out.println(" in p2 " + p2.mass() + " " + p2.p());
				Particle tempP = new Particle();
				tempP.copy(p1);
				tempP.combine(p2, +1);

				returnList.add(tempP);
			}
		}
		return returnList;
	}

	private Coordinate makeHistogramCoordinate(Coordinate aCoordinate, String string) {
		int size = aCoordinate.getSize() + 1;
		String[] sb = new String[size];
		sb[0] = string;
		for (int i = 0; i < aCoordinate.getSize(); i++) {
			sb[i + 1] = aCoordinate.getStrings()[i];

		}
		Coordinate coordinate = new Coordinate(sb);

		return coordinate;
	}

}
