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

import org.jlab.groot.data.DataVector;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.math.F1D;
import org.jlab.groot.ui.TCanvas;

import domain.DalitzEvent.KrollWadaFormula;
import domain.utils.SaveCanvas;
import services.MainService;
import services.ServiceManager;

public class AnalysisPlots {

	private MainService mainService = null;

	private H1F genPlot1 = null;
	private H1F genPlot2 = null;
	private H1F recPlot1 = null;
	private H1F recPlot2 = null;

	public AnalysisPlots() {
		this.mainService = ServiceManager.getSession();

		genPlot1 = new H1F();
		genPlot2 = new H1F();
		recPlot1 = new H1F();
		recPlot2 = new H1F();

	}

	public void run() {
		plotHistograms();
	}

	private void plotHistograms() {
		plotGen();
		plotRec();
		acceptance();
		// plotQ();
		// plotW();
		// plotWandQ();
	}

	private void plotGen() {
		// Gen Mxpe-

		TCanvas c1 = new TCanvas("Generated Mx(pe-)", 800, 800);
		c1.divide(1, 3);
		String cuts[] = { "genMxPEm2" };
		double cutVals[] = { 0.957 };
		double[] cutLimits = { 2.5 * 0.03 };
		String[] cutOperations = { "<" };
		String[] cutSeperators = { "" };

		c1.cd(0);
		H1F mxPEm2 = Mplots("genMxPEm2", "Mx(pe-) [GeV]", 100, 0.8, 3.6, 4, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators);

		c1.draw(mxPEm2);
		H1F mxPEm1 = Mplots("genMxPEm1", "Mx(pe-) [GeV]", 100, 0.8, 3.6, 2, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators);
		c1.cd(1);
		c1.draw(mxPEm1);
		c1.cd(2);
		c1.draw(mxPEm2);
		c1.draw(mxPEm1, "same");

		TCanvas cprint = new TCanvas("Generated Mx(pe-) with fits", 800, 800);
		cprint.cd(0);
		cprint.draw(mxPEm2);
		cprint.draw(mxPEm1, "same");

		SaveCanvas.saveCanvas(cprint);
		// Gen Me+e-
		TCanvas c2 = new TCanvas("Generated M(e+e-)", 800, 800);
		c2.divide(1, 3);
		H1F mEpEm2test = Mplots("genMEmEp2", "M(e+e-) [GeV]", 100, 0.0, 3.6, 2, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators);

		H1F mEpEm1test = Mplots("genMEmEp1", "M(e+e-) [GeV]", 100, 0.0, 3.6, 4, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators);
		c2.cd(0);
		c2.draw(mEpEm1test);
		c2.cd(1);
		c2.draw(mEpEm2test);
		c2.cd(2);
		c2.draw(mEpEm1test);
		c2.draw(mEpEm2test, "same");

		// MxPe Cut

		TCanvas c3 = new TCanvas("Generated Mx(pe-) with cuts", 800, 800);
		c3.divide(1, 3);
		c3.cd(0);
		H1F mxPEm2Cut = Mplots("genMxPEm2", "Mx(pe-) [GeV]", 100, 0.8, 3.6, 4, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators);

		c3.draw(mxPEm2Cut);
		cuts[0] = "genMxPEm1";

		H1F mxPEm1Cut = Mplots("genMxPEm1", "Mx(pe-) [GeV]", 100, 0.8, 3.6, 2, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators);
		c3.cd(1);
		c3.draw(mxPEm1Cut);
		c3.cd(2);
		c3.draw(mxPEm2Cut);
		c3.draw(mxPEm1Cut, "same");

		// MEpEmCut
		TCanvas c4 = new TCanvas("Generated M(e+e-) with cuts", 800, 800);
		c4.divide(1, 3);
		H1F mEpEm2Cut = Mplots("genMEmEp2", "M(e+e-) [GeV]", 100, 0.0, 3.6, 2, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators);

		cuts[0] = "genMxPEm2";

		H1F mEpEm1Cut = Mplots("genMEmEp1", "M(e+e-) [GeV]", 100, 0.0, 3.6, 4, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators);
		c4.cd(0);
		c4.draw(mEpEm1Cut);
		c4.cd(1);
		c4.draw(mEpEm2Cut);
		c4.cd(2);
		c4.draw(mEpEm1Cut);
		c4.draw(mEpEm2Cut, "same");

		TCanvas c5 = new TCanvas("Generated M(e+e-) with cuts Scaled", 800, 800);
		setCanvasAttributes(c5);
		cuts[0] = "genMxPEm1";

		H1F mEpEm2CutScaled = Mplots("genMEmEp2", "M(e+e-) [GeV]", 100, 0.0, 0.96, 2, cuts, cutVals, cutLimits,
				cutOperations, cutSeperators);

		cuts[0] = "genMxPEm2";

		H1F mEpEm1CutScaled = Mplots("genMEmEp1", "M(e+e-) [GeV]", 100, 0.0, 0.96, 4, cuts, cutVals, cutLimits,
				cutOperations, cutSeperators);
		c5.cd(0);
		c5.draw(mEpEm1CutScaled);
		c5.draw(mEpEm2CutScaled, "same");

		SaveCanvas.saveCanvas(c5);
		genPlot1 = mEpEm1CutScaled.histClone("genPlot1");
		genPlot2 = mEpEm2CutScaled.histClone("genPlot2");

	}

	private void plotRec() {
		// Rec Mxpe-

		TCanvas c1 = new TCanvas("Reconstructed Mx(pe-)", 800, 800);
		TCanvas printCanvas = new TCanvas("Reconstructed Mx(pe-) with fits", 800, 800);
		TCanvas c2 = new TCanvas("Reconstructed M(e+e-)", 800, 800);
		TCanvas c3 = new TCanvas("Reconstructed Mx(pe-) with cuts", 800, 800);
		TCanvas c4 = new TCanvas("Reconstructed M(e+e-) with cuts", 800, 800);
		TCanvas c5 = new TCanvas("Reconstructed M(e+e-) with cuts Scaled", 800, 800);

		c1.divide(1, 3);
		setCanvasAttributes(c1);
		c2.divide(1, 3);
		setCanvasAttributes(c2);
		c3.divide(1, 3);
		setCanvasAttributes(c3);
		c4.divide(1, 3);
		setCanvasAttributes(c4);
		setCanvasAttributes(c5);

		F1D f1 = new F1D("f1", "[amp]*gaus(x,[mean],[sigma])+[p0]+[p1]*x+[p2]*x*x", 0.8, 1.65);
		F1D f2 = new F1D("f2", "[amp]*gaus(x,[mean],[sigma])+[p0]+[p1]*x+[p2]*x*x", 0.8, 1.65);

		setEtaFitParms(f1);
		setFunctionAttributes(f1);

		setEtaFitParms(f2);
		setFunctionAttributes(f2);

		String[] cuts = { "genMxPEm2", "recMxPEm1", "recMxPEm2" };
		double[] cutVals = { 0.957, 0.0, 0.0 };
		double[] cutLimits = { 2.5 * 0.03, 0.0, 0.0 };
		String[] cutOperations = { "<", ">", ">" };
		String[] cutSeperators = { "&& ", "&& ", "" };
		String[] cutDesign = { "sym", "side", "side" };

		H1F mxPEm2 = Mplots("recMxPEm2", "Mx(pe-) [GeV]", 100, 0.8, 3.6, 2, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators, cutDesign);

		H1F mxPEm1 = Mplots("recMxPEm1", "Mx(pe-) [GeV]", 100, 0.8, 3.6, 4, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators, cutDesign);

		H1F mEpEm2test = Mplots("recMEmEp2", "M(e+e-) [GeV]", 100, 0.0, 3.6, 4, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators, cutDesign);

		H1F mEpEm1test = Mplots("recMEmEp1", "M(e+e-) [GeV]", 100, 0.0, 3.6, 2, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators, cutDesign);

		f1.setParameter(0, mxPEm1.getIntegral());

		f2.setParameter(0, mxPEm2.getIntegral() / 100.0);

		c1.cd(0);
		DataFitter.fit(f1, mxPEm1, "QREM"); // No options uses error for sigma
		f1.show();
		c1.draw(f1, "same");
		c1.draw(mxPEm1);

		c1.cd(1);
		DataFitter.fit(f2, mxPEm2, "QREM"); // No options uses error for sigma
		c1.draw(mxPEm2);

		c1.cd(2);
		c1.draw(mxPEm1);
		c1.draw(mxPEm2, "same");

		printCanvas.draw(mxPEm1);
		printCanvas.draw(mxPEm2, "same"); // MxPe Cut
		SaveCanvas.saveCanvas(printCanvas);

		//
		// Rec Me+e-

		c2.cd(0);
		c2.draw(mEpEm2test);
		c2.cd(1);
		c2.draw(mEpEm1test);
		c2.cd(2);
		c2.draw(mEpEm2test);
		c2.draw(mEpEm1test, "same");

		// now we have fit parms, lets use them
		String[] cutsNew = { "genMxPEm2", "recMxPEm1" };
		double[] cutValsNew = { 0.957, f1.getParameter(1) };
		double[] cutLimitsNew = { 2.5 * 0.03, 2.5 * f1.getParameter(2) };
		String[] cutOperationsNew = { "<", "<" };
		String[] cutSeperatorsNew = { "&& ", "" };
		String[] cutDesignNew = { "sym", "sym" };
		H1F mxPEm1Cut = Mplots("recMxPEm1", "Mx(pe-) [GeV]", 100, 0.8, 3.6, 4, cutsNew, cutValsNew, cutLimitsNew,
				cutOperationsNew, cutSeperatorsNew);

		cutsNew[1] = "recMxPEm2";
		cutValsNew[1] = f2.getParameter(1);
		cutLimitsNew[1] = 2.5 * f2.getParameter(2);
		H1F mxPEm2Cut = Mplots("recMxPEm2", "Mx(pe-) [GeV]", 100, 0.8, 3.6, 2, cutsNew, cutValsNew, cutLimitsNew,
				cutOperationsNew, cutSeperatorsNew);

		cutsNew[1] = "recMxPEm2";

		H1F mEpEm1Cuttest = Mplots("recMEmEp1", "M(e+e-) [GeV]", 100, 0.0, 3.6, 2, cutsNew, cutValsNew, cutLimitsNew,
				cutOperationsNew, cutSeperatorsNew);

		cutsNew[1] = "recMxPEm1";
		cutValsNew[1] = f1.getParameter(1);
		H1F mEpEm2Cuttest = Mplots("recMEmEp2", "M(e+e-) [GeV]", 100, 0.0, 3.6, 4, cutsNew, cutValsNew, cutLimitsNew,
				cutOperationsNew, cutSeperatorsNew);

		cutsNew[1] = "recMxPEm2";
		cutValsNew[1] = f2.getParameter(1);

		H1F mEpEm1CutScaled = Mplots("recMEmEp1", "M(e+e-) [GeV]", 100, 0.0, 0.96, 2, cutsNew, cutValsNew, cutLimitsNew,
				cutOperationsNew, cutSeperatorsNew);

		cutsNew[1] = "recMxPEm1";
		cutValsNew[1] = f1.getParameter(1);
		H1F mEpEm2CutScaled = Mplots("recMEmEp2", "M(e+e-) [GeV]", 100, 0.0, 0.96, 4, cutsNew, cutValsNew, cutLimitsNew,
				cutOperationsNew, cutSeperatorsNew);

		// get cuts from fits

		c3.cd(0);
		c3.draw(mxPEm1Cut);
		c3.cd(1);
		c3.draw(mxPEm2Cut);
		c3.cd(2);
		c3.draw(mxPEm1Cut);
		c3.draw(mxPEm2Cut, "same");

		// MEpEmCut

		c4.cd(0);
		c4.draw(mEpEm2Cuttest);
		c4.cd(1);
		c4.draw(mEpEm1Cuttest);
		c4.cd(2);
		c4.draw(mEpEm2Cuttest);
		c4.draw(mEpEm1Cuttest, "same");

		c5.cd(0);
		c5.draw(mEpEm2CutScaled);
		c5.draw(mEpEm1CutScaled, "same");

		SaveCanvas.saveCanvas(c5);

		recPlot1 = mEpEm1CutScaled.histClone("recPlot1");
		recPlot2 = mEpEm2CutScaled.histClone("recPlot2");

	}

	private void plotQ() {
		TCanvas c1 = new TCanvas("Reconstructed Qsqr with cuts", 800, 800);
		c1.divide(1, 2);

		String[] cuts = { "genMxPEm2", "recMxPEm1" };
		double[] cutVals = { 0.957, 0.96637 };
		double[] cutLimits = { 2.5 * 0.03, 2.5 * 0.03 };
		String[] cutOperations = { "<", "<" };
		String[] cutSeperators = { "&& ", "" };

		c1.cd(0);
		H1F recMEmEp2Scaled = Mplots("recQSq2", "M(e+e-) [GeV]", 100, 0.0, 4.0, 2, cuts, cutVals, cutLimits,
				cutOperations, cutSeperators);
		c1.draw(recMEmEp2Scaled);
		cuts[1] = "recMxPEm2";
		H1F recMEmEp1Scaled = Mplots("recQSq1", "M(e+e-) [GeV]", 100, 0.0, 4.0, 4, cuts, cutVals, cutLimits,
				cutOperations, cutSeperators);
		c1.draw(recMEmEp1Scaled, "same");

		H1F total = recMEmEp2Scaled.histClone("total");
		total.add(recMEmEp1Scaled);
		c1.cd(1);
		c1.draw(total);

		// gen plots

		// MEpEmCut
		String cutsGen[] = { "genMxPEm1" };
		double cutValsGen[] = { 0.957 };
		double[] cutLimitsGen = { 2.5 * 0.03 };
		String[] cutOperationsGen = { "<" };
		String[] cutSeperatorsGen = { "" };

		TCanvas c2 = new TCanvas("Generated Qsqr with cuts", 800, 800);
		c2.divide(1, 2);
		H1F genMEmEp2Scaled = Mplots("genQSq2", "M(e+e-) [GeV]", 100, 0.0, 4.0, 4, cutsGen, cutValsGen, cutLimitsGen,
				cutOperationsGen, cutSeperatorsGen);

		cutsGen[0] = "genMxPEm2";

		H1F genMEmEp1Scaled = Mplots("genQSq1", "M(e+e-) [GeV]", 100, 0.0, 4.0, 2, cutsGen, cutValsGen, cutLimitsGen,
				cutOperationsGen, cutSeperatorsGen);
		c2.cd(0);
		c2.draw(genMEmEp1Scaled);
		c2.draw(genMEmEp2Scaled, "same");

		H1F gentotal = genMEmEp2Scaled.histClone("total");
		gentotal.add(genMEmEp1Scaled);
		c2.cd(1);
		c2.draw(gentotal);

		TCanvas c3 = new TCanvas("Acceptance in  Qsqr", 800, 800);

		H1F totalClone = total.histClone("totalClone");
		totalClone.setFillColor(5);
		totalClone.setLineColor(1);
		totalClone.divide(gentotal);
		c3.draw(totalClone, "E");

		SaveCanvas.saveCanvas(c1);
		SaveCanvas.saveCanvas(c2);
		SaveCanvas.saveCanvas(c3);
	}

	private void plotW() {
		TCanvas c1 = new TCanvas("Reconstructed W with cuts", 800, 800);
		c1.divide(1, 2);

		String[] cuts = { "genMxPEm2", "recMxPEm1" };
		double[] cutVals = { 0.957, 0.96637 };
		double[] cutLimits = { 2.5 * 0.03, 2.5 * 0.03 };
		String[] cutOperations = { "<", "<" };
		String[] cutSeperators = { "&& ", "" };

		c1.cd(0);
		H1F recMEmEp2Scaled = Mplots("recW2", "M(e+e-) [GeV]", 100, 0.0, 5.0, 2, cuts, cutVals, cutLimits,
				cutOperations, cutSeperators);
		c1.draw(recMEmEp2Scaled);
		cuts[1] = "recMxPEm2";
		H1F recMEmEp1Scaled = Mplots("recW1", "M(e+e-) [GeV]", 100, 0.0, 5.0, 4, cuts, cutVals, cutLimits,
				cutOperations, cutSeperators);
		c1.draw(recMEmEp1Scaled, "same");

		H1F total = recMEmEp2Scaled.histClone("total");
		total.add(recMEmEp1Scaled);
		c1.cd(1);
		c1.draw(total);

		// gen plots

		// MEpEmCut
		String cutsGen[] = { "genMxPEm1" };
		double cutValsGen[] = { 0.957 };
		double[] cutLimitsGen = { 2.5 * 0.03 };
		String[] cutOperationsGen = { "<" };
		String[] cutSeperatorsGen = { "" };

		TCanvas c2 = new TCanvas("Generated W with cuts", 800, 800);
		c2.divide(1, 2);
		H1F genMEmEp2Scaled = Mplots("genW2", "M(e+e-) [GeV]", 100, 0.0, 5.0, 4, cutsGen, cutValsGen, cutLimitsGen,
				cutOperationsGen, cutSeperatorsGen);

		cutsGen[0] = "genMxPEm2";

		H1F genMEmEp1Scaled = Mplots("genW1", "M(e+e-) [GeV]", 100, 0.0, 5.0, 2, cutsGen, cutValsGen, cutLimitsGen,
				cutOperationsGen, cutSeperatorsGen);
		c2.cd(0);
		c2.draw(genMEmEp1Scaled);
		c2.draw(genMEmEp2Scaled, "same");

		H1F gentotal = genMEmEp2Scaled.histClone("total");
		gentotal.add(genMEmEp1Scaled);
		c2.cd(1);
		c2.draw(gentotal);

		TCanvas c3 = new TCanvas("Acceptance in  W", 800, 800);

		// c3.getCanvas().divide(1, 2);

		H1F totalClone = total.histClone("totalClone");
		totalClone.setFillColor(5);
		totalClone.setLineColor(1);
		totalClone.divide(gentotal);
		c3.draw(totalClone, "E");
		// c3.getCanvas().getPad(0).draw(totalClone, "E");

		// im not trusting the error propagation of the histogram division, lets
		// check manually
		H1F errorPlot = new H1F("Error Plot", 100, 0.0, 0.965);
		for (int i = 0; i < total.getXaxis().getNBins(); i++) {
			errorPlot.setBinContent(i, totalClone.getBinError(i));
		}
		// c3.getCanvas().getPad(1).draw(errorPlot, "");

		// TCanvas c4 = new TCanvas("Error in Acceptance in M(e+e-)", 800, 800);
		// c4.draw(errorPlot);

		SaveCanvas.saveCanvas(c1);
		SaveCanvas.saveCanvas(c2);
		SaveCanvas.saveCanvas(c3);

	}

	private void plotWandQ() {
		TCanvas c1 = new TCanvas("Reconstructed W with cuts", 800, 800);
		c1.divide(1, 3);

		String[] cuts = { "genMxPEm2", "recMxPEm1" };
		double[] cutVals = { 0.957, 0.96637 };
		double[] cutLimits = { 2.5 * 0.03, 2.5 * 0.03 };
		String[] cutOperations = { "<", "<" };
		String[] cutSeperators = { "&& ", "" };
		String[] cutDesign = { "sym", "sym" };

		c1.cd(0);
		H2F recMEmEp2Scaled = Mplots2D("recW2", "recQSq2", "recW2 [GeV]", "recQSq2 [GeV]", 100, 0.0, 5.0, 100, 0.0, 5.0,
				2, cuts, cutVals, cutLimits, cutOperations, cutSeperators, cutDesign);
		c1.draw(recMEmEp2Scaled);
		cuts[1] = "recMxPEm2";
		H2F recMEmEp1Scaled = Mplots2D("recW1", "recQSq1", "recW1 [GeV]", "recQSq1 [GeV]", 100, 0.0, 5.0, 100, 0.0, 5.0,
				2, cuts, cutVals, cutLimits, cutOperations, cutSeperators, cutDesign);
		c1.cd(1);

		c1.draw(recMEmEp1Scaled);

		H2F total = recMEmEp2Scaled.histClone("total");
		total.add(recMEmEp1Scaled);
		c1.cd(2);
		c1.draw(total);
	}

	private void acceptance() {
		// zoom in to EpEm plots with cuts
		TCanvas c1 = new TCanvas("Reconstructed M(e+e-) with cuts", 800, 800);
		c1.divide(1, 2);

		String[] cuts = { "genMxPEm2", "recMxPEm1" };
		double[] cutVals = { 0.957, 0.96637 };
		double[] cutLimits = { 2.5 * 0.03, 2.5 * 0.03 };
		String[] cutOperations = { "<", "<" };
		String[] cutSeperators = { "&& ", "" };

		c1.cd(0);
		H1F recMEmEp2Scaled = Mplots("recMEmEp2", "M(e+e-) [GeV]", 100, 0.0, 0.96, 2, cuts, cutVals, cutLimits,
				cutOperations, cutSeperators);
		c1.draw(recMEmEp2Scaled);
		cuts[1] = "recMxPEm2";
		H1F recMEmEp1Scaled = Mplots("recMEmEp1", "M(e+e-) [GeV]", 100, 0.0, 0.96, 4, cuts, cutVals, cutLimits,
				cutOperations, cutSeperators);
		c1.draw(recMEmEp1Scaled, "same");

		H1F total = this.recPlot1.histClone("total");
		total.add(this.recPlot2);
		total.setOptStat(1000001);

		c1.cd(1);
		c1.draw(total);
		// gen plots

		// MEpEmCut
		String cutsGen[] = { "genMxPEm1" };
		double cutValsGen[] = { 0.957 };
		double[] cutLimitsGen = { 2.5 * 0.03 };
		String[] cutOperationsGen = { "<" };
		String[] cutSeperatorsGen = { "" };

		TCanvas c2 = new TCanvas("Generated M(e+e-) with cuts", 800, 800);
		c2.divide(1, 2);
		H1F genMEmEp2Scaled = Mplots("genMEmEp2", "M(e+e-) [GeV]", 100, 0.0, 0.96, 4, cutsGen, cutValsGen, cutLimitsGen,
				cutOperationsGen, cutSeperatorsGen);

		cutsGen[0] = "genMxPEm2";

		H1F genMEmEp1Scaled = Mplots("genMEmEp1", "M(e+e-) [GeV]", 100, 0.0, 0.96, 2, cutsGen, cutValsGen, cutLimitsGen,
				cutOperationsGen, cutSeperatorsGen);
		c2.cd(0);
		c2.draw(genMEmEp1Scaled);
		c2.draw(genMEmEp2Scaled, "same");

		H1F gentotal = genMEmEp2Scaled.histClone("total");
		gentotal.add(genMEmEp1Scaled);
		gentotal.setOptStat(1000001);

		c2.cd(1);
		c2.draw(gentotal);

		TCanvas c3 = new TCanvas("Acceptance in  M(e+e-)", 800, 800);

		// c3.getCanvas().divide(1, 2);

		H1F totalClone = total.histClone("totalClone");
		totalClone.setFillColor(5);
		totalClone.setLineColor(1);
		totalClone.divide(gentotal);

		c3.draw(totalClone, "E");
		// c3.getCanvas().getPad(0).draw(totalClone, "E");

		// im not trusting the error propagation of the histogram division, lets
		// check manually
		H1F errorPlot = new H1F("Error Plot", 100, 0.0, 0.96);
		for (int i = 0; i < total.getXaxis().getNBins(); i++) {
			errorPlot.setBinContent(i, totalClone.getBinError(i));
		}
		// c3.getCanvas().getPad(1).draw(errorPlot, "");

		// TCanvas c4 = new TCanvas("Error in Acceptance in M(e+e-)", 800, 800);
		// c4.draw(errorPlot);

		SaveCanvas.saveCanvas(c1);
		SaveCanvas.saveCanvas(c2);
		SaveCanvas.saveCanvas(c3);

		// lets calculate expected yield as we did in the proposal
		// Lets now calculate the number of expected results
		double etaP_eegam_BR = 5.13e-04; // BR from PRELIM CLAS result

		double total_etaP_expected = 2.05897e+09;
		double total_etaP_Dalitzexpected = total_etaP_expected * etaP_eegam_BR;
		System.out.println(
				total_etaP_Dalitzexpected + " total_etaP_Dalitzexpected / total_etaP_Dalitzexpected with aceptance--> "
						+ total_etaP_Dalitzexpected * totalClone.integral());
		//

		double InclIntegrated_Events_upper = 0.0;
		double spread_events = total_etaP_Dalitzexpected / total.getXaxis().getNBins();// number
		// seen
		// per
		// bin

		for (int i = 0; i < totalClone.getXaxis().getNBins(); i++) {
			double intotal_acceptance = totalClone.getBinContent(i);
			double intotal_events_upper;
			if (intotal_acceptance == 0) {
				intotal_events_upper = 0;
			} else {
				intotal_events_upper = (spread_events * intotal_acceptance); // THIS
																				// IS
																				// HOW
																				// Ntot
																				// was
																				// calculated
			}
			InclIntegrated_Events_upper = InclIntegrated_Events_upper + intotal_events_upper;
		}
		System.out.println(InclIntegrated_Events_upper + " Events Expected  52134.4 from previous production analysis");

		// Done counting
		// Now lets make plots of what the spectrum will look like with and
		// without acceptance and QED normalized
		// Before acceptance
		int Nincl_accepted = 0;// hIVEpEm_cut->GetEntries();
		for (int i = 0; i < total.getxAxis().getNBins(); i++) {
			Nincl_accepted += total.getBinContent(i);
		}
		H1F hIVEpEm_cut_clone = total.histClone("hIVEpEm_cut_clone");
		hIVEpEm_cut_clone.setTitle("hIVEpEm_cut_clone");
		System.out.println(" BEFORe clone " + hIVEpEm_cut_clone.integral() + " Nincl_accepted = " + Nincl_accepted);
		for (int i = 0; i < hIVEpEm_cut_clone.getxAxis().getNBins(); i++) {
			double n = total.getBinContent(i);
			hIVEpEm_cut_clone.setBinContent(i, (InclIntegrated_Events_upper / Nincl_accepted) * n);

		}
		double clone_sum = 0.0;
		System.out.println(" IN clone " + hIVEpEm_cut_clone.integral());
		// Now lets normalozed by QED
		// For QED normalization
		//

		double[] QED_par = { total_etaP_Dalitzexpected, 0.957 }; // {Mass}// /
		double[] QED_parII = { InclIntegrated_Events_upper, 0.957 }; // {Mass}//
		H1F hIVEpEm_cut_Anotherclone = total.histClone("hIVEpEm_cut_clone");
		hIVEpEm_cut_Anotherclone.setTitle("hIVEpEm_cut_Anotherclone");

		for (int i = 0; i < hIVEpEm_cut_Anotherclone.getXaxis().getNBins(); i++) {
			double intotal = hIVEpEm_cut_Anotherclone.getBinContent(i);
			double intotal_events_upper;

			intotal_events_upper = (InclIntegrated_Events_upper / intotal);
			hIVEpEm_cut_Anotherclone.setBinContent(i, intotal_events_upper);
		}

		double[] pole_par = { 10, 0.59, 0.0144 }; // {A,Lambda,Gamma}

		H1F hEpEm_corrected = new H1F("hEpEm_corrected", "hEpEm_corrected", 100, 0.0, 0.96);
		H1F hEpEm_QEDnorm = new H1F("hEpEm_QEDnorm", "hEpEm_QEDnorm", 100, 0.0, 0.96);
		H1F hEpEm_QEDnormcorrected = new H1F("hEpEm_QEDnormcorrected", "hEpEm_QEDnormcorrected", 100, 0.0, 0.96);

		H1F hEpEm_QEDFF = new H1F("hEpEm_QEDFF", "hEpEm_QEDFF", 100, 0.0, 0.96);

		for (int i = 0; i < hIVEpEm_cut_clone.getXaxis().getNBins(); i++) {

			// first I want to make sure the spectrum of the data has the proper
			// errors

			hIVEpEm_cut_clone.setBinError(i, Math.sqrt(hIVEpEm_cut_clone.getBinContent(i)));

			double bin_factor = 1.0;// 1.65; //this needs to be solved at
									// somepoint
			double intotal_acceptance = totalClone.getBinContent(i) * bin_factor;
			clone_sum = clone_sum + hIVEpEm_cut_clone.getBinContent(i);
			double intotal_events_upper;
			if (intotal_acceptance == 0) {
				intotal_events_upper = 0;
			} else {
				intotal_events_upper = hIVEpEm_cut_clone.getBinContent(i) / intotal_acceptance;
			}
			hEpEm_corrected.setBinContent(i, intotal_events_upper);
			hEpEm_corrected.setBinError(i, Math.sqrt(intotal_events_upper));

			double QED_factor = KrollWadaFormula.Eval_Kroll_wada(hIVEpEm_cut_clone.getXaxis().getBinCenter(i), QED_par);
			double QED_factorII = KrollWadaFormula.Eval_Kroll_wada(hIVEpEm_cut_clone.getXaxis().getBinCenter(i),
					QED_parII);

			double VMD = KrollWadaFormula.Pole_FFII(hIVEpEm_cut_clone.getXaxis().getBinCenter(i), pole_par);
			// System.out.println(
			// intotal_events_upper * QED_factor * VMD + " suposedly the number
			// I should see per bin at bin "
			// + hIVEpEm_cut_clone.getXaxis().getBinCenter(i) + " with VMD
			// events as "
			// + intotal_events_upper * VMD + " VMD value " + VMD);

			hEpEm_QEDnorm.setBinContent(i, intotal_events_upper * intotal_acceptance * (QED_factor * VMD));

			hEpEm_QEDnormcorrected.setBinContent(i, intotal_events_upper * (QED_factor * VMD));
			hEpEm_QEDFF.setBinContent(i, intotal_events_upper * (VMD));

			// hEpEm_QEDnorm->SetBinError(i,sqrt(intotal_events_upper/QED_factor));

		}

		int Nincl_corrected = 0;// hIVEpEm_cut->GetEntries();
		for (int i = 0; i < hEpEm_QEDnorm.getxAxis().getNBins(); i++) {
			Nincl_corrected += hEpEm_QEDnorm.getBinContent(i);
		}

		for (int i = 0; i < hEpEm_QEDnorm.getxAxis().getNBins(); i++) {
			double n = hEpEm_QEDnorm.getBinContent(i);
			hEpEm_QEDnorm.setBinContent(i, (InclIntegrated_Events_upper / Nincl_corrected) * n);

		}
		System.out.println(" IN clone Again" + hEpEm_QEDnorm.integral());

		// what was calculated was for 80 days of running. To simplify
		// everything, take all plots and divide by 4 for 20 days of running

		for (int i = 0; i < hEpEm_QEDnorm.getxAxis().getNBins(); i++) {
			double n = hEpEm_QEDnorm.getBinContent(i);
			hEpEm_QEDnorm.setBinContent(i, n / 4.0);
			double m = hEpEm_QEDnormcorrected.getBinContent(i);

			hEpEm_QEDnormcorrected.setBinContent(i, m / 4.0);
		}

		System.out.println("hEpEm_corrected " + hEpEm_QEDnorm.integral());
		TCanvas cQED = new TCanvas("Count Rates for 20 Days of BeamTime", 800, 800);
		cQED.divide(1, 2);
		// cQED.cd(0);
		// cQED.draw(hIVEpEm_cut_clone, "EP");
		// cQED.cd(1);
		// cQED.draw(genClone, "EP");
		cQED.cd(0);
		hEpEm_QEDnorm.getStatText()[1].replaceAll("0", Double.toString(hEpEm_QEDnorm.integral()));
		hEpEm_QEDnorm.setOptStat(1000001);
		hEpEm_QEDnorm.setFillColor(2);
		hEpEm_QEDnorm.setLineColor(1);
		hEpEm_QEDnorm.setTitleX("M(e+e-) [GeV]");
		hEpEm_QEDnorm.setTitleY("Entries / 9.6 MeV");
		cQED.draw(hEpEm_QEDnorm, "EP");
		cQED.cd(1);
		hEpEm_QEDnormcorrected.setTitleX("M(e+e-) [GeV]");
		hEpEm_QEDnormcorrected.setTitleY("Entries / 9.6 MeV");
		hEpEm_QEDnormcorrected.setFillColor(2);
		hEpEm_QEDnormcorrected.setLineColor(1);
		hEpEm_QEDnormcorrected.setOptStat(1000001);

		cQED.draw(hEpEm_QEDnormcorrected, "EP");

		SaveCanvas.saveCanvas(cQED);

	}

	private void setCanvasAttributes(TCanvas can) {
		can.getCanvas().setFont("HanziPen TC");
		can.getCanvas().setTitleSize(18);
		can.getCanvas().setAxisTitleSize(14);
		can.getCanvas().setAxisLabelSize(12);
	}

	private void setFunctionAttributes(F1D f1) {
		f1.setLineColor(1);
		f1.setLineWidth(3);
		f1.setOptStat(1100);

	}

	private void setEtaFitParms(F1D f1) {
		f1.setParameter(1, 0.957);
		f1.setParameter(2, 0.02);
		f1.setParameter(3, 1.0);
		f1.setParameter(4, 1.0);
		f1.setParameter(5, 1.0);
	}

	private H1F Mplots(String topology, String title, int bins, double xmin, double xmax, int color, String[] cuts,
			double[] cutVals, double[] cutLimits, String[] cutOperations, String[] cutSeperators) {
		H1F h1 = new H1F(topology, bins, xmin, xmax);
		h1.setTitleX(title);
		h1.setOptStat(1000001);
		h1.setFillColor(color);
		h1.setLineColor(color);

		double energyPerBin = 1000.0 * (xmax - xmin) / ((double) bins);
		h1.setTitleY("Entries / " + Double.toString(energyPerBin) + " MeV");
		String thisCut = loadTotalCut(cuts, cutVals, cutLimits, cutOperations, cutSeperators);
		h1.fill(this.mainService.getTree().getDataVector(topology, thisCut));
		return h1;
	}

	private H1F Mplots(String topology, String title, int bins, double xmin, double xmax, int color, String[] cuts,
			double[] cutVals, double[] cutLimits, String[] cutOperations, String[] cutSeperators, String[] cutDesign) {
		H1F h1 = new H1F(topology, bins, xmin, xmax);
		h1.setTitleX(title);
		h1.setOptStat(1000001);
		h1.setFillColor(color);
		h1.setLineColor(color);

		double energyPerBin = 1000.0 * (xmax - xmin) / ((double) bins);
		h1.setTitleY("Entries / " + Double.toString(energyPerBin) + " MeV");
		String thisCut = loadTotalCut(cuts, cutVals, cutLimits, cutOperations, cutSeperators, cutDesign);
		h1.fill(this.mainService.getTree().getDataVector(topology, thisCut));
		return h1;
	}

	private H2F Mplots2D(String topology1, String topology2, String xTitle, String yTitle, int xbins, double xmin,
			double xmax, int ybins, double ymin, double ymax, int color, String[] cuts, double[] cutVals,
			double[] cutLimits, String[] cutOperations, String[] cutSeperators, String[] cutDesign) {
		H2F h1 = new H2F(xTitle, xbins, xmin, xmax, ybins, ymin, ymax);
		h1.setTitleX(xTitle);
		h1.setTitleY(yTitle);

		double energyPerBin = 1000.0 * (xmax - xmin) / ((double) xbins);
		h1.setTitle("Entries / " + Double.toString(energyPerBin) + " MeV");
		String thisCut = loadTotalCut(cuts, cutVals, cutLimits, cutOperations, cutSeperators, cutDesign);

		DataVector vector1 = this.mainService.getTree().getDataVector(topology1, thisCut);
		DataVector vector2 = this.mainService.getTree().getDataVector(topology2, thisCut);

		System.out.println(vector1.size() + " " + vector2.size());
		for (int i = 0; i < vector1.size(); i++) {
			h1.fill(vector1.getValue(i), vector2.getValue(i));
		}
		return h1;
	}

	private String loadTotalCut(String[] cuts, double[] cutVals, double[] cutLimits, String[] cutOperations,
			String[] cutSeperators) {

		StringBuilder myCut = new StringBuilder();
		if (cuts.length != cutVals.length || cuts.length != cutLimits.length || cuts.length != cutOperations.length
				|| cuts.length != cutSeperators.length) {
			System.err.println("[[---AnalysisPlots---]] -----> Operations not equal size");
		}

		for (int i = 0; i < cuts.length; i++) {
			myCut.append(
					"abs(" + cuts[i] + "-" + cutVals[i] + ")" + cutOperations[i] + cutLimits[i] + cutSeperators[i]);
		}

		// System.out.println(myCut);
		return myCut.toString();
	}

	private String loadTotalCut(String[] cuts, double[] cutVals, double[] cutLimits, String[] cutOperations,
			String[] cutSeperators, String[] cutDesign) {

		StringBuilder myCut = new StringBuilder();
		if (cuts.length != cutVals.length || cuts.length != cutLimits.length || cuts.length != cutOperations.length
				|| cuts.length != cutSeperators.length || cuts.length != cutDesign.length) {
			System.err.println("[[---AnalysisPlots---]] -----> Operations not equal size");
		}

		for (int i = 0; i < cuts.length; i++) {
			if (cutDesign[i] == "sym") {
				myCut.append(
						"abs(" + cuts[i] + "-" + cutVals[i] + ")" + cutOperations[i] + cutLimits[i] + cutSeperators[i]);
			} else if (cutDesign[i] == "side") {
				myCut.append(
						"(" + cuts[i] + "-" + cutVals[i] + ")" + cutOperations[i] + cutLimits[i] + cutSeperators[i]);
			}

		}

		// System.out.println(myCut);
		return myCut.toString();
	}

}
