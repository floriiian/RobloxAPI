package org.robloxapi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import okhttp3.Response;
import java.io.IOException;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;


public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static Connection con = null;
    public static Statement stmt = null;

    // Where the action happens
    public static void main(String[] args){
        // Which game you want to request data for
        long gameId = Long.parseLong("5166944221");
        ObjectMapper mapper = new ObjectMapper();
        NumberFormat numForm = NumberFormat.getCurrencyInstance(Locale.GERMANY);

        // Connect to database
        prepareDatabase("postgres", "Creeper008");

        try{
            String requestResult = requestData("https://games.roblox.com/v1/games?universeIds=".concat(String.valueOf(gameId)));
            logger.info("Successfully sent request to API");

            JsonNode jsonResult  = mapper.readTree(requestResult).get("data").get(0);
            String nameData = jsonResult.get("name").asText();
            long priceData = jsonResult.get("price").asLong();
            int playingData = jsonResult.get("playing").asInt();
            long visitsData = jsonResult.get("visits").asLong();

            logger.debug("Name : {}", nameData);
            logger.debug("Playing: {}", playingData);
            logger.debug("Visits: {}", visitsData);
            logger.debug("Price: {}", numForm.format(priceData));

            try{
                // Create roblox_game_data
                PreparedStatement insert = con.prepareStatement("INSERT INTO roblox_game_data (game_id, playing, timestamp) VALUES (?, ?, ?);");
                insert.setLong(1, gameId);
                insert.setLong(2, playingData);
                insert.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                insert.execute();
                logger.debug("Added {} to database", nameData);
                con.commit();
                stmt.close();
            }catch (SQLException e){
                logger.debug("Was not able to create {} in database: {}", nameData, e);
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

    // Prepares Database
    public static void prepareDatabase(String username, String password){
        // Initiate connection to database
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", username, password);
            logger.info("Database connection initialized.");
            try {
                stmt = con.createStatement();
                stmt.executeUpdate("CREATE DATABASE roblox_games");
                con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/roblox_games", username, password);
                con.setAutoCommit(false);
                logger.debug("Database: \"roblox_games\" created successfully.");
                try {
                    stmt = con.createStatement();
                    stmt.executeUpdate(
                            "CREATE TABLE roblox_game_data" +
                                    "(" +
                                    "id SERIAL NOT NULL PRIMARY KEY," +
                                    "game_id BIGINT  NOT NULL," +
                                    "playing INT NOT NULL," +
                                    "timestamp TIMESTAMP NOT NULL"
                                    + ")"
                    );
                    logger.debug("Table: \"roblox_game_data\" has been created.\n");
                }catch(SQLException e){
                    logger.debug("Error creating \"roblox_game_data\": ", e);
                }
                stmt.close();
            }catch (SQLException e){
                logger.debug("Database: \"roblox_games\" already exists; skipping creation.\n");
            }
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/roblox_games", username, password);
            con.setAutoCommit(false);
        } catch (Exception e) {
            logger.error(e);
            System.exit(0);
        }
    }
}