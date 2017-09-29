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
package domain.classifiers;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.ml.classification.MultilayerPerceptronClassificationModel;
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import domain.spark.utils.SparkManager;

public class MultilayerPerceptronClassifierExample {
	public static void main(String[] args) {
		SparkSession spark = SparkManager.getSession();
		Logger.getLogger("org.apache.spark.SparkContext").setLevel(Level.WARN);
		Logger.getLogger("org").setLevel(Level.OFF);
		Logger.getLogger("akka").setLevel(Level.OFF);
		// $example on$
		// Load training data
		// String dir = "/usr/local/Cellar/apache-spark/2.1.1/libexec/";
		//
		// String path = dir +
		// "data/mllib/sample_multiclass_classification_data.txt";

		String dir = "/Users/michaelkunkel/WORK/GIT_HUB/CLASEtaPrimeAnalysis/MachineLearningPID/";
		String file = "Electron.txt";
		// Load the data stored in LIBSVM format as a DataFrame.
		Dataset<Row> dataFrame = spark.read().format("libsvm").load(dir + file);

		// Dataset<Row> dataFrame = spark.read().format("libsvm").load(path);

		// Split the data into train and test
		Dataset<Row>[] splits = dataFrame.randomSplit(new double[] { 0.6, 0.4 }, 1234L);
		Dataset<Row> train = splits[0];
		Dataset<Row> test = splits[1];

		// specify layers for the neural network:
		// input layer of size 4 (features), two intermediate of size 5 and 4
		// and output of size 3 (classes)
		int[] layers = new int[] { 30, 50, 40, 3 };

		// create the trainer and set its parameters
		MultilayerPerceptronClassifier trainer = new MultilayerPerceptronClassifier().setLayers(layers)
				.setBlockSize(128).setSeed(1234L).setMaxIter(100);

		// train the model
		MultilayerPerceptronClassificationModel model = trainer.fit(train);

		// compute accuracy on the test set
		Dataset<Row> result = model.transform(test);
		Dataset<Row> predictionAndLabels = result.select("prediction", "label");
		MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator().setMetricName("accuracy");

		System.out.println("Test set accuracy = " + evaluator.evaluate(predictionAndLabels));
		// $example off$

		spark.stop();

	}

}
