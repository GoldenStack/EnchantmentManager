package dev.goldenstack.enchantment;

import net.minestom.server.utils.WeightedRandomItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record WeightedEnchant(@NotNull EnchantmentData data, int level) implements WeightedRandomItem {

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
