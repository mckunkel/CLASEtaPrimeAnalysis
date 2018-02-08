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
package testJavaR;

import javax.script.ScriptEngine;

import org.renjin.script.RenjinScriptEngineFactory;

// ... add additional imports here ...

public class Test {

	public static void main(String[] args) throws Exception {
		// create a script engine manager:
		RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();
		// create a Renjin engine:
		ScriptEngine engine = factory.getScriptEngine();

		// ... put your Java code here ...
		engine.eval("df <- data.frame(x=1:10, y=(1:10)+rnorm(n=10))");
		engine.eval("print(df)");
		engine.eval("print(lm(y ~ x, df))");
		String dir = "/Users/michaelkunkel/Google Drive/";
		String path = "UdemyCourses/R/R-Course-HTML-Notes/R-for-Data-Science-and-Machine-Learning/Machine Learning with R/";
		String trainData = "titanic_test.csv";
		System.out.println(dir + path + trainData);
		engine.put("dir", dir);
		engine.put("path", path);
		engine.put("trainData", trainData);

		engine.eval("df.train <-read.csv(paste0(dir,path,trainData))");
		engine.eval("print(head(df.train))");
		engine.eval("ggplot(df.train,aes(Survived))+geom_bar()");

	}
}
