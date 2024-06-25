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
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;


public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static Connection con = null;
    public static Statement stmt = null;

    // Where the action happens
    public static void main(String[] args) throws IOException {
        // Which game you want to request data for
        BigInteger gameId = new BigInteger("5166944221");
        ObjectMapper mapper = new ObjectMapper();
        NumberFormat numForm = NumberFormat.getCurrencyInstance(Locale.GERMANY);

        // Connect to database
        prepareDatabase("postgres", "Creeper008");

        try{
            String requestResult = requestData("https://games.roblox.com/v1/games?universeIds=".concat(gameId.toString()));
            logger.info("Successfully sent request to API");

            JsonNode jsonResult  = mapper.readTree(requestResult).get("data").get(0);
            String nameData = jsonResult.get("name").asText();
            int playingData = jsonResult.get("playing").asInt();
            int visitsData = jsonResult.get("visits").asInt();
            String priceData = numForm.format(jsonResult.get("price").asLong());

            logger.debug("Name : {}", nameData);
            logger.debug("Playing: {}", playingData);
            logger.debug("Visits: {}", visitsData);
            logger.debug("Price: {}", priceData);

            try{
                stmt = con.createStatement();
                ResultSet  result = stmt.executeQuery("SELECT " + nameData + " FROM roblox_games");
                if(!result.first()){
                    stmt.executeUpdate(
                            "CREATE TABLE" + nameData +
                            "(" +
                            "PLAYING INT NOT NULL," +
                            "VISITS BIGINT NOT NULL," +
                            "PRICE MONEY)"
                    );
                    stmt = con.createStatement();
                    stmt.executeUpdate(
                            "INSERT INTO " + nameData + " (PLAYING,VISITS,PRICE) "
                            + "VALUES (playingData, visitsData, priceData " + ")"
                    );
                    logger.debug("Added{}to database", nameData);
                }
                else{
                    stmt = con.createStatement();
                    stmt.executeUpdate("UPDATE " + nameData +  " set (PLAYING, VISITS, PRICE )" + "VALUES(playingData, visitsData, priceData " + ")");
                    con.commit();
                    logger.debug("Updated {} in database", nameData);
                }
                stmt.close();
            }catch (SQLException e){
                // TODO: Error right fucking here.
                logger.debug("Was not able to create or update {} in database.\nError: {}", nameData, e);
            }

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

    public static void prepareDatabase(String username, String password){
        // Initiate connection to database
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "Creeper008");
            logger.info("Database connection initialized.");
            try{
                stmt = con.createStatement();
                stmt.executeUpdate("CREATE DATABASE roblox_games");
                stmt.close();
                logger.debug("Database: \"roblox_games\" created successfully.\n");
            }catch (SQLException e){
                logger.debug("Database: \"roblox_games\" already exists; skipping creation.\n");
            }
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/roblox_games", "postgres", "Creeper008");

        } catch (Exception e) {
            logger.error(e);
            System.exit(0);
        }
    }
}