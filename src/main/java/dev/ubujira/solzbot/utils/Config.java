package dev.ubujira.solzbot.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Config {

    private String botToken;

    public Config() {
        File configFile = new File("config.json");

        if (configFile.exists()) {
            try {
                JsonElement jsonElement = JsonParser.parseReader(new FileReader(configFile));

                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    if (jsonObject.has("botToken") && !jsonObject.get("botToken").isJsonNull()) {

                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
