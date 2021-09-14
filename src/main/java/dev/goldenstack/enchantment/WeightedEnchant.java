package dev.goldenstack.enchantment;

import net.minestom.server.utils.WeightedRandomItem;
import org.jetbrains.annotations.NotNull;

public record WeightedEnchant(@NotNull EnchantmentData data, int level) implements WeightedRandomItem {

    @Override
    public double getWeight() {
        return this.data.weight();
    }
}
