package de.florian.roblox;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data
{
    private Game[] data;

    public Data()
    {
    }

    public Data(Game[] data)
    {
        this.data = data;
    }

    public Game[] getData()
    {
        return data;
    }
}