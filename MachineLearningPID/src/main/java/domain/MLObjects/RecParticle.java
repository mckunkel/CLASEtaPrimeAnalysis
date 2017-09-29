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
package domain.MLObjects;

import java.util.HashMap;
import java.util.Map;

import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;

public class RecParticle {

	private static String bankName = "REC::Particle";

	public RecParticle() {
	}

	public static Map<MLObject, Integer> skimBank(DataEvent aEvent) {
		Map<MLObject, Integer> pMap = new HashMap<>();
		if (aEvent.hasBank(bankName)) {
			DataBank aBank = aEvent.getBank(bankName);
			int Nrows = aBank.rows();
			for (int t = 0; t < Nrows; t++) {
				if (getPID(aBank, t) != 0) {
					MLObject mlObject = new MLObject();
					mlObject.setpContainer(createMLParticle(aBank, t));
					mlObject.setEcList(RecCalorimeter.createCalorimeter(aEvent, t));
					mlObject.setCcList(RecCherenkov.createCherenkov(aEvent, t));
					pMap.put(mlObject, getPID(aBank, t));
				}

			}
		}
		return pMap;
	}

	public static Map<MLObject, Integer> skimBankExclusive(DataEvent aEvent, int pid) {
		Map<MLObject, Integer> pMap = new HashMap<>();
		if (aEvent.hasBank(bankName)) {
			DataBank aBank = aEvent.getBank(bankName);
			int Nrows = aBank.rows();
			for (int t = 0; t < Nrows; t++) {
				if (getPID(aBank, t) == pid) {
					MLObject mlObject = new MLObject();
					mlObject.setpContainer(createMLParticle(aBank, t));
					mlObject.setEcList(RecCalorimeter.createCalorimeter(aEvent, t));
					mlObject.setCcList(RecCherenkov.createCherenkov(aEvent, t));
					pMap.put(mlObject, getPID(aBank, t));
				}

			}
		}
		return pMap;
	}

	public static Map<MLObject, Integer> skimBankIDCrisis(DataEvent aEvent, int pid) {
		Map<MLObject, Integer> pMap = new HashMap<>();
		if (aEvent.hasBank(bankName)) {
			DataBank aBank = aEvent.getBank(bankName);
			int Nrows = aBank.rows();
			for (int t = 0; t < Nrows; t++) {
				if (getPID(aBank, t) != 0) {
					MLObject mlObject = new MLObject();
					mlObject.setpContainer(createMLParticle(aBank, t));
					mlObject.setEcList(RecCalorimeter.createCalorimeter(aEvent, t));
					mlObject.setCcList(RecCherenkov.createCherenkov(aEvent, t));
					pMap.put(mlObject, pid);
				}

			}
		}
		return pMap;
	}

	private static ParticleContainer createMLParticle(DataBank aBank, int evntIndex) {

		ParticleContainer pContainer = new ParticleContainer();
		pContainer.setPx(aBank.getFloat("px", evntIndex));
		pContainer.setPy(aBank.getFloat("py", evntIndex));
		pContainer.setPz(aBank.getFloat("pz", evntIndex));
		double ptot = Math.sqrt(
				Math.pow(pContainer.getPx(), 2) + Math.pow(pContainer.getPy(), 2) + Math.pow(pContainer.getPz(), 2));

		pContainer.setPtot(ptot);
		double theta = Math.acos(pContainer.getPz() / ptot);
		double phi = Math.atan2(pContainer.getPy(), pContainer.getPx());

		pContainer.setTheta(theta);
		pContainer.setPhi(phi);
		pContainer.setVx(aBank.getFloat("vx", evntIndex));
		pContainer.setVy(aBank.getFloat("vy", evntIndex));
		pContainer.setVz(aBank.getFloat("vz", evntIndex));

		return pContainer;
	}

	private static int getPID(DataBank aBank, int evntIndex) {
		return aBank.getInt("pid", evntIndex);
	}

}
