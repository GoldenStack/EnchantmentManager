package dev.goldenstack.enchantment;

import org.jetbrains.annotations.NotNull;

/**
 * <p>Represents something that can provide an integer (usually a minimum/maximum level) based on the level and
 * EnchantmentData that were provided.</p>
 * <p>All the methods that default Minecraft uses to calculate level numbers are here, but you are free to use your own!</p>
 */
@FunctionalInterface
public interface LevelProvider {
    int getLevel(@NotNull EnchantmentData data, int level);

    static @NotNull LevelProvider multiply(int value){
        return (data, level) -> value * level;
    }
    static @NotNull LevelProvider adjusted(int min, int lvl){
        return (data, level) -> min + (level - 1) * lvl;
    }
    static @NotNull LevelProvider basic(int min, int lvl){
        return (data, level) -> min + level * lvl;
    }
    static @NotNull LevelProvider addToMin(int value){
        return (data, level) -> data.getMinimumLevel(level) + value;
    }
    static @NotNull LevelProvider addToDefault(int value){
        return (data, level) -> (1 + level * 10) + value;
    }
    static @NotNull LevelProvider constant(int value){
        return (data, level) -> value;
    }
}