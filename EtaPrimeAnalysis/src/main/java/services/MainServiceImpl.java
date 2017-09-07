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
package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jlab.groot.data.H1F;
import org.jlab.io.hipo.HipoDataSync;

import domain.Coordinate;

public class MainServiceImpl implements MainService {

	private boolean mcFlag = false;
	private boolean recFlag = false;
	private boolean writeFlag = false;

	private Map<Coordinate, List<H1F>> plotsByCoordinate = null;
	private List<Coordinate> invariantMassList = null;
	private List<Coordinate> missingMassList = null;
	private Map<Coordinate, Integer> neededHists = null;

	private HipoDataSync hipoDataSync = null;

	public MainServiceImpl() {
		this.plotsByCoordinate = new HashMap<Coordinate, List<H1F>>();
		this.invariantMassList = new ArrayList<>();
		this.missingMassList = new ArrayList<>();
		this.neededHists = new HashMap<>();

		if (writeFlag) {
			this.hipoDataSync = new HipoDataSync();
			this.hipoDataSync.open("outFile.hipo");
		}

	}

	private void makeHistograms(Map<Coordinate, Integer> aMap, String dataType) {

		for (Coordinate aCoordinate : invariantMassList) {
			List<H1F> aList = new ArrayList<>();
			String topology = getTopology(aCoordinate, "M(");
			String title = getTitle(aCoordinate, "Invariant Mass of ");
			for (int i = 0; i < this.neededHists.get(aCoordinate); i++) {
				H1F h1f = new H1F(topology, "", 100, 0, 1.0);
				h1f.setTitleX(topology);
				h1f.setTitleY("Entries");
				h1f.setTitle(title);
				aList.add(h1f);
			}

			plotsByCoordinate.put(makeHistogramCoordinate(aCoordinate, (dataType + "M")), aList);
		}
		for (Coordinate aCoordinate : missingMassList) {
			List<H1F> aList = new ArrayList<>();
			String topology = getTopology(aCoordinate, "Mx(");
			String title = getTitle(aCoordinate, "Missing Mass of ");
			for (int i = 0; i < this.neededHists.get(aCoordinate); i++) {
				H1F h1f = new H1F(topology, "", 100, 0, 4.0);
				h1f.setTitleX(topology);
				h1f.setTitleY("Entries");
				h1f.setTitle(title);
				aList.add(h1f);
			}
			plotsByCoordinate.put(makeHistogramCoordinate(aCoordinate, (dataType + "Mx")), aList);

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

	private String getTopology(Coordinate coordinate, String start) {
		StringBuilder sb = new StringBuilder(start);
		for (String str : coordinate) {
			sb.append(str);
		}
		sb.append(")[GeV]");

		return sb.toString();
	}

	private String getTitle(Coordinate coordinate, String start) {
		StringBuilder sb = new StringBuilder(start);
		for (String str : coordinate) {
			sb.append(str);
		}
		return sb.toString();
	}

	public void setMCFlag(boolean flag) {
		this.mcFlag = flag;
	}

	public void setRECFlag(boolean flag) {
		this.recFlag = flag;
	}

	public boolean getMCFlag() {
		return this.mcFlag;
	}

	public boolean getRECFlag() {
		return this.recFlag;
	}

	public void setInvariantList(List<Coordinate> aList) {
		this.invariantMassList.addAll(aList);
	}

	public void setMissingMassList(List<Coordinate> aList) {
		this.missingMassList.addAll(aList);
	}

	public List<Coordinate> getInvariantList() {
		return this.invariantMassList;
	}

	public List<Coordinate> getMissingMassList() {
		return this.missingMassList;
	}

	public void addToInvariantList(String... strings) {
		Coordinate aCoordinate = new Coordinate(strings);
		this.invariantMassList.add(aCoordinate);
	}

	public void addToIMissingMassList(String... strings) {
		Coordinate aCoordinate = new Coordinate(strings);
		this.missingMassList.add(aCoordinate);
	}

	public Map<Coordinate, List<H1F>> getH1Map() {
		return this.plotsByCoordinate;

	}

	public void setHists(Map<Coordinate, Integer> aMap, String dataType) {
		this.neededHists = aMap;
		makeHistograms(aMap, dataType);
	}

	public Map<Coordinate, Integer> getHistSetter() {
		return this.neededHists;
	}

	public void setWriteFlag() {
		this.writeFlag = true;
	}

	public boolean getWriteFlag() {
		return this.writeFlag;
	}

	public HipoDataSync getHipoDataSync() {
		return this.hipoDataSync;
	}

	public void closeHipoDataSync() {
		this.hipoDataSync.close();
	}

}
