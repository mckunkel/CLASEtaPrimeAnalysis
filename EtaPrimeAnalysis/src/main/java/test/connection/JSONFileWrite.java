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
package test.connection;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author Crunchify.com
 */

public class JSONFileWrite {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {

		Random random = new Random();
		JSONObject obj = new JSONObject();
		obj.put("Name", "crunchify.com");
		obj.put("Author", "App Shah");

		JSONArray company = new JSONArray();
		company.add("Compnay: eBay");
		company.add("Compnay: Paypal");
		company.add("Compnay: Google");
		obj.put("Company List", company);

		Map<String, JSONArray> aMap = new HashMap<>();
		aMap.put("MxPepem", new JSONArray());
		aMap.put("MxPepemgamma", new JSONArray());

		for (int i = 0; i < 10; i++) {
			aMap.get("MxPepem").add(random.nextInt(10));
			aMap.get("MxPepemgamma").add(random.nextInt(5));

		}

		// for (String key : aMap.keySet()) {
		//
		// }
		// for (JSONArray value : aMap.values()) {
		// // ...
		// }

		for (Map.Entry<String, JSONArray> entry : aMap.entrySet()) {
			String key = entry.getKey();
			JSONArray value = entry.getValue();

			obj.put(key, value);
		}

		// try-with-resources statement based on post comment below :)
		try (FileWriter file = new FileWriter("file.json")) {
			file.write(obj.toJSONString());
			System.out.println("Successfully Copied JSON Object to File...");
			System.out.println("\nJSON Object: " + obj);
		}
	}
}