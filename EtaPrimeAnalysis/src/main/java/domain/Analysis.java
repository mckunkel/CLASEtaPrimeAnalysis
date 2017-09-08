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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.Vector3;
import org.jlab.groot.data.H1F;
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
	private String[] fileList = null;
	private int NinputFiles = 0;

	private String mcBankName;
	private String recBankName;
	private List<String> reactionList = null;
	private ReactionFilter genFilter = null;
	private ReactionFilter reconFilter = null;
	private MainService mainService = null;

	private int screenWidth = 1200;// myCanvas1.getWidth();// 100;// (int)
	// (screensize.getWidth() / 2);
	private int screenHeight = 800;// myCanvas1.getHeight();// 100;// (int)
	// (screensize.getHeight() /
	// 2);

	public Analysis(String[] fileList, List<String> reactionList) {
		this.fileList = fileList;
		this.NinputFiles = fileList.length;
		this.reactionList = reactionList;
		this.genFilter = new ReactionFilter();
		this.reconFilter = new ReactionFilter();
		this.mainService = ServiceManager.getSession();

		init();
		run();

	}

	private void init() {
		this.mcBankName = "MC::Particle";
		this.recBankName = "REC::Particle";

		this.genFilter.setReactionList(this.reactionList);
		this.reconFilter.setReactionList(this.reactionList);

	}

	private void run() {
		for (int i = 0; i < NinputFiles; i++) {
			System.out.println("operating on file " + this.fileList[i]);
			this.hipoReader = new HipoDataSource();
			this.hipoReader.open(this.fileList[i]);
			readHipo();
		}
		plotHistograms("gen");
		plotHistograms("rec");
		makeAcceptance();

		// System.exit(0);
	}

	private void readHipo() {

		for (int evnt = 1; evnt < getNEvents(); evnt++) {// getNEvents()
			DataEvent event = (DataEvent) this.hipoReader.gotoEvent(evnt);
			List<Particle> genList = fillParticleList(event, this.mcBankName);
			List<Particle> recList = fillParticleList(event, this.recBankName);
			this.genFilter.setParticleList(genList);
			this.reconFilter.setParticleList(recList);

			MakePlots makeGenPlots = new MakePlots(this.genFilter.reactionList(), "gen");
			makeGenPlots.init();
			MakePlots makeRecPlots = new MakePlots(this.reconFilter.reactionList(), "rec");
			makeRecPlots.init();

		}

	}

	private void plotHistograms(String dataType) {
		JFrame frame = makeJFrame(dataType);
		JPanel mainPanel = makeJPanel();

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

		savePlots(mainPanel, dataType);

	}

	private void makeAcceptance() {
		JFrame frame2 = makeJFrame("Acceptance");
		JPanel mainPanel2 = makeJPanel();
		EmbeddedCanvas can2 = new EmbeddedCanvas();
		can2.draw(makeAcceptancePlot());
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

	private H1F makeAcceptancePlot() {
		H1F h1f = this.mainService.getH1Map()
				.get(makeHistogramCoordinate(this.mainService.getInvariantList().get(0), ("rec" + "M"))).get(0)
				.histClone("h1f");
		h1f.setTitleX("M(e+e-) GeV");
		h1f.setTitleY("Acceptance");
		h1f.divide(this.mainService.getH1Map()
				.get(makeHistogramCoordinate(this.mainService.getInvariantList().get(0), ("gen" + "M"))).get(1));

		return h1f;

	}

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
