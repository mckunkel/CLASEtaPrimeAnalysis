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
import org.jlab.groot.data.GraphErrors;
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
	
	private String myCut = null;
	private GraphErrors grRatio = null;

	public AnalysisPlotsForDaniel() {
		this.mainService = ServiceManager.getSession();
	}

	public void run() {
		plotHistograms();
		/*
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
		*/
	}
	
	
	private void plotHistograms() {

		plotGenWCuts(0.975,0.03,30,"total");
		plotRecWCuts(0.975,0.03,30,"total");
		plotAcceptance();
		plotEstimatedYield();

	}
	
	//*******************************************************************************************************
	

	private void plotGenWCuts(double mean, double sigma, int Nsteps, String option) {
		DataVector vector = this.mainService.getTree().getDataVector("genMxPEm1","");
		DataVector vector2 = this.mainService.getTree().getDataVector("genMxPEm2","");

        cutScanner("genMxPEm1", "genMxPEm2", Nsteps, mean, sigma,option);
		
        drawRatio(mean,sigma,"gen");
	}
	
	//========================================

	private void plotRecWCuts(double mean, double sigma, int Nsteps, String option) {
		DataVector vector = this.mainService.getTree().getDataVector("recMxPEm1","");
		DataVector vector2 = this.mainService.getTree().getDataVector("recMxPEm2","");

        cutScanner("recMxPEm1", "recMxPEm2", Nsteps, mean, sigma,option);
		
        drawRatio(mean,sigma,"rec");
	}
	
	//========================================

	private void plotAcceptance() {

	}
	
	//========================================

	private void plotEstimatedYield() {

	}
	
	//========================================
	
	public void loadCut(String myVar, double mean, double sigma, double sigmaRange, String option) {
		resetCut();
		
		if(option.equals("")) {
			this.myCut = "abs(" + myVar + "-" + mean + ") < " + sigmaRange + "*" + sigma;
		}else if(option.equals("left")) {
			this.myCut = myVar + ">" + mean + "-" + sigmaRange + "*" + sigma;
		}else if(option.equals("right")) {
			this.myCut = myVar + "<" + mean + "+" + sigmaRange + "*" + sigma;
		}else {
			System.out.println("Sorry. You did not specify any cut. Nothing will happen.");
			myCut = "";
		}
	}
	
	//========================================
	
	public void resetCut() {
		myCut = null;
	}
	
	//========================================
	
	public String applyCut() {
		return myCut;
	}
	
	//========================================
	
	public void cutScanner(String var1, String var2, int Nsteps, double mean, double sigma, String option) {
		grRatio = null;
		grRatio = new GraphErrors();
		double step = (3.0-mean)/(sigma*Nsteps);
		
		DataVector vec1,vec2;
		H1F hist1 = new H1F("hist1",100,0,3.0);
		H1F hist2 = new H1F("hist2",100,0,3.0);
		double ratio = 0;
		double cutval = 0;
		
		double maxRatio = 0;
		double foundCutMax = 0;
		int foundNMax = 0;
		
		double minRatio = 10000;
		double foundCutMin = 0;
		int foundNMin = 0;
		
		for(int k=0;k<Nsteps;k++) {
			cutval = mean + sigma*step*(k+1);
			loadCut(var1,mean,sigma,step*(k+1),"right");
			vec1 = this.mainService.getTree().getDataVector(var1,applyCut());
			hist1.fill(vec1);
			loadCut(var2,mean,sigma,step*(k+1),"right");
			vec2 = this.mainService.getTree().getDataVector(var2,applyCut());
			hist2.fill(vec2);
			
			ratio = getRatio(hist1,hist2,option);
			//System.out.println("Step: " + k + " with cut-val: " +  cutval  +  " with ratio: " + ratio);
			grRatio.addPoint(k+1, ratio, 0.0, 0.0);
			
			if(ratio >= maxRatio) {
				maxRatio = ratio;
				foundCutMax = cutval;
				foundNMax = k+1;
			}
			
			if(ratio <= minRatio) {
				minRatio = ratio;
				foundCutMin = cutval;
				foundNMin = k+1;
			}
			
			vec1.clear();
			vec2.clear();
			hist1.reset();
			hist2.reset();
		}
		
		System.out.println("    ");
		System.out.println("Found cut value: " + foundCutMax +  " at "+ foundNMax + " sigma at max. S/(S+B) ratio:  " + maxRatio);
		System.out.println("Found cut value: " + foundCutMin +  " at "+ foundNMin + " sigma at min. S/(S+B) ratio:  " + minRatio);
		System.out.println("    ");
		
	}
	
	//========================================
	
	public double getRatio(H1F hist1, H1F hist2, String option) {
		double ratio = 0.0;
		
		double leftR = 0.975 - 2.5*0.03;
		double rightR = 0.975 + 2.5*0.03;
		
		int leftInt = hist1.getAxis().getBin(leftR);
		int rightInt = hist1.getAxis().getBin(rightR);
		
		if(option.equals("peak")) {
		 ratio = hist1.integral(leftInt,rightInt) / (hist1.integral(leftInt,rightInt) + hist2.integral(leftInt,rightInt));
		}else if(option.equals("total")) {
			ratio = hist1.integral() / (hist1.integral() + hist2.integral());
		}
		return ratio;
	}
	
	//========================================
	
	
	public void drawRatio(double mean, double sigma, String option) {
		String xAxisName = "n from " + mean + " < " + "n*" + sigma; 
		grRatio.setTitleX(xAxisName);
		grRatio.setTitleY("S/(S + B)");
		
		String cname;
		if(option.equals("gen")) {
			cname = "Scan of cuts for generated particles";
		}else if(option.equals("rec")) {
			cname = "Scan of cuts for reconstructed particles";
		}else cname = "";
		TCanvas rc = new TCanvas(cname,500,500);
		rc.draw(grRatio);
	}
	//*******************************************************************************************************
	
/*
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
*/
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
