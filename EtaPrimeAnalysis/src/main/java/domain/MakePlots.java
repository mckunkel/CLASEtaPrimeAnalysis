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
import java.util.LinkedHashMap;
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
	private String dataType = null;
	private Map<String, Number> sortMap = null;

	private List<String> kludgeMap = null;

	public MakePlots(List<Particle> aList, String dataType) {
		this.particles = aList;
		this.dataType = dataType;

	}

	public void init() {
		this.mainService = ServiceManager.getSession();
		this.aMap = new HashMap<>();
		this.sortMap = new LinkedHashMap<>();
		setKludgeMap();

		if (this.particles.size() == 0) {
			fillEmptyTree();
			sortAndFillTree();
			System.out.println(" ?????   " + this.mainService.getTree().getDataVector("recMxPEm1", "").size());

		}
		if (this.particles.size() > 0) {

			seperateList(this.mainService.getMissingMassList());
			seperateList(this.mainService.getInvariantList());

			fillTree(this.mainService.getMissingMassList(), "Mx");
			fillTree(this.mainService.getInvariantList(), "M");

			sortAndFillTree();
		}
	}

	private void setKludgeMap() {
		if (dataType == "gen") {
			kludgeMap = this.mainService.getGenList();
		} else
			kludgeMap = this.mainService.getRecList();
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

	private void fillTree(List<Coordinate> coordinates, String topologyType) {
		for (Coordinate aCoordinate : coordinates) {
			String branchName = "";

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
				if (tempList.size() > 1) {
					for (int i = 0; i < tempList.size(); i++) {
						branchName = dataType + topologyType + coordinateToString(aCoordinate)
								+ Integer.toString(i + 1);

						Particle sum = beamTargetParticle();
						sum.combine(tempList.get(i), -1);
						// System.out.println(sum.mass() + " blah method " +
						// branchName);
						sortMap.put(branchName, sum.mass());
					}
				} else {
					branchName = dataType + topologyType + coordinateToString(aCoordinate);
					Particle sum = beamTargetParticle();
					sum.combine(tempList.get(0), -1);
					sortMap.put(branchName, sum.mass());
				}

			} else {
				if (tempList.size() > 1) {
					for (int i = 0; i < tempList.size(); i++) {
						branchName = dataType + topologyType + coordinateToString(aCoordinate)
								+ Integer.toString(i + 1);

						Particle sum = new Particle();
						sum.copy(tempList.get(i));
						// System.out.println(sum.mass() + " blah method " +
						// branchName);
						sortMap.put(branchName, sum.mass());
					}
				} else {
					branchName = dataType + topologyType + coordinateToString(aCoordinate);
					sortMap.put(branchName, tempList.get(0).mass());
				}
			}
		}
	}

	private void fillEmptyTree() {
		for (String string : kludgeMap) {
			sortMap.put(string, -10000.0);
		}
	}

	private void sortAndFillTree() {
		// System.out.println("####################################");
		DataPoint dPoint = new DataPoint();
		for (Map.Entry<String, Number> entry : sortMap.entrySet()) {
			String key = entry.getKey();
			Number value = entry.getValue();
			// System.out.println(key + " " + value + " from sortMap");
			for (String str : this.mainService.getTree().getListOfBranches()) {
				// System.out.println(str + " is a branch");
				if (str.equals(key)) {
					System.out.println(str + " filled with value " + (Double) value);

					dPoint = dPoint.addDataPoint(new DataPoint((Double) value));
				}
			}
		}

		this.mainService.getTree().addToTree(dPoint);

	}

	private Particle beamTargetParticle() {
		Particle sum = new Particle();
		sum.copy(EventList.beamParticle);
		sum.combine(EventList.targetParticle, +1);
		return sum;
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

	private String coordinateToString(Coordinate aCoordinate) {
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

}
