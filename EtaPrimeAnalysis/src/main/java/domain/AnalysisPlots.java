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

import org.jlab.groot.data.DataVector;
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
		plotGen();
		plotRec();

	}

	private void plotGen() {
		H1F mxPEm1 = new H1F("title", 100, 0.8, 3.6);
		H1F mxPEm2 = new H1F("title", 100, 0.8, 3.6);

		mxPEm1.setFillColor(2);
		mxPEm1.setLineColor(2);
		mxPEm2.setFillColor(4);
		mxPEm2.setLineColor(4);
		mxPEm1.setTitleX("Mx(pe-) [GeV]");
		mxPEm2.setTitleX("Mx(pe-) [GeV]");
		mxPEm1.setTitleY("Entries / 28 MeV");
		mxPEm2.setTitleY("Entries / 28 MeV");
		mxPEm1.setOptStat(11);
		mxPEm2.setOptStat(11);

		mxPEm1.setTitleX("Mx(pe-) [GeV]");
		mxPEm2.setTitleX("Mx(pe-) [GeV]");

		mxPEm1.setTitleY("Entries / 26 GeV");
		mxPEm2.setTitleY("Entries / 26 GeV");

		DataVector vector = this.mainService.getTree().getDataVector("genMxPEm1",
				"(abs(genMxPEm2 - 0.957)<(2.5*0.03))");
		DataVector vector2 = this.mainService.getTree().getDataVector("genMxPEm2",
				"(abs(genMxPEm2 - 0.957)<(2.5*0.03)) ");

		mxPEm1.fill(vector);
		mxPEm2.fill(vector2);
		TCanvas canvas = new TCanvas("name", 800, 800);
		canvas.divide(1, 3);
		canvas.cd(0);
		canvas.draw(mxPEm2);
		canvas.cd(1);
		canvas.draw(mxPEm1);
		canvas.cd(2);
		canvas.draw(mxPEm2);
		canvas.draw(mxPEm1, "same");

		H1F mEpEm1 = new H1F("title", 100, 0.0, 3.6);
		H1F mEpEm2 = new H1F("title", 100, 0.0, 3.6);
		mEpEm1.setFillColor(2);
		mEpEm1.setLineColor(2);
		mEpEm2.setFillColor(4);
		mEpEm2.setLineColor(4);
		mEpEm1.setTitleX("M(e+e-) [GeV]");
		mEpEm2.setTitleX("M(e+e-) [GeV]");
		mEpEm1.setTitleY("Entries / 36 MeV");
		mEpEm2.setTitleY("Entries / 36 MeV");
		mEpEm1.setOptStat(11);
		mEpEm2.setOptStat(11);
		DataVector genMEmEp1 = this.mainService.getTree().getDataVector("genMEmEp1",
				"(abs(genMxPEm2 - 0.957)<(2.5*0.03))");
		DataVector genMEmEp2 = this.mainService.getTree().getDataVector("genMEmEp2",
				"(abs(genMxPEm2 - 0.957)<(2.5*0.03))");

		mEpEm1.fill(genMEmEp1);
		mEpEm2.fill(genMEmEp2);
		TCanvas canvas2 = new TCanvas("name", 800, 800);
		canvas2.divide(1, 3);
		canvas2.cd(0);
		canvas2.draw(mEpEm2);
		canvas2.cd(1);
		canvas2.draw(mEpEm1);
		canvas2.cd(2);
		canvas2.draw(mEpEm1);
		canvas2.draw(mEpEm2, "same");

		// MxPe Cut
		H1F mxPEm1Cut = new H1F("title", 100, 0.8, 3.6);
		H1F mxPEm2Cut = new H1F("title", 100, 0.8, 3.6);

		mxPEm1Cut.setFillColor(2);
		mxPEm1Cut.setLineColor(2);
		mxPEm2Cut.setFillColor(4);
		mxPEm2Cut.setLineColor(4);
		mxPEm1Cut.setTitleX("Mx(pe-) [GeV]");
		mxPEm2Cut.setTitleX("Mx(pe-) [GeV]");
		mxPEm1Cut.setTitleY("Entries / 28 MeV");
		mxPEm2Cut.setTitleY("Entries / 28 MeV");
		mxPEm1Cut.setOptStat(11);
		mxPEm2Cut.setOptStat(11);

		mxPEm1Cut.setTitleX("Mx(pe-) [GeV]");
		mxPEm2Cut.setTitleX("Mx(pe-) [GeV]");

		mxPEm1Cut.setTitleY("Entries / 26 GeV");
		mxPEm2Cut.setTitleY("Entries / 26 GeV");

		DataVector genmxPEm1Cut = this.mainService.getTree().getDataVector("genMxPEm1",
				" (abs(genMxPEm1 - 0.957)<(2.5*0.03))");
		DataVector genmxPEm2Cut = this.mainService.getTree().getDataVector("genMxPEm2",
				"(abs(genMxPEm2 - 0.957)<(2.5*0.03))");

		mxPEm1Cut.fill(genmxPEm1Cut);
		mxPEm2Cut.fill(genmxPEm2Cut);
		TCanvas canvas3 = new TCanvas("name", 800, 800);
		canvas3.divide(1, 3);
		canvas3.cd(0);
		canvas3.draw(mxPEm2Cut);
		canvas3.cd(1);
		canvas3.draw(mxPEm1Cut);
		canvas3.cd(2);
		canvas3.draw(mxPEm2Cut);
		canvas3.draw(mxPEm1Cut, "same");

		// MEpEmCut
		H1F mEpEm1Cut = new H1F("mEpEm1Cut", 100, 0.0, 3.6);
		H1F mEpEm2Cut = new H1F("mEpEm2Cut", 100, 0.0, 3.6);
		mEpEm1Cut.setFillColor(2);
		mEpEm1Cut.setLineColor(2);
		mEpEm2Cut.setFillColor(4);
		mEpEm2Cut.setLineColor(4);
		mEpEm1Cut.setTitleX("M(e+e-) [GeV]");
		mEpEm2Cut.setTitleX("M(e+e-) [GeV]");
		mEpEm1Cut.setTitleY("Entries / 36 MeV");
		mEpEm2Cut.setTitleY("Entries / 36 MeV");
		mEpEm1Cut.setOptStat(11);
		mEpEm2Cut.setOptStat(11);
		mEpEm1Cut.setTitle("mEpEm1Cut");
		mEpEm2Cut.setTitle("mEpEm2Cut");
		DataVector genEpEm1Cut = this.mainService.getTree().getDataVector("genMEmEp1",
				"(abs(genMxPEm2 - 0.957)<(2.5*0.03))");
		DataVector genEpEm2Cut = this.mainService.getTree().getDataVector("genMEmEp2",
				"(abs(genMxPEm1 - 0.957)<(2.5*0.03))");

		mEpEm1Cut.fill(genEpEm1Cut);
		mEpEm2Cut.fill(genEpEm2Cut);
		TCanvas canvas4 = new TCanvas("Cut Plots", 800, 800);
		canvas4.divide(1, 3);
		canvas4.cd(0);
		canvas4.draw(mEpEm2Cut);
		canvas4.cd(1);
		canvas4.draw(mEpEm1Cut);
		canvas4.cd(2);
		canvas4.draw(mEpEm1Cut);
		canvas4.draw(mEpEm2Cut, "same");

	}

	private void plotRec() {
		String globalMCCut = "abs(genMxPEm2 - 0.957)<(2.5*0.03)";
		H1F mxPEm1 = new H1F("title", 100, 0.8, 3.6);
		H1F mxPEm2 = new H1F("title", 100, 0.8, 3.6);
		mxPEm1.setFillColor(4);
		mxPEm1.setLineColor(4);
		mxPEm2.setFillColor(2);
		mxPEm2.setLineColor(2);
		mxPEm1.setTitleX("Mx(pe-) [GeV]");
		mxPEm2.setTitleX("Mx(pe-) [GeV]");
		mxPEm1.setTitleY("Entries / 28 MeV");
		mxPEm2.setTitleY("Entries / 28 MeV");
		mxPEm1.setOptStat(11);
		mxPEm2.setOptStat(11);

		DataVector vector = this.mainService.getTree().getDataVector("recMxPEm1",
				"(abs(genMxPEm2 - 0.957)<(2.5*0.03)) && recMxPEm1>0.0 && recMxPEm2>0.0 ");
		DataVector vector2 = this.mainService.getTree().getDataVector("recMxPEm2",
				"(abs(genMxPEm2 - 0.957)<(2.5*0.03))&& recMxPEm1>0.0 && recMxPEm2>0.0 ");

		mxPEm1.fill(vector);
		mxPEm2.fill(vector2);
		TCanvas canvas = new TCanvas("name", 800, 800);
		canvas.divide(1, 3);
		canvas.cd(0);
		canvas.draw(mxPEm1);
		canvas.cd(1);
		canvas.draw(mxPEm2);
		canvas.cd(2);
		canvas.draw(mxPEm1);
		canvas.draw(mxPEm2, "same");

		H1F mEpEm1 = new H1F("title", 100, 0.0, 3.6);
		H1F mEpEm2 = new H1F("title", 100, 0.0, 3.6);
		mEpEm1.setFillColor(4);
		mEpEm1.setLineColor(4);
		mEpEm2.setFillColor(2);
		mEpEm2.setLineColor(2);
		mEpEm1.setTitleX("M(e+e-) [GeV]");
		mEpEm2.setTitleX("M(e+e-) [GeV]");
		mEpEm1.setTitleY("Entries / 36 MeV");
		mEpEm2.setTitleY("Entries / 36 MeV");
		mEpEm1.setOptStat(11);
		mEpEm2.setOptStat(11);

		DataVector recMEmEp1 = this.mainService.getTree().getDataVector("recMEmEp1",
				"(abs(genMxPEm2 - 0.957)<(2.5*0.03))  && recMxPEm1>0.0 && recMxPEm2>0.0");
		DataVector recMEmEp2 = this.mainService.getTree().getDataVector("recMEmEp2",
				"(abs(genMxPEm2 - 0.957)<(2.5*0.03))  && recMxPEm1>0.0 && recMxPEm2>0.0");

		mEpEm1.fill(recMEmEp1);
		mEpEm2.fill(recMEmEp2);
		TCanvas canvas2 = new TCanvas("name", 800, 800);
		canvas2.divide(1, 3);
		canvas2.cd(0);
		canvas2.draw(mEpEm1);
		canvas2.cd(1);
		canvas2.draw(mEpEm2);
		canvas2.cd(2);
		canvas2.draw(mEpEm2);
		canvas2.draw(mEpEm1, "same");

		// MxPe Cut
		H1F mxPEm1Cut = new H1F("title", 100, 0.8, 3.6);
		H1F mxPEm2Cut = new H1F("title", 100, 0.8, 3.6);

		mxPEm1Cut.setFillColor(4);
		mxPEm1Cut.setLineColor(4);
		mxPEm2Cut.setFillColor(2);
		mxPEm2Cut.setLineColor(2);
		mxPEm1Cut.setTitleX("Mx(pe-) [GeV]");
		mxPEm2Cut.setTitleX("Mx(pe-) [GeV]");
		mxPEm1Cut.setTitleY("Entries / 28 MeV");
		mxPEm2Cut.setTitleY("Entries / 28 MeV");
		mxPEm1Cut.setOptStat(11);
		mxPEm2Cut.setOptStat(11);

		mxPEm1Cut.setTitleX("Mx(pe-) [GeV]");
		mxPEm2Cut.setTitleX("Mx(pe-) [GeV]");

		mxPEm1Cut.setTitleY("Entries / 26 GeV");
		mxPEm2Cut.setTitleY("Entries / 26 GeV");

		DataVector recmxPEm1Cut = this.mainService.getTree().getDataVector("recMxPEm1",
				" (abs(recMxPEm1 - 0.96637)<(2.5*0.03))");
		DataVector recmxPEm2Cut = this.mainService.getTree().getDataVector("recMxPEm2",
				"(abs(recMxPEm2 - 0.96637)<(2.5*0.03))");

		mxPEm1Cut.fill(recmxPEm1Cut);
		mxPEm2Cut.fill(recmxPEm2Cut);
		TCanvas canvas3 = new TCanvas("name", 800, 800);
		canvas3.divide(1, 3);
		canvas3.cd(0);
		canvas3.draw(mxPEm1Cut);
		canvas3.cd(1);
		canvas3.draw(mxPEm2Cut);
		canvas3.cd(2);
		canvas3.draw(mxPEm1Cut);
		canvas3.draw(mxPEm2Cut, "same");

		// MEpEmCut
		H1F mEpEm1Cut = new H1F("mEpEm1Cut", 100, 0.0, 3.6);
		H1F mEpEm2Cut = new H1F("mEpEm2Cut", 100, 0.0, 3.6);
		mEpEm1Cut.setFillColor(4);
		mEpEm1Cut.setLineColor(4);
		mEpEm2Cut.setFillColor(2);
		mEpEm2Cut.setLineColor(2);
		mEpEm1Cut.setTitleX("M(e+e-) [GeV]");
		mEpEm2Cut.setTitleX("M(e+e-) [GeV]");
		mEpEm1Cut.setTitleY("Entries / 36 MeV");
		mEpEm2Cut.setTitleY("Entries / 36 MeV");
		mEpEm1Cut.setOptStat(11);
		mEpEm2Cut.setOptStat(11);
		mEpEm1Cut.setTitle("mEpEm1Cut");
		mEpEm2Cut.setTitle("mEpEm2Cut");
		DataVector recEpEm1Cut = this.mainService.getTree().getDataVector("recMEmEp1",
				"(abs(recMxPEm2 - 0.96637)<(2.5*0.03))");
		DataVector recEpEm2Cut = this.mainService.getTree().getDataVector("recMEmEp2",
				"(abs(recMxPEm1 - 0.96637)<(2.5*0.03))");

		mEpEm1Cut.fill(recEpEm1Cut);
		mEpEm2Cut.fill(recEpEm2Cut);
		TCanvas canvas4 = new TCanvas("Cut Plots", 800, 800);
		canvas4.divide(1, 3);
		canvas4.cd(0);
		canvas4.draw(mEpEm1Cut);
		canvas4.cd(1);
		canvas4.draw(mEpEm2Cut);
		canvas4.cd(2);
		canvas4.draw(mEpEm2Cut);
		canvas4.draw(mEpEm1Cut, "same");
	}

	private void plotEstimatedYield() {

	}
	// private void plotHistograms(String dataType) {
	// List<String> plotList = new ArrayList<>();
	// if (dataType == "gen") {
	// plotList.addAll(this.mainService.getGenList());
	// } else if (dataType == "rec") {
	// plotList.addAll(this.mainService.getGenList());
	// }
	// for (String str : plotList) {
	// this.mainService.getTree().drawH1F(this.mainService.getTree().getDataVector(str,
	// ""), str);
	// }
	// //
	// //// this.mainService.getTree().drawH1F(
	// //// this.mainService.getTree().getDataVector("recMxPEm1",
	// // "abs(recMxPEm1 - 0.957)<(2.5*0.03)"),
	// //// "recMxPEm1");
	// //
	// // H1F aH1f = new H1F("title", 100, 0.8, 3.2);
	// // H1F aH12 = new H1F("title", 100, 0.8, 3.2);
	// // aH1f.setLineColor(1);
	// // aH12.setLineColor(2);
	// //
	// // DataVector vector =
	// // this.mainService.getTree().getDataVector("recMxPEm1", "");
	// // DataVector vector2 =
	// // this.mainService.getTree().getDataVector("recMxPEm2", "");
	// // // DataVector vector3 =
	// // // this.mainService.getTree().getDataVector("genMEmEp1", "");
	// // // DataVector vector4 =
	// // // this.mainService.getTree().getDataVector("genMEmEp2", "");
	// // // DataVector vector5 =
	// // // this.mainService.getTree().getDataVector("genMxPEp", "");
	// //
	// // aH1f.fill(vector);
	// // aH12.fill(vector2);
	// //
	// // TCanvas canvas = new TCanvas("name", 800, 800);
	// // canvas.draw(aH1f);
	// // TCanvas canvas2 = new TCanvas("name", 800, 800);
	// // canvas2.draw(aH12);
	// // TCanvas canvas3 = new TCanvas("name", 800, 800);
	// // canvas3.draw(aH1f);
	// // canvas3.draw(aH12, "same");
	// //
	// //
	// this.mainService.getTree().drawH1F(this.mainService.getTree().getDataVector("genMxPEm1",
	// // ""), "genMxPEm1");
	// //
	// this.mainService.getTree().drawH1F(this.mainService.getTree().getDataVector("genMxPEm2",
	// // ""), "genMxPEm2");
	// //
	// this.mainService.getTree().drawH1F(this.mainService.getTree().getDataVector("genMEmEp1",
	// // ""), "genMEmEp1");
	// //
	// this.mainService.getTree().drawH1F(this.mainService.getTree().getDataVector("genMEmEp2",
	// // ""), "genMEmEp2");
	// //
	// // H1F aGenH1f = new H1F("title", 100, 0.8, 3.2);
	// // H1F aGenH12 = new H1F("title", 100, 0.8, 3.2);
	// // aGenH1f.setLineColor(1);
	// // aGenH12.setLineColor(2);
	// //
	// // DataVector vectorGen =
	// // this.mainService.getTree().getDataVector("genMxPEm1", "");
	// // DataVector vectorGen2 =
	// // this.mainService.getTree().getDataVector("genMxPEm2", "");
	// // // DataVector vector3 =
	// // // this.mainService.getTree().getDataVector("genMEmEp1", "");
	// // // DataVector vector4 =
	// // // this.mainService.getTree().getDataVector("genMEmEp2", "");
	// // // DataVector vector5 =
	// // // this.mainService.getTree().getDataVector("genMxPEp", "");
	// //
	// // aGenH1f.fill(vectorGen);
	// // aGenH12.fill(vectorGen2);
	// //
	// // TCanvas canvasGen = new TCanvas("name", 800, 800);
	// // canvasGen.draw(aGenH1f);
	// // TCanvas canvasGen2 = new TCanvas("name", 800, 800);
	// // canvasGen2.draw(aGenH12);
	// // TCanvas canvasGen3 = new TCanvas("name", 800, 800);
	// // canvasGen3.draw(aGenH1f);
	// // canvasGen3.draw(aGenH12, "same");
	//
	// }

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
