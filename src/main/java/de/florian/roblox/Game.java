package de.florian.roblox;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.logging.Logger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Game
{
    private long id;
    private long rootPlaceId;
    private String name;
    @JsonProperty("playing")
    private int playing;

    public Game()
    {
    }

    public Game(long id, long rootPlaceId, String name, int playing)
    {
        this.id = id;
        this.rootPlaceId = rootPlaceId;
        this.name = name;
        this.playing = playing;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPlaying() {
        return playing;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", rootPlaceId=" + rootPlaceId +
                ", name='" + name + '\'' +
                ", playing=" + playing +
                '}';
    }
}