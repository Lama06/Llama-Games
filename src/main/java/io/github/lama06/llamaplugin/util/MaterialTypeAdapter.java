package io.github.lama06.llamaplugin.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

import java.io.IOException;

public class MaterialTypeAdapter extends TypeAdapter<Material> {
    @Override
    public void write(JsonWriter out, Material value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.getKey().asString());
        }
    }

    @Override
    public Material read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        NamespacedKey key = NamespacedKey.fromString(in.nextString());
        if (key == null) {
            return null;
        }

        return Registry.MATERIAL.get(key);
    }
}
