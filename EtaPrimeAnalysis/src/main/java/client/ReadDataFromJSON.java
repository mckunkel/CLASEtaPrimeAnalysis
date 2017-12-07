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
package client;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.jlab.groot.data.DataVector;
import org.jlab.groot.data.H1F;
import org.jlab.groot.ui.TCanvas;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import domain.DataPoint;
import domain.TreeVector;

public class ReadDataFromJSON {
	private TreeMap<String, DataVector> aMap;
	private TreeVector treeVector = null;
	private DataPoint[] dataPoints;

	public ReadDataFromJSON() {
		this.aMap = new TreeMap<>();
		this.treeVector = new TreeVector("T");

		makeMap();
		makeTree();
	}

	public Map<String, DataVector> getaMap() {
		return aMap;
	}

	public TreeVector getTree() {
		return treeVector;
	}

	private void makeTree() {
		dataPoints = new DataPoint[aMap.firstEntry().getValue().size()];
		for (String key : aMap.keySet()) {
			treeVector.addBranch(key, "", "");
		}

		for (DataVector value : aMap.values()) {
			for (int i = 0; i < value.size(); i++) {

				if (dataPoints[i] == null) {
					dataPoints[i] = new DataPoint((Double) value.getValue(i));
				} else {
					dataPoints[i] = dataPoints[i].addDataPoint(new DataPoint((Double) value.getValue(i)));
				}
			}
		}
		for (DataPoint dataPoint : dataPoints) {
			treeVector.addToTree(dataPoint);
		}
	}

	private void makeMap() {
		JSONParser parser = new JSONParser();

		try {
			Object obj = parser.parse(new FileReader("EtaPTor-0.75Sol0.8WithFT.json"));

			JSONObject jsonObject = (JSONObject) obj;

			for (Object key : jsonObject.keySet()) {
				// based on you key types
				String keyStr = (String) key;
				Object keyvalue = jsonObject.get(keyStr);
				if (!(keyvalue instanceof String)) {
					System.out.println("key: " + keyStr);

					this.aMap.put(keyStr, new DataVector());

					JSONArray points = (JSONArray) jsonObject.get(keyStr);

					for (Object c : points) {
						this.aMap.get(keyStr).add((double) c);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ReadDataFromJSON rJson = new ReadDataFromJSON();
		TreeVector treeVector = rJson.getTree();
		Map<String, DataVector> aMap = rJson.getaMap();
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
		h1.fill(treeVector.getDataVector("recMEmEpgamma", "recEm_Pz<2.0"));

		H1F h2 = new H1F("M(e+e-)", bins, xmin, xmax);
		h2.setTitleX("Generated M(e+e-)");
		h2.setOptStat(1000001);
		h2.setFillColor(2);
		h2.setLineColor(2);
		h2.setTitleY("Entries / " + Double.toString(energyPerBin) + " MeV");
		h2.fill(treeVector.getDataVector("recMEmEp", "recEm_Pz<2.0"));

		// c1.getCanvas().getPad(0).getAxisY().setLog(true);
		// c1.getCanvas().getPad(1).getAxisY().setLog(true);

		c1.cd(0);
		c1.draw(h1);
		c1.cd(1);
		c1.draw(h2);
	}
}
