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
package analysis;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestData {
	private List<Container> aList = new ArrayList<>();
	private String bankName;
	private String dir;
	private int nFiles;
	private ExecutorService executorService;

	public TestData(String dir, String bankName, int nFiles) {
		this.dir = dir;
		this.bankName = bankName;
		this.nFiles = nFiles;
		this.executorService = Executors.newCachedThreadPool();
		run();
	}

	private void run() {

		File[] listOfFiles = new File(dir).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".hipo");
			}
		});
		for (int i = 0; i < nFiles; i++) {// listOfFiles.length
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
				executorService.submit(new DataContainer(dir + listOfFiles[i].getName(), bankName, aList));
			}
		}
		executorService.shutdown();
	}

	public List<Container> getaList() {
		return aList;
	}

}
