package org.robloxapi;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import okhttp3.Response;
import java.io.IOException;
import java.math.BigInteger;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) throws IOException {
        // Which game you want to request data for
        BigInteger gameId = new BigInteger("5166944221");

        logger.debug("{}", () -> {
            try {
                return requestData("https://games.roblox.com/v1/games?universeIds=5166944221");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        logger.debug("balls");
        String requestResult = requestData("https://games.roblox.com/v1/games?universeIds=".concat(gameId.toString()));

    }

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