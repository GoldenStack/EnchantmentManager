package dev.goldenstack.enchantment;

import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * The EnchantmentManager class is meant to help you enchant items.<br>
 * Importantly, this is not an enchantment <b>table</b> or anvil library. It gives you convenient methods to enchant an
 * item with a number of levels. This will not make enchantment tables automatically work, and the EnchantmentData
 * classes aren't compatible with anything relating to anvils because they don't account for the fact that, for example,
 * sharpness can be added to axes.<br>
 * Each instance stores its own enchantability and EnchantmentData maps, so other extensions can't mess up yours if you
 * don't want them to.<br>
 * <br>
 * <h3>Guide to implementing default Minecraft enchanting</h3>
 * Just initialize a new manager with both parameters as true if you want the default values for everything.
 * Run {@link EnchantmentManager#enchantWithLevels(ItemStack, int, Random, Predicate, Predicate)
 * manager.enchantWithLevels(itemStack, levels, random, enchantmentPredicate, alwaysAddPredicate} where:
 * <ul>
 *     <li>{@code manager} is the EnchantmentManager that you created</li>
 *     <li>{@code itemStack} is the ItemStack that you want enchanted</li>
 *     <li>{@code levels} is the number of levels that you want to enchant the item with</li>
 *     <li>{@code random} is the Random instance that the code should use</li>
 *     <li>{@code enchantmentPredicate} is the predicate that determines if an enchantment should be considered</li>
 *     <li>{@code alwaysAddPredicate} is the predicate that determines if slot types should be ignored</li>
 * </ul>
 * Now, if you want default behavior, you'd want to put {@link EnchantmentManager#alwaysAddIfBook(ItemStack)} as the
 * {@code alwaysAddPredicate} argument, since it mimics default behavior. Note that, if you use this, you probably will
 * want to manually change {@link Material#BOOK}s to {@link Material#ENCHANTED_BOOK}s if they get enchanted.<br>
 * For {@code enchantmentPredicate}, you'll want to put {@link EnchantmentManager#discoverable(EnchantmentData)} if you
 * want to allow treasure enchantments and {@link EnchantmentManager#discoverableAndNotTreasure(EnchantmentData)} if you
 * don't.<br>
 * <br>
 * Here is some correct but extremely unorthodox code so you can see an example. Since this is just example code, it's
 * not a good idea to actually use this example for anything.<br>
 * <code>new EnchantmentManager(true, true).enchantWithLevels(ItemStack.of(Material.DIAMOND_SWORD), 30, new Random(),
 * EnchantmentManager::discoverableAndNotTreasure, EnchantmentManager::alwaysAddIfBook)</code>
 */
public class EnchantmentManager {

    private final @NotNull Map<NamespaceID, EnchantmentData> data = new ConcurrentHashMap<>();
    private final @NotNull Map<Material, Integer> enchantability = new ConcurrentHashMap<>();

    /**
     * Creates a new EnchantmentManager with the provided settings
     * @param useDefaultEnchantmentData when true, automatically registers the default enchantment data from
     * {@link EnchantmentData#getDefaultData()}
     * @param useDefaultEnchantability when true, automatically registers the default enchantability values from
     * {@link EnchantmentData#getDefaultEnchantability()}
     */
    public EnchantmentManager(boolean useDefaultEnchantmentData, boolean useDefaultEnchantability) {
        if (useDefaultEnchantmentData) {
            data.putAll(EnchantmentData.getDefaultData());
        }
        if (useDefaultEnchantability) {
            enchantability.putAll(EnchantmentData.getDefaultEnchantability());
        }
    }

    /**
     * This is meant to act as an argument when you need to pass a Predicate<EnchantmentData> into methods of this class
     * (with the name "enchantmentPredicate").<br>
     * In default Minecraft, you cannot get any non-discoverable enchantments from any enchantment mechanism. Since
     * that's a restriction that you may not want, you can decide if you want to use it or not, via this predicate.<br>
     * This predicate acts identically to if you want to enchant an item and accept both treasure and non-treasure
     * enchantments. For example, this is the method you'd use if you wanted to enchant the items in end city chests.
     */
    public static boolean discoverable(@NotNull EnchantmentData data) {
        return data.enchantment().registry().isDiscoverable();
    }

    /**
     * This is meant to act as an argument when you need to pass a Predicate<EnchantmentData> into methods of this class
     * (with the name "enchantmentPredicate").<br>
     * In default Minecraft, you cannot get any non-discoverable enchantments from any enchantment mechanism. Since
     * that's a restriction that you may not want, you can decide if you want to use it or not, via this predicate.<br>
     * This predicate acts identically to if you want to enchant an item and accept only non-treasure enchantments. For
     * example, this is the method you'd use if you wanted to enchant an item at an enchantment table.
     */
    public static boolean discoverableAndNotTreasure(@NotNull EnchantmentData data) {
        return data.enchantment().registry().isDiscoverable() && !data.enchantment().registry().isTreasureOnly();
    }

    /**
     * This is meant to act as an argument when you need to pass a Predicate<ItemStack> into methods of this class (with
     * the name "alwaysAddPredicate").<br>
     * In default Minecraft, enchantments can only be added to items if the enchantment's slot type applies to the item
     * - except if the item is a book. If it's a book, all enchantments will work, and it will be converted to an
     * enchanted book. Since this isn't default Minecraft, you'll have to convert it to an enchanted book yourself if
     * you want it to be.<br>
     * Basically, this is the default predicate that would be used anywhere in default Minecraft if you wanted to
     * enchant something.
     */
    public static boolean alwaysAddIfBook(@NotNull ItemStack itemStack) {
        return itemStack.material() == Material.BOOK;
    }

    /**
     * Adds the enchantments from {@link #getEnchantsWithLevels(ItemStack, int, Random, Predicate, Predicate)} to the
     * provided ItemStack. There's not really much to it, but it just converts the WeightedEnchants into a Map and then
     * adds that to the ItemStack.<br>
     * <b>For parameter information, please check the documentation of {@link #getEnchantsWithLevels(ItemStack, int,
     * Random, Predicate, Predicate)}</b><br>
     * @return The enchanted ItemStack
     */
    public @NotNull ItemStack enchantWithLevels(@NotNull ItemStack itemStack, int levels, @NotNull Random random,
                                                @NotNull Predicate<EnchantmentData> enchantmentPredicate,
                                                @NotNull Predicate<ItemStack> alwaysAddPredicate) {
        final Map<Enchantment, Short> enchantments = new HashMap<>();
        for (WeightedEnchant enchant : getEnchantsWithLevels(itemStack, levels, random, enchantmentPredicate, alwaysAddPredicate)) {
            enchantments.put(enchant.data().enchantment(), (short) enchant.level());
        }
        return itemStack.withMeta(builder -> builder.enchantments(enchantments));
    }

    /**
     * Picks some valid enchantments from the list of enchantments that {@link #getWeightedEnchantments(ItemStack, int, Predicate, Predicate)}
     * provides. It's not as simple as picking from a list; there are multiple other factors that should be considered:
     * <ul>
     *     <li>The level is automatically incremented by 1. Note that this is before all other calculations so it can
     *     easily be manipulated.</li>
     *     <li>A random number of levels from {@code 0} to {@code (enchantability / 2) + 2} are added to the total. The
     *     number is picked with somewhat normal distribution according to the central limit theorem (as it's simply two
     *     equally ranged random numbers added together).</li>
     *     <li>The current level is then multiplied by a random number from 0.85 to 1.15 (again, following roughly
     *     normal distribution as it's just two equally ranged random numbers added together), and then rounded to the
     *     nearest integer.</li>
     * </ul>
     * After the manipulations to the level count, the enchantments must be randomly picked from the list of
     * enchantments retrieved from {@link #getWeightedEnchantments(ItemStack, int, Predicate, Predicate)}. Importantly,
     * one enchantment is always picked from the list of possible enchantments (unless the list is empty).<br>
     * All the code in the following list is under a {@code while} loop.<br>
     * <ul>
     *     <li>There is a {@code levels / 50} probability that the loop decides to pick an enchantment. However, as the
     *     random number internally generated can be zero and still continue the loop, the lowest chance of an
     *     additional enchantment is actually 1/50.</li>
     *     <li>First, the most recently added enchantment gets all of its colliding enchantments removed from the list
     *     of possible enchantments. Since this is done before any other calculations, on the {@code while} loop's first
     *     iteration, it covers the collisions of the enchantment that was added before the loop started.</li>
     *     <li>Next, the loop breaks if the list of enchantments that can be added is empty.</li>
     *     <li>An enchantment is then randomly picked from the list of possible enchantments. It follows the normal
     *     rules for picking an enchantment, so weights do affect how likely an enchantment is to get picked.</li>
     *     <li>Finally, the number of levels is divided by 2. Since the list of possible enchantments has already been
     *     calculated, the only effect this has is that it decreases the chance of another enchantment being picked
     *     (unless the chance is already 1/50 or the level count is already 0, which results in it staying the same).</li>
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
        Integer value = getEnchantability(itemStack.material());
        int enchantability = value == null ? 0 : value;

        levels += 1 + random.nextInt(enchantability / 4 + 1) + random.nextInt(enchantability / 4 + 1);

        double multiplier = (random.nextDouble() + random.nextDouble() - 1) * 0.15;

        levels = Math.max((int) Math.round(levels * (1 + multiplier)), 1);

        List<WeightedEnchant> list = getWeightedEnchantments(itemStack, levels, enchantmentPredicate, alwaysAddPredicate);

        if (list.isEmpty()) {
            return enchants;
        }

        // Store the total because there's no need to re-calculate it each time
        double total = 0;
        for (WeightedEnchant weightedEnchant : list) {
            total += weightedEnchant.data().weight();
        }

        WeightedEnchant first = WeightedEnchant.getItemFrom(list, random.nextDouble() * total);
        if (first == null) {
            return enchants;
        }
        total -= first.getWeight();
        enchants.add(first);

        // Note that there is always a chance of this returning true, resulting in the very small chance of
        // getting an almost completely maxed item as long as you have a high enough level.
        while (random.nextInt(50) <= levels) {
            WeightedEnchant last = enchants.get(enchants.size() - 1);
            Iterator<WeightedEnchant> iterator = list.iterator();
            while (iterator.hasNext()) {
                WeightedEnchant next = iterator.next();
                if (last.data().collidesWith(next.data())) {
                    total -= next.getWeight();
                    iterator.remove();
                }
            }
            if (list.isEmpty()) {
                break;
            }
            WeightedEnchant element = WeightedEnchant.getItemFrom(list, random.nextDouble() * total);
            if (element != null) {
                total -= element.getWeight();
                enchants.add(element);
            }
            levels /= 2;
        }
        return enchants;
    }

    /**
     * Generates a list of possible enchantments that could be applied to the given item.<br>
     *  The process is relatively simple. For each enchantment that is registered in this instance's {@code EnchantmentData}
     * map:<br>
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
                                                                  @NotNull Predicate<ItemStack> alwaysAddPredicate) {
        List<WeightedEnchant> enchants = new ArrayList<>();
        boolean addUnconditionally = alwaysAddPredicate.test(itemStack);
        for (EnchantmentData data : this.getAllEnchantmentData()) {
            if (!enchantmentPredicate.test(data)) {
                continue;
            }
            if (!addUnconditionally && !data.slotType().canEnchant(itemStack)) {
                continue;
            }
            for (int i = (int) data.enchantment().registry().maxLevel(); i > 0; i--) {
                if (levels >= data.getMinimumLevel(i) && levels <= data.getMaximumLevel(i)) {
                    enchants.add(new WeightedEnchant(data, i));
                    break;
                }
            }
        }
        return enchants;
    }

    /**
     * Associates the key with the provided EnchantmentData.
     * @param key The key
     * @param data The value
     */
    public void putEnchantmentData(@NotNull NamespaceID key, @NotNull EnchantmentData data) {
        this.data.put(key, data);
    }

    /**
     * Finds the EnchantmentData instance that is associated with the provided key.
     * @param key The key to search
     * @return The data
     */
    public @Nullable EnchantmentData getEnchantmentData(@NotNull NamespaceID key) {
        return this.data.get(key);
    }

    /**
     * @return A collection of all the keys from this instance's EnchantmentData map.
     */
    public @NotNull Collection<NamespaceID> getAllKeys() {
        return Collections.unmodifiableCollection(this.data.keySet());
    }

    /**
     * @return A collection of all the values from this instance's EnchantmentData map.
     */
    public @NotNull Collection<EnchantmentData> getAllEnchantmentData() {
        return Collections.unmodifiableCollection(this.data.values());
    }

    /**
     * Removes the provided key from this manager.
     * @param key The key to remove
     */
    public void removeEnchantmentData(@NotNull NamespaceID key) {
        this.data.remove(key);
    }

    /**
     * Associates the provided material with the value.
     * @param material The material to set
     * @param value The value to associate with the material
     */
    public void putEnchantability(@NotNull Material material, @NotNull Integer value) {
        this.enchantability.put(material, value);
    }

    /**
     * Attempts to get the enchantability for the provided material.
     * @param material The material to search for
     * @return The enchantability value for the provided material
     */
    public @Nullable Integer getEnchantability(@NotNull Material material) {
        return this.enchantability.get(material);
    }

    /**
     * Removes the provided material from this manager's enchantability map.
     * @param material The material to remove
     */
    public void removeEnchantability(@NotNull Material material) {
        this.enchantability.remove(material);
    }

}
