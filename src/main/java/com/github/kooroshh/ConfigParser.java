package com.github.kooroshh;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.gsonfire.builders.JsonObjectBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigParser {
    public static AppConfig parseConfigFile(String path) throws IOException {
        File f = new File(path);
        if(f.isFile() && f.canRead()) {
           byte[] bytes =  Files.readAllBytes(Paths.get(f.getPath()));
           String content = new String(bytes, StandardCharsets.UTF_8);
           Gson gson = new Gson();
           return gson.fromJson(content,AppConfig.class);
        }else{
            throw new IOException("Config file not found");
        }
    }
}
