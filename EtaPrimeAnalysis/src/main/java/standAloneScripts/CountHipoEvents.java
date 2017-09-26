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
package standAloneScripts;

import java.util.ArrayList;
import java.util.List;

import org.jlab.io.hipo.HipoDataSource;

public class CountHipoEvents {
	private HipoDataSource hipoReader = null;
	private String[] fileList = null;
	private int NinputFiles = 0;
	private int nEvents = 0;

	public CountHipoEvents(String[] fileList) {
		this.fileList = fileList;
		this.NinputFiles = fileList.length;
	}

	public int run() {
		for (int i = 0; i < NinputFiles; i++) {
			System.out.println("operating on file " + this.fileList[i]);
			this.hipoReader = new HipoDataSource();
			this.hipoReader.open(this.fileList[i]);
			nEvents += this.hipoReader.getSize();
		}
		return this.nEvents;
	}

	public static void main(String[] args) {
		String dirName = "/Users/michaelkunkel/WORK/CLAS/CLAS12/CLAS12Data/EtaPrimeDilepton/";
		String part1Name = "out_EtaPrimeDilepton_Tor-0.75Sol0.6_100.hipo";
		String part2Name = "out_EtaPrimeDilepton_Tor-0.75Sol0.6_101.hipo";
		String part3Name = "out_EtaPrimeDilepton_Tor-0.75Sol0.6_102.hipo";
		List<String> aList = new ArrayList<>();
		aList.add(dirName + part1Name);
		aList.add(dirName + part2Name);
		aList.add(dirName + part3Name);

		String[] array = aList.toArray(new String[0]);

		CountHipoEvents countHipoEvents = new CountHipoEvents(args);
		System.out.println("Number of Events in all files = " + countHipoEvents.run());
	}

}
