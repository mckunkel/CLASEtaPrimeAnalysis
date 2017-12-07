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
import org.jlab.groot.math.F1D;
import org.jlab.groot.ui.TCanvas;

import services.MainService;
import services.ServiceManager;

public class AnalysisPlotsEpEmGam {
	private MainService mainService = null;

	public AnalysisPlotsEpEmGam() {
		this.mainService = ServiceManager.getSession();

	}

	public void run() {
		plotHistograms();
	}

	private void plotHistograms() {
		plotGen();
		plotGenWithCuts();
		// plotRec();
		// acceptance();
		// plotQ();
		// plotW();
		// plotWandQ();
		// plotP();
	}

	private void plotGen() {
		// Gen Mxpe-

		double xmin = 0.05;
		double xmax = 2.5;
		int bins = 100;
		double energyPerBin = 1000.0 * (xmax - xmin) / ((double) bins);

		TCanvas c1 = new TCanvas("Generated M(e+e-gamma)", 800, 800);
		c1.divide(1, 2);
		H1F h1 = new H1F("M(e+e-gamma)", bins, xmin, xmax);
		h1.setTitleX("Generated M(e+e-gamma)");
		h1.setOptStat(1000001);
		h1.setFillColor(4);
		h1.setLineColor(4);

		h1.setTitleY("Entries / " + Double.toString(energyPerBin) + " MeV");
		h1.fill(this.mainService.getTree().getDataVector("genMEmEpgamma", ""));

		H1F h2 = new H1F("M(e+e-)", bins, xmin, xmax);
		h2.setTitleX("Generated M(e+e-)");
		h2.setOptStat(1000001);
		h2.setFillColor(2);
		h2.setLineColor(2);
		h2.setTitleY("Entries / " + Double.toString(energyPerBin) + " MeV");
		h2.fill(this.mainService.getTree().getDataVector("genMEmEp", ""));
		c1.getCanvas().getPad(0).getAxisY().setLog(true);
		c1.getCanvas().getPad(1).getAxisY().setLog(true);

		c1.cd(0);
		c1.draw(h1);
		c1.cd(1);
		c1.draw(h2);

		// SaveCanvas.saveCanvas(c1);
		// genPlot1 = mEpEm1CutScaled.histClone("genPlot1");
		// genPlot2 = mEpEm2CutScaled.histClone("genPlot2");

	}

	private void plotGenWithCuts() {
		// Gen Mxpe-
		String cutString = "genMEmEpgamma<1.0";
		double xmin = 0.05;
		double xmax = 2.5;
		int bins = 100;
		double energyPerBin = 1000.0 * (xmax - xmin) / ((double) bins);

		TCanvas c1 = new TCanvas("Generated M(e+e-gamma) with M(e+e-) cut", 800, 800);
		c1.divide(1, 2);
		H1F h1 = new H1F("M(e+e-gamma)", bins, xmin, xmax);
		h1.setTitleX("Generated M(e+e-gamma)");
		h1.setOptStat(1000001);
		h1.setFillColor(4);
		h1.setLineColor(4);

		h1.setTitleY("Entries / " + Double.toString(energyPerBin) + " MeV");
		h1.fill(this.mainService.getTree().getDataVector("genMEmEpgamma", cutString));

		H1F h2 = new H1F("M(e+e-gamma)", bins, xmin, xmax);
		h2.setTitleX("Generated M(e+e-gamma)");
		h2.setOptStat(1000001);
		h2.setFillColor(2);
		h2.setLineColor(2);
		h2.setTitleY("Entries / " + Double.toString(energyPerBin) + " MeV");
		h2.fill(this.mainService.getTree().getDataVector("genMEmEp", cutString));
		c1.getCanvas().getPad(0).getAxisY().setLog(true);
		c1.getCanvas().getPad(1).getAxisY().setLog(true);

		c1.cd(0);
		c1.draw(h1);
		c1.cd(1);
		c1.draw(h2);

		// SaveCanvas.saveCanvas(c1);
		// genPlot1 = mEpEm1CutScaled.histClone("genPlot1");
		// genPlot2 = mEpEm2CutScaled.histClone("genPlot2");

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
