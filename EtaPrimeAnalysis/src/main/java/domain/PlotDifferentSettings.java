
package domain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.ui.TCanvas;

public class PlotDifferentSettings {

	private GraphErrors grEff = null;
	private TCanvas myCanvas = null;
	private List<GraphErrors> graphList = null;

	public static void main(String[] args) throws FileNotFoundException {
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
		}

		new PlotDifferentSettings(args);

	}

	public PlotDifferentSettings(String[] textFiles) throws FileNotFoundException {
		this.graphList = new ArrayList<>();
		readTextFile(textFiles);
		setCanvas();
	}

	public void readTextFile(String[] textFile) throws FileNotFoundException {
		for (int i = 0; i < textFile.length; i++) {
			setGraph();
			grEff.setMarkerColor(i + 1);

			Scanner sc = new Scanner(new FileInputStream(textFile[i]));

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

					if (IMval > 0.1) {
						grEff.addPoint(IMval, efficiency, IMvalWidth, Defficiency);

					}

				}
			}
			graphList.add(grEff);
			sc.close();
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
		myCanvas.setVisible(true);
		for (GraphErrors graphErrors : graphList) {
			myCanvas.draw(graphErrors, "same");
		}
		myCanvas.save(
				"/Volumes/Mac_Storage/Work_Codes/CLAS12/EtaPrimeDilepton/EtaPrimeDilepton_Analysis/diffentSetting");
	}

}
