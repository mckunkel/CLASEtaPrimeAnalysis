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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import services.MainService;
import services.ServiceManager;

public class RunAnalysis {

	public static void main(String[] args) {
		// public static void main(File file, String[] args) {

		MainService mainService = ServiceManager.getSession();
		mainService.addToIMissingMassList("p", "e-");
		mainService.addToIMissingMassList("e-");
		mainService.calQ();
		mainService.calW();
		mainService.calP();

		// mainService.addToIMissingMassList("p", "e-", "e+");
		// mainService.addToIMissingMassList("p", "e+");

		mainService.addToInvariantList("e-", "e+");
		mainService.addToInvariantList("e-", "e+", "gamma");

		// mainService.addCut(0.96637, 0.03, 2.5, "Mx", "p", "e-");
		// mainService.addCut(0.96637, 1, "M", "e+", "e-");

		List<String> reactionList = new ArrayList<>();
		reactionList.add("p");
		// reactionList.add("e-");
		reactionList.add("e+");
		reactionList.add("e-");
		reactionList.add("gamma");

		String[] reactionArray = reactionList.toArray(new String[0]);
		mainService.setReaction(reactionArray);

		mainService.addMCService();
		mainService.addReconService();

		// String dirName =
		// "/Volumes/Mac_Storage/Work_Data/CLAS12/EtaPrimeDilepton/";
		// String dirName =
		// "/Users/michaelkunkel/WORK/CLAS/CLAS12/CLAS12Data/EtaPrimeDilepton/";
		String dirName = "/Volumes/DATA/CLAS12/EtaPrimeAnalysis/Torus-0.75Sol0.8/";

		String baseName = "out_FullDilepton_Tor-0.75Sol0.8";
		String part1Name = baseName + "_100.hipo";
		String part2Name = baseName + "_101.hipo";
		String part3Name = baseName + "_102.hipo";
		String part4Name = baseName + "_103.hipo";
		String part5Name = baseName + "_104.hipo";
		String part6Name = baseName + "_105.hipo";
		String part7Name = baseName + "_106.hipo";

		List<String> aList = new ArrayList<>();
		File[] listOfFiles = new File("/Volumes/DATA/CLAS12/EtaPrimeAnalysis/Torus-0.75Sol0.8")
				.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".hipo");
					}
				});
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
				// aList.add(dirName + listOfFiles[i].getName());
			}
		}

		aList.add(dirName + part1Name);
		aList.add(dirName + part2Name);
		aList.add(dirName + part3Name);
		aList.add(dirName + part4Name);
		aList.add(dirName + part5Name);
		aList.add(dirName + part6Name);
		aList.add(dirName + part7Name);

		String[] array = aList.toArray(new String[0]);
		mainService.runService(array);
		// mainService.runService(args);
		// System.exit(0);

	}

}
