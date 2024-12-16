package top.bearcabbage.lanterninstorm.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static com.mojang.text2speech.Narrator.LOGGER;

public class Config {
    private final Path filePath;
    private JsonObject jsonObject;
    private final Gson gson;

    public Config(Path filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            if (Files.notExists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                try (FileWriter writer = new FileWriter(filePath.toFile())) {
                    writer.write("{}");
                }
            }

        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
        load();
    }

    public void load() {
        try (FileReader reader = new FileReader(filePath.toFile())) {
            this.jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            this.jsonObject = new JsonObject();
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
    }

    public void set(String key, Object value) {
        jsonObject.add(key, gson.toJsonTree(value));
    }

    public Map<?, ?> getMap(String key) {
        return gson.fromJson(jsonObject.get(key), Map.class);
    }

    public <T> T get(String key, Class<T> clazz) {
        return gson.fromJson(jsonObject.get(key), clazz);
    }
    
}
