package domain.spark.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import domain.utils.StringConstants;

public enum SparkManager {
	INSTANCE;

	// A name for the spark instance. Can be any string
	private static String appName = StringConstants.APP_NAME;
	// Pointer / URL to the Spark instance - embedded
	private static String sparkMaster = StringConstants.SPARK_MASTER;
	private static String tempDir = StringConstants.TEMP_DIR;

	private static JavaSparkContext spContext = null;
	private static SparkSession sparkSession = null;

	private static void getConnection() {

		if (spContext == null) {
			// Setup Spark configuration
			SparkConf conf = new SparkConf().setAppName(appName).setMaster(sparkMaster);

			// Make sure you download the winutils binaries into this directory
			// from
			// https://github.com/srccodes/hadoop-common-2.2.0-bin/archive/master.zip
			System.setProperty("hadoop.home.dir", ".");

			// Create Spark Context from configuration
			spContext = new JavaSparkContext(conf);
			// spContext.addJar("/usr/local/Cellar/apache-spark/2.1.1/mysql-connector-java-5.1.40-bin.jar");
			sparkSession = SparkSession.builder().appName(appName).master(sparkMaster)
					.config("spark.sql.warehouse.dir", tempDir).getOrCreate();

		}

	}

	public static JavaSparkContext getContext() {

		if (spContext == null) {
			getConnection();
		}
		return spContext;
	}

	public static SparkSession getSession() {
		if (sparkSession == null) {
			getConnection();
		}
		return sparkSession;
	}

	public static Map<String, String> jdbcOptions() {
		Map<String, String> jdbcOptions = new HashMap<String, String>();
		jdbcOptions.put("url", "jdbc:mysql://localhost:3306/test?jdbcCompliantTruncation=false");
		jdbcOptions.put("driver", "com.mysql.jdbc.Driver");
		jdbcOptions.put("dbtable", "status_change");
		jdbcOptions.put("user", "root");
		jdbcOptions.put("password", "");
		return jdbcOptions;
	}

	public static String jdbcAppendOptions() {

		return SparkManager.jdbcOptions().get("url") + "&user=" + SparkManager.jdbcOptions().get("user") + "&password="
				+ SparkManager.jdbcOptions().get("password");

	}

	public static Dataset<Row> mySqlDataset() {
		SparkSession spSession = getSession();
		spSession.sql("set spark.sql.caseSensitive=false");
		// Dataset<Row> demoDf =
		// spSession.read().format("jdbc").options(jdbcOptions()).load();

		Dataset<Row> demoDf = spSession.read().format("jdbc").options(jdbcOptions()).option("inferSchema", true)
				.option("header", true).option("comment", "#").load();

		return demoDf;
	}

	public static void hold() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void shutdown() {
		SparkManager.sparkSession.stop();
	}

	public static void restart() {
		SparkSession.clearActiveSession();
	}
}
