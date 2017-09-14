package standAloneScripts;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

//Example of reading in a hipo-file
//Last date worked on: 08/28/17
//Michael C. Kunkel: m.kunkel@fz-juelich.de
//Daniel Lersch: d.kunkel@fz-juelich.de

import org.jlab.clas.physics.Particle;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.data.H1F;
import org.jlab.groot.ui.TCanvas;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

public class readFile {
	// Stuff for reading in a hipo-file:
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private HipoDataSource[] hipoReader = null;
	private int[] Nevents = null;
	private int NinputFiles = 0;
	private boolean runOnFarm = false;
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++

	// Stuff for looking at different banks:
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private DataEvent Event = null;

	private DataBank MCBank = null;
	private boolean is_MCBank = false;
	private int[] isMCEntrySet = null;

	private DataBank recParticleBank = null;
	private boolean is_recParticleBank = false;
	private int[] isRECEntrySet = null;

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++

	// Stuff for looking at specific reactions:
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private int[] particleIDS = null;
	private Particle[] genParticle = null;
	private Particle[] recParticle = null;
	private int[] countParticleinBank = null;
	private double[] particleScaling = null;
	private boolean isDecayFound = false;
	private List<Object> CandidateList = null;
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++

	// Some stuff for physics analysis:
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private double missing_mass_rec1 = 0;
	private double missing_mass_rec2 = 0;
	private double invariant_mass_rec1 = 0;
	private double invariant_mass_rec2 = 0;

	private double missing_mass_gen1 = 0;
	private double missing_mass_gen2 = 0;
	private double invariant_mass_gen1 = 0;
	private double invariant_mass_gen2 = 0;
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++

	// Define Histograms and Canvas here:
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private H1F mmHist_rec1 = null;
	private H1F mmHist_rec2 = null;
	private H1F imHist_rec1 = null;
	private H1F imHist_rec2 = null;
	private H1F mmHist_gen1 = null;
	private H1F mmHist_gen2 = null;
	private H1F imHist_gen1 = null;
	private H1F imHist_gen2 = null;
	private GraphErrors grEff = null;
	private TCanvas myCanvas1 = null;
	private TCanvas myCanvas2 = null;
	private TCanvas myCanvas3 = null;
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++

	// Stuff for determining the efficiency:
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private int countGenerated = 0;
	private int countReconstructed = 0;
	private String outFileName = null;

	private int[] countGeneratedPerIMbin = null;
	private int[] countReconstructedPerIMbin = null;
	private double[] efficiencyPerIMbin = null;
	private double[] DefficiencyPerIMbin = null;
	private double[] IMval = null;
	private double[] IMvalWidth = null;
	private int NIMpoints = 0;
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++

	public static void main(String[] args) throws FileNotFoundException {

		// String[] IN = {"/Users/daniellersch/Desktop/CLAS/EtaPrimeDilepton",
		// "EtaPrimeDilepton_Tor-1.0Sol0.8_", "1", "L", "100"};

		new readFile(args);
	}

	public readFile(String[] input) throws FileNotFoundException {

		// **************************************************
		// STAFF THAT HAS TO BE SET:
		int Nfiles = Integer.parseInt(input[2]);
		NIMpoints = Integer.parseInt(input[4]);
		setNinputFile(Nfiles); // --> Set the number of hipo-files you want to
								// analyse
		setReaders(input[0], input[1]); // -->Define the directoy, where the
										// out_bla.hipo files are located
		outFileName = input[0] + "/" + input[1] + "results.txt";
		// and put in the name of each file e.g. fullEtaPrime_....
		String[] myReaction = { "e-", "p", "e-", "e+" }; // Define the
															// DECAY-particles
															// here / Beam and
															// Target are
															// automatically set
		loadReaction(myReaction);
		// **************************************************

		runOnFarm = false;
		if (input[3].equals("L")) {
			runOnFarm = false;
		} else
			runOnFarm = true;

		if (!runOnFarm)
			setHistograms();
		doAnalysis();
		if (!runOnFarm)
			setANDdrawCanvas();
	}

	// Everything for reading in N hipo-files:
	// *********************************************************************************************
	// *********************************************************************************************
	public void setNinputFile(int Nin) {
		NinputFiles = Nin;
	}

	public int getNinputFile() {
		return NinputFiles;
	}

	public void setReaders(String DATADIR, String FileName) {
		hipoReader = new HipoDataSource[NinputFiles];
		Nevents = new int[NinputFiles];

		String hipoFileName;
		int fileIndex = 0; // In case your index starts at 1 or whatever...
		for (int i = 0; i < NinputFiles; i++) {
			fileIndex = i;
			hipoFileName = DATADIR + "/out_" + FileName + fileIndex + ".hipo";
			hipoReader[i] = new HipoDataSource();
			hipoReader[i].open(hipoFileName);
			Nevents[i] = hipoReader[i].getSize();
		}

	}

	// Look at various banks:
	public void setBanks(DataEvent event) {
		String MCName = "MC::Particle";
		MCBank = null;
		is_MCBank = false;

		String recParticleName = "REC::Particle";
		recParticleBank = null;
		is_recParticleBank = false;

		if (event.hasBank(MCName)) {
			is_MCBank = true;
			MCBank = event.getBank(MCName);
			isMCEntrySet = new int[MCBank.rows()];
			for (int mcE = 0; mcE < MCBank.rows(); mcE++) {
				isMCEntrySet[mcE] = 0;
			}
		}

		if (event.hasBank(recParticleName)) {
			is_recParticleBank = true;
			recParticleBank = event.getBank(recParticleName);
			isRECEntrySet = new int[recParticleBank.rows()];
			for (int recE = 0; recE < recParticleBank.rows(); recE++) {
				isRECEntrySet[recE] = 0;
			}
		}

	}
	// *********************************************************************************************
	// *********************************************************************************************

	// Loop over all files and events:
	// *********************************************************************************************
	// *********************************************************************************************
	public void doAnalysis() throws FileNotFoundException {
		// Loop over the number of hipo-files
		for (int iF = 0; iF < getNinputFile(); iF++) {
			// =============================================================
			// File-Loop

			// Loop over all events in each hipo-file:
			// =============================================================
			// Event-Loop
			for (int ev = 0; ev < Nevents[iF]; ev++) {
				Event = (DataEvent) hipoReader[iF].gotoEvent(ev);
				setBanks(Event); // Define the banks here...

				setParticle(2, particleIDS.length + 1);

				// Reconstructed Data:
				if (reactionFilter("REC", "bank") == 1) {
					missing_mass_rec1 = getMissingMass(recParticle, 0, 1, 2, 3, "");
					missing_mass_rec2 = getMissingMass(recParticle, 0, 1, 3, 4, "");

					invariant_mass_rec1 = getInvariantMass(recParticle, 4, 5, -1, -1, "");
					invariant_mass_rec2 = getInvariantMass(recParticle, 2, 5, 3, 4, "");

					if (!runOnFarm) {
						mmHist_rec1.fill(missing_mass_rec1);
						mmHist_rec2.fill(missing_mass_rec2);

						imHist_rec2.fill(invariant_mass_rec2);
						imHist_rec1.fill(invariant_mass_rec1);
					}

					if (doMMCut(missing_mass_rec1, 2.5) == 1) {
						countReconstructed++;
						scanIMbins(invariant_mass_rec1, "REC"); // This is the
																// "correct"
																// combination
																// for
																// reconstruction
					}

				}

				// Generated Data:
				if (reactionFilter("MC", "bank") == 1) {
					missing_mass_gen1 = getMissingMass(genParticle, 0, 1, 2, 3, "");
					missing_mass_gen2 = getMissingMass(genParticle, 0, 1, 3, 4, "");

					invariant_mass_gen1 = getInvariantMass(genParticle, 4, 5, -1, -1, "");
					invariant_mass_gen2 = getInvariantMass(genParticle, 2, 5, 3, 4, "");

					if (!runOnFarm) {
						mmHist_gen1.fill(missing_mass_gen1);
						mmHist_gen2.fill(missing_mass_gen2);

						imHist_gen1.fill(invariant_mass_gen1);
						imHist_gen2.fill(invariant_mass_gen2);
					}

					countGenerated++;
					scanIMbins(invariant_mass_gen2, "MC"); // This is the
															// "correct"
															// combination for
															// generation
				}

				resetParticle(2, particleIDS.length + 1);
			}
			// =============================================================
			// Event-Loop
		}
		// =============================================================
		// File-Loop
		resetParticle(0, particleIDS.length + 1);

		// Calculate efficiencies:
		double efficiency = ((double) countReconstructed / (double) countGenerated) * 100;

		getEfficiencyPerIMbin();

		PrintWriter writer = new PrintWriter(outFileName);
		writer.println("");
		writer.println("Number of generated events: " + countGenerated);
		writer.println("Number of reconstructed events: " + countReconstructed);
		writer.println("Efficiency [%] = " + efficiency);
		writer.println("");

		for (int k = 0; k < NIMpoints; k++) {
			writer.println("Inavriant mass: " + IMval[k] + " +- " + IMvalWidth[k] + " Acceptance: "
					+ efficiencyPerIMbin[k] + " +-  " + DefficiencyPerIMbin[k]);
			if (!runOnFarm)
				grEff.addPoint(IMval[k], efficiencyPerIMbin[k], IMvalWidth[k], efficiencyPerIMbin[k]);
		}

		writer.close();

		countGenerated = 0;
		countReconstructed = 0;
		efficiency = 0;
	}
	// *********************************************************************************************
	// *********************************************************************************************

	// Get information from particle-banks:
	// *********************************************************************************************
	// *********************************************************************************************

	// PID:
	public int[] getPIDfromBank(DataBank Bank, String Option) {
		int Nrows = Bank.rows();
		int out[] = null;
		out = new int[Nrows];

		if (Option.equals("bank")) {
			for (int t = 0; t < Nrows; t++) {
				out[t] = Bank.getInt("pid", t);
			}
		}

		return out;
	}

	// Momentum:
	public double[][] getMomentumfromBank(DataBank Bank) {
		int Nrows = Bank.rows();
		double out[][] = null;
		out = new double[3][Nrows];

		for (int t = 0; t < Nrows; t++) {
			out[0][t] = Bank.getFloat("px", t);
			out[1][t] = Bank.getFloat("py", t);
			out[2][t] = Bank.getFloat("pz", t);
		}

		return out;
	}

	// Vertex:
	public double[][] getVertexfromBank(DataBank Bank) {
		int Nrows = Bank.rows();
		double out[][] = null;
		out = new double[3][Nrows];

		for (int t = 0; t < Nrows; t++) {
			out[0][t] = Bank.getFloat("vx", t);
			out[1][t] = Bank.getFloat("vy", t);
			out[2][t] = Bank.getFloat("vz", t);
		}

		return out;
	}

	// Finally, get the Vector:
	public Particle[] getParticlesfromBank(DataBank Bank, String Option) {
		int Nrows = Bank.rows();
		Particle[] part = null;
		part = new Particle[Nrows];

		double[][] mom = null;
		mom = getMomentumfromBank(Bank);
		double[][] vert = null;
		vert = getVertexfromBank(Bank);
		int[] id = null;
		id = getPIDfromBank(Bank, Option);

		int NdecayParticles = particleIDS.length;

		for (int pt = 0; pt < NdecayParticles; pt++) {
			countParticleinBank[pt] = 0;
		}

		CandidateList = new ArrayList<Object>(Nrows);

		for (int t = 0; t < Nrows; t++) {
			part[t] = new Particle();
			part[t].setVector(id[t], mom[0][t], mom[1][t], mom[2][t], vert[0][t], vert[1][t], vert[2][t]);

			CandidateList.add(t, part[t]);

			for (int pt = 0; pt < NdecayParticles; pt++) {
				if (particleIDS[pt] == id[t]) {
					countParticleinBank[pt]++;
				}
			}
		}

		// Now check, if the desired reaction is present:
		isDecayFound = false;
		double val = 0;
		for (int h = 0; h < NdecayParticles; h++) {
			val += countParticleinBank[h] * particleScaling[h];
		}

		if (val >= NdecayParticles) {
			isDecayFound = true;
		} else
			isDecayFound = false;

		return part;
	}
	// *********************************************************************************************
	// *********************************************************************************************

	// Set histograms:
	// *********************************************************************************************
	// *********************************************************************************************
	public void setHistograms() {
		this.mmHist_rec1 = new H1F("mmHist_rec1", 100, 0, 4.0);
		mmHist_rec1.setTitleX("Missing Mass M_x(pe-)");
		mmHist_rec1.setTitleY("Entries");
		mmHist_rec1.setTitle("Reconstructed Particles: Comb1");

		this.mmHist_rec2 = new H1F("mmHist_rec2", 100, 0, 4.0);
		mmHist_rec2.setTitleX("Missing Mass M_x(pe-)");
		mmHist_rec2.setTitleY("Entries");
		mmHist_rec2.setTitle("Reconstructed Particles: Comb2");

		this.mmHist_gen1 = new H1F("mmHist_gen1", 100, 0, 4.0);
		mmHist_gen1.setTitleX("Missing Mass M_x(pe-)");
		mmHist_gen1.setTitleY("Entries");
		mmHist_gen1.setTitle("Generated Particles: Comb1");

		this.mmHist_gen2 = new H1F("mmHist_gen2", 100, 0, 4.0);
		mmHist_gen2.setTitleX("Missing Mass M_x(pe-)");
		mmHist_gen2.setTitleY("Entries");
		mmHist_gen2.setTitle("Generated Particles: Comb2");

		this.imHist_rec1 = new H1F("imHist_rec1", 100, 0, 1.0);
		imHist_rec1.setTitleX("Invariant Mass M(e-e+)");
		imHist_rec1.setTitleY("Entries");
		imHist_rec1.setTitle("Reconstructed Particles: Comb1");

		this.imHist_rec2 = new H1F("imHist_rec2", 100, 0, 1.0);
		imHist_rec2.setTitleX("Invariant Mass M(e-e+)");
		imHist_rec2.setTitleY("Entries");
		imHist_rec2.setTitle("Reconstructed Particles: Comb2");

		this.imHist_gen1 = new H1F("imHist_gen1", 100, 0, 1.0);
		imHist_gen1.setTitleX("Invariant Mass M(e-e+)");
		imHist_gen1.setTitleY("Entries");
		imHist_gen1.setTitle("Generated Particles: Comb1");

		this.imHist_gen2 = new H1F("imHist_gen2", 100, 0, 1.0);
		imHist_gen2.setTitleX("Invariant Mass M(e-e+)");
		imHist_gen2.setTitleY("Entries");
		imHist_gen2.setTitle("Generated Particles: Comb2");

		this.grEff = new GraphErrors();
		grEff.setTitle("Acceptance per IM(e+e-) bin");
		grEff.setTitleX("Invariant Mass(e+e-)");
		grEff.setTitleY("Acceptance [%]");
	}
	// *********************************************************************************************
	// *********************************************************************************************

	// Define canvas here:
	// *********************************************************************************************
	// *********************************************************************************************
	public void setANDdrawCanvas() {
		this.myCanvas1 = new TCanvas("myCanvas1", 1200, 800);
		myCanvas1.divide(2, 2);
		myCanvas1.cd(0);
		myCanvas1.draw(mmHist_rec1);
		myCanvas1.cd(1);
		myCanvas1.draw(mmHist_gen1);
		myCanvas1.cd(2);
		myCanvas1.draw(mmHist_rec2);
		myCanvas1.cd(3);
		myCanvas1.draw(mmHist_gen2);

		this.myCanvas2 = new TCanvas("myCanvas2", 1200, 800);
		myCanvas2.divide(2, 2);
		myCanvas2.cd(0);
		myCanvas2.draw(imHist_rec1);
		myCanvas2.cd(1);
		myCanvas2.draw(imHist_gen1);
		myCanvas2.cd(2);
		myCanvas2.draw(imHist_rec2);
		myCanvas2.cd(3);
		myCanvas2.draw(imHist_gen2);

		this.myCanvas3 = new TCanvas("myCanvas3", 500, 500);
		myCanvas3.draw(grEff);
	}
	// *********************************************************************************************
	// *********************************************************************************************

	// Stuff for looking at specific particle types within a given reaction:
	// *********************************************************************************************
	// *********************************************************************************************
	public void loadReaction(String[] Reaction) {

		int NdecayParticles = Reaction.length;
		particleIDS = new int[NdecayParticles];

		String[] names = { "p", "e-", "e+", "pi+", "pi-", "g" };
		int[] numbers = { 2212, 11, -11, 211, -211, 22 };

		recParticle = new Particle[NdecayParticles + 2];
		genParticle = new Particle[NdecayParticles + 2];

		countParticleinBank = new int[NdecayParticles];
		particleScaling = new double[NdecayParticles];

		setParticle(0, 1);
		recParticle[0].setVector(11, 0.0, 0.0, 10.6, 0.0, 0.0, 0.0);
		recParticle[1].setVector(2212, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
		genParticle[0].setVector(11, 0.0, 0.0, 10.6, 0.0, 0.0, 0.0);
		genParticle[1].setVector(2212, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

		// Get the particle ids according to the specified reaction:
		for (int r = 0; r < NdecayParticles; r++) {
			countParticleinBank[r] = 0;
			for (int h = 0; h < names.length; h++) {
				if (Reaction[r].equals(names[h])) {
					particleIDS[r] = numbers[h];
				}
			}
		}

		// Now, check how often each particle type is present:
		int count_particle_type = 0;
		for (int r1 = 0; r1 < NdecayParticles; r1++) {
			count_particle_type = 1;

			for (int r2 = 0; r2 < NdecayParticles; r2++) {
				if (r2 != r1) {
					if (particleIDS[r1] == particleIDS[r2]) {
						count_particle_type++;
					}
				}
			}
			particleScaling[r1] = 1.0 / (double) count_particle_type;
		}

		countGenerated = countReconstructed = 0;

		countGeneratedPerIMbin = new int[NIMpoints];
		countReconstructedPerIMbin = new int[NIMpoints];
		efficiencyPerIMbin = new double[NIMpoints];
		DefficiencyPerIMbin = new double[NIMpoints];

		IMval = new double[NIMpoints];
		IMvalWidth = new double[NIMpoints];

		for (int h = 0; h < NIMpoints; h++) {
			countGeneratedPerIMbin[h] = countReconstructedPerIMbin[h] = 0;

			efficiencyPerIMbin[h] = DefficiencyPerIMbin[h] = IMval[h] = IMvalWidth[h] = 0;
		}

	}

	// Set and reset particles:
	public void resetParticle(int firstParticle, int lastParticle) {
		for (int u = firstParticle; u < lastParticle + 1; u++) {
			genParticle[u] = null;
			recParticle[u] = null;
		}
	}

	public void setParticle(int firstParticle, int lastParticle) {
		for (int u = firstParticle; u < lastParticle + 1; u++) {
			genParticle[u] = new Particle();
			recParticle[u] = new Particle();
		}
	}

	// Reaction filter: is one if all particles that have been specified
	// in the reaction string are found in the rec::particle-bank:
	public int reactionFilter(String Option, String PIDOption) {
		int out = 0;

		int NdecayParticles = particleIDS.length;
		DataBank currentBank = null;
		boolean is_currentBank = false;
		int[] isEntryUsed = null;

		if (Option.equals("REC")) {
			currentBank = recParticleBank;
			is_currentBank = is_recParticleBank;
			isEntryUsed = isRECEntrySet;
		} else if (Option.equals("MC")) {
			currentBank = MCBank;
			is_currentBank = is_MCBank;
			isEntryUsed = isMCEntrySet;
		}

		if (is_currentBank && currentBank.rows() >= NdecayParticles) {
			Particle[] part = null;
			part = getParticlesfromBank(currentBank, PIDOption);

			boolean is_pid_not_found = true;
			int count_matched_particles = 0;

			for (int dP = 0; dP < NdecayParticles; dP++) {
				// ---------------------------------------------------------------------------------------
				is_pid_not_found = true;
				for (int bP = 0; bP < part.length; bP++) {
					if (particleIDS[dP] == part[bP].pid() && is_pid_not_found && isEntryUsed[bP] == 0) {
						is_pid_not_found = false;

						if (Option.equals("REC")) {
							recParticle[dP + 2].copy(part[bP]);
						} else if (Option.equals("MC")) {
							genParticle[dP + 2].copy(part[bP]);
						}
						isEntryUsed[bP] = 1;
						count_matched_particles++;
					}
				}
				// ---------------------------------------------------------------------------------------

			}

			if (count_matched_particles == NdecayParticles) {
				out = 1;
			} else
				out = 0;

		}

		return out;
	}

	// *********************************************************************************************
	// *********************************************************************************************

	// Analysis of physics-vectors:
	// *********************************************************************************************
	// *********************************************************************************************
	// Simple adding of two 4vectors:
	public Particle add4Vectors(Particle firstVec, Particle secondVec, int Sign) {
		Particle part = null;
		part = new Particle();

		part.copy(firstVec);
		part.combine(secondVec, Sign);

		return part;
	}

	// Adding several vectors at once:
	public Particle sum4Vectors(Particle[] yourParticles, int start, int end, int Sign) {
		Particle part = null;
		part = new Particle();

		part.copy(yourParticles[start]);
		for (int u = start + 1; u < end + 1; u++) {
			part.combine(yourParticles[u], Sign);
		}

		return part;
	}

	// Get the missing mass:
	public double getMissingMass(Particle[] yourParticles, int inStart, int inEnd, int outStart, int outEnd,
			String Option) {
		double mass = 0;

		Particle inVec = null;
		inVec = new Particle();
		Particle outVec = null;
		outVec = new Particle();

		inVec = sum4Vectors(yourParticles, inStart, inEnd, +1);
		outVec = sum4Vectors(yourParticles, outStart, outEnd, +1);

		Particle missingVec = null;
		missingVec = new Particle();
		missingVec = inVec;
		missingVec.combine(outVec, -1);

		if (Option.equals("2")) {
			mass = missingVec.mass2();
		} else
			mass = missingVec.mass();

		return mass;
	}

	// get the invariant mass:
	public double getInvariantMass(Particle[] yourParticles, int start, int end, int startExcl, int endExcl,
			String Option) {
		double mass = 0;

		Particle part = null;
		part = new Particle();

		if (!(startExcl >= 0 && endExcl >= 0)) {
			part = sum4Vectors(yourParticles, start, end, +1);
		} else {
			Particle part1 = null;
			part1 = new Particle();
			part1 = sum4Vectors(yourParticles, start, startExcl - 1, +1);

			Particle part2 = null;
			part2 = new Particle();
			part2 = sum4Vectors(yourParticles, endExcl + 1, end, +1);

			part = add4Vectors(part1, part2, +1);
		}

		if (Option.equals("2")) {
			mass = part.mass2();
		} else
			mass = part.mass();

		return mass;
	}

	// Do a cut on the missing mass of e-p:
	public int doMMCut(double mm, double nSigma) {
		int out = 0;

		double mean = 0.9782;
		double sigma = 0.07799;

		if (mm <= mean + nSigma * sigma) {
			out = 1;
		} else
			out = 0;

		return out;
	}

	// Get efficiency per invariant mass bin:
	public void scanIMbins(double IM, String Option) {
		double maxIM = 1.0;
		double minIM = 0.0;
		double step = (maxIM - minIM) / NIMpoints;

		// -------------------------------------------------------------
		for (int k = 0; k < NIMpoints; k++) {

			if (IM >= step * k && IM < step * (k + 1)) {
				if (Option.equals("REC")) {
					countReconstructedPerIMbin[k]++;
				} else if (Option.equals("MC")) {
					countGeneratedPerIMbin[k]++;
				}
			}

		}
		// -------------------------------------------------------------
	}

	public void getEfficiencyPerIMbin() {
		double maxIM = 1.0;
		double minIM = 0.0;
		double step = (maxIM - minIM) / NIMpoints;

		for (int k = 0; k < NIMpoints; k++) {
			if (countGeneratedPerIMbin[k] != 0) {
				efficiencyPerIMbin[k] = ((double) countReconstructedPerIMbin[k] / (double) countGeneratedPerIMbin[k]);
				DefficiencyPerIMbin[k] = (Math.sqrt((double) countReconstructedPerIMbin[k])
						/ (double) countGeneratedPerIMbin[k]);
				IMval[k] = step * (k + 0.5);
				IMvalWidth[k] = 0.5 * step;
			}
		}

	}

	// *********************************************************************************************
	// *********************************************************************************************

	// ===================== FIN =========================

}
