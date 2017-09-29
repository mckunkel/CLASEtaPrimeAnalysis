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

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.DecisionTreeClassificationModel;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.IndexToString;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.StringIndexerModel;
import org.apache.spark.ml.feature.VectorIndexer;
import org.apache.spark.ml.feature.VectorIndexerModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import domain.spark.utils.SparkManager;

public class DecisionTree {

	public static void main(String[] args) {
		SparkSession spark = SparkManager.getSession();
		Logger.getLogger("org.apache.spark.SparkContext").setLevel(Level.WARN);
		Logger.getLogger("org").setLevel(Level.OFF);
		Logger.getLogger("akka").setLevel(Level.OFF);
		// String dir =
		// "/usr/local/Cellar/apache-spark/2.1.1/libexec/data/mllib/";
		String dir = "/Users/michaelkunkel/WORK/GIT_HUB/CLASEtaPrimeAnalysis/MachineLearningPID/";
		String file = "Electron.txt";
		// Load the data stored in LIBSVM format as a DataFrame.
		Dataset<Row> data = spark.read().format("libsvm").load(dir + file);
		/// Dataset<Row> data = spark.read().format("libsvm").load(dir +
		/// "sample_libsvm_data.txt");

		// Index labels, adding metadata to the label column.
		// Fit on whole dataset to include all labels in index.
		StringIndexerModel labelIndexer = new StringIndexer().setInputCol("label").setOutputCol("indexedLabel")
				.fit(data);

		// Automatically identify categorical features, and index them.
		VectorIndexerModel featureIndexer = new VectorIndexer().setInputCol("features").setOutputCol("indexedFeatures")
				.setMaxCategories(4) // features with > 4 distinct values are
										// treated as continuous.
				.fit(data);

		// Split the data into training and test sets (30% held out for
		// testing).
		Dataset<Row>[] splits = data.randomSplit(new double[] { 0.7, 0.3 });
		Dataset<Row> trainingData = splits[0];
		Dataset<Row> testData = splits[1];

		// Train a DecisionTree model.
		DecisionTreeClassifier dt = new DecisionTreeClassifier().setLabelCol("indexedLabel")
				.setFeaturesCol("indexedFeatures");

		// Convert indexed labels back to original labels.
		IndexToString labelConverter = new IndexToString().setInputCol("prediction").setOutputCol("predictedLabel")
				.setLabels(labelIndexer.labels());

		// Chain indexers and tree in a Pipeline.
		Pipeline pipeline = new Pipeline()
				.setStages(new PipelineStage[] { labelIndexer, featureIndexer, dt, labelConverter });

		// Train model. This also runs the indexers.
		PipelineModel model = pipeline.fit(trainingData);

		// Make predictions.
		Dataset<Row> predictions = model.transform(testData);

		// Select example rows to display.
		// predictions.select("predictedLabel", "label", "features").show(50);
		predictions.select("predictedLabel", "label", "features").filter("predictedLabel = 11.0").show(50);
		// Select (prediction, true label) and compute test error.
		MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
				.setLabelCol("indexedLabel").setPredictionCol("prediction").setMetricName("accuracy");
		double accuracy = evaluator.evaluate(predictions);
		System.out.println("Test Error = " + (1.0 - accuracy));

		DecisionTreeClassificationModel treeModel = (DecisionTreeClassificationModel) (model.stages()[2]);
		System.out.println("Learned classification tree model:\n" + treeModel.toDebugString());
		// $example off$
		try {
			model.save("target/models/DecisionTreeClassifierls");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		spark.stop();

	}
}
