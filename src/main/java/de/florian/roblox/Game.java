package de.florian.roblox;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Game
{
    private long id;
    private long rootPlaceId;
    private String name;
    private int playerCount;

    public Game()
    {
    }

    public Game(long universeId, long rootPlaceId, String name, int playerCount)
    {
        this.id = universeId;
        this.rootPlaceId = rootPlaceId;
        this.name = name;
        this.playerCount = playerCount;
    }

    public long getId() {
        return id;
    }

    public long getRootPlaceId() {
        return rootPlaceId;
    }

    public String getName() {
        return name;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    @Override
    public String toString() {
        return "Game{" +
                "universeId=" + id +
                ", rootPlaceId=" + rootPlaceId +
                ", name='" + name + '\'' +
                ", playerCount=" + playerCount +
                '}';
    }
}