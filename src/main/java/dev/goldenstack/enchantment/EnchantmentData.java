package dev.goldenstack.enchantment;

import net.minestom.server.item.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EnchantmentData {

    private static final @NotNull Map<Enchantment, EnchantmentData> DEFAULT_DATA;
    static {
        Map<Enchantment, EnchantmentData> map = new HashMap<>();
        // Put default data here
        DEFAULT_DATA = Collections.unmodifiableMap(map);
    }

    public static @NotNull Map<Enchantment, EnchantmentData> getDefaultData(){
        return DEFAULT_DATA;
    }
}