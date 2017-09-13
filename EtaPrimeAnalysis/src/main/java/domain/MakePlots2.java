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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.clas.physics.Particle;

import domain.DalitzEvent.EventList;
import services.MainService;
import services.ServiceManager;

public class MakePlots2 {

	private MainService mainService = null;
	private List<Particle> particles = null;
	private Map<String, List<Particle>> aMap = null;
	private String dataType = null;;
	private Map<Coordinate, List<Particle>> particleMap = null;
	private Map<String, Number> sortMap = null;

	public MakePlots2(List<Particle> aList, String dataType) {
		this.particles = aList;
		this.dataType = dataType;

	}

	public void init() {
		if (this.particles.size() > 0) {

			this.mainService = ServiceManager.getSession();
			this.aMap = new HashMap<>();
			this.particleMap = new HashMap<>();
			this.sortMap = new LinkedHashMap<>();
			seperateList(this.mainService.getMissingMassList());
			seperateList(this.mainService.getInvariantList());

			// printPartMap();

			if (getFlag() == false) {
				setFlag();
				setHists();
			}
			createHists(this.mainService.getMissingMassList(), "Mx");
			createHists(this.mainService.getInvariantList(), "M");
			fillTree(this.mainService.getMissingMassList(), "Mx");
			fillTree(this.mainService.getInvariantList(), "M");

			sortAndFillTree();

			this.particleMap.putAll(createHistMap(this.mainService.getMissingMassList(), "Mx"));
			this.particleMap.putAll(createHistMap(this.mainService.getInvariantList(), "M"));

			// printHistMap(this.particleMap);

			// applyCuts(this.particleMap);
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
			int histsNeeded = totalSize - aCoordinate.getStrSize() + 1;
			if (!aMap.containsKey(aCoordinate)) {
				aMap.put(aCoordinate, histsNeeded);
			}
		}
		for (Coordinate aCoordinate : this.mainService.getInvariantList()) {
			int totalSize = 0;
			for (String string : aCoordinate) {
				totalSize = totalSize + this.aMap.get(string).size();
			}
			int histsNeeded = totalSize - aCoordinate.getStrSize() + 1;
			if (!aMap.containsKey(aCoordinate)) {
				aMap.put(aCoordinate, histsNeeded);
			}
		}
		// this.mainService.setHists(aMap, dataType);

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

	private void sortAndFillTree() {
		System.out.println("####################################");
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

	private Map<Coordinate, List<Particle>> createHistMap(List<Coordinate> coordinates, String topologyType) {
		Map<Coordinate, List<Particle>> fillMap = new HashMap<>();
		for (Coordinate aCoordinate : coordinates) {
			Coordinate histCoordinate = makeHistogramCoordinate(aCoordinate, topologyType);
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
			List<Particle> returnList = new ArrayList<>();

			if (topologyType.equals("Mx")) {
				for (int i = 0; i < tempList.size(); i++) {
					Particle sum = new Particle();
					sum.copy(EventList.beamParticle);
					sum.combine(EventList.targetParticle, +1);
					sum.combine(tempList.get(i), -1);
					returnList.add(sum);
				}
			} else {
				for (int i = 0; i < tempList.size(); i++) {
					Particle sum = new Particle();
					sum.copy(tempList.get(i));
					returnList.add(sum);
				}
			}
			fillMap.put(histCoordinate, returnList);
		}
		return fillMap;
	}

	private void printPartMap() {
		// System.out.println("############################");
		for (Map.Entry<String, List<Particle>> entry : aMap.entrySet()) {
			String key = entry.getKey();
			List<Particle> value = entry.getValue();
			System.out.println(key + "  " + value.size());
			for (Particle particle : value) {
				System.out.println(particle.mass() + "    " + particle.p() + "      " + value.indexOf(particle));
			}
		}
	}

	private void printHistMap(Map<Coordinate, List<Particle>> aMap) {
		System.out.println("############################");
		for (Map.Entry<Coordinate, List<Particle>> entry : aMap.entrySet()) {
			Coordinate key = entry.getKey();
			List<Particle> value = entry.getValue();
			System.out.println(coordinateToString(key) + "  " + value.size());
			for (Particle particle : value) {
				System.out.println(particle.mass() + "      " + value.indexOf(particle));
			}
		}
	}

	private void applyCuts(Map<Coordinate, List<Particle>> aMap) {
		Map<Coordinate, List<Pair<Particle, Integer>>> passedCutMap = new HashMap<>();
		Map<Coordinate, List<Pair<Particle, Integer>>> failedCutMap = new HashMap<>();
		// first fill passedCutMap with all values from aMap. Then we will
		// remove then and place in failedCutMap during routine
		for (Map.Entry<Coordinate, List<Particle>> entry : aMap.entrySet()) {
			Coordinate key = entry.getKey();
			List<Particle> value = entry.getValue();
			List<Pair<Particle, Integer>> passedParticles = new ArrayList<>();
			for (Particle particle : value) {
				passedParticles.add(Pair.of(particle, value.indexOf(particle)));
			}
			passedCutMap.put(key, passedParticles);
		}

		for (Map.Entry<Coordinate, List<Particle>> entry : aMap.entrySet()) {
			Coordinate key = entry.getKey();
			List<Particle> value = entry.getValue();

			List<Pair<Particle, Integer>> passedParticles = new ArrayList<>();
			List<Pair<Particle, Integer>> failedParticles = new ArrayList<>();
			List<Cuts> cutsCopy = new ArrayList<>();
			cutsCopy.addAll(this.mainService.getcutList());
			Iterator<Cuts> cutsIterator = cutsCopy.iterator();
			while (cutsIterator.hasNext()) {
				Cuts cut = cutsIterator.next();
				if (cut.getTopology().equals(key)) {

				}
			}
			for (Cuts cut : this.mainService.getcutList()) {
				if (cut.getTopology().equals(key)) {
					for (Particle particle : value) {
						if (cut.isOneSides()) {
							if (cut.getSide() == -1) {
								if (particle.mass() < cut.getMean()) {
									passedParticles.add(Pair.of(particle, value.indexOf(particle)));
								} else {
									failedParticles.add(Pair.of(particle, value.indexOf(particle)));
								}
							} else {
								if (particle.mass() > cut.getMean()) {
									passedParticles.add(Pair.of(particle, value.indexOf(particle)));
								} else {
									failedParticles.add(Pair.of(particle, value.indexOf(particle)));
								}
							}
						} else {
							if (Math.abs(particle.mass() - cut.getMean()) < cut.getSigmaRange() * cut.getSigma()) {
								passedParticles.add(Pair.of(particle, value.indexOf(particle)));
							} else {
								failedParticles.add(Pair.of(particle, value.indexOf(particle)));

							}
						}
					}
				}
			}
			passedCutMap.put(key, passedParticles);
			failedCutMap.put(key, failedParticles);

		}

	}

	private CutParms getCuts(Coordinate key) {
		for (Cuts cut : this.mainService.getcutList()) {
			if (cut.getTopology().equals(key)) {
				return new CutParms(cut.getMean(), cut.getSigma(), cut.getSigmaRange());

			}
		}
		return null;
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

	private Coordinate makeHistogramCoordinate(Coordinate aCoordinate, String string) {
		int size = aCoordinate.getStrSize() + 1;
		String[] sb = new String[size];
		sb[0] = string;
		for (int i = 0; i < aCoordinate.getStrSize(); i++) {
			sb[i + 1] = aCoordinate.getStrings()[i];

		}
		Coordinate coordinate = new Coordinate(sb);

		return coordinate;
	}

	private static class CutParms {
		private double mean;
		private double sigma;
		private double sigmaRange;

		private CutParms(double mean, double sigma, double sigmaRange) {
			this.mean = mean;
			this.sigma = sigma;
			this.sigmaRange = sigmaRange;
		}

		public double getMean() {
			return mean;
		}

		public double getSigma() {
			return sigma;
		}

		public double getSigmaRange() {
			return sigmaRange;
		}

	}

}
