package dev.goldenstack.enchantment;

import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * <p>The EnchantmentManager class is meant to help you enchant items.</p>
 * <p>Each instance stores its own enchantability and EnchantmentData tables, so other extensions can't mess up yours
 * if you don't want them to.</p>
 * <p>You are completely free to modify the tables that this class has. If you have concurrency issues for some reason,
 * consider enabling the {@link EnchantmentManager.Builder#useConcurrentHashMap(boolean)} setting.</p>
 * <p>EnchantmentManager instances have their EnchantmentData and enchantability tables lazily initialized. In this
 * case, this means that they do not get created until the user needs them. When you have the {@code useDefaultEnchantmentData}
 * or {@code useDefaultEnchantability} settings enabled and the map for it has not been initialized, it will grab the
 * data from the default map instead of creating a new one.</p>
 */
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

    /**
     * Picks some valid enchantments from the list of enchantments that {@link #getWeightedEnchantments(ItemStack, int, Predicate, Predicate)}
     * provides. It's not as simple as picking from a list; there are multiple other factors that should be considered:
     * <ul>
     *     <li>The level is automatically incremented by 1. Note that this is before all other calculations so it can
     *     easily be manipulated.</li>
     *     <li>For each 4 enchantability points (rounded down), 0 to 2 extra levels are added to the calculation.</li>
     *     <li>Additionally, 0 to 2 levels are added regardless of the item's
     *     enchantability. </li>
     *     <li>The current level is then multiplied by a random number from 0.85 to 1.15, and then rounded
     *     to the nearest integer.</li>
     * </ul>
     * <p>After the manipulations to the level count, the enchantments must be randomly picked from the list of
     * enchantments retrieved from {@link #getWeightedEnchantments(ItemStack, int, Predicate, Predicate)}. Importantly,
     * one enchantment is always picked from the list of possible enchantments (unless the list is empty).</p>
     * <p>All the code in the following list is under a {@code while} loop.</p>
     * <ul>
     *     <li>There is a {@code levels / 50} probability that the loop decides to pick an enchantment. However, if the
     *     chance would be 0/50, it is 1/50 instead./li>
     *     <li>First, the most recently added enchantment gets all of its colliding enchantments removed from the list
     *     of possible enchantments. Since this is done before any other calculations, on the {@code while} loop's first
     *     iteration, it covers the collisions of the enchantment that was added before the loop started.</li>
     *     <li>Next, the loop breaks if the list of enchantments that can be added is empty.</li>
     *     <li>An enchantment is then randomly picked from the list of possible enchantments. It follows the normal
     *     rules for picking an enchantment, so weights do affect how likely an enchantment is to get picked.</li>
     *     <li>Finally, the number of levels is divided by 2. Since the list of possible enchantments has already been
     *     calculated, the only effect this has is that it decreases the chance of another enchantment being picked
     *     (unless the chance is already 1/50, which results in it staying the same).</li>
     * </ul>
     * @param itemStack The ItemStack to randomize enchantments for
     * @param levels The number of levels to enchant with
     * @param random The random generator to use for randomization
     * @param enchantmentPredicate Decides what enchantment should be considered. Note that this is not actually used in the
     *                             method and instead is passed on to {@link #getWeightedEnchantments(ItemStack, int, Predicate, Predicate)}
     *                             when this method calls it.
     * @param alwaysAddPredicate Decides if enchantments should be added regardless of if they fit the slot of the
     *                           provided item. This should generally be used to make sure that books can receive all
     *                           enchantments. Note that this is not actually used in the method and instead is passed
     *                           on to {@link #getWeightedEnchantments(ItemStack, int, Predicate, Predicate)} when this
     *                           method calls it.
     * @return The list of randomly picked enchantments
     */
    public @NotNull List<WeightedEnchant> getEnchantsWithLevels(@NotNull ItemStack itemStack, int levels, @NotNull Random random,
                                                                @NotNull Predicate<EnchantmentData> enchantmentPredicate,
                                                                @NotNull Predicate<ItemStack> alwaysAddPredicate) {
        List<WeightedEnchant> enchants = new ArrayList<>();
        int enchantability = getEnchantability(itemStack.getMaterial());

        // Simplified version of the (official) code: levels += 1 + random.nextInt(enchantability / 4 + 1) + random.nextInt(enchantability / 4 + 1);
        levels += 1 + random.nextInt((enchantability / 4) * 2 + 2);

        // Simplified version of the (official) code: (random.nextDouble() + random.nextDouble() - 1) * 0.15;
        double multiplier = random.nextDouble() - 0.5 * 0.3;
        levels = Math.max((int) Math.round(levels + levels * multiplier), 1);

        List<WeightedEnchant> list = getWeightedEnchantments(itemStack, levels, enchantmentPredicate, alwaysAddPredicate);
        // Don't perform unnecessary calculations if the list is empty.
        if (list.isEmpty()){
            return enchants;
        }

        double total = 0;
        for (WeightedEnchant weightedEnchant : list) {
            total += weightedEnchant.data().weight();
        }
        WeightedEnchant first = WeightedEnchant.getWeightedItemFrom(list, random.nextDouble() * total);
        if (first == null){
            return enchants;
        }
        total -= first.getWeight();
        enchants.add(first);

        // Note that there is always a chance of this returning true, resulting in the very small chance of
        // getting an almost completely maxed sword as long as you have a high enough level.
        while(random.nextInt(50) <= levels){
            WeightedEnchant last = enchants.get(enchants.size() - 1);
            Iterator<WeightedEnchant> iterator = list.iterator();
            while (iterator.hasNext()) {
                WeightedEnchant next = iterator.next();
                if (last.data().collidesWith(next.data())) {
                    total -= next.getWeight();
                    iterator.remove();
                }
            }
            if (list.isEmpty()){
                break;
            }
            WeightedEnchant element = WeightedEnchant.getWeightedItemFrom(list, random.nextDouble() * total);
            if (element != null){
                total -= element.getWeight();
                enchants.add(element);
            }
            levels /= 2;
        }
        return enchants;
    }

    /**
     * <p>Generates a list of possible enchantments that could be applied to the given item.</p>
     * <p>The process is relatively simple. For each enchantment that is registered in this instance's {@code EnchantmentData}
     * map:</p>
     * <ul>
     *     <li>If the enchantment does not pass the {@code enchantmentPredicate} predicate, it will skip to the next
     *     one.</li>
     *     <li>If the {@code ItemStack} provided does not fit the slot that the {@code EnchantmentData} requires it to,
     *     it will skip to the next one, unless the {@code alwaysAddPredicate} returns true for the provided ItemStack.</li>
     *     <li>For each of the enchantment's levels, iterating from the enchantment's max level to 1 (inclusive), if the
     *     provided {@code level} argument is in the range of the enchantment with the current level, the enchantment is
     *     added to the list. Otherwise, the loop continues down the list until it reaches a level that the provided
     *     {@code level} argument is in range of.</li>
     * </ul>
     * @param itemStack The ItemStack to get enchantments for
     * @param levels The number of levels to enchant with
     * @param enchantmentPredicate Decides if enchantments should be considered
     * @param alwaysAddPredicate Decides if enchantments should be added regardless of if they fit the slot of the
     *                           provided item. This should generally be used to make sure that books can receive all
     *                           enchantments.
     * @return The list of possible enchantments
     */
    public @NotNull List<WeightedEnchant> getWeightedEnchantments(@NotNull ItemStack itemStack, int levels,
                                                                  @NotNull Predicate<EnchantmentData> enchantmentPredicate,
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
