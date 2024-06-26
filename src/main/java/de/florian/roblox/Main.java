package de.florian.roblox;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import okhttp3.Response;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    // Where the action happens
    public static void main(String[] args) throws InterruptedException {

        ObjectMapper mapper = new ObjectMapper();

        Connection connection = prepareDatabase("postgres", "Creeper008");

        Scanner getGameId = new Scanner(System.in);

        System.out.print("Roblox Game-ID: ");

        if (!getGameId.hasNextLine()) {
            return;
        }

        long gameId = getGameId.nextLong();

        while (true) {
            try {
                String requestResult = requestData("https://games.roblox.com/v1/games?universeIds=" + gameId);

                LOGGER.info("Successfully sent request to API");

                // Reads requestResult and inserts it into an Object (Data)
                Data data = mapper.readValue(requestResult, Data.class);

                Game game = data.getData()[0];

                // Create roblox_game_data
                try (PreparedStatement insert = connection.prepareStatement("INSERT INTO roblox_game_data (game_name, game_id, playing, timestamp) VALUES (?, ?, ?, ?)"))
                {
                    insert.setString(1, game.getName());
                    insert.setLong(2, game.getId());
                    insert.setInt(3, game.getPlaying());
                    insert.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

                    insert.execute();
                }

                LOGGER.debug("Added new entry for {} into database", game.getName());

                connection.commit();
            } catch (Exception e) {
                LOGGER.error("{Failed to send API request: }", e);
            }

            Thread.sleep(10000);
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
    public static Connection prepareDatabase(String username, String password){
        Connection connection = null;

        // Initiate connection to database
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", username, password);

            LOGGER.info("Database connection initialized.");

            try (Statement statement = connection.createStatement())
            {
                statement.executeUpdate("CREATE DATABASE roblox_games");

                LOGGER.debug("Database: \"roblox_games\" created successfully.");
            }
            catch(SQLException e){
                LOGGER.debug("Database already exists. Skipping.");
            }

            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/roblox_games", username, password);
            connection.setAutoCommit(false);

            try (Statement statement = connection.createStatement())
            {
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS roblox_game_data" +
                                "(" +
                                "id SERIAL NOT NULL PRIMARY KEY," +
                                "game_name TEXT  NOT NULL," +
                                "game_id BIGINT  NOT NULL," +
                                "playing INT NOT NULL," +
                                "timestamp TIMESTAMP NOT NULL"
                                + ")"
                );
            }

            LOGGER.debug("Table: \"roblox_game_data\" has been created.\n");
        } catch (Exception e) {
            LOGGER.error(e);

            System.exit(0);
        }

        return connection;
    }
}