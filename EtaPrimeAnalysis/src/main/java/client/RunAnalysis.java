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
package client;

import java.util.ArrayList;
import java.util.List;

import domain.Analysis;
import services.MainService;
import services.ServiceManager;

public class RunAnalysis {

	public static void main(String[] args) {
		MainService mainService = ServiceManager.getSession();
		mainService.addToIMissingMassList("p", "e-");
		// mainService.addToIMissingMassList("p", "e-", "e+");

		mainService.addToInvariantList("e+", "e-");

		mainService.addCut(0.96637, 0.03, 2.5, "Mx", "p", "e-");

		// String dirName =
		// "/Volumes/Mac_Storage/Work_Data/CLAS12/EtaPrimeDilepton/";
		String dirName = "/Users/michaelkunkel/WORK/CLAS/CLAS12/CLAS12Data/EtaPrimeDilepton/";

		String part1Name = "out_EtaPrimeDilepton_Tor-0.75Sol0.6_100.hipo";
		String part2Name = "out_EtaPrimeDilepton_Tor-0.75Sol0.6_101.hipo";
		String part3Name = "out_EtaPrimeDilepton_Tor-0.75Sol0.6_102.hipo";
		String part4Name = "out_EtaPrimeDilepton_Tor-0.75Sol0.6_103.hipo";
		String part5Name = "out_EtaPrimeDilepton_Tor-0.75Sol0.6_104.hipo";
		String part6Name = "out_EtaPrimeDilepton_Tor-0.75Sol0.6_105.hipo";
		String part7Name = "out_EtaPrimeDilepton_Tor-0.75Sol0.6_106.hipo";

		List<String> reactionList = new ArrayList<>();
		reactionList.add("p");
		reactionList.add("e-");
		reactionList.add("e+");
		reactionList.add("e-");

		List<String> aList = new ArrayList<>();
		aList.add(dirName + part1Name);
		// aList.add(dirName + part2Name);
		// aList.add(dirName + part3Name);
		// aList.add(dirName + part4Name);
		// aList.add(dirName + part5Name);
		// aList.add(dirName + part6Name);
		// aList.add(dirName + part7Name);

		// aList.add(
		// "/Users/michaelkunkel/WORK/CLAS/CLAS12/CODES/SIMUALTION/EtaPrimeDilepton/ReconstructedFiles/out_EtaPrimeDilepton_Tor-0.75Sol0.8_51.hipo");
		String[] array = aList.toArray(new String[0]);

		// Analysis analysis = new Analysis(fileName, reactionList);
		Analysis analysis = new Analysis(array, reactionList);
		// Analysis analysis = new Analysis(args, reactionList);

	}

}
