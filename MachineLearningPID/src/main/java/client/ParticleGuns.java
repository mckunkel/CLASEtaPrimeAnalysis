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

import java.util.HashMap;
import java.util.Map;

import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.ParticleGenerator;

import domain.LundHeader;
import domain.OpenCloseLundFile;
import domain.OpenCloseLundFileImpl;

public class ParticleGuns {

	public static void main(String[] args) {
		LundHeader lundHeader = new LundHeader(1, 1, 1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

		int NEvents = 4000000;
		int nEventsPerFile = 10000;

		Map<String, Integer> particleMap = new HashMap<>();
		particleMap.put("Electron", 11);
		particleMap.put("Positron", -11);
		particleMap.put("Proton", 2212);
		particleMap.put("AntiProton", -2212);
		particleMap.put("Neutron", 2112);
		particleMap.put("AntiNeutron", -2112);
		particleMap.put("PiPlus", 211);
		particleMap.put("PiMinus", -211);
		particleMap.put("KPlus", 321);
		particleMap.put("KMinus", -321);
		particleMap.put("Gamma", 22);

		double pMin = 0.25; // GeV
		double pMax = 8.5; // GeV
		double thetaMin = 3.0; // degrees
		double thetaMax = Math.toDegrees(Math.PI / 2.0); // degrees
		double phiMin = -180.0; // degrees
		double phiMax = 180.0; // degrees
		for (Map.Entry<String, Integer> entry : particleMap.entrySet()) {
			int lundPartNum = 1;
			int eventNumber = 0;

			String key = entry.getKey();
			Integer value = entry.getValue();

			OpenCloseLundFile openCloseLundFile = new OpenCloseLundFileImpl(
					key + "/" + key + "_" + lundPartNum + ".lund");
			openCloseLundFile.openLundFile();

			ParticleGenerator pgun = new ParticleGenerator(value, pMin, pMax, thetaMin, thetaMax, phiMin, phiMax);
			for (int loop = 0; loop < NEvents; loop++) {
				if (eventNumber == nEventsPerFile) {
					eventNumber = 0;
					lundPartNum++;
					openCloseLundFile.closeLundFile();
					openCloseLundFile.openLundFile(key + "/" + key + "_" + lundPartNum + ".lund");
				}

				Particle particle = pgun.getParticle();

				openCloseLundFile.writeEvent(lundHeader);
				openCloseLundFile.writeEvent("1 " + particle.toLundString());
				openCloseLundFile.writeFlush();
				eventNumber++;
			}
			openCloseLundFile.closeLundFile();
			System.out.println("Done with " + key + " genertation");
		}
	}
}
