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

import java.util.List;
import java.util.Map;

import org.jlab.groot.data.H1F;

import domain.Coordinate;
import domain.Cuts;
import domain.TreeVector;

public interface MainService {

	public void runService(String[] array);

	public void setReaction(String... strings);

	public void addMCService();

	public void addReconService();

	public List<String> getSkimService();

	public List<String> getReactionList();

	public void setMCFlag(boolean flag);

	public void setRECFlag(boolean flag);

	public boolean getMCFlag();

	public boolean getRECFlag();

	public void addCut(double mean, double sigmaRange, String... strings);

	public void addCut(double mean, double sigma, double sigmaRange, String... strings);

	public void addCut(double mean, int side, String... strings);

	public List<Cuts> getcutList();

	public List<Coordinate> getInvariantList();

	public List<Coordinate> getMissingMassList();

	public void addToInvariantList(String... strings);

	public void addToIMissingMassList(String... strings);

	public Map<Coordinate, List<H1F>> getH1Map();

	public void setHists(Map<Coordinate, Integer> aMap, List<String> skimService);

	public List<String> getGenList();

	public List<String> getRecList();

	public Map<Coordinate, Integer> getHistSetter();

	public TreeVector getTree();

}
