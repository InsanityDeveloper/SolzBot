package dev.ubujira.solzbot.utils;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.ubujira.solzbot.SolzBot;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Config {

    @Getter
    private String botToken;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Config() {
        logger.info("Loading config...");
        File configFile = new File("config.json");

        if (configFile.exists()) {
            try {
                JsonElement jsonElement = JsonParser.parseReader(new FileReader(configFile));

                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    if (jsonObject.has("botToken") && !jsonObject.get("botToken").isJsonNull()) {
                        botToken = jsonObject.get("botToken").getAsString();
                    }

                    logger.info("Loaded config!");
                }
            } catch (FileNotFoundException e) {
                logger.error("Somehow the file wasn't found after checking if it exists???", e);
            }
        } else {
            try {
                configFile.createNewFile();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("botToken", "");
                JsonWriter jsonWriter = new JsonWriter(new FileWriter(configFile));
                SolzBot.getGson().toJson(jsonObject, jsonWriter);
                logger.info("Config file created! Configure the values and start the bot again!");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
