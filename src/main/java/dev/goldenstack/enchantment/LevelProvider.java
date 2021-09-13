package dev.goldenstack.enchantment;

import org.jetbrains.annotations.NotNull;

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