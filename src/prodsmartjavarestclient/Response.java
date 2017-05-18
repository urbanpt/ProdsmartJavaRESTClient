package prodsmartjavarestclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author b2rosado
 */
public class Response {
  public int statusCode;
  public JSONObject data = null;
  public JSONArray dataArray = null;

  public Response(HttpURLConnection connection) {
    try {
      // Response code
      this.statusCode = connection.getResponseCode();
      if(this.statusCode < 300){
        // Get the response
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String dataStr = "";
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
          dataStr += inputLine;
        }
        in.close();

        // Get the access token
        JSONParser jsonParser = new JSONParser();
        if(dataStr.startsWith("[")){
          this.dataArray = (JSONArray) jsonParser.parse(dataStr);          
        }else{
          this.data = (JSONObject) jsonParser.parse(dataStr);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
