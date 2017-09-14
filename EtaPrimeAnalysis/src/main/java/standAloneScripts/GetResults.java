
package standAloneScripts;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.ui.TCanvas;

public class GetResults {

	private GraphErrors grEff = null;
	private TCanvas myCanvas = null;

	public static void main(String[] args) throws FileNotFoundException {
		// String in =
		// "/Volumes/Mac_Storage/Work_Codes/CLAS12/EtaPrimeDilepton/EtaPrimeDilepton_Analysis/EtaPrimeDilepton_Tor-1.0Sol0.8_results.txt";

		new GetResults(args[0]);
		// new GetResults(in);

	}

	public GetResults(String textFile) throws FileNotFoundException {
		setGraph();
		readTextFile(textFile);
		setCanvas();
	}

	public void readTextFile(String textFile) throws FileNotFoundException {
		FileInputStream fStream = new FileInputStream(textFile);
		Scanner sc = new Scanner(fStream);

		String currentLine;
		String im_line = "Inavriant mass";

		double IMval, IMvalWidth, efficiency, Defficiency;
		IMval = IMvalWidth = efficiency = Defficiency = 0;

		while (sc.hasNextLine()) {
			currentLine = sc.nextLine();

			if (currentLine.contains(im_line)) {
				String[] segments = currentLine.split(" ");

				IMval = Double.parseDouble(segments[2]);
				IMvalWidth = Double.parseDouble(segments[4]);
				efficiency = Double.parseDouble(segments[6]);
				Defficiency = Double.parseDouble(segments[9]);

				grEff.addPoint(IMval, efficiency, IMvalWidth, Defficiency);

			}
		}
	}

	public void setGraph() {
		this.grEff = new GraphErrors();
		grEff.setTitle("Acceptance per IM(e+e-) bin");
		grEff.setTitleX("Invariant Mass(e+e-)");
		grEff.setTitleY("Acceptance [%]");
	}

	public void setCanvas() {
		this.myCanvas = new TCanvas("myCanvas", 500, 500);
		myCanvas.draw(grEff);
	}

}
