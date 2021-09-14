package dev.goldenstack.enchantment;

import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

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


    public @NotNull List<WeightedEnchant> getWeightedEnchantments(@NotNull ItemStack itemStack,
                                                                  int levels, @NotNull Predicate<EnchantmentData> enchantmentPredicate,
                                                                  @NotNull Predicate<ItemStack> alwaysAddPredicate){
        List<WeightedEnchant> enchants = new ArrayList<>();
        boolean addUnconditionally = alwaysAddPredicate.test(itemStack);
        for (EnchantmentData data : this.getAllEnchantmentData()){
            if (!enchantmentPredicate.test(data)){
                continue;
            }
            if (!addUnconditionally && !data.slotType().canEnchant(itemStack)){
                continue;
            }
            for (int i = (int) data.enchantment().registry().maxLevel(); i > 0; i --){
                if (levels >= data.getMinimumLevel(i) && levels <= data.getMaximumLevel(i)){
                    enchants.add(new WeightedEnchant(data, i));
                    break;
                }
            }
        }
        return enchants;
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

    public @NotNull Collection<EnchantmentData> getAllEnchantmentData(){
        if (this.data == null){
            if (useDefaultEnchantmentData){
                return EnchantmentData.getDefaultData().values();
            }
            return Collections.emptyList();
        }
        return this.data.values();
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

    /**
     * Utility class for building EnchantmentManager instances
     */
    public static class Builder {
        private boolean useConcurrentHashMap = false,
                        useDefaultEnchantmentData = true,
                        useDefaultEnchantability = true;

        private Builder(){}

        /**
         * <p>When true, sets the {@code data} and {@code enchantability} maps to ConcurrentHashMaps when they
         * get initialized instead of normal HashMaps.</p>
         * <p>Default: {@code false}</p>
         */
        @Contract("_ -> this")
        public @NotNull Builder useConcurrentHashMap(boolean useConcurrentHashMap) {
            this.useConcurrentHashMap = useConcurrentHashMap;
            return this;
        }

        /**
         * <p>When true, makes the built EnchantmentManager automatically add the default enchantment data from
         * {@link EnchantmentData#getDefaultData()} when the {@code data} map gets initialized.</p>
         * <p>Default: {@code true}</p>
         */
        @Contract("_ -> this")
        public @NotNull Builder useDefaultEnchantmentData(boolean useDefaultEnchantmentData) {
            this.useDefaultEnchantmentData = useDefaultEnchantmentData;
            return this;
        }

        /**
         * When true, makes the built EnchantmentManager automatically add the default enchantability data from
         * {@link EnchantmentData#getDefaultEnchantability()} when the {@code enchantability} map gets initialized.
         * <p>Default: {@code true}</p>
         */
        @Contract("_ -> this")
        public @NotNull Builder useDefaultEnchantability(boolean useDefaultEnchantability) {
            this.useDefaultEnchantability = useDefaultEnchantability;
            return this;
        }

        /**
         * Turns this builder into an EnchantmentManager
         */
        public @NotNull EnchantmentManager build(){
            return new EnchantmentManager(this);
        }
    }
}
