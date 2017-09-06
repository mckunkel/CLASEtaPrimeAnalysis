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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.Vector3;
import org.jlab.groot.graphics.EmbeddedCanvas;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import services.MainService;
import services.ServiceManager;

public class Analysis {

	private HipoDataSource hipoReader = null;
	private String fileName;
	private String mcBankName;
	private String recBankName;
	private List<String> reactionList = null;
	private ReactionFilter genFilter = null;
	private ReactionFilter reconFilter = null;
	private MainService mainService = null;

	public Analysis(String fileName, List<String> reactionList) {
		this.fileName = fileName;
		this.reactionList = reactionList;
		this.genFilter = new ReactionFilter();
		this.reconFilter = new ReactionFilter();
		this.mainService = ServiceManager.getSession();

		init();
		readHipo();

	}

	private void init() {
		this.hipoReader = new HipoDataSource();
		this.hipoReader.open(fileName);

		this.mcBankName = "MC::Particle";
		this.recBankName = "REC::Particle";

		this.genFilter.setReactionList(this.reactionList);
		this.reconFilter.setReactionList(this.reactionList);

	}

	private void readHipo() {
		for (int evnt = 1; evnt < getNEvents(); evnt++) {// getNEvents()
			DataEvent event = (DataEvent) hipoReader.gotoEvent(evnt);
			List<Particle> genList = fillParticleList(event, mcBankName);
			List<Particle> recList = fillParticleList(event, recBankName);
			genFilter.setParticleList(genList);
			reconFilter.setParticleList(recList);

			MakePlots makeGenPlots = new MakePlots(genFilter.reactionList(), "gen");
			makeGenPlots.init();
			MakePlots makeRecPlots = new MakePlots(reconFilter.reactionList(), "rec");
			makeRecPlots.init();

		}
		plotHistograms("gen");
		plotHistograms("rec");

	}

	private void plotHistograms(String dataType) {
		String plotName = dataType + "Plots";
		JFrame frame = new JFrame(plotName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screensize = null;
		screensize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = 1200;// myCanvas1.getWidth();// 100;// (int)
								// (screensize.getWidth() / 2);
		int screenHeight = 800;// myCanvas1.getHeight();// 100;// (int)
								// (screensize.getHeight() /
								// 2);

		frame.setSize(screenWidth, screenHeight);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setSize(screenWidth, screenHeight);

		EmbeddedCanvas can1 = new EmbeddedCanvas();
		can1.divide(2, 2);
		can1.cd(0);
		can1.draw(this.mainService.getH1Map()
				.get(makeHistogramCoordinate(this.mainService.getMissingMassList().get(0), (dataType + "Mx"))).get(0));
		can1.cd(1);
		can1.draw(this.mainService.getH1Map()
				.get(makeHistogramCoordinate(this.mainService.getMissingMassList().get(0), (dataType + "Mx"))).get(1));
		can1.cd(2);
		can1.draw(this.mainService.getH1Map()
				.get(makeHistogramCoordinate(this.mainService.getInvariantList().get(0), (dataType + "M"))).get(1));
		can1.cd(3);
		can1.draw(this.mainService.getH1Map()
				.get(makeHistogramCoordinate(this.mainService.getInvariantList().get(0), (dataType + "M"))).get(0));
		mainPanel.add(can1);

		frame.add(mainPanel);
		frame.setVisible(true);
		try {
			Document d = new Document(PageSize.A4);// PageSize.A4.rotate()
			PdfWriter writer = PdfWriter.getInstance(d, new FileOutputStream(
					"/Users/michaelkunkel/WORK/GiBUU/clas/EtaPrimeDilepton/ReconstructedFiles/" + plotName + ".pdf"));
			d.open();

			PdfContentByte cb = writer.getDirectContent();
			cb.saveState();
			// PageSize.A4.getWidth(), PageSize.A4.getHeight()
			PdfTemplate template = cb.createTemplate(screenWidth, screenHeight);
			// can1.getWidth(), can1.getHeight()
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
		// myCanvas1.cd(2);
	}

	private Coordinate makeHistogramCoordinate(Coordinate aCoordinate, String string) {
		int size = aCoordinate.getSize() + 1;
		String[] sb = new String[size];
		sb[0] = string;
		for (int i = 0; i < aCoordinate.getSize(); i++) {
			sb[i + 1] = aCoordinate.getStrings()[i];

		}
		Coordinate coordinate = new Coordinate(sb);

		return coordinate;
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
		return aList;
	}

	private int getpid(DataBank aBank, int evntIndex) {
		int pid = aBank.getInt("pid", evntIndex);
		if (pid == 0) {
			return -3312;
		}
		return pid;
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
