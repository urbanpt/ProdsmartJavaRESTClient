package prodsmartjavarestclient;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author sam
 */
public class ProdsmartJavaRESTClient {

  // API Key and API Secret can be found at user profile inside Prodsmart
  private static final String apiKey = "YOUR API KEY";
  private static final String apiSecret = "YOUR API SECRET";
  private static final String authorizationRequest = "{ \"scopes\": [ \"productions_write\" ] }";

  public static void getRequest(String appUrl, String urlTo, String token, String parameters) {
    try {
      String tokenAndParam = parameters != null ? token + parameters : token;
      URL url = new URL(appUrl + urlTo + "?" + tokenAndParam);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      Response response = new Response(connection);
      System.out.println("GET: " + urlTo);
      System.out.println("Request status: " + response.statusCode);
      if (response.data != null) {
        System.out.println("Request body: " + response.data);
      } else if (response.dataArray != null) {
        System.out.println("Request body: " + response.dataArray);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void postRequest(String appUrl, String urlTo, String token, String parameters, String body) {
    try {
      String tokenAndParam = parameters != null ? token + parameters : token;
      URL url = new URL(appUrl + urlTo + "?" + tokenAndParam);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(true);

      OutputStreamWriter w = new OutputStreamWriter(connection.getOutputStream());
      w.write(body);
      w.flush();

      Response response = new Response(connection);
      System.out.println("POST: " + urlTo);
      System.out.println("Request status: " + response.statusCode);
      if (response.data != null) {
        System.out.println("Request body: " + response.data);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void deleteRequest(String appUrl, String urlTo, String token, String parameters) {
    try {
      String tokenAndParam = parameters != null ? token + parameters : token;
      URL url = new URL(appUrl + urlTo + "?" + tokenAndParam);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("DELETE");
      connection.connect();

      Response response = new Response(connection);
      System.out.println("DELETE: " + urlTo);
      System.out.println("Request status: " + response.statusCode);
      if (response.data != null) {
        System.out.println("Request body: " + response.data);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String authRequest(String appUrl) {
    String accessToken = null;
    try {
      URL authUrl = new URL(appUrl + "/api/authorization");
      String key = apiKey + ":" + apiSecret;
      // Encode the key using Base 64
      String encoding = new sun.misc.BASE64Encoder().encode(key.getBytes());

      HttpURLConnection connection = (HttpURLConnection) authUrl.openConnection();
      connection.setRequestProperty("Authorization", "Basic " + encoding);
      connection.setDoOutput(true);

      Response response = new Response(connection);
      System.out.println("Request status: " + response.statusCode);
      accessToken = response.data.get("token").toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return accessToken;
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    String appUrl = "https://app.prodsmart.com";

    // Example JSON Data to create a Production Order via API
    //   The Workers, and Products used must exist in the system before
    //   creating Production Orders using them
    JSONObject worker1 = new JSONObject();
    worker1.put("number", "1");
    JSONObject worker2 = new JSONObject();
    worker2.put("number", "2");

    JSONObject productionOrderProduct = new JSONObject();
    productionOrderProduct.put("product", "Prod1");
    productionOrderProduct.put("quantity-ordered", "500");
    productionOrderProduct.put("observations", "");
    JSONArray products = new JSONArray();
    products.add(productionOrderProduct);
    JSONArray workersAssigned = new JSONArray();
    workersAssigned.add(worker1);
    workersAssigned.add(worker2);

    JSONObject productionOrder = new JSONObject();
    productionOrder.put("products", products);
    productionOrder.put("code", "V23076 - Week 47");
    productionOrder.put("description", "");
    productionOrder.put("notes", "");
    productionOrder.put("start-date", "2017-04-01T02:15:15Z");
    productionOrder.put("due-date", "2017-04-25T22:15:15Z");
    productionOrder.put("workers-assigned", workersAssigned);

    try {
      // Create authorization
      String accessToken = authRequest(appUrl);

      if (accessToken == null) {
        System.out.print("No valid accesToken was obtained.");
        return;
      }

      // Example API Requests
      // 1) List Production Orders
      getRequest(appUrl, "/api/production-orders/", accessToken, "");

      // 2) Create a Production Order
      postRequest(appUrl, "/api/production-orders/", accessToken, "", productionOrder.toJSONString());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
