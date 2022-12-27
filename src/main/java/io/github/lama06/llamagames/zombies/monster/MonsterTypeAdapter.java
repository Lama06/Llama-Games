package io.github.lama06.llamagames.zombies.monster;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class MonsterTypeAdapter extends TypeAdapter<MonsterType<?, ?>> {
    @Override
    public void write(JsonWriter out, MonsterType<?, ?> value) throws IOException {
        out.value(value.getName());
    }

    @Override
    public MonsterType<?, ?> read(JsonReader in) throws IOException {
        String name = in.nextString();
        return MonsterType.getByName(name).orElse(null);
    }
}
