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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jlab.groot.data.H1F;

import domain.Analysis;
import domain.Coordinate;
import domain.Cuts;
import domain.TreeVector;

public class MainServiceImpl implements MainService {

	private boolean mcFlag = false;
	private boolean recFlag = false;

	private List<String> reactionList = null;
	private List<String> skimService = null;
	// placers...this is a kuldge..will fix later

	private List<String> genList = null;
	private List<String> recList = null;

	private Map<Coordinate, List<H1F>> plotsByCoordinate = null;
	private List<Coordinate> invariantMassList = null;
	private List<Coordinate> missingMassList = null;
	private Map<Coordinate, Integer> neededHists = null;
	private List<Cuts> cutList = null;

	private TreeVector treeVector = null;

	public MainServiceImpl() {
		this.reactionList = new ArrayList<>();
		this.skimService = new ArrayList<>();

		this.genList = new ArrayList<>();
		this.recList = new ArrayList<>();

		this.plotsByCoordinate = new HashMap<Coordinate, List<H1F>>();
		this.invariantMassList = new ArrayList<>();
		this.missingMassList = new ArrayList<>();
		this.neededHists = new HashMap<>();
		this.cutList = new ArrayList<>();

		this.treeVector = new TreeVector("T");

	}

	private void createTreeBranches(String dataType) {
		for (Coordinate aCoordinate : invariantMassList) {
			String branchName = "";
			if (this.neededHists.get(aCoordinate) > 1) {
				for (int i = 0; i < this.neededHists.get(aCoordinate); i++) {
					branchName = dataType + "M" + coordinateToString(aCoordinate) + Integer.toString(i + 1);
					treeVector.addBranch(branchName, "", "");
				}
			} else {
				branchName = dataType + "M" + coordinateToString(aCoordinate);
				treeVector.addBranch(branchName, "", "");
			}
		}
		for (Coordinate aCoordinate : missingMassList) {
			String branchName = "";
			if (this.neededHists.get(aCoordinate) > 1) {
				for (int i = 0; i < this.neededHists.get(aCoordinate); i++) {
					branchName = dataType + "Mx" + coordinateToString(aCoordinate) + Integer.toString(i + 1);
					treeVector.addBranch(branchName, "", "");
				}
			} else {
				branchName = dataType + "Mx" + coordinateToString(aCoordinate);
				treeVector.addBranch(branchName, "", "");
			}
		}
		// for (String str : treeVector.getListOfBranches()) {
		// System.out.println(str + " is a branch that you set");
		// }
	}

	private void makeHistograms(Map<Coordinate, Integer> aMap, String dataType) {

		for (Coordinate aCoordinate : invariantMassList) {
			List<H1F> aList = new ArrayList<>();
			String topology = getTopology(aCoordinate, "M(");
			String title = getTitle(aCoordinate, "Invariant Mass of ");
			for (int i = 0; i < this.neededHists.get(aCoordinate); i++) {
				H1F h1f = new H1F(topology, "", 100, 0, 4.0);
				h1f.setTitleX(topology);
				h1f.setTitleY("Entries");
				h1f.setTitle(title);
				aList.add(h1f);
			}

			plotsByCoordinate.put(makeHistogramCoordinate(aCoordinate, (dataType + "M")), aList);
		}
		for (Coordinate aCoordinate : missingMassList) {
			List<H1F> aList = new ArrayList<>();
			String topology = getTopology(aCoordinate, "Mx(");
			String title = getTitle(aCoordinate, "Missing Mass of ");
			for (int i = 0; i < this.neededHists.get(aCoordinate); i++) {
				H1F h1f = new H1F(topology, "", 100, 0, 4.0);
				h1f.setTitleX(topology);
				h1f.setTitleY("Entries");
				h1f.setTitle(title);
				aList.add(h1f);
			}
			plotsByCoordinate.put(makeHistogramCoordinate(aCoordinate, (dataType + "Mx")), aList);

		}
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

	private String getTopology(Coordinate coordinate, String start) {
		StringBuilder sb = new StringBuilder(start);
		for (String str : coordinate) {
			sb.append(str);
		}
		sb.append(")[GeV]");

		return sb.toString();
	}

	private String getTitle(Coordinate coordinate, String start) {
		StringBuilder sb = new StringBuilder(start);
		for (String str : coordinate) {
			sb.append(str);
		}
		return sb.toString();
	}

	private void makeBranches() {
		Map<Coordinate, Integer> aCoordMap = new HashMap<>();
		for (Coordinate aCoordinate : getMissingMassList()) {
			int occurrences = 0;
			for (String string : aCoordinate) {
				occurrences = occurrences + Collections.frequency(reactionList, string);
			}
			int histsNeeded = occurrences - aCoordinate.getStrSize() + 1;
			if (!aCoordMap.containsKey(aCoordinate)) {
				aCoordMap.put(aCoordinate, histsNeeded);
			}
		}
		for (Coordinate aCoordinate : getInvariantList()) {
			int occurrences = 0;
			for (String string : aCoordinate) {
				occurrences = occurrences + Collections.frequency(reactionList, string);
			}
			int histsNeeded = occurrences - aCoordinate.getStrSize() + 1;
			if (!aCoordMap.containsKey(aCoordinate)) {
				aCoordMap.put(aCoordinate, histsNeeded);
			}
		}
		setHists(aCoordMap, skimService);
	}

	public void runService(String[] array) {
		makeBranches();

		System.out.println("in runService and the number of branches is " + getTree().getListOfBranches().size());
		Analysis analysis = new Analysis(array);
		analysis.run();
	}

	public void setReaction(String... strings) {
		for (String string : strings) {
			this.reactionList.add(string);
		}
	}

	public void addMCService() {
		this.skimService.add("gen");
	}

	public void addReconService() {
		this.skimService.add("rec");

	}

	public List<String> getSkimService() {
		return this.skimService;
	}

	public List<String> getReactionList() {
		return this.reactionList;
	}

	public void setMCFlag(boolean flag) {
		this.mcFlag = flag;
	}

	public void setRECFlag(boolean flag) {
		this.recFlag = flag;
	}

	public boolean getMCFlag() {
		return this.mcFlag;
	}

	public boolean getRECFlag() {
		return this.recFlag;
	}

	public void addCut(double mean, double sigmaRange, String... strings) {
		Coordinate aCoordinate = new Coordinate(strings);
		Cuts cuts = new Cuts(aCoordinate, mean, sigmaRange);
		this.cutList.add(cuts);
	}

	public void addCut(double mean, double sigma, double sigmaRange, String... strings) {
		Coordinate aCoordinate = new Coordinate(strings);
		Cuts cuts = new Cuts(aCoordinate, mean, sigma, sigmaRange);
		this.cutList.add(cuts);
	}

	public void addCut(double mean, int side, String... strings) {
		Coordinate aCoordinate = new Coordinate(strings);
		Cuts cuts = new Cuts(aCoordinate, mean, side);
		this.cutList.add(cuts);
	}

	public List<Cuts> getcutList() {
		return this.cutList;
	}

	public List<Coordinate> getInvariantList() {
		return this.invariantMassList;
	}

	public List<Coordinate> getMissingMassList() {
		return this.missingMassList;
	}

	public void addToInvariantList(String... strings) {
		Coordinate aCoordinate = new Coordinate(strings);
		this.invariantMassList.add(aCoordinate);
	}

	public void addToIMissingMassList(String... strings) {
		Coordinate aCoordinate = new Coordinate(strings);
		this.missingMassList.add(aCoordinate);
	}

	public Map<Coordinate, List<H1F>> getH1Map() {
		return this.plotsByCoordinate;

	}

	public void setHists(Map<Coordinate, Integer> aMap, List<String> skimService) {
		this.neededHists = aMap;
		for (String dataType : skimService) {
			makeHistograms(aMap, dataType);
			createTreeBranches(dataType);
			makeDataTypePlacers(dataType);
		}

	}

	private void makeDataTypePlacers(String dataType) {
		List<String> tmpList = new ArrayList<>();
		for (Coordinate aCoordinate : invariantMassList) {
			String branchName = "";
			if (this.neededHists.get(aCoordinate) > 1) {
				for (int i = 0; i < this.neededHists.get(aCoordinate); i++) {
					branchName = dataType + "M" + coordinateToString(aCoordinate) + Integer.toString(i + 1);
					tmpList.add(branchName);
				}
			} else {
				branchName = dataType + "M" + coordinateToString(aCoordinate);
				tmpList.add(branchName);
			}
		}
		for (Coordinate aCoordinate : missingMassList) {
			String branchName = "";
			if (this.neededHists.get(aCoordinate) > 1) {
				for (int i = 0; i < this.neededHists.get(aCoordinate); i++) {
					branchName = dataType + "Mx" + coordinateToString(aCoordinate) + Integer.toString(i + 1);
					tmpList.add(branchName);
				}
			} else {
				branchName = dataType + "Mx" + coordinateToString(aCoordinate);
				tmpList.add(branchName);
			}
		}
		if (dataType.equals("gen")) {
			this.genList.addAll(tmpList);
		} else {
			this.recList.addAll(tmpList);
		}
	}

	public List<String> getGenList() {
		return this.genList;
	}

	public List<String> getRecList() {
		return this.recList;
	}

	public Map<Coordinate, Integer> getHistSetter() {
		return this.neededHists;
	}

	public TreeVector getTree() {
		return this.treeVector;
	}

}
