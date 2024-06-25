package org.robloxapi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import okhttp3.Response;
import java.io.IOException;
import java.math.BigDecimal;
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
        BigDecimal gameId = new BigDecimal("5166944221");
        ObjectMapper mapper = new ObjectMapper();
        NumberFormat numForm = NumberFormat.getCurrencyInstance(Locale.GERMANY);

        // Connect to database
        prepareDatabase("postgres", "Creeper008");

        try{
            String requestResult = requestData("https://games.roblox.com/v1/games?universeIds=".concat(gameId.toString()));
            logger.info("Successfully sent request to API");

            JsonNode jsonResult  = mapper.readTree(requestResult).get("data").get(0);
            String nameData = jsonResult.get("name").asText();
            long priceData = jsonResult.get("price").asLong();
            int playingData = jsonResult.get("playing").asInt();
            BigDecimal visitsData = BigDecimal.valueOf(jsonResult.get("visits").asInt());


            logger.debug("Name : {}", nameData);
            logger.debug("Playing: {}", playingData);
            logger.debug("Visits: {}", visitsData);
            logger.debug("Price: {}", numForm.format(priceData));

            try{
                // Create roblox_game_data
                stmt = con.createStatement();
                ResultSet  result = stmt.executeQuery("SELECT " + gameId + " FROM roblox_game_data");
                if(result.getRow() != 0){
                    PreparedStatement insert = con.prepareStatement("INSERT INTO roblox_game_data (game_id, name, playing, visits, price) VALUES (?, ?, ?, ?, ?);");
                    insert.setBigDecimal(1, gameId);
                    insert.setString(2, nameData);
                    insert.setInt(3, playingData);
                    insert.setBigDecimal(4, visitsData);
                    insert.setLong(5, priceData);
                    insert.executeUpdate();
                    logger.debug("Added{}to database", nameData);
                }
                else{
                    PreparedStatement update = con.prepareStatement("UPDATE  roblox_game_data SET name = ?, playing = ?, visits = ?, price = ? WHERE game_id = ?");
                    update.setString(1, nameData);
                    update.setInt(2, playingData);
                    update.setBigDecimal(3, visitsData);
                    update.setLong(4, priceData);
                    update.setBigDecimal(5, gameId);
                    update.executeUpdate();
                    logger.debug("Updated {} in database", nameData);
                }
                con.commit();
                stmt.close();
            }catch (SQLException e){
                logger.debug("Was not able to create or update {} in database: {}", nameData, e);
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
                con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/roblox_games", "postgres", "Creeper008");
                try {
                    stmt = con.createStatement();
                    stmt.executeUpdate(
                            "CREATE TABLE roblox_game_data" +
                                    "(" +
                                    "ID SERIAL NOT NULL PRIMARY KEY," +
                                    "GAME_ID INT NOT NULL," +
                                    "NAME TEXT NOT NULL," +
                                    "PLAYING INT NOT NULL," +
                                    "VISITS BIGINT NOT NULL," +
                                    "PRICE MONEY)"
                    );

                }catch(SQLException e){
                    logger.debug("Error creating \"roblox_game_data\": ", e);
                }
                stmt.close();
                logger.debug("Database: \"roblox_games\" created successfully.\n");
            }catch (SQLException e){
                logger.debug("Database: \"roblox_games\" already exists; skipping creation.\n");
            }
        } catch (Exception e) {
            logger.error(e);
            System.exit(0);
        }
    }
}