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
import java.util.Iterator;
import java.util.List;

import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.Vector3;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

import services.MainService;
import services.ServiceManager;

public class Analysis {

	private HipoDataSource hipoReader = null;
	private String[] fileList = null;
	private int NinputFiles = 0;

	private String mcBankName;
	private String recBankName;
	private List<String> reactionList = null;
	private ReactionFilter genFilter = null;
	private ReactionFilter reconFilter = null;
	private MainService mainService = null;

	public Analysis(String[] fileList) {
		this.fileList = fileList;
		this.NinputFiles = fileList.length;
		this.genFilter = new ReactionFilter();
		this.reconFilter = new ReactionFilter();
		this.mainService = ServiceManager.getSession();
		this.reactionList = mainService.getReactionList();

		init();

	}

	private void init() {
		this.mcBankName = "MC::Particle";
		this.recBankName = "REC::Particle";

		this.genFilter.setReactionList(this.reactionList);
		this.reconFilter.setReactionList(this.reactionList);
	}

	public void run() {
		for (int i = 0; i < NinputFiles; i++) {
			System.out.println("operating on file " + this.fileList[i]);
			this.hipoReader = new HipoDataSource();
			this.hipoReader.open(this.fileList[i]);
			readHipo();
		}
	}

	private void readHipo() {

		for (int evnt = 1; evnt < getNEvents(); evnt++) {// getNEvents()
			DataEvent event = (DataEvent) this.hipoReader.gotoEvent(evnt);
			List<Particle> genList = fillParticleList(event, this.mcBankName);
			List<Particle> recList = fillParticleList(event, this.recBankName);
			this.genFilter.setParticleList(genList);
			this.reconFilter.setParticleList(recList);

			MakePlots makeGenPlots = new MakePlots(this.genFilter.reactionList(), "gen");
			MakePlots makeRecPlots = new MakePlots(this.reconFilter.reactionList(), "rec");

			runConfiguration(makeGenPlots, makeRecPlots);
			this.mainService.assembleDataPoint();

		}

	}

	private void runConfiguration(MakePlots makeGenPlots, MakePlots makeRecPlots) {
		if (this.mainService.getSkimService().size() == 2) {
			makeGenPlots.init();
			makeRecPlots.init();
		} else if (this.mainService.getSkimService().size() == 1 && this.mainService.getSkimService().contains("gen")) {
			makeGenPlots.init();
		} else if (this.mainService.getSkimService().size() == 1 && this.mainService.getSkimService().contains("rec")) {
			makeRecPlots.init();
		} else {
			System.err.println("Did you set the MC or REC service correctly?!? ");
			System.exit(1);
		}
	}

	private int getNEvents() {
		return this.hipoReader.getSize();
	}

	private List<Particle> fillParticleList(DataEvent aEvent, String bankName) {
		List<Particle> aList = new ArrayList<>();
		if (aEvent.hasBank(bankName)) {
			DataBank aBank = aEvent.getBank(bankName);
			int Nrows = aBank.rows();
			for (int t = 0; t < Nrows; t++) {
				aList.add(getParticle(aBank, t));
			}
		}

		if (bankName.equals("REC::Particle") && aEvent.hasBank(bankName) && aEvent.hasBank("FT::particles")) {
			DataBank ftBank = aEvent.getBank("FT::particles");
			int Nrows = ftBank.rows();
			for (int t = 0; t < Nrows; t++) {
				Particle particle = getFTParticle(ftBank, t);
				compareWithRec(particle, aList);
			}
		}

		return aList;
	}

	private void compareWithRec(Particle particle, List<Particle> aList) {
		Iterator<Particle> itr = aList.iterator();

		while (itr.hasNext()) {
			Particle part = itr.next();
			if (Math.abs(part.pz() - particle.pz()) < 0.01 && part.pid() == particle.pid()) {
				itr.remove();
			}
		}
	}

	private int getpid(DataBank aBank, int evntIndex) {
		int pid = aBank.getInt("pid", evntIndex);
		if (pid == 0) {
			return -3312;
		}
		return pid;
	}

	private int getCharge(DataBank aBank, int evntIndex) {
		int charge = aBank.getInt("charge", evntIndex);
		return charge;

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

	private Vector3 getFTMomentumVector(DataBank aBank, int evntIndex) {
		Vector3 momVector = new Vector3();
		double mass = PDGDatabase.getParticleById(11).mass();
		double energy = aBank.getFloat("energy", evntIndex);
		double totalMomentum = Math.sqrt(Math.pow(energy, 2) - Math.pow(mass, 2));
		momVector.setXYZ(totalMomentum * aBank.getFloat("cx", evntIndex),
				totalMomentum * aBank.getFloat("cy", evntIndex), totalMomentum * aBank.getFloat("cz", evntIndex));
		return momVector;
	}

	private LorentzVector getFTLorentzVector(DataBank aBank, int evntIndex) {
		LorentzVector aLorentzVector = new LorentzVector();
		double mass = PDGDatabase.getParticleById(11).mass();
		aLorentzVector.setVectM(getFTMomentumVector(aBank, evntIndex), mass);
		return aLorentzVector;
	}

	private Particle getFTParticle(DataBank aBank, int evntIndex) {
		Particle aParticle = new Particle();
		Vector3 vrtVector = new Vector3();
		vrtVector.setXYZ(0.0, 0.0, 0.0);
		aParticle.setVector(getFTLorentzVector(aBank, evntIndex), vrtVector);
		if (getCharge(aBank, evntIndex) == -1) {
			aParticle.changePid(11);
		} else
			aParticle.changePid(-3312);
		return aParticle;
	}

}
