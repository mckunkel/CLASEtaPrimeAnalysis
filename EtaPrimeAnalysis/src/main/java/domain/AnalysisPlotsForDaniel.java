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

public class AnalysisPlotsForDaniel {

	private int screenWidth = 1200;// myCanvas1.getWidth();// 100;// (int)
	// (screensize.getWidth() / 2);
	private int screenHeight = 800;// myCanvas1.getHeight();// 100;// (int)
	// (screensize.getHeight() /
	// 2);
	private MainService mainService = null;

	public AnalysisPlotsForDaniel() {
		this.mainService = ServiceManager.getSession();
	}

	public void run() {
		if (this.mainService.getSkimService().size() == 2) {
			plotHistograms("gen");
			plotHistograms("rec");
			// makeAcceptance();
		} else if (this.mainService.getSkimService().size() == 1 && this.mainService.getSkimService().contains("gen")) {
			plotHistograms("gen");
		} else if (this.mainService.getSkimService().size() == 1 && this.mainService.getSkimService().contains("rec")) {
			plotHistograms("rec");
		} else {
			System.err.println("Did you set the MC or REC service correctly?!? ");
			System.exit(1);
		}
	}

	private void plotHistograms(String dataType) {

		this.mainService.getTree().drawH1F(
				this.mainService.getTree().getDataVector("recMxPEm1", "abs(recMxPEm1 - 0.957)<(2.5*0.03)"),
				"recMxPEm1");
		this.mainService.getTree().drawH1F(
				this.mainService.getTree().getDataVector("recMxPEm2", "abs(recMxPEm1 - 0.957)<(2.5*0.03)"),
				"recMxPEm2");
		this.mainService.getTree().drawH1F(
				this.mainService.getTree().getDataVector("recMEmEp1", "abs(recMxPEm1 - 0.957)<(2.5*0.03)"),
				"recMEmEp1");
		this.mainService.getTree().drawH1F(
				this.mainService.getTree().getDataVector("recMEmEp2", "abs(recMxPEm1 - 0.957)<(2.5*0.03)"),
				"recMEmEp2");
		// this.mainService.getTree().drawH1F(this.mainService.getTree().getDataVector("genMxPEp",
		// ""));
		H1F aH1f = new H1F("title", 100, 0.8, 3.2);
		H1F aH12 = new H1F("title", 100, 0.8, 3.2);
		aH1f.setLineColor(1);
		aH12.setLineColor(2);

		DataVector vector = this.mainService.getTree().getDataVector("recMxPEm1", "");
		DataVector vector2 = this.mainService.getTree().getDataVector("recMxPEm2", "");
		// DataVector vector3 =
		// this.mainService.getTree().getDataVector("genMEmEp1", "");
		// DataVector vector4 =
		// this.mainService.getTree().getDataVector("genMEmEp2", "");
		// DataVector vector5 =
		// this.mainService.getTree().getDataVector("genMxPEp", "");

		aH1f.fill(vector);
		aH12.fill(vector2);

		TCanvas canvas = new TCanvas("name", 500, 500);
		canvas.draw(aH1f);
		TCanvas canvas2 = new TCanvas("name", 500, 500);
		canvas2.draw(aH12);
		TCanvas canvas3 = new TCanvas("name", 500, 500);
		canvas3.draw(aH1f);
		canvas3.draw(aH12, "same");

		this.mainService.getTree().drawH1F(this.mainService.getTree().getDataVector("genMxPEm1", ""), "genMxPEm1");
		this.mainService.getTree().drawH1F(this.mainService.getTree().getDataVector("genMxPEm2", ""), "genMxPEm2");
		this.mainService.getTree().drawH1F(this.mainService.getTree().getDataVector("genMEmEp1", ""), "genMEmEp1");
		this.mainService.getTree().drawH1F(this.mainService.getTree().getDataVector("genMEmEp2", ""), "genMEmEp2");

		H1F aGenH1f = new H1F("title", 100, 0.8, 3.2);
		H1F aGenH12 = new H1F("title", 100, 0.8, 3.2);
		aGenH1f.setLineColor(1);
		aGenH12.setLineColor(2);

		DataVector vectorGen = this.mainService.getTree().getDataVector("genMxPEm1", "");
		DataVector vectorGen2 = this.mainService.getTree().getDataVector("genMxPEm2", "");
		// DataVector vector3 =
		// this.mainService.getTree().getDataVector("genMEmEp1", "");
		// DataVector vector4 =
		// this.mainService.getTree().getDataVector("genMEmEp2", "");
		// DataVector vector5 =
		// this.mainService.getTree().getDataVector("genMxPEp", "");

		aGenH1f.fill(vectorGen);
		aGenH12.fill(vectorGen2);

		TCanvas canvasGen = new TCanvas("name", 500, 500);
		canvasGen.draw(aGenH1f);
		TCanvas canvasGen2 = new TCanvas("name", 500, 500);
		canvasGen2.draw(aGenH12);
		TCanvas canvasGen3 = new TCanvas("name", 500, 500);
		canvasGen3.draw(aGenH1f);
		canvasGen3.draw(aGenH12, "same");

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
