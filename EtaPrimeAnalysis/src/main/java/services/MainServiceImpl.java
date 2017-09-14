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

import domain.Analysis;
import domain.AnalysisPlotsForDaniel;
import domain.Coordinate;
import domain.Cuts;
import domain.DataPoint;
import domain.TreeVector;

public class MainServiceImpl implements MainService {

	private List<String> reactionList = null;
	private List<String> skimService = null;
	private DataPoint dataPoint = null;

	private List<Coordinate> invariantMassList = null;
	private List<Coordinate> missingMassList = null;
	private Map<Coordinate, Integer> neededHists = null;
	private List<Cuts> cutList = null;

	private TreeVector treeVector = null;

	public MainServiceImpl() {
		this.reactionList = new ArrayList<>();
		this.skimService = new ArrayList<>();
		this.dataPoint = new DataPoint();

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

	private void setHists(Map<Coordinate, Integer> aMap, List<String> skimService) {
		this.neededHists = aMap;
		for (String dataType : skimService) {
			createTreeBranches(dataType);
		}

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

	public TreeVector getTree() {
		return this.treeVector;
	}

	public void addDataPoint(DataPoint dataPoint) {
		this.dataPoint = this.dataPoint.addDataPoint(dataPoint);
	}

	public void assembleDataPoint() {
		getTree().addToTree(this.dataPoint);
		this.dataPoint = new DataPoint();
	}

	public void runService(String[] array) {
		makeBranches();
		for (String str : treeVector.getListOfBranches()) {
			System.out.println(str + " is a branch that is made");
		}
		Analysis analysis = new Analysis(array);
		analysis.run();
		AnalysisPlotsForDaniel analysisPlots = new AnalysisPlotsForDaniel();
		analysisPlots.run();
		// AnalysisPlots analysisPlots = new AnalysisPlots();
		// analysisPlots.run();
	}

}
