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
		mainService.addCut("e+", "e-", 0.957);
		String fileName = "/Users/michaelkunkel/WORK/GiBUU/clas/EtaPrimeDilepton/ReconstructedFiles/out_EtaPrimeDilepton_Tor-0.75Sol0.8_50.hipo";
		// String fileName =
		// "/Volumes/Mac_Storage/Work_Codes/CLAS12/EtaPrimeDilepton/EtaPrimeDilepton_Analysis/out_EtaPrimeDilepton_Tor-0.75Sol0.6_50.hipo";
		List<String> reactionList = new ArrayList<>();
		reactionList.add("p");
		reactionList.add("e-");
		reactionList.add("e+");
		reactionList.add("e-");

		Analysis analysis = new Analysis(fileName, reactionList);
	}

}
