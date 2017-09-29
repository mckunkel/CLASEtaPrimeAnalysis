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

import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.Vector3;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvas;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

import domain.utils.SaveCanvas;

public class TriggerAnalysis {
	private HipoDataSource hipoReader = null;
	private String[] fileList = null;
	private String recBankName;
	private String mcBankName;
	private String tbHitsBankName;

	private int NinputFiles = 0;
	private H2F positronHist = null;
	private H2F electronronHist = null;
	private H2F pVsp = null;
	private H2F thetaVstheta = null;

	private H2F positronHistGen = null;
	private H2F electronronHistGen = null;
	private H2F pVspGen = null;
	private H2F thetaVsthetaGen = null;

	private H2F NvsN = null;

	private double thetaMin = 0.0;
	private double thetaMax = 180.0;
	private double pMin = 0.0;
	private double pMax = 6.0;

	public TriggerAnalysis(String[] fileList) {
		this.fileList = fileList;
		this.NinputFiles = fileList.length;
		setHists();

		init();
	}

	private void setHists() {
		positronHist = new H2F("Theta vs p", 100, pMin, pMax, 100, thetaMin, thetaMax);
		electronronHist = new H2F("Theta vs p", 100, pMin, pMax, 100, thetaMin, thetaMax);
		pVsp = new H2F("p vs p", 100, pMin, pMax, 100, pMin, pMax);
		thetaVstheta = new H2F("Theta vs Theta", 100, thetaMin, thetaMax, 100, thetaMin, thetaMax);

		positronHist.setTitle("e+ Theta vs. p");
		positronHist.setTitleY("Theta [degrees]");
		positronHist.setTitleX("p [GeV]");

		electronronHist.setTitle("e- Theta vs. p");
		electronronHist.setTitleY("Theta [degrees]");
		electronronHist.setTitleX("p [GeV]");

		pVsp.setTitle("e- p vs. e+ p");
		pVsp.setTitleY("e- p [GeV]");
		pVsp.setTitleX("e+ p [GeV]");

		thetaVstheta.setTitle("e- theta vs. e+ theta");
		thetaVstheta.setTitleY("e- Theta [degrees]");
		thetaVstheta.setTitleX("e+ Theta [degrees]");

		positronHistGen = new H2F("Generated Theta vs p", 100, pMin, pMax, 100, thetaMin, thetaMax);
		electronronHistGen = new H2F("Generated Theta vs p", 100, pMin, pMax, 100, thetaMin, thetaMax);
		pVspGen = new H2F("Generated p vs p", 100, pMin, pMax, 100, pMin, pMax);
		thetaVsthetaGen = new H2F("Generated Theta vs Theta", 100, thetaMin, thetaMax, 100, thetaMin, thetaMax);

		positronHistGen.setTitle("Generated e+ Theta vs. p");
		positronHistGen.setTitleY("Theta [degrees]");
		positronHistGen.setTitleX("p [GeV]");

		electronronHistGen.setTitle("Generated e- Theta vs. p");
		electronronHistGen.setTitleY("Theta [degrees]");
		electronronHistGen.setTitleX("p [GeV]");

		pVspGen.setTitle("Generated e- p vs. e+ p");
		pVspGen.setTitleY("e- p [GeV]");
		pVspGen.setTitleX("e+ p [GeV]");

		thetaVsthetaGen.setTitle("Generated e- theta vs. e+ theta");
		thetaVsthetaGen.setTitleY("e- Theta [degrees]");
		thetaVsthetaGen.setTitleX("e+ Theta [degrees]");

		NvsN = new H2F("N time the loop was filled", 100, 0, 4, 100, 0, 4);

		NvsN.setTitle("N vs N");
		NvsN.setTitleY("N e+ fills");
		NvsN.setTitleX("N e- fills");

	}

	private void init() {
		this.recBankName = "REC::Particle";
		this.mcBankName = "MC::Particle";
		this.tbHitsBankName = "TimeBasedTrkg::TBTracks";

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
			List<Particle> recList = fillParticleList(event, this.recBankName);
			List<Particle> DalitzList = fillDalitzList(event, this.mcBankName);
			List<Particle> tbHitsList = fillParticleTBList(event, this.tbHitsBankName);

			if (recList.size() > 1) {
				compareLists(getOnlyDCHits(recList, tbHitsList), DalitzList);
				// compareLists(recList, DalitzList);

			}
			drawGenHists(DalitzList);
		}

	}

	private List<Particle> fillDalitzList(DataEvent aEvent, String bankName) {
		List<Particle> aList = new ArrayList<>();
		if (aEvent.hasBank(bankName)) {
			DataBank aBank = aEvent.getBank(bankName);
			int Nrows = aBank.rows();
			for (int t = 0; t < Nrows - 1; t++) {
				aList.add(getMCParticle(aBank, t));
			}
		}
		return aList;
	}

	private List<Particle> fillParticleTBList(DataEvent aEvent, String bankName) {
		List<Particle> aList = new ArrayList<>();
		if (aEvent.hasBank(bankName)) {
			DataBank aBank = aEvent.getBank(bankName);
			int Nrows = aBank.rows();
			for (int t = 0; t < Nrows; t++) {
				aList.add(getTBParticle(aBank, t));
			}
		}
		return aList;
	}

	private List<Particle> fillParticleList(DataEvent aEvent, String bankName) {
		List<Particle> aList = new ArrayList<>();
		if (aEvent.hasBank(bankName)) {
			DataBank aBank = aEvent.getBank(bankName);
			int Nrows = aBank.rows();
			for (int t = 0; t < Nrows; t++) {
				if (getpid(aBank, t) == -11 || getpid(aBank, t) == 11) {
					aList.add(getParticle(aBank, t));
				}
			}
		}
		return aList;
	}

	private int getpid(DataBank aBank, int evntIndex) {
		// For this analysis, lets do some simple beta charge determination

		double beta = getBeta(aBank, evntIndex);
		int charge = getCharge(aBank, evntIndex);
		int pid = -1000;
		if (charge == -1) { // for electrons
			if (beta > 0.98) {
				pid = 11;
			}
		}
		if (charge == 1) { // for positrons/protons
			if (beta > 0.98) {
				pid = -11;
			}
		}
		return pid;
	}

	private int getMCpid(DataBank aBank, int evntIndex) {
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

	private float getBeta(DataBank aBank, int evntIndex) {
		float beta = aBank.getFloat("beta", evntIndex);
		return beta;

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

	private LorentzVector getMCLorentzVector(DataBank aBank, int evntIndex) {
		LorentzVector aLorentzVector = new LorentzVector();
		double mass = PDGDatabase.getParticleById(getMCpid(aBank, evntIndex)).mass();
		aLorentzVector.setVectM(getMomentumVector(aBank, evntIndex), mass);
		return aLorentzVector;
	}

	private Particle getParticle(DataBank aBank, int evntIndex) {
		Particle aParticle = new Particle();
		aParticle.setVector(getLorentzVector(aBank, evntIndex), getVertexVector(aBank, evntIndex));
		aParticle.changePid(getpid(aBank, evntIndex));
		return aParticle;

	}

	private Particle getTBParticle(DataBank aBank, int evntIndex) {
		Particle aParticle = new Particle();
		LorentzVector aLorentzVector = new LorentzVector();
		Vector3 momVector = new Vector3();
		momVector.setXYZ(aBank.getFloat("p0_x", evntIndex), aBank.getFloat("p0_y", evntIndex),
				aBank.getFloat("p0_z", evntIndex));
		double mass = PDGDatabase.getParticleMass(11);

		aLorentzVector.setVectM(momVector, mass);

		Vector3 vrtVector = new Vector3();
		vrtVector.setXYZ(aBank.getFloat("Vtx0_x", evntIndex), aBank.getFloat("Vtx0_y", evntIndex),
				aBank.getFloat("Vtx0_z", evntIndex));

		aParticle.setVector(aLorentzVector, vrtVector);
		aParticle.changePid(11);
		return aParticle;

	}

	private Particle getMCParticle(DataBank aBank, int evntIndex) {
		Particle aParticle = new Particle();
		aParticle.setVector(getMCLorentzVector(aBank, evntIndex), getVertexVector(aBank, evntIndex));
		aParticle.changePid(getMCpid(aBank, evntIndex));
		return aParticle;

	}

	private int getNEvents() {
		return this.hipoReader.getSize();
	}

	private List<Particle> getOnlyDCHits(List<Particle> recList, List<Particle> tbHitsList) {
		List<Particle> aList = new ArrayList<>();
		for (Particle recParticle : recList) {
			for (Particle genParticle : tbHitsList) {
				if (Math.abs(recParticle.px() - genParticle.px()) < 0.01
						&& Math.abs(recParticle.py() - genParticle.py()) < 0.01
						&& Math.abs(recParticle.pz() - genParticle.pz()) < 0.01) {
					aList.add(recParticle);
				}
			}
		}

		return aList;
	}

	private void compareLists(List<Particle> recList, List<Particle> DalitzList) {
		List<Particle> aList = new ArrayList<>();

		int epCount = 0;
		int emCount = 0;

		for (Particle recParticle : recList) {
			if (recParticle.pid() == 11) {
				emCount++;
			}
			if (recParticle.pid() == -11) {
				epCount++;
			}
		}

		int goodEp = 0;
		int goodEm = 0;

		if (epCount > 0 && emCount > 0) {
			for (Particle recParticle : recList) {
				for (Particle genParticle : DalitzList) {
					if (recParticle.pid() == genParticle.pid()) {
						if (Math.abs(recParticle.px() - genParticle.px()) < 0.01
								&& Math.abs(recParticle.py() - genParticle.py()) < 0.01
								&& Math.abs(recParticle.py() - genParticle.py()) < 0.01) {
							if (recParticle.pid() == 11) {
								aList.add(recParticle);
								goodEm++;
							}
							if (recParticle.pid() == -11) {
								aList.add(recParticle);
								goodEp++;
							}
						}
					}
				}
			}
		}

		if (goodEm == 1 && goodEp == 1) {
			double emP = 0.0;
			double epP = 0.0;
			double emTheta = 0.0;
			double epTheta = 0.0;
			for (Particle particle : aList) {
				if (particle.pid() == 11) {
					electronronHist.fill(particle.p(), particle.theta() * 180.0 / Math.PI);
					emP = particle.p();
					emTheta = particle.theta() * 180.0 / Math.PI;

				}
				if (particle.pid() == -11) {
					positronHist.fill(particle.p(), particle.theta() * 180.0 / Math.PI);
					epP = particle.p();
					epTheta = particle.theta() * 180.0 / Math.PI;
				}
			}
			pVsp.fill(epP, emP);
			thetaVstheta.fill(epTheta, emTheta);
		}
	}

	private void drawGenHists(List<Particle> aList) {
		// System.out.println("###############################");
		double emP = 0.0;
		double epP = 0.0;
		double emTheta = 0.0;
		double epTheta = 0.0;
		int nEp = 0;
		int nEm = 0;
		for (Particle particle : aList) {
			if (particle.pid() == 11) {
				electronronHistGen.fill(particle.p(), particle.theta() * 180.0 / Math.PI);
				emP = particle.p();
				emTheta = particle.theta() * 180.0 / Math.PI;
				nEm++;
			}
			if (particle.pid() == -11) {
				positronHistGen.fill(particle.p(), particle.theta() * 180.0 / Math.PI);
				epP = particle.p();
				epTheta = particle.theta() * 180.0 / Math.PI;
				nEp++;
			}
		}
		// System.out.println(nEm + " " + nEp);
		NvsN.fill(nEm, nEp);
		pVspGen.fill(epP, emP);
		thetaVsthetaGen.fill(epTheta, emTheta);
		// System.out.println(emP + " " + epP);
	}

	/**
	 * @return the nvsN
	 */
	public H2F getNvsN() {
		return NvsN;
	}

	/**
	 * @return the positronHistGen
	 */
	public H2F getPositronHistGen() {
		return positronHistGen;
	}

	/**
	 * @return the electronronHistGen
	 */
	public H2F getElectronronHistGen() {
		return electronronHistGen;
	}

	/**
	 * @return the pVspGen
	 */
	public H2F getpVspGen() {
		return pVspGen;
	}

	/**
	 * @return the thetaVsthetaGen
	 */
	public H2F getThetaVsthetaGen() {
		return thetaVsthetaGen;
	}

	/**
	 * @return the pVsp
	 */
	public H2F getpVsp() {
		return pVsp;
	}

	/**
	 * @return the thetaVstheta
	 */
	public H2F getThetaVstheta() {
		return thetaVstheta;
	}

	/**
	 * @return the positronHist
	 */
	public H2F getPositronHist() {
		return positronHist;
	}

	/**
	 * @return the electronronHist
	 */
	public H2F getElectronronHist() {
		return electronronHist;
	}

	public static void main(String[] args) {
		String dirName = "/Users/michaelkunkel/WORK/CLAS/CLAS12/CLAS12Data/EtaPrimeDilepton/";
		// String dirName =
		// "/Volumes/Mac_Storage/Work_Data/CLAS12/EtaPrimeDilepton/";
		String part1Name = "out_EtaPrimeDilepton_Tor-0.75Sol0.6_100.hipo";
		String part2Name = "out_EtaPrimeDilepton_Tor-0.75Sol0.6_101.hipo";
		String part3Name = "out_EtaPrimeDilepton_Tor-0.75Sol0.6_102.hipo";
		String part4Name = "out_EtaPrimeDilepton_Tor-0.75Sol0.6_103.hipo";
		String part5Name = "out_EtaPrimeDilepton_Tor-0.75Sol0.6_104.hipo";
		String part6Name = "out_EtaPrimeDilepton_Tor-0.75Sol0.6_105.hipo";
		String part7Name = "out_EtaPrimeDilepton_Tor-0.75Sol0.6_106.hipo";

		List<String> aList = new ArrayList<>();
		aList.add(dirName + part1Name);
		aList.add(dirName + part2Name);
		aList.add(dirName + part3Name);
		aList.add(dirName + part4Name);
		aList.add(dirName + part5Name);
		aList.add(dirName + part6Name);
		aList.add(dirName + part7Name);

		String[] array = aList.toArray(new String[0]);

		TriggerAnalysis triggerAnalysis = new TriggerAnalysis(array);
		triggerAnalysis.run();

		// TCanvas c1 = new TCanvas("Electron Plots", 800, 800);
		EmbeddedCanvas c1 = new EmbeddedCanvas();
		c1.setName("Reconstructed Electron Plots");
		c1.draw(triggerAnalysis.getElectronronHist());

		EmbeddedCanvas c2 = new EmbeddedCanvas();
		c2.setName("Reconstructed Positron Plots");
		c2.draw(triggerAnalysis.getPositronHist());

		EmbeddedCanvas c3 = new EmbeddedCanvas();
		c3.setName("Reconstructed P vs. P Plots");
		c3.draw(triggerAnalysis.getpVsp());

		EmbeddedCanvas c4 = new EmbeddedCanvas();
		c4.setName("Reconstructed Theta vs. Theta Plots");
		c4.draw(triggerAnalysis.getThetaVstheta());

		SaveCanvas.saveCanvas(c1);
		SaveCanvas.saveCanvas(c2);
		SaveCanvas.saveCanvas(c3);
		SaveCanvas.saveCanvas(c4);

		EmbeddedCanvas c5 = new EmbeddedCanvas();
		c5.setName("Generated Electron Plots");
		c5.draw(triggerAnalysis.getElectronronHistGen());

		EmbeddedCanvas c6 = new EmbeddedCanvas();
		c6.setName("Generated Positron Plots");
		c6.draw(triggerAnalysis.getPositronHistGen());

		EmbeddedCanvas c7 = new EmbeddedCanvas();
		c7.setName("Generated P vs. P Plots");
		c7.draw(triggerAnalysis.getpVspGen());

		EmbeddedCanvas c8 = new EmbeddedCanvas();
		c8.setName("Generated Theta vs. Theta Plots");
		c8.draw(triggerAnalysis.getThetaVsthetaGen());

		SaveCanvas.saveCanvas(c5);
		SaveCanvas.saveCanvas(c6);
		SaveCanvas.saveCanvas(c7);
		SaveCanvas.saveCanvas(c8);

		EmbeddedCanvas projection = new EmbeddedCanvas();
		projection.setName("projection");

		projection.divide(2, 2);
		projection.cd(0);
		H1F posPvP = triggerAnalysis.getpVspGen().projectionX();
		posPvP.setTitle("e+ projection for p vs. p plot");
		posPvP.setTitleX("p(e+) [GeV] ");
		projection.draw(posPvP);
		projection.cd(1);
		H1F elePvP = triggerAnalysis.getpVspGen().projectionY();
		elePvP.setTitle("e- projection for p vs. p plot");
		elePvP.setTitleX("p(e-) [GeV] ");

		projection.draw(elePvP);
		projection.cd(2);
		H1F posPvTheta = triggerAnalysis.getPositronHistGen().projectionX();
		posPvTheta.setTitle("e+ projection for p vs. theta plot");
		posPvTheta.setTitleX("p(e+) [GeV] ");

		projection.draw(posPvTheta);

		projection.cd(3);
		H1F elePvTheta = triggerAnalysis.getElectronronHistGen().projectionX();
		elePvTheta.setTitle("e- projection for p vs. theta plot");
		elePvTheta.setTitleX("p(e-) [GeV] ");

		projection.draw(elePvTheta);

		SaveCanvas.saveCanvas(projection);

		EmbeddedCanvas c9 = new EmbeddedCanvas();
		c9.setName("N vs N");
		c9.draw(triggerAnalysis.getNvsN());
		SaveCanvas.saveCanvas(c9);

		EmbeddedCanvas thetaprojection = new EmbeddedCanvas();
		thetaprojection.setName("thetaprojection");

		thetaprojection.divide(1, 2);
		thetaprojection.cd(0);
		H1F posthetaPvP = triggerAnalysis.getPositronHistGen().projectionY();
		posthetaPvP.setTitle("e+ projection for theta vs. p plot");
		posthetaPvP.setTitleX("theta(e+) [degree] ");
		thetaprojection.draw(posthetaPvP);
		thetaprojection.cd(1);
		H1F elethetavP = triggerAnalysis.getElectronronHistGen().projectionY();
		elethetavP.setTitle("e- projection for theta vs. p plot");
		elethetavP.setTitleX("theta(e-) [degree] ");

		thetaprojection.draw(elethetavP);

		SaveCanvas.saveCanvas(thetaprojection);

		System.exit(0);

	}

}
