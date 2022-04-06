package dev.goldenstack.enchantment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents an EnchantmentData object paired with a valid level for its enchantment. You likely will not have to mess
 * with this class directly.
 */
record WeightedEnchant(@NotNull EnchantmentData data, int level) {

    /**
     * @return this data's weight
     */
    public double getWeight() {
        return this.data.weight();
    }

    /**
     * Picks a random item from the list of weighted items based on the provided value. The value should be between zero
     * and the total weight of all of the items.
     * @param enchantments the list of items
     * @param value the value
     * @return the item that was selected, or null if the value was too large.
     */
    public static @Nullable WeightedEnchant getItemFrom(@NotNull List<WeightedEnchant> enchantments, double value) {
        for (WeightedEnchant weightedEnchant : enchantments) {
            value -= weightedEnchant.getWeight();
            if (value < 0) {
                return weightedEnchant;
            }
        }
        return null;
    }
}
