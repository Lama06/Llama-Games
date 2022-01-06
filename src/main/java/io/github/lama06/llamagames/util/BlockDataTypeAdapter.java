package io.github.lama06.llamagames.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;

import java.io.IOException;

public class BlockDataTypeAdapter extends TypeAdapter<BlockData> {
    @Override
    public void write(JsonWriter out, BlockData value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.getAsString());
        }
    }

    @Override
    public BlockData read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        return Bukkit.createBlockData(in.nextString());
    }
}
