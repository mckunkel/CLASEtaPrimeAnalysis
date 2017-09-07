
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

		List<String> aList = new ArrayList<>();
		aList.add(
				"/Users/michaelkunkel/WORK/GIT_HUB/CLASEtaPrimeAnalysis/TXTfiles/EtaPrimeDilepton_Tor-0.75Sol0.6_results.txt");
		aList.add(
				"/Users/michaelkunkel/WORK/GIT_HUB/CLASEtaPrimeAnalysis/TXTfiles/EtaPrimeDilepton_Tor-0.75Sol0.8_results.txt");
		aList.add(
				"/Users/michaelkunkel/WORK/GIT_HUB/CLASEtaPrimeAnalysis/TXTfiles/EtaPrimeDilepton_Tor0.75Sol0.6_results.txt");
		aList.add(
				"/Users/michaelkunkel/WORK/GIT_HUB/CLASEtaPrimeAnalysis/TXTfiles/EtaPrimeDilepton_Tor0.75Sol0.8_results.txt");
		aList.add(
				"/Users/michaelkunkel/WORK/GIT_HUB/CLASEtaPrimeAnalysis/TXTfiles/EtaPrimeDilepton_Tor-1.0Sol0.6_results.txt");
		aList.add(
				"/Users/michaelkunkel/WORK/GIT_HUB/CLASEtaPrimeAnalysis/TXTfiles/EtaPrimeDilepton_Tor-1.0Sol0.8_results.txt");
		aList.add(
				"/Users/michaelkunkel/WORK/GIT_HUB/CLASEtaPrimeAnalysis/TXTfiles/EtaPrimeDilepton_Tor1.0Sol0.6_results.txt");
		aList.add(
				"/Users/michaelkunkel/WORK/GIT_HUB/CLASEtaPrimeAnalysis/TXTfiles/EtaPrimeDilepton_Tor1.0Sol0.8_results.txt");

		// new PlotDifferentSettings(args);
		String[] array = aList.toArray(new String[0]);
		new PlotDifferentSettings(array);

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

			System.out.println(grEff.getMarkerColor() + "  " + textFile.toString());
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
		this.myCanvas = new TCanvas("myCanvas", 1200, 1200);
		myCanvas.setVisible(true);
		for (GraphErrors graphErrors : graphList) {
			myCanvas.draw(graphErrors, "same");
		}
		int numCanvas = graphList.size();

		TCanvas aCanvas = new TCanvas("newCanvas", 1200, 1200);
		aCanvas.divide(3, 3);

		for (int i = 0; i < numCanvas; i++) {
			aCanvas.cd(i);
			aCanvas.draw(graphList.get(i));
		}
		myCanvas.save("diffentSetting.png");
		aCanvas.save("individualdiffentSetting.png");
	}

}
