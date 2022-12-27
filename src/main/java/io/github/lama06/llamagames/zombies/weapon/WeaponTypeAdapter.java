package io.github.lama06.llamagames.zombies.weapon;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class WeaponTypeAdapter extends TypeAdapter<WeaponType<?>> {
    @Override
    public void write(JsonWriter out, WeaponType<?> value) throws IOException {
        out.value(value.getName());
    }

    @Override
    public WeaponType<?> read(JsonReader in) throws IOException {
        String name = in.nextString();
        return WeaponType.getByName(name).orElse(null);
    }
}
