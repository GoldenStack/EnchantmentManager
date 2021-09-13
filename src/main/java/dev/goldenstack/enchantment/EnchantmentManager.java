package dev.goldenstack.enchantment;

import net.minestom.server.item.Enchantment;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnchantmentManager {

    private final @NotNull Map<Enchantment, EnchantmentData> data;
    private EnchantmentManager(@NotNull EnchantmentManager.Builder builder){
        if (builder.useConcurrentHashMap){
            data = new ConcurrentHashMap<>();
        } else {
            data = new HashMap<>();
        }

        if (builder.useDefaultEnchantmentData){
            this.data.putAll(EnchantmentData.getDefaultData());
        }

    }


    public static class Builder {
        private boolean useConcurrentHashMap, useDefaultEnchantmentData;

        @Contract("_ -> this")
        public @NotNull Builder useConcurrentHashMap(boolean useConcurrentHashMap) {
            this.useConcurrentHashMap = useConcurrentHashMap;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder useDefaultEnchantmentData(boolean useDefaultEnchantmentData) {
            this.useDefaultEnchantmentData = useDefaultEnchantmentData;
            return this;
        }

        public @NotNull EnchantmentManager builder(){
            return new EnchantmentManager(this);
        }
    }
}
