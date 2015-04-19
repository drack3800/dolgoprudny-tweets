import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by drack3800 on 13.12.2014.
 */
public class DolgoprudnyTweets {
    private static final String BEARER_TOKEN;
    private static final String TWEETS_SEARCH_RESOURCE_URL = "https://api.twitter.com/1.1/search/tweets.json";

    private static final Double DOLGOPRUDNY_LAT = 55.933333;
    private static final Double DOLGOPRUDNY_LONG = 37.5;
    private static final Integer SEARCH_RADIUS_KM = 10;
    private static Integer tweetsToShow = 15;

    private static final Map<String, Object> QUERY;
    static {
    	BEARER_TOKEN = System.getenv("DOLGOPRUDNY_TWEETS_BEARER_TOKEN");
    	if (BEARER_TOKEN == null) {
    		throw new RuntimeException("DOLGOPRUDNY_TWEETS_BEARER_TOKEN env variable is not set");
    	}

        QUERY = new HashMap<String, Object>();
        QUERY.put("result_type", "recent");
        QUERY.put("geocode", DOLGOPRUDNY_LAT.toString() + ","
                + DOLGOPRUDNY_LONG.toString() + "," + SEARCH_RADIUS_KM.toString() + "km");
    }

    public static void main(String[] args) throws Exception {
        HttpRequest getRequest = Unirest.get(TWEETS_SEARCH_RESOURCE_URL)
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .queryString(QUERY);
//        for (String header : getRequest.getHeaders().keySet()) {
//            System.out.println(header + ": " + String.join(",", getRequest.getHeaders().get(header)));
//        }
        HttpResponse<JsonNode> jsonResponse = getRequest.asJson();

        if (jsonResponse.getStatus() != 200) {
            System.out.println("Error has occured: ");
            System.out.println(jsonResponse.getStatusText());
            Headers headers = jsonResponse.getHeaders();
            for (String header : headers.keySet()) {
                System.out.println(header + ": " + String.join(",", headers.get(header)));
            }
            System.out.println(jsonResponse.getBody().toString());
        } else {
            printTweets(jsonResponse.getBody());
        }
    }

    private static void printTweets(JsonNode json) {
        JSONArray tweetsArray = json.getObject().getJSONArray("statuses");
        for (int i = 0; i < tweetsArray.length(); ++i) {
            JSONObject tweet = tweetsArray.getJSONObject(i);
            System.out.println(tweet.get("created_at").toString());
            System.out.println(tweet.get("text").toString());
            System.out.println(tweet.getJSONObject("user").get("name"));
            System.out.println("Location: " + tweet.getJSONObject("place").get("full_name").toString());
            System.out.println("_______________");
        }
    }

}
