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
package domain.utils;

public class PlotUtils {

	private PlotUtils() {
	}
	/*
	 * public static H2F Mplots2D(String topology1, String topology2, String
	 * xTitle, String yTitle, int xbins, double xmin, double xmax, int ybins,
	 * double ymin, double ymax, int color, String[] cuts, double[] cutVals,
	 * double[] cutLimits, String[] cutOperations, String[] cutSeperators,
	 * String[] cutDesign) { H2F h1 = new H2F(xTitle, xbins, xmin, xmax, ybins,
	 * ymin, ymax); h1.setTitleX(xTitle); h1.setTitleY(yTitle);
	 * 
	 * double energyPerBin = 1000.0 * (xmax - xmin) / ((double) xbins);
	 * h1.setTitle("Entries / " + Double.toString(energyPerBin) + " MeV");
	 * String thisCut = loadTotalCut(cuts, cutVals, cutLimits, cutOperations,
	 * cutSeperators, cutDesign);
	 * 
	 * DataVector vector1 = this.mainService.getTree().getDataVector(topology1,
	 * thisCut); DataVector vector2 =
	 * this.mainService.getTree().getDataVector(topology2, thisCut);
	 * 
	 * System.out.println(vector1.size() + " " + vector2.size()); for (int i =
	 * 0; i < vector1.size(); i++) { h1.fill(vector1.getValue(i),
	 * vector2.getValue(i)); } return h1; }
	 * 
	 * private String loadTotalCut(String[] cuts, double[] cutVals, double[]
	 * cutLimits, String[] cutOperations, String[] cutSeperators) {
	 * 
	 * StringBuilder myCut = new StringBuilder(); if (cuts.length !=
	 * cutVals.length || cuts.length != cutLimits.length || cuts.length !=
	 * cutOperations.length || cuts.length != cutSeperators.length) {
	 * System.err.
	 * println("[[---AnalysisPlots---]] -----> Operations not equal size"); }
	 * 
	 * for (int i = 0; i < cuts.length; i++) { myCut.append( "abs(" + cuts[i] +
	 * "-" + cutVals[i] + ")" + cutOperations[i] + cutLimits[i] +
	 * cutSeperators[i]); }
	 * 
	 * // System.out.println(myCut); return myCut.toString(); }
	 * 
	 * private String loadTotalCut(String[] cuts, double[] cutVals, double[]
	 * cutLimits, String[] cutOperations, String[] cutSeperators, String[]
	 * cutDesign) {
	 * 
	 * StringBuilder myCut = new StringBuilder(); if (cuts.length !=
	 * cutVals.length || cuts.length != cutLimits.length || cuts.length !=
	 * cutOperations.length || cuts.length != cutSeperators.length ||
	 * cuts.length != cutDesign.length) { System.err.
	 * println("[[---AnalysisPlots---]] -----> Operations not equal size"); }
	 * 
	 * for (int i = 0; i < cuts.length; i++) { if (cutDesign[i] == "sym") {
	 * myCut.append( "abs(" + cuts[i] + "-" + cutVals[i] + ")" +
	 * cutOperations[i] + cutLimits[i] + cutSeperators[i]); } else if
	 * (cutDesign[i] == "side") { myCut.append( "(" + cuts[i] + "-" + cutVals[i]
	 * + ")" + cutOperations[i] + cutLimits[i] + cutSeperators[i]); }
	 * 
	 * }
	 * 
	 * // System.out.println(myCut); return myCut.toString(); }
	 */
}
