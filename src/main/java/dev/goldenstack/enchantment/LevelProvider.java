package dev.goldenstack.enchantment;

import org.jetbrains.annotations.NotNull;

/**
 * Represents something that can provide an integer (usually a minimum/maximum level) based on the level and
 * EnchantmentData that were provided.<br>
 * All the methods that default Minecraft uses to calculate level numbers are here, but you are free to use your own!
 */
@FunctionalInterface
public interface LevelProvider {

    /**
     * @return the designated experience level for the provided enchantment and enchantment level
     */
    int getLevel(@NotNull EnchantmentData data, int level);

    /**
     * @return an object that returns the product of {@code value * level}, where level is the provided level of the
     * enchantment.
     */
    static @NotNull LevelProvider multiply(int value) {
        return (data, level) -> value * level;
    }

    /**
     * @return an object that returns the product of {@code min + (level - 1) * lvl}, where level is the provided level
     * of the enchantment.
     */
    static @NotNull LevelProvider adjusted(int min, int lvl) {
        return (data, level) -> min + (level - 1) * lvl;
    }

    /**
     * @return an object that returns the product of {@code min + level * lvl}, where level is the provided level of the
     * enchantment.
     */
    static @NotNull LevelProvider basic(int min, int lvl) {
        return (data, level) -> min + level * lvl;
    }

    /**
     * @return an object that returns the product of {@code min(level) + value}, where min(level) is the value of the
     * provided {@code data} object's minimum level for the provided level. <b>DO NOT</b> set this to an
     * EnchantmentData's minimum level provider, as it will keep recursing indefinitely until an error occurs.
     */
    static @NotNull LevelProvider addToMin(int value) {
        return (data, level) -> data.getMinimumLevel(level) + value;
    }

    /**
     * @return an object that returns the product of {@code (1 + level * 10) + value}, where level is the provided level
     * of the enchantment.
     */
    static @NotNull LevelProvider addToDefault(int value) {
        return (data, level) -> (1 + level * 10) + value;
    }

    /**
     * @return an object that always returns the value that you provided for the parameter {@code value}.
     */
    static @NotNull LevelProvider constant(int value) {
        return (data, level) -> value;
    }

}