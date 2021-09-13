package dev.goldenstack.enchantment;

import net.minestom.server.item.Enchantment;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnchantmentManager {
    // Store builder and initialize data as null so we can lazily initialize values
    private Map<Enchantment, EnchantmentData> data = null;
    private Map<Material, Integer> enchantability = null;

    private final boolean useConcurrentHashMap, useDefaultEnchantmentData, useDefaultEnchantability;
    private EnchantmentManager(@NotNull EnchantmentManager.Builder builder){
        this.useConcurrentHashMap = builder.useConcurrentHashMap;
        this.useDefaultEnchantmentData = builder.useDefaultEnchantmentData;
        this.useDefaultEnchantability = builder.useDefaultEnchantability;
    }

    private void initializeData(){
        if (useConcurrentHashMap){
            this.data = new ConcurrentHashMap<>();
        } else {
            this.data = new HashMap<>();
        }
        if (useDefaultEnchantmentData){
            this.data.putAll(EnchantmentData.getDefaultData());
        }
    }

    private void initializeEnchantability(){
        if (useConcurrentHashMap){
            this.enchantability = new ConcurrentHashMap<>();
        } else {
            this.enchantability = new HashMap<>();
        }
        if (useDefaultEnchantability){
            this.enchantability.putAll(EnchantmentData.getDefaultEnchantability());
        }
    }

    public void putEnchantmentData(@NotNull Enchantment enchantment, @NotNull EnchantmentData data){
        if (this.data == null){
            this.initializeData();
        }
        this.data.put(enchantment, data);
    }

    public EnchantmentData getEnchantmentData(@NotNull Enchantment enchantment){
        if (this.data == null){
            if (useDefaultEnchantmentData){
                return EnchantmentData.getDefaultData().get(enchantment);
            }
            return null;
        }
        return this.data.get(enchantment);
    }

    public void removeEnchantmentData(@NotNull Enchantment enchantment){
        if (this.data == null){
            // Don't initialize maps if there isn't a value to remove
            if (this.useDefaultEnchantmentData && !EnchantmentData.getDefaultData().containsKey(enchantment)){
                return;
            }
            this.initializeData();
        }
        this.data.remove(enchantment);
    }

    public void putEnchantability(@NotNull Material material, @NotNull Integer value){
        if (this.enchantability == null){
            // Don't initialize maps if the default is set to the provided value
            if (this.useDefaultEnchantability && value.equals(EnchantmentData.getDefaultEnchantability().get(material))){
                return;
            }
            this.initializeEnchantability();
        }
        this.enchantability.put(material, value);
    }

    public Integer getEnchantability(@NotNull Material material){
        if (this.enchantability == null){
            if (useDefaultEnchantability){
                return EnchantmentData.getDefaultEnchantability().get(material);
            }
            return null;
        }
        return this.enchantability.get(material);
    }

    public void removeEnchantability(@NotNull Material material){
        if (this.enchantability == null) {
            // Don't initialize maps if there isn't a value to remove
            if (this.useDefaultEnchantability && !EnchantmentData.getDefaultEnchantability().containsKey(material)) {
                return;
            }
            this.initializeEnchantability();
        }
        this.enchantability.remove(material);
    }

    public static @NotNull Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private boolean useConcurrentHashMap = false,
                        useDefaultEnchantmentData = true,
                        useDefaultEnchantability = true;

        private Builder(){}

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

        @Contract("_ -> this")
        public @NotNull Builder useDefaultEnchantability(boolean useDefaultEnchantability) {
            this.useDefaultEnchantability = useDefaultEnchantability;
            return this;
        }

        public @NotNull EnchantmentManager builder(){
            return new EnchantmentManager(this);
        }
    }
}
