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

public interface MainService {

	public void setMCFlag(boolean flag);

	public void setRECFlag(boolean flag);

	public boolean getMCFlag();

	public boolean getRECFlag();

	public void setInvariantList(List<Coordinate> aList);

	public void setMissingMassList(List<Coordinate> aList);

	public List<Coordinate> getInvariantList();

	public List<Coordinate> getMissingMassList();

	public void addToInvariantList(String... strings);

	public void addToIMissingMassList(String... strings);

	public Map<Coordinate, List<H1F>> getH1Map();

	public void setHists(Map<Coordinate, Integer> aMap, String dataType);

	public Map<Coordinate, Integer> getHistSetter();

	public void setWriteFlag();

	public boolean getWriteFlag();

}
