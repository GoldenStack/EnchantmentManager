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

    /**
     * Returns an object that returns the product of {@code value * level}, where level is the provided level of the
     * enchantment.
     */
    static @NotNull LevelProvider multiply(int value){
        return (data, level) -> value * level;
    }

    /**
     * Returns an object that returns the product of {@code min + (level - 1) * lvl}, where level is the provided level
     * of the enchantment.
     */
    static @NotNull LevelProvider adjusted(int min, int lvl){
        return (data, level) -> min + (level - 1) * lvl;
    }

    /**
     * Returns an object that returns the product of {@code min + level * lvl}, where level is the provided level
     * of the enchantment.
     */
    static @NotNull LevelProvider basic(int min, int lvl){
        return (data, level) -> min + level * lvl;
    }

    /**
     * Returns an object that returns the product of {@code min(level) + value}, where min(level) is the value of the
     * provided {@code data} object's minimum level for the provided level. <b>DO NOT</b> set this to an
     * EnchantmentData's minimum level provider, as it will keep recursing indefinitely until an error occurs (well, I
     * haven't actually tested this, but you get the point).
     */
    static @NotNull LevelProvider addToMin(int value){
        return (data, level) -> data.getMinimumLevel(level) + value;
    }

    /**
     * Returns an object that returns the product of {@code (1 + level * 10) + value}, where level is the provided level
     * of the enchantment.
     */
    static @NotNull LevelProvider addToDefault(int value){
        return (data, level) -> (1 + level * 10) + value;
    }

    /**
     * Returns an object that always returns the value that you provided for the parameter {@code value}.
     */
    static @NotNull LevelProvider constant(int value){
        return (data, level) -> value;
    }
}