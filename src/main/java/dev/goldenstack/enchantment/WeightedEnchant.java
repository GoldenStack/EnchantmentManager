package dev.goldenstack.enchantment;

import net.minestom.server.utils.WeightedRandomItem;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents an EnchantmentData object paired with a valid level for its enchantment. You likely will not have to mess
 * with this class directly.
 */
@ApiStatus.Internal
class WeightedEnchant implements WeightedRandomItem {
    private final @NotNull EnchantmentData data;
    private final int level;

    public WeightedEnchant(@NotNull EnchantmentData data, int level){
        this.data = data;
        this.level = level;
    }

    /**
     * @return This instance's data
     */
    public @NotNull EnchantmentData data(){
        return data;
    }

    /**
     * @return The level of the enchantment
     */
    public int level(){
        return level;
    }

    @Override
    public String toString() {
        return "WeightedEnchant[data=" + data + ", level=" + level + "]";
    }

    /**
     * @return This data's weight
     */
    @Override
    public double getWeight() {
        return this.data.weight();
    }

    /**
     * Picks a random item from the list of weighted items based on the provided value. The value should be between zero
     * and the total weight of all of the items.
     * @param list The list of items
     * @param value The value
     * @return The item that was selected, or null if the value was too large.
     */
    public static <T extends WeightedRandomItem> @Nullable T getWeightedItemFrom(@NotNull List<T> list, double value){
        for (T item : list){
            value -= item.getWeight();
            if (value < 0){
                return item;
            }
        }
        return null;
    }
}
