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
package analysis;

import java.util.ArrayList;
import java.util.List;

import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.Vector3;
import org.jlab.io.base.DataBank;

public class AnalysisMethods {

	private AnalysisMethods() {

	}

	public static int getpid(DataBank aBank, int evntIndex) {
		int pid = aBank.getInt("pid", evntIndex);
		if (pid == 0) {
			return -3312;
		}
		return pid;
	}

	public static int getCharge(DataBank aBank, int evntIndex) {
		int charge = aBank.getInt("charge", evntIndex);
		return charge;

	}

	public static Vector3 getMomentumVector(DataBank aBank, int evntIndex) {
		Vector3 momVector = new Vector3();
		momVector.setXYZ(aBank.getFloat("px", evntIndex), aBank.getFloat("py", evntIndex),
				aBank.getFloat("pz", evntIndex));
		return momVector;
	}

	public static Vector3 getVertexVector(DataBank aBank, int evntIndex) {
		Vector3 vrtVector = new Vector3();
		vrtVector.setXYZ(aBank.getFloat("vx", evntIndex), aBank.getFloat("vy", evntIndex),
				aBank.getFloat("vz", evntIndex));
		return vrtVector;
	}

	public static LorentzVector getLorentzVector(DataBank aBank, int evntIndex) {
		LorentzVector aLorentzVector = new LorentzVector();
		double mass = PDGDatabase.getParticleById(getpid(aBank, evntIndex)).mass();
		aLorentzVector.setVectM(getMomentumVector(aBank, evntIndex), mass);
		return aLorentzVector;
	}

	public static Particle getParticle(DataBank aBank, int evntIndex) {
		Particle aParticle = new Particle();
		aParticle.setVector(getLorentzVector(aBank, evntIndex), getVertexVector(aBank, evntIndex));
		aParticle.changePid(getpid(aBank, evntIndex));
		return aParticle;

	}

	public static Particle beamTargetParticle() {
		Particle sum = new Particle();
		sum.copy(EventList.beamParticle);
		sum.combine(EventList.targetParticle, +1);
		return sum;
	}

	private static List<Particle> mcList(DataBank aBank) {
		List<Particle> aList = new ArrayList<>();
		int epCount = 0;
		int emCount = 0;
		int gammaCount = 0;
		for (int h = 0; h < aBank.rows(); h++) {
			if (aBank.getInt("pid", h) == 11) {
				aList.add(AnalysisMethods.getParticle(aBank, h));
				emCount++;
			}
			if (aBank.getInt("pid", h) == -11) {
				aList.add(AnalysisMethods.getParticle(aBank, h));
				epCount++;
			}
			if (aBank.getInt("pid", h) == 22) {
				aList.add(AnalysisMethods.getParticle(aBank, h));
				gammaCount++;
			}
		}
		if (emCount == 2 && epCount == 1 && gammaCount == 1) {
			aList.remove(aList.size() - 1);
			return aList;
		}
		return new ArrayList<>();
	}

	public static boolean matchTrack(DataBank aBank, Particle p) {
		List<Particle> mcList = mcList(aBank);
		for (Particle particle : mcList) {
			// && Math.abs(p.p() - particle.p()) < 0.05

			if (p.pid() == particle.pid() && Math.abs(p.phi() - particle.phi()) < 0.025
					&& Math.abs(p.theta() - particle.theta()) < 0.0075) {
				return true;
			}
		}
		return false;
	}

	public static Particle matchedMCParticle(DataBank aBank, Particle p) {
		Particle pnew = new Particle();
		List<Particle> mcList = mcList(aBank);
		for (Particle particle : mcList) {
			if (p.pid() == particle.pid()) {
				pnew.copy(particle);
			}
		}
		return pnew;
	}

	public static double mclistmass(DataBank aBank) {
		Particle particle = new Particle(22, 0, 0, 0);
		for (Particle p : mcList(aBank)) {
			particle.combine(p, +1);
		}
		return particle.mass();
	}

	public static String getPName(double mass, int signal, double limit) {
		if (signal == 1) {
			if (Math.abs(mass - 0.1349) < limit) {
				return "pi0";
			} else if (Math.abs(mass - 0.547) < limit) {
				return "eta";

			} else if (Math.abs(mass - 0.957) < limit) {
				return "etaPrime";

			} else {
				return "Unknown";
			}
		}
		return "Background";
	}
}
