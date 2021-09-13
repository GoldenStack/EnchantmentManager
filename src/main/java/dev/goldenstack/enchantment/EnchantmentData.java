package dev.goldenstack.enchantment;

import net.minestom.server.item.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record EnchantmentData(@NotNull Enchantment enchantment, int weight, @NotNull SlotType slotType,
                              @NotNull LevelProvider minLP, @NotNull LevelProvider maxLP, @NotNull Enchantment@NotNull... incompatible) {
    public int getMinimumLevel(int level){
        return this.minLP.getLevel(this, level);
    }
    public int getMaximumLevel(int level){
        return this.maxLP.getLevel(this, level);
    }

    public boolean collidesWith(@NotNull EnchantmentData data){
        if (this.enchantment == data.enchantment){
            return true;
        }
        if (this.incompatible.length != 0){
            for (Enchantment enchantment : this.incompatible){
                if (enchantment == data.enchantment){
                    return true;
                }
            }
        }
        if (data.incompatible.length != 0){
            for (Enchantment enchantment : data.incompatible){
                if (enchantment == this.enchantment){
                    return true;
                }
            }
        }
        return false;
    }

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