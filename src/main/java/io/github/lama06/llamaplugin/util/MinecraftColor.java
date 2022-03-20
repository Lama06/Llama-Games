package io.github.lama06.llamaplugin.util;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

public enum MinecraftColor {
    WHITE(NamedTextColor.WHITE, Material.WHITE_WOOL, Material.WHITE_CONCRETE),
    BLACK(NamedTextColor.BLACK, Material.BLACK_WOOL, Material.BLACK_CONCRETE),
    RED(NamedTextColor.RED, Material.RED_WOOL, Material.RED_CONCRETE),
    BLUE(NamedTextColor.BLUE, Material.BLUE_WOOL, Material.BLUE_CONCRETE),
    BROWN(TextColor.color(130, 84, 50), Material.BROWN_WOOL, Material.BROWN_CONCRETE),
    CYAN(TextColor.color(22, 156, 157), Material.CYAN_WOOL, Material.CYAN_CONCRETE),
    GRAY(NamedTextColor.GRAY, Material.GRAY_WOOL, Material.GRAY_CONCRETE),
    GREEN(NamedTextColor.GREEN, Material.GREEN_WOOL, Material.GREEN_CONCRETE),
    LIGHT_BLUE(TextColor.color(58, 179, 218), Material.LIGHT_BLUE_WOOL, Material.LIGHT_BLUE_CONCRETE),
    LIGHT_GRAY(TextColor.color(156, 157, 151), Material.LIGHT_GRAY_WOOL, Material.LIGHT_GRAY_CONCRETE),
    LIME(TextColor.color(128, 199, 31), Material.LIME_WOOL, Material.LIME_CONCRETE),
    MAGENTA(TextColor.color(198, 79, 189), Material.MAGENTA_WOOL, Material.MAGENTA_CONCRETE),
    ORANGE(TextColor.color(249, 128, 29), Material.ORANGE_WOOL, Material.ORANGE_CONCRETE),
    PINK(TextColor.color(243, 140, 170), Material.PINK_WOOL, Material.PINK_CONCRETE),
    PURPLE(TextColor.color(137, 50, 183), Material.PURPLE_WOOL, Material.PURPLE_CONCRETE),
    YELLOW(NamedTextColor.YELLOW, Material.YELLOW_WOOL, Material.YELLOW_CONCRETE);

    private final TextColor textColor;
    private final Material wool;
    private final Material concrete;

    MinecraftColor(TextColor textColor, Material wool, Material concrete) {
        this.textColor = textColor;
        this.wool = wool;
        this.concrete = concrete;
    }

    public TextColor getTextColor() {
        return textColor;
    }

    public Material getWool() {
        return wool;
    }

    public Material getConcrete() {
        return concrete;
    }

    public static MinecraftColor getColorOfMaterial(Material material) {
        for (MinecraftColor value : values()) {
            if (value.wool == material || value.concrete == material) {
                return value;
            }
        }

        return null;
    }
}
