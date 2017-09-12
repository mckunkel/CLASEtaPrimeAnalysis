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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.jlab.groot.data.DataVector;
import org.jlab.groot.tree.Branch;
import org.jlab.groot.tree.Tree;
import org.jlab.groot.tree.TreeProvider;

public class TreeVector extends Tree implements TreeProvider {

	private static int TREEFILE_CSV = 1;
	private static int TREEFILE_SPACE = 2;
	private static int TREEFILE_SEMICOLON = 3;

	private String dataSeparator = "\\s+";
	private int textFileType = 2;

	private List<DataVector> dataVectors = new ArrayList<DataVector>();
	private int currentData = 0;

	public TreeVector(String name) {
		super(name);

	}

	public TreeVector(String name, int type) {
		super(name);
		this.textFileType = type;
		if (type == TreeVector.TREEFILE_CSV) {
			dataSeparator = ",";
		}
		if (type == TreeVector.TREEFILE_SEMICOLON) {
			dataSeparator = ";";
		}
	}

	public TreeVector(String name, String filename, int columns) {
		super(name);
	}

	public void readFile(String filename) {
		this.readFile(filename, dataSeparator);
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

	public void readFile(String filename, String separator) {

		dataVectors.clear();
		String line = null;
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(filename);
			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int lineNumber = 0;
			int firstLineRead = 0;

			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("#") == false) {
					// System.out.println(line);
					String[] tokens = line.split(separator);
					if (tokens.length > 0) {
						DataVector vector = new DataVector();
						for (int i = 0; i < tokens.length; i++) {

							try {
								double value = Double.parseDouble(tokens[i]);
								vector.add(value);
							} catch (Exception e) {

							}
						}

						if (dataVectors.size() > 0) {
							if (vector.getSize() != dataVectors.get(0).getSize()) {
								System.out.println("[TreeTextFile::read] ---> error on line # " + lineNumber);
							} else {
								dataVectors.add(vector);
							}
						} else {
							dataVectors.add(vector);
						}

						if (firstLineRead == 0 && this.getListOfBranches().size() == 0) {
							int size = vector.getSize();
							String[] names = this.generateBranchNames(size);
							this.initBranches(names);
							firstLineRead++;
						}
					}
				} else {
					line = line.replace("#!", "");
					String[] labels = line.split(":");
					initBranches(labels);
				}
				lineNumber++;
			}
			// Always close files.
			bufferedReader.close();
			this.currentData = 0;
		} catch (FileNotFoundException ex) {
			// ClasUtilsFile.printLog("Unable to open file : '" + filename +
			// "'");
		} catch (IOException ex) {
			// Or we could just do this:
			// ex.printStackTrace();
		}
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

	@Override
	public void reset() {
		this.currentData = 0;
	}

	@Override
	public boolean readNext() {
		if (this.currentData >= this.dataVectors.size())
			return false;
		this.readEntry(currentData);
		currentData++;
		return true;
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
			icounter++;
		}
		return 1;
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
		this.readFile(filename);
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

	public static void main(String[] args) {
		TreeVector tree = new TreeVector("T");
		DataPoint d1 = new DataPoint(11.0, 12.0, 13.0, 14.0);
		DataPoint d2 = new DataPoint(3.0, 12.0, 13.0, 14.0);
		DataPoint d3 = new DataPoint(6.0, 12.0, 13.0, 14.0);
		DataPoint d4 = new DataPoint(19.0, 13.0, 23.0, 14.0);
		DataPoint d5 = new DataPoint(21.0, 14.0, 33.0, 14.0);
		DataPoint d6 = new DataPoint(12.0, 15.0, 43.0, 14.0);

		tree.addToTree(d1);
		tree.addToTree(d2);
		tree.addToTree(d3);
		tree.addToTree(d4);
		tree.addToTree(d5);
		tree.addToTree(d6);
		String[] labels = { "p1", "p2", "p3", "p4" };
		tree.initBranches(labels);

		System.out.println(" entries = " + tree.getEntries());

		DataVector vec = tree.getDataVector("p1", "p2>12.5&&p3>23.5", 10);
		System.out.println(" datavector size =  " + vec.getSize());
		for (int i = 0; i < vec.getSize(); i++) {
			System.out.println(" element " + i + " =  " + vec.getValue(i));
		}
	}

}
