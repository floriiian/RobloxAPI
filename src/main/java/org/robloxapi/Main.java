package org.robloxapi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import okhttp3.Response;
import java.io.IOException;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Locale;


public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    // Where the action happens
    public static void main(String[] args) throws IOException {
        // Which game you want to request data for
        BigInteger gameId = new BigInteger("5166944221");
        ObjectMapper mapper = new ObjectMapper();
        NumberFormat numForm = NumberFormat.getCurrencyInstance(Locale.GERMANY);

        try{
            String requestResult = requestData("https://games.roblox.com/v1/games?universeIds=".concat(gameId.toString()));
            logger.debug("Successfully sent request to API");

            JsonNode jsonResult  = mapper.readTree(requestResult).get("data").get(0);
            logger.debug(jsonResult);
            String nameData = jsonResult.get("name").asText();
            int playingData = jsonResult.get("playing").asInt();
            int visitsData = jsonResult.get("visits").asInt();
            String priceData = numForm.format(jsonResult.get("price").asLong());




            logger.debug("Name : {}", nameData);
            logger.debug("Playing: {}", playingData);
            logger.debug("Visits: {}", visitsData);
            logger.debug("Price: {}", priceData);



        }catch(Exception e){
            logger.error("{Failed to send API request: }", e);
        }
    }

    // Sends API Requests
    public static String requestData(String url) throws IOException {
        OkHttpClient client = new OkHttpClient(); // New Client
        Request request = new Request.Builder() // New Request
                .url(url) // Sets URL
                .build(); // Builds the Request
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}