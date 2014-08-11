package prodsmartjavarestclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author sam
 */
public class ProdsmartJavaRESTClient {

    public class response {

        public JSONObject object;
        public int status;

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String apiKey = "";
        String apiSecret = "";
        String appUrl = "app.prodsmart.com";
        
        String authorizationRequest = "{ \"scopes\": [ \"productions_write\" ] }";

        JSONObject worker1 = new JSONObject();
        worker1.put("number", "1");
        worker1.put("name", "John Doe");
        worker1.put("entry_time", "07:00");
        worker1.put("entry_time", "17:00");

        JSONObject worker2 = new JSONObject();
        worker2.put("number", "12");
        worker2.put("name", "Jamie Duke");
        worker2.put("entry_time", "07:00");
        worker2.put("entry_time", "17:00");

        JSONObject product = new JSONObject();
        product.put("code", "productCode");
        product.put("name", "productName");

        JSONObject position = new JSONObject();
        position.put("code", "positionCode");
        position.put("name", "positionName");
        position.put("product", product);
        position.put("task_duration", "00H15");
        position.put("quantity_ratio", "1");

        JSONObject productionOrder = new JSONObject();
        JSONArray products = new JSONArray();
        JSONObject productionOrderProduct = new JSONObject();
        productionOrderProduct.put("product", product);
        productionOrderProduct.put("quantity", "1000");
        productionOrderProduct.put("note", "");
        products.add(productionOrderProduct);
        productionOrder.put("products", products);
        productionOrder.put("code", "productionOrderCode");
        productionOrder.put("description", "description for this production order");
        productionOrder.put("start_date", "2014-04-01T02:15:15Z");
        productionOrder.put("end_date", "2014-04-25T22:15:15Z");
        JSONArray workersAssigned = new JSONArray();
        workersAssigned.add(worker1);
        workersAssigned.add(worker2);
        productionOrder.put("workers_assigned", workersAssigned);

        JSONObject waste = new JSONObject();
        waste.put("code", "wasteCode1");
        waste.put("description", "waste 1 description");
        waste.put("flaw", "waste type");
        waste.put("quantity", "1");

        JSONObject production = new JSONObject();
        production.put("start_time", "2014-04-14T10:15:15Z");
        production.put("end_time", "2014-04-14T12:20:15Z");
        production.put("production_order", productionOrder);
        production.put("position", position);
        production.put("quantity", "4");
        production.put("worker", worker1);
        JSONArray wasteList = new JSONArray();
        wasteList.add(waste);
        production.put("waste", wasteList);

        
        try {

            //create authorization
            URL authUrl = new URL("http://"+appUrl+"/api/authorization");
            String key = apiKey + ":" + apiSecret;
            String encoding = new sun.misc.BASE64Encoder().encode(key.getBytes());

            HttpURLConnection authConnection =(HttpURLConnection) authUrl.openConnection();
            authConnection.setRequestProperty("Authorization", "Basic " + encoding);
            authConnection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(authConnection.getOutputStream());
            wr.write(authorizationRequest);
            wr.flush();
            int code = authConnection.getResponseCode();
            System.out.println("Auth request status: "+code);

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(authConnection.getInputStream()));
            String data = "";
            String line;
            while ((line = rd.readLine()) != null) {
                data += line;
            }
            wr.close();
            rd.close();
            
            //get the token
            JSONParser jsonParser = new JSONParser();
            JSONObject authorization = (JSONObject) jsonParser.parse(data);
            String token = authorization.get("token").toString();
            
            if(token!=null && !token.isEmpty()){
                //post production
                URL url = new URL("http://"+appUrl+"/api/production?"+token);
                HttpURLConnection connection =(HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                OutputStreamWriter writer2 = new OutputStreamWriter(connection.getOutputStream());
                writer2 = new OutputStreamWriter(connection.getOutputStream());
                writer2.write(production.toJSONString());
                writer2.flush();
                code = connection.getResponseCode();
                System.out.println("Request status: "+code);
                writer2.close();
            }
            else{
                System.out.print("AuthResponse didn't have a token");
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
