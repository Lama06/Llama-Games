package io.github.lama06.llamaplugin;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ModuleTypeAdapter extends TypeAdapter<ModuleType<?>> {
    @Override
    public void write(JsonWriter out, ModuleType<?> value) throws IOException {
        out.value(value.name());
    }

    @Override
    public ModuleType<?> read(JsonReader in) throws IOException {
        return ModuleType.byName(in.nextString()).orElse(null);
    }
}
