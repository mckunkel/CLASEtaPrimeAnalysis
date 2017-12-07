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

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jlab.groot.data.DataVector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DataToJSON {
	private MainService mainService = null;
	private JSONObject obj;
	private FileWriter file;

	public DataToJSON() {
		this.mainService = ServiceManager.getSession();
		obj = new JSONObject();
	}

	@SuppressWarnings("unchecked")
	public void init() {
		obj.put("Author", "Michael C. Kunkel");
		obj.put("Project", "EtaPrimeAnalysis");
		obj.put("Email", "mkunkel@jlab.org");

		Map<String, JSONArray> aMap = new HashMap<>();

		for (String str : this.mainService.getTree().getListOfBranches()) {
			// System.out.println(str);
			aMap.put(str, new JSONArray());
			// H1F h1 = new H1F(str, 100, 0, 3);
			// h1.fill(this.mainService.getTree().getDataVector(str, ""));

			DataVector dataVector = this.mainService.getTree().getDataVector(str, "");
			for (Double double1 : dataVector.getArray()) {
				aMap.get(str).add(double1);
			}
		}

		for (Map.Entry<String, JSONArray> entry : aMap.entrySet()) {
			String key = entry.getKey();
			JSONArray value = entry.getValue();

			obj.put(key, value);
		}
		write();
	}

	private void write() {
		try {
			file = new FileWriter("test.json");

			file.write(obj.toJSONString());
			System.out.println("Successfully Copied JSON Object to File...");
			// System.out.println("\nJSON Object: " + obj);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				file.flush();
				file.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
