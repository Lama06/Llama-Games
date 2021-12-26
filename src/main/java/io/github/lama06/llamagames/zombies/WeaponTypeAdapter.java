package io.github.lama06.llamagames.zombies;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.lama06.llamagames.zombies.weapon.WeaponType;

import java.io.IOException;

public class WeaponTypeAdapter extends TypeAdapter<WeaponType<?>> {
    @Override
    public void write(JsonWriter out, WeaponType<?> value) throws IOException {
        out.value(value.getName());
    }

    @Override
    public WeaponType<?> read(JsonReader in) throws IOException {
        return WeaponType.byName(in.nextString()).orElse(null);
    }
}
