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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

import domain.MLObjects.CalorimeterContainer;
import domain.MLObjects.CherenkovContainer;
import domain.MLObjects.MLObject;
import domain.MLObjects.ParticleContainer;
import domain.MLObjects.RecParticle;
import domiain.svmWriter.OpenCloseSVMWriterImpl;

public class RunMLObject {
	private HipoDataSource hipoReader = null;
	private String[] fileList = null;
	private int NinputFiles = 0;

	private OpenCloseSVMWriterImpl openSVMWriter = null;

	public RunMLObject(String[] fileList, String outputSVMName) {
		this.fileList = fileList;
		this.NinputFiles = fileList.length;
		openSVMWriter = new OpenCloseSVMWriterImpl(outputSVMName);
		openSVMWriter.openSVMFile();
	}

	public void run() {
		for (int i = 0; i < NinputFiles; i++) {
			System.out.println("operating on file " + this.fileList[i]);
			this.hipoReader = new HipoDataSource();
			this.hipoReader.open(this.fileList[i]);
			readHipo();
		}

		openSVMWriter.closeSVMFile();
	}

	private int getNEvents() {
		return this.hipoReader.getSize();
	}

	private void readHipo() {

		for (int evnt = 1; evnt < getNEvents(); evnt++) {// getNEvents()
			DataEvent aEvent = (DataEvent) this.hipoReader.gotoEvent(evnt);
			Map<MLObject, Integer> aMap = RecParticle.skimBank(aEvent);
			if (aMap.size() > 0) {
				// printMap(aMap, evnt);
				openSVMWriter.writeEvent(aMap);
				openSVMWriter.writeFlush();
			}

		}

	}

	private void printMap(Map<MLObject, Integer> aMap, int evnt) {
		System.out.println("############### " + evnt + " ###############");
		for (Map.Entry<MLObject, Integer> entry : aMap.entrySet()) {
			System.out.println("###############  NEW Object ###############");

			MLObject key = entry.getKey();
			Integer value = entry.getValue();
			ParticleContainer pContainer = key.getpContainer();
			List<CherenkovContainer> cherenkovContainer = key.getCcList();
			List<CalorimeterContainer> calorimeterContainer = key.getEcList();

			for (CalorimeterContainer cc : calorimeterContainer) {
				System.out.println(cc.getDetectorType() + " " + cc.getEnergy() + "  " + cc.getLayer() + " ec");
			}
			for (CherenkovContainer cc : cherenkovContainer) {
				System.out.println(cc.getDetectorType() + "  " + cc.getNphe() + "  " + cc.getPhi() + "  "
						+ cc.getTheta() + "  cc");
			}
			System.out.println(pContainer.getPx() + "  " + pContainer.getPy() + " particle  "
					+ PDGDatabase.getParticleById(value).name());
		}
	}

	public static void main(String[] args) {

		// String dirName =
		// "/Volumes/Mac_Storage/Work_Data/CLAS12/EtaPrimeDilepton/";
		String dirName = "/Volumes/DATA/CLAS12/MachineLearning/ReconstructedFiles/Electron/Torus-0.75Sol0.8/";
		String partName = "out_Electron_Tor-0.75Sol0.8_";

		List<String> aList = new ArrayList<>();
		for (int i = 1; i <= 100; i++) {
			aList.add(dirName + partName + i + ".hipo");
		}

		String[] array = aList.toArray(new String[0]);
		// RunMLObject runMLObject = new RunMLObject(array, "Electron.txt");
		String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
		String outName = args[0];
		RunMLObject runMLObject = new RunMLObject(newArgs, outName);

		runMLObject.run();
		System.exit(0);

	}

}
