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
package domain;

import java.util.ArrayList;
import java.util.List;

import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.Vector3;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

public class Analysis {

	private HipoDataSource hipoReader = null;
	private String fileName;
	private String mcBankName;
	private String recBankName;
	private List<String> reactionList = null;
	private ReactionFilter reactionFilter = null;

	public Analysis(String fileName, List<String> reactionList) {
		this.fileName = fileName;
		this.reactionList = reactionList;
		this.reactionFilter = new ReactionFilter();
		init();
		readHipo();

	}

	private void init() {
		this.hipoReader = new HipoDataSource();
		this.hipoReader.open(fileName);

		this.mcBankName = "MC::Particle";
		this.recBankName = "REC::Particle";

	}

	private void readHipo() {
		for (int evnt = 0; evnt < 10; evnt++) {// getNEvents()
			DataEvent event = (DataEvent) hipoReader.gotoEvent(evnt);
			List<Particle> genList = fillGenList(event);
			reactionFilter.setParticleList(genList);
			reactionFilter.setReactionList(this.reactionList);
			reactionFilter.runComparision();
		}

	}

	private int getNEvents() {
		return this.hipoReader.getSize();
	}

	private List<Particle> fillGenList(DataEvent aEvent) {
		List<Particle> aList = new ArrayList<>();
		if (aEvent.hasBank(mcBankName)) {
			DataBank mcBank = aEvent.getBank(mcBankName);
			int Nrows = mcBank.rows();
			for (int t = 0; t < Nrows; t++) {
				aList.add(getParticle(mcBank, t));
			}
		}
		return aList;
	}

	private int getpid(DataBank aBank, int evntIndex) {
		return aBank.getInt("pid", evntIndex);
	}

	private Vector3 getMomentumVector(DataBank aBank, int evntIndex) {
		Vector3 momVector = new Vector3();
		momVector.setXYZ(aBank.getFloat("px", evntIndex), aBank.getFloat("py", evntIndex),
				aBank.getFloat("pz", evntIndex));
		return momVector;
	}

	private Vector3 getVertexVector(DataBank aBank, int evntIndex) {
		Vector3 vrtVector = new Vector3();
		vrtVector.setXYZ(aBank.getFloat("vx", evntIndex), aBank.getFloat("vy", evntIndex),
				aBank.getFloat("vz", evntIndex));
		return vrtVector;
	}

	private LorentzVector getLorentzVector(DataBank aBank, int evntIndex) {
		LorentzVector aLorentzVector = new LorentzVector();
		double mass = PDGDatabase.getParticleById(getpid(aBank, evntIndex)).mass();
		aLorentzVector.setVectM(getMomentumVector(aBank, evntIndex), mass);
		return aLorentzVector;
	}

	private Particle getParticle(DataBank aBank, int evntIndex) {
		Particle aParticle = new Particle();
		aParticle.setVector(getLorentzVector(aBank, evntIndex), getVertexVector(aBank, evntIndex));
		aParticle.changePid(getpid(aBank, evntIndex));
		return aParticle;

	}

}
