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

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.io.FileOutputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jlab.groot.data.H1F;
import org.jlab.groot.graphics.EmbeddedCanvas;
import org.jlab.groot.ui.TCanvas;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import services.MainService;
import services.ServiceManager;

public class AnalysisPlots {

	private int screenWidth = 1200;// myCanvas1.getWidth();// 100;// (int)
	// (screensize.getWidth() / 2);
	private int screenHeight = 800;// myCanvas1.getHeight();// 100;// (int)
	// (screensize.getHeight() /
	// 2);
	private MainService mainService = null;

	public AnalysisPlots() {
		this.mainService = ServiceManager.getSession();
	}

	public void run() {
		plotHistograms();

		// if (this.mainService.getSkimService().size() == 2) {
		// plotHistograms("gen");
		// plotHistograms("rec");
		// // makeAcceptance();
		// } else if (this.mainService.getSkimService().size() == 1 &&
		// this.mainService.getSkimService().contains("gen")) {
		// plotHistograms("gen");
		// } else if (this.mainService.getSkimService().size() == 1 &&
		// this.mainService.getSkimService().contains("rec")) {
		// plotHistograms("rec");
		// } else {
		// System.err.println("Did you set the MC or REC service correctly?!?
		// ");
		// System.exit(1);
		// }
	}

	private void plotHistograms() {

		plotGenWCuts();
		plotRecWCuts();
		plotAcceptance();
		plotEstimatedYield();

	}

	private void plotGenWCuts() {

	}

	private void plotRecWCuts() {

	}

	private void plotAcceptance() {
		// plotGen();
		// plotRec();
		acceptance();

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

		// Gen Me+e-
		TCanvas c2 = new TCanvas("Generated M(e+e-)", 800, 800);
		c2.divide(1, 3);
		c2.cd(0);
		H1F mEpEm2test = Mplots("genMEmEp2", "M(e+e-) [GeV]", 100, 0.0, 3.6, 4, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators);

		c2.draw(mEpEm2test);
		H1F mEpEm1test = Mplots("genMEmEp1", "M(e+e-) [GeV]", 100, 0.0, 3.6, 2, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators);
		c2.cd(1);
		c2.draw(mEpEm1test);
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
		c4.cd(0);
		H1F mEpEm2Cut = Mplots("genMEmEp2", "M(e+e-) [GeV]", 100, 0.0, 3.6, 4, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators);

		c4.draw(mEpEm2Cut);
		cuts[0] = "genMxPEm2";

		H1F mEpEm1Cut = Mplots("genMEmEp1", "M(e+e-) [GeV]", 100, 0.0, 3.6, 2, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators);
		c4.cd(1);
		c4.draw(mEpEm1Cut);
		c4.cd(2);
		c4.draw(mEpEm1Cut);
		c4.draw(mEpEm2Cut, "same");

	}

	private void plotRec() {
		// Rec Mxpe-

		TCanvas c1 = new TCanvas("Reconstructed Mx(pe-)", 800, 800);
		c1.divide(1, 3);
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
		c1.cd(0);

		c1.draw(mxPEm1);
		c1.cd(1);
		c1.draw(mxPEm2);
		c1.cd(2);
		c1.draw(mxPEm1);
		c1.draw(mxPEm2, "same");
		//
		// Rec Me+e-
		TCanvas c2 = new TCanvas("Reconstructed M(e+e-)", 800, 800);
		c2.divide(1, 3);

		H1F mEpEm2test = Mplots("recMEmEp2", "M(e+e-) [GeV]", 100, 0.0, 3.6, 2, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators, cutDesign);

		H1F mEpEm1test = Mplots("recMEmEp1", "M(e+e-) [GeV]", 100, 0.0, 3.6, 4, cuts, cutVals, cutLimits, cutOperations,
				cutSeperators, cutDesign);
		c2.cd(0);
		c2.draw(mEpEm1test);
		c2.cd(1);
		c2.draw(mEpEm2test);
		c2.cd(2);
		c2.draw(mEpEm2test);
		c2.draw(mEpEm1test, "same");

		// MxPe Cut

		String[] cutsNew = { "genMxPEm2", "recMxPEm1" };
		double[] cutValsNew = { 0.957, 0.96637 };
		double[] cutLimitsNew = { 2.5 * 0.03, 2.5 * 0.03 };
		String[] cutOperationsNew = { "<", "<" };
		String[] cutSeperatorsNew = { "&& ", "" };
		String[] cutDesignNew = { "sym", "sym" };

		TCanvas c3 = new TCanvas("Reconstructed Mx(pe-) with cuts", 800, 800);
		c3.divide(1, 3);
		c3.cd(0);

		H1F mxPEm1Cut = Mplots("recMxPEm1", "Mx(pe-) [GeV]", 100, 0.8, 3.6, 4, cutsNew, cutValsNew, cutLimitsNew,
				cutOperationsNew, cutSeperatorsNew);
		c3.draw(mxPEm1Cut);
		cutsNew[1] = "recMxPEm2";

		H1F mxPEm2Cut = Mplots("recMxPEm2", "Mx(pe-) [GeV]", 100, 0.8, 3.6, 2, cutsNew, cutValsNew, cutLimitsNew,
				cutOperationsNew, cutSeperatorsNew);
		c3.cd(1);
		c3.draw(mxPEm2Cut);
		c3.cd(2);
		c3.draw(mxPEm1Cut);
		c3.draw(mxPEm2Cut, "same");

		// MEpEmCut
		TCanvas c4 = new TCanvas("Reconstructed M(e+e-) with cuts", 800, 800);
		c4.divide(1, 3);
		c4.cd(0);
		cutsNew[1] = "recMxPEm2";

		H1F mEpEm1Cuttest = Mplots("recMEmEp1", "M(e+e-) [GeV]", 100, 0.0, 3.6, 4, cutsNew, cutValsNew, cutLimitsNew,
				cutOperationsNew, cutSeperatorsNew);

		c4.draw(mEpEm1Cuttest);
		cutsNew[1] = "recMxPEm1";
		H1F mEpEm2Cuttest = Mplots("recMEmEp2", "M(e+e-) [GeV]", 100, 0.0, 3.6, 2, cutsNew, cutValsNew, cutLimitsNew,
				cutOperationsNew, cutSeperatorsNew);
		c4.cd(1);
		c4.draw(mEpEm2Cuttest);
		c4.cd(2);
		c4.draw(mEpEm2Cuttest);
		c4.draw(mEpEm1Cuttest, "same");

	}

	private void acceptance() {
		// zoom in to EpEm plots with cuts
		TCanvas c1 = new TCanvas("Cut Plots", 800, 800);
		c1.divide(1, 2);

		String[] cuts = { "genMxPEm2", "recMxPEm1" };
		double[] cutVals = { 0.957, 0.96637 };
		double[] cutLimits = { 2.5 * 0.03, 2.5 * 0.03 };
		String[] cutOperations = { "<", "<" };
		String[] cutSeperators = { "&& ", "" };

		c1.cd(0);
		H1F recMEmEp2Scaled = Mplots("recMEmEp2", "M(e+e-) [GeV]", 100, 0.0, 0.965, 2, cuts, cutVals, cutLimits,
				cutOperations, cutSeperators);
		c1.draw(recMEmEp2Scaled);
		cuts[1] = "recMxPEm2";
		H1F recMEmEp1Scaled = Mplots("recMEmEp1", "M(e+e-) [GeV]", 100, 0.0, 0.965, 4, cuts, cutVals, cutLimits,
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

		TCanvas c2 = new TCanvas("Generated M(e+e-) with cuts", 800, 800);
		c2.divide(1, 2);
		H1F genMEmEp2Scaled = Mplots("genMEmEp2", "M(e+e-) [GeV]", 100, 0.0, 0.965, 4, cutsGen, cutValsGen,
				cutLimitsGen, cutOperationsGen, cutSeperatorsGen);

		cutsGen[0] = "genMxPEm2";

		H1F genMEmEp1Scaled = Mplots("genMEmEp1", "M(e+e-) [GeV]", 100, 0.0, 0.965, 2, cutsGen, cutValsGen,
				cutLimitsGen, cutOperationsGen, cutSeperatorsGen);
		c2.cd(0);
		c2.draw(genMEmEp1Scaled);
		c2.draw(genMEmEp2Scaled, "same");

		H1F gentotal = genMEmEp2Scaled.histClone("total");
		gentotal.add(genMEmEp1Scaled);
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
		H1F errorPlot = new H1F("Error Plot", 100, 0.0, 0.965);
		for (int i = 0; i < total.getXaxis().getNBins(); i++) {
			errorPlot.setBinContent(i, totalClone.getBinError(i));
		}
		// c3.getCanvas().getPad(1).draw(errorPlot, "");

		// TCanvas c4 = new TCanvas("Error in Acceptance in M(e+e-)", 800, 800);
		// c4.draw(errorPlot);
		
		saveCanvas(c1);
		saveCanvas(c2);
		saveCanvas(c3);

	}

	private H1F Mplots(String topology, String title, int bins, double xmin, double xmax, int color, String[] cuts,
			double[] cutVals, double[] cutLimits, String[] cutOperations, String[] cutSeperators) {
		H1F h1 = new H1F(topology, bins, xmin, xmax);
		h1.setTitleX(title);
		h1.setOptStat(11);
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
		h1.setOptStat(11);
		h1.setFillColor(color);
		h1.setLineColor(color);

		double energyPerBin = 1000.0 * (xmax - xmin) / ((double) bins);
		h1.setTitleY("Entries / " + Double.toString(energyPerBin) + " MeV");
		String thisCut = loadTotalCut(cuts, cutVals, cutLimits, cutOperations, cutSeperators, cutDesign);
		h1.fill(this.mainService.getTree().getDataVector(topology, thisCut));
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

	private void plotEstimatedYield() {

	}

	private void saveCanvas(TCanvas can) {
		String plotName = can.getTitle();
		System.out.println(plotName);
		JPanel mainPanel = makeJPanel();
		mainPanel.add(can.getCanvas());
		System.out.println(mainPanel.getWidth());
		System.out.println(can.getWidth());
		try {
			Document d = new Document(PageSize.A4.rotate());// PageSize.A4.rotate()
			PdfWriter writer = PdfWriter.getInstance(d, new FileOutputStream(plotName + ".pdf"));
			d.open();

			PdfContentByte cb = writer.getDirectContent();
			cb.saveState();
			// PageSize.A4.getWidth(), PageSize.A4.getHeight()
			PdfTemplate template = cb.createTemplate(mainPanel.getWidth(), mainPanel.getHeight());
			Graphics2D g2d = new PdfGraphics2D(cb, can.getWidth(), can.getHeight() * 75 / 100);

			g2d.scale(1.0, 0.75);
			// g2d.translate(0.0, 400.0);
			mainPanel.print(g2d);
			// frame.addNotify();
			// frame.validate();
			g2d.dispose();
			cb.addTemplate(template, 0, 0);

			cb.restoreState();
			d.close();
		} catch (Exception e) {
			//
		}
	}

	private void makeAcceptance() {
		JFrame frame2 = makeJFrame("Acceptance");
		JPanel mainPanel2 = makeJPanel();
		EmbeddedCanvas can2 = new EmbeddedCanvas();
		// can2.draw(makeAcceptancePlot());
		mainPanel2.add(can2);
		frame2.add(mainPanel2);
		frame2.setVisible(true);
	}

	private JFrame makeJFrame(String dataType) {
		String plotName = dataType + "Plots";

		JFrame frame = new JFrame(plotName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setSize(screenWidth, screenHeight);

		return frame;
	}

	private JPanel makeJPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setSize(screenWidth, screenHeight);
		return mainPanel;
	}

	private EmbeddedCanvas makeCanvas() {

		EmbeddedCanvas can1 = new EmbeddedCanvas();
		return can1;
	}

	// private H1F makeAcceptancePlot() {
	// H1F h1f = this.mainService.getH1Map()
	// .get(makeHistogramCoordinate(this.mainService.getInvariantList().get(0),
	// ("rec" + "M"))).get(0)
	// .histClone("h1f");
	// h1f.setTitleX("M(e+e-) GeV");
	// h1f.setTitleY("Acceptance");
	// h1f.divide(this.mainService.getH1Map()
	// .get(makeHistogramCoordinate(this.mainService.getInvariantList().get(0),
	// ("gen" + "M"))).get(1));
	//
	// return h1f;
	//
	// }

	private void savePlots(JPanel mainPanel, String dataType) {
		String plotName = dataType + "Plots";
		try {
			Document d = new Document(PageSize.A4);// PageSize.A4.rotate()
			PdfWriter writer = PdfWriter.getInstance(d, new FileOutputStream(plotName + ".pdf"));
			d.open();

			PdfContentByte cb = writer.getDirectContent();
			cb.saveState();
			// PageSize.A4.getWidth(), PageSize.A4.getHeight()
			PdfTemplate template = cb.createTemplate(screenWidth, screenHeight);
			Graphics2D g2d = new PdfGraphics2D(cb, screenWidth, screenHeight);

			g2d.scale(0.5, 0.5);
			// g2d.translate(4.0, 0.0);
			mainPanel.print(g2d);
			// frame.addNotify();
			// frame.validate();
			g2d.dispose();
			cb.addTemplate(template, 0, 0);

			cb.restoreState();
			d.close();
		} catch (Exception e) {
			//
		}
	}

	private Coordinate makeHistogramCoordinate(Coordinate aCoordinate, String string) {
		int size = aCoordinate.getStrSize() + 1;
		String[] sb = new String[size];
		sb[0] = string;
		for (int i = 0; i < aCoordinate.getStrSize(); i++) {
			sb[i + 1] = aCoordinate.getStrings()[i];

		}
		Coordinate coordinate = new Coordinate(sb);

		return coordinate;
	}

}
