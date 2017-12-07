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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.jlab.groot.data.DataVector;
import org.jlab.groot.data.H1F;
import org.jlab.groot.tree.Branch;
import org.jlab.groot.tree.Tree;
import org.jlab.groot.tree.TreeProvider;
import org.jlab.groot.ui.TCanvas;

public class TreeVector extends Tree implements TreeProvider {

	private List<DataVector> dataVectors = new ArrayList<DataVector>();

	public TreeVector(String name) {
		super(name);

	}

	public void addToTree(DataPoint aDataPoint) {
		DataVector vector = new DataVector();
		for (Double double1 : aDataPoint) {
			vector.add(double1);
		}
		if (dataVectors.size() > 0) {
			if (vector.getSize() != dataVectors.get(0).getSize()) {
				System.out.println("[TreeVector::add] ---> error on data input # ");
			} else {
				dataVectors.add(vector);
			}
		} else {
			dataVectors.add(vector);
		}
	}

	public void addToTree(List<DataVector> aList) {
		dataVectors.addAll(aList);
	}

	private String[] generateBranchNames(int count) {
		byte currentH = 'a';
		byte currentL = 'a';
		int clow = 0;
		String[] branchNames = new String[count];
		for (int i = 0; i < count; i++) {
			branchNames[i] = new String(new byte[] { currentH, currentL });
			currentL++;
			clow++;
			if (clow >= 26) {
				clow = 0;
				currentL = 'a';
				currentH++;
			}
		}
		return branchNames;
	}

	public void initBranches(String[] names) {
		this.getBranches().clear();
		for (String n : names) {
			addBranch(n, "", "");
			// System.out.println("---> adding branch : " + n);
		}
		System.out.println("[TreeTextFile::init] ---> initializing branches. count = " + this.getBranches().size());
	}

	public double[] getRow() {
		double[] data = new double[getBranches().size()];

		return data;
	}

	public void openFile() {

	}

	@Override
	public int getEntries() {
		return this.dataVectors.size();
	}

	@Override
	public int readEntry(int entry) {
		DataVector vec = this.dataVectors.get(entry);
		int icounter = 0;
		for (Map.Entry<String, Branch> branches : getBranches().entrySet()) {
			String key = branches.getKey();
			branches.getValue().setValue(vec.getValue(icounter));
			// System.out.println(key + " what what " + vec.getValue(icounter) +
			// " counter " + icounter);
			icounter++;
		}
		return 1;
	}

	public void drawH1F(DataVector dVector, String name) {
		TCanvas canvas = new TCanvas("aCanvas", 500, 500);
		canvas.draw(create(name, 100, dVector));
	}

	@Override
	public TreeModel getTreeModel() {
		return new DefaultTreeModel(getRootNode());
	}

	@Override
	public List<DataVector> actionTreeNode(TreePath[] path, int limit) {

		String expression = "";
		List<DataVector> vectors = new ArrayList<DataVector>();

		if (path.length == 1) {
			expression = path[0].getLastPathComponent().toString();
			DataVector vec = getDataVector(expression, "", limit);
			vectors.add(vec);
			return vectors;
		}

		if (path.length > 1) {
			String xTitle = path[0].getLastPathComponent().toString();
			String yTitle = path[1].getLastPathComponent().toString();
			expression = xTitle + ":" + yTitle;
			scanTree(expression, "", limit, false);
			List<DataVector> vecs = this.getScanResults();
			return vecs;
			/*
			 * H2F h2d = H2F.create(expression,
			 * 100,100,vecs.get(0),vecs.get(1)); h2d.setTitle(expression);
			 * h2d.setTitleX(xTitle); h2d.setTitleY(yTitle);
			 * canvas.drawNext(h2d); canvas.update();
			 */
		}
		return vectors;
		// this.drawCanvas.drawNext(h1d);
		// this.drawCanvas.getPad(0).addPlotter(new HistogramPlotter(h1d));
		// canvas.update();
	}

	@Override
	public void setSource(String filename) {
		// this.readFile(filename);
		// throw new UnsupportedOperationException("Not supported yet."); //To
		// change body of generated methods, choose Tools | Templates.
	}

	@Override
	public JDialog treeConfigure() {
		return null;
	}

	@Override
	public Tree tree() {
		return this;
	}

	public static H1F create(String name, int bins, DataVector vec) {
		DataVector newVec = new DataVector();
		for (int i = 0; i < vec.getSize(); i++) {
			if (vec.getValue(i) == -10000.0) {
				continue;
			} else
				newVec.add(vec.getValue(i));
		}

		double min = newVec.getMin();
		double max = newVec.getMax();
		if (min == max) {
			min = .9999 * min;
			max = 1.0001 * max;
		}
		// lets increment max by one increment so we can see last bin
		double increment = (max - min) / bins;
		max = max + increment;
		H1F h = new H1F(name, "", bins, min, max);
		h.setTitle(name);
		for (int i = 0; i < newVec.getSize(); i++) {
			h.fill(newVec.getValue(i));
		}
		h.setFillColor(43);
		return h;
	}

	public static void main(String[] args) {
		TreeVector tree = new TreeVector("T");
		DataPoint d1 = new DataPoint(11.0, 12.0, 13.0, 14.0);
		DataPoint d2 = new DataPoint(3.0, 12.0, 13.0, 14.0);
		DataPoint d3 = new DataPoint(6.0, 12.0, 13.0, 14.0);
		DataPoint d4 = new DataPoint(19.0, 13.0, 23.0, 14.0);
		DataPoint d5 = new DataPoint(21.0, 14.0, 33.0, 14.0);
		DataPoint d6 = new DataPoint(12.0, 15.0, 43.0, 14.0);
		DataPoint d7 = new DataPoint(0.0, 15.0, 43.0, 14.0);
		DataPoint d8 = new DataPoint(100.0, 15.0, 43.0, 14.0);

		tree.addToTree(d1);
		tree.addToTree(d2);
		tree.addToTree(d3);
		tree.addToTree(d4);
		tree.addToTree(d5);
		tree.addToTree(d6);
		tree.addToTree(d7);
		tree.addToTree(d8);

		String[] labels = { "p1", "p2", "p3", "genMxpe1" };
		// tree.initBranches(labels);
		tree.addBranch("p1", "a p1", "mass");
		tree.addBranch("p2", "a p2", "cm");
		tree.addBranch("p3", "a p3", "time");
		tree.addBranch("genMxpe1", "a p4", "W");

		System.out.println(" entries = " + tree.getEntries());

		DataVector vec = tree.getDataVector("p1", "", 10);
		tree.drawH1F(vec, "");
		// DataVector vec2 = tree.getDataVector("p2", "", 10);

		tree.drawH1F(tree.getDataVector("genMxpe1", ""), "my name");
		System.out.println(" datavector size =  " + vec.getSize());
		for (int i = 0; i < vec.getSize(); i++) {
			System.out.println(" element " + i + " =  " + vec.getValue(i));
		}

		String teString = "Me+e-1";
		String my_new_str = teString.replaceAll("e\\+", "Ep");
		my_new_str = my_new_str.replaceAll("e\\-", "Em");

		System.out.println(my_new_str);
	}

}
