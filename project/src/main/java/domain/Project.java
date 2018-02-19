package domain;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class Project {

	public static void main(String[] args) {
		String SAMPLE_CSV_FILE = "MLsample.csv";
		CSVPrinter csvPrinter = null;
		BufferedWriter writer = null;
		CSVRecord csvRecord = null;
		boolean onceRan = true;
		try {
			writer = Files.newBufferedWriter(Paths.get(SAMPLE_CSV_FILE), Charset.forName("US-ASCII"),
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			if (onceRan) {
				csvPrinter = new CSVPrinter(writer,
						CSVFormat.DEFAULT.withHeader("ID", "PID", "EpEmAngle", "EmEpGamAngle", "InvariantMassEpEmGam",
								"InvariantMassEpEm", "EmPx", "EmPy", "EmPz", "EmTheta", "EmPhi"));
				onceRan = false;
			} else {
				csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withIgnoreHeaderCase());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
