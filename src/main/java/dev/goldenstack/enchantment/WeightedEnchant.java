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
record WeightedEnchant(@NotNull EnchantmentData data, int level) implements WeightedRandomItem {

    @Override
    public double getWeight() {
        return this.data.weight();
    }

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
