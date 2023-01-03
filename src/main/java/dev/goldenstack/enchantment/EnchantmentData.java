package dev.goldenstack.enchantment;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static dev.goldenstack.enchantment.LevelProvider.*;

/**
 * Represents extra data about an enchantment. This data is only carried in this object, so different
 * {@code EnchantmentManager}s could have completely different values for the same enchantment.
 */
public record EnchantmentData(@NotNull Enchantment enchantment, int weight, @NotNull SlotType slotType,
                              @NotNull LevelProvider minimumLevelProvider, @NotNull LevelProvider maximumLevelProvider,
                              @NotNull List<Enchantment> incompatible) {

    /**
     * Creates a new EnchantmentData instance with all the values directly set - except for {@code incompatible}, which
     * is converted into an ImmutableList.
     */
    public EnchantmentData(@NotNull Enchantment enchantment, int weight, @NotNull SlotType slotType,
                           @NotNull LevelProvider minimumLevelProvider, @NotNull LevelProvider maximumLevelProvider,
                           @NotNull Enchantment @NotNull ... incompatible) {
        this(enchantment, weight, slotType, minimumLevelProvider, maximumLevelProvider, List.of(incompatible));
    }

    /**
     * @return the minimum level for the provided enchantment level according to {@link #minimumLevelProvider}.
     */
    public int getMinimumLevel(int level) {
        return this.minimumLevelProvider.getLevel(this, level);
    }

    /**
     * @return the maximum level for the provided enchantment level according to {@link #maximumLevelProvider}.
     */
    public int getMaximumLevel(int level) {
        return this.maximumLevelProvider.getLevel(this, level);
    }

    /**
     * Tests if this EnchantmentData's enchantment collides in some way with the other object's enchantment. An
     * enchantment is considered to be colliding with another one's enchantment if:
     * <ul>
     *     <li>The enchantment is equal to the other object's enchantment</li>
     *     <li>The enchantment is contained on the other object's list of incompatible enchantments</li>
     *     <li>or the other object's enchantment is contained on this object's list of incompatible enchantments.</li>
     * </ul>
     *
     * @param data The other EnchantmentData
     * @return if this object's enchantment collides with the other's enchantment
     */
    public boolean collidesWith(@NotNull EnchantmentData data) {
        return enchantment.equals(data.enchantment) || incompatible.contains(data.enchantment) || data.incompatible.contains(enchantment);
    }

    /**
     * @return the default (and immutable) map of default NamespaceID -> EnchantmentData
     */
    public static @NotNull Map<NamespaceID, EnchantmentData> getDefaultData() {
        return DEFAULT_DATA;
    }

    /**
     * @return the default (and immutable) map of default enchantability data
     */
    public static @NotNull Object2IntMap<Material> getDefaultEnchantability() {
        return DEFAULT_ENCHANTABILITY;
    }

    private static final @NotNull Map<NamespaceID, EnchantmentData> DEFAULT_DATA = Map.ofEntries(
            Map.entry(Enchantment.FIRE_PROTECTION.namespace(), new EnchantmentData(Enchantment.FIRE_PROTECTION, 5, SlotType::ARMOR, adjusted(10, 8), addToMin(8), Enchantment.PROTECTION, Enchantment.BLAST_PROTECTION, Enchantment.PROJECTILE_PROTECTION)),
            Map.entry(Enchantment.KNOCKBACK.namespace(), new EnchantmentData(Enchantment.KNOCKBACK, 5, SlotType::WEAPON, adjusted(5, 20), addToDefault(50))),
            Map.entry(Enchantment.MENDING.namespace(), new EnchantmentData(Enchantment.MENDING, 2, SlotType::BREAKABLE, multiply(25), addToMin(50))),
            Map.entry(Enchantment.POWER.namespace(), new EnchantmentData(Enchantment.POWER, 10, SlotType::BOW, adjusted(1, 10), addToMin(15))),
            Map.entry(Enchantment.LUCK_OF_THE_SEA.namespace(), new EnchantmentData(Enchantment.LUCK_OF_THE_SEA, 2, SlotType::FISHING_ROD, adjusted(15, 9), addToDefault(50))),
            Map.entry(Enchantment.THORNS.namespace(), new EnchantmentData(Enchantment.THORNS, 1, SlotType::ARMOR, adjusted(10, 20), addToDefault(50))),
            Map.entry(Enchantment.SWEEPING.namespace(), new EnchantmentData(Enchantment.SWEEPING, 2, SlotType::WEAPON, adjusted(5, 9), addToMin(15))),
            Map.entry(Enchantment.FEATHER_FALLING.namespace(), new EnchantmentData(Enchantment.FEATHER_FALLING, 5, SlotType::ARMOR_FEET, adjusted(5, 6), addToMin(6))),
            Map.entry(Enchantment.FLAME.namespace(), new EnchantmentData(Enchantment.FLAME, 2, SlotType::BOW, multiply(20), constant(50))),
            Map.entry(Enchantment.BINDING_CURSE.namespace(), new EnchantmentData(Enchantment.BINDING_CURSE, 1, SlotType::ARMOR, multiply(25), constant(50))),
            Map.entry(Enchantment.AQUA_AFFINITY.namespace(), new EnchantmentData(Enchantment.AQUA_AFFINITY, 2, SlotType::ARMOR_HELMET, multiply(1), addToMin(40))),
            Map.entry(Enchantment.PROJECTILE_PROTECTION.namespace(), new EnchantmentData(Enchantment.PROJECTILE_PROTECTION, 5, SlotType::ARMOR, adjusted(3, 6), addToMin(6), Enchantment.PROTECTION, Enchantment.BLAST_PROTECTION, Enchantment.FIRE_PROTECTION)),
            Map.entry(Enchantment.SMITE.namespace(), new EnchantmentData(Enchantment.SMITE, 5, SlotType::WEAPON, adjusted(5, 8), addToMin(20), Enchantment.SHARPNESS, Enchantment.BANE_OF_ARTHROPODS)),
            Map.entry(Enchantment.FROST_WALKER.namespace(), new EnchantmentData(Enchantment.FROST_WALKER, 2, SlotType::ARMOR_FEET, multiply(10), addToMin(15), Enchantment.DEPTH_STRIDER)),
            Map.entry(Enchantment.VANISHING_CURSE.namespace(), new EnchantmentData(Enchantment.VANISHING_CURSE, 1, SlotType::ALL, multiply(25), constant(50))),
            Map.entry(Enchantment.PUNCH.namespace(), new EnchantmentData(Enchantment.PUNCH, 2, SlotType::BOW, adjusted(12, 20), addToMin(25))),
            Map.entry(Enchantment.BLAST_PROTECTION.namespace(), new EnchantmentData(Enchantment.BLAST_PROTECTION, 2, SlotType::ARMOR, adjusted(5, 8), addToMin(8), Enchantment.PROTECTION, Enchantment.FIRE_PROTECTION, Enchantment.PROJECTILE_PROTECTION)),
            Map.entry(Enchantment.IMPALING.namespace(), new EnchantmentData(Enchantment.IMPALING, 2, SlotType::TRIDENT, adjusted(1, 8), addToMin(20))),
            Map.entry(Enchantment.BANE_OF_ARTHROPODS.namespace(), new EnchantmentData(Enchantment.BANE_OF_ARTHROPODS, 5, SlotType::WEAPON, adjusted(5, 8), addToMin(20), Enchantment.SHARPNESS, Enchantment.SMITE)),
            Map.entry(Enchantment.SHARPNESS.namespace(), new EnchantmentData(Enchantment.SHARPNESS, 10, SlotType::WEAPON, adjusted(1, 11), addToMin(20), Enchantment.SMITE, Enchantment.BANE_OF_ARTHROPODS)),
            Map.entry(Enchantment.EFFICIENCY.namespace(), new EnchantmentData(Enchantment.EFFICIENCY, 10, SlotType::TOOL, adjusted(1, 10), addToDefault(50))),
            Map.entry(Enchantment.SILK_TOUCH.namespace(), new EnchantmentData(Enchantment.SILK_TOUCH, 1, SlotType::TOOL, multiply(15), addToDefault(50), Enchantment.FORTUNE)),
            Map.entry(Enchantment.LOOTING.namespace(), new EnchantmentData(Enchantment.LOOTING, 2, SlotType::WEAPON, adjusted(15, 9), addToDefault(50))),
            Map.entry(Enchantment.LURE.namespace(), new EnchantmentData(Enchantment.LURE, 2, SlotType::FISHING_ROD, adjusted(15, 9), addToDefault(50))),
            Map.entry(Enchantment.DEPTH_STRIDER.namespace(), new EnchantmentData(Enchantment.DEPTH_STRIDER, 2, SlotType::ARMOR_FEET, multiply(10), addToMin(15), Enchantment.FROST_WALKER)),
            Map.entry(Enchantment.SOUL_SPEED.namespace(), new EnchantmentData(Enchantment.SOUL_SPEED, 1, SlotType::ARMOR_FEET, multiply(10), addToMin(15))),
            Map.entry(Enchantment.RESPIRATION.namespace(), new EnchantmentData(Enchantment.RESPIRATION, 2, SlotType::ARMOR_HELMET, multiply(10), addToMin(30))),
            Map.entry(Enchantment.FIRE_ASPECT.namespace(), new EnchantmentData(Enchantment.FIRE_ASPECT, 2, SlotType::WEAPON, adjusted(10, 20), addToDefault(50))),
            Map.entry(Enchantment.PIERCING.namespace(), new EnchantmentData(Enchantment.PIERCING, 10, SlotType::CROSSBOW, adjusted(1, 10), constant(50), Enchantment.MULTISHOT)),
            Map.entry(Enchantment.LOYALTY.namespace(), new EnchantmentData(Enchantment.LOYALTY, 5, SlotType::TRIDENT, basic(5, 7), constant(50))),
            Map.entry(Enchantment.UNBREAKING.namespace(), new EnchantmentData(Enchantment.UNBREAKING, 5, SlotType::BREAKABLE, adjusted(5, 8), addToDefault(50))),
            Map.entry(Enchantment.RIPTIDE.namespace(), new EnchantmentData(Enchantment.RIPTIDE, 2, SlotType::TRIDENT, basic(10, 7), constant(50), Enchantment.LOYALTY, Enchantment.CHANNELING)),
            Map.entry(Enchantment.QUICK_CHARGE.namespace(), new EnchantmentData(Enchantment.QUICK_CHARGE, 5, SlotType::CROSSBOW, adjusted(12, 20), constant(50))),
            Map.entry(Enchantment.PROTECTION.namespace(), new EnchantmentData(Enchantment.PROTECTION, 10, SlotType::ARMOR, adjusted(1, 11), addToMin(11), Enchantment.BLAST_PROTECTION, Enchantment.FIRE_PROTECTION, Enchantment.PROJECTILE_PROTECTION)),
            Map.entry(Enchantment.INFINITY.namespace(), new EnchantmentData(Enchantment.INFINITY, 1, SlotType::BOW, multiply(20), constant(50), Enchantment.MENDING)),
            Map.entry(Enchantment.FORTUNE.namespace(), new EnchantmentData(Enchantment.FORTUNE, 2, SlotType::TOOL, adjusted(15, 9), addToDefault(50), Enchantment.SILK_TOUCH)),
            Map.entry(Enchantment.MULTISHOT.namespace(), new EnchantmentData(Enchantment.MULTISHOT, 2, SlotType::CROSSBOW, multiply(20), constant(50), Enchantment.PIERCING)),
            Map.entry(Enchantment.CHANNELING.namespace(), new EnchantmentData(Enchantment.CHANNELING, 1, SlotType::TRIDENT, multiply(25), constant(50)))
    );

    private static final @NotNull Object2IntMap<Material> DEFAULT_ENCHANTABILITY;

    static {
        Object2IntMap<Material> enchantability = new Object2IntOpenHashMap<>();

        // Misc
        putMap(enchantability, 1, Material.TRIDENT, Material.BOOK, Material.FISHING_ROD, Material.BOW, Material.CROSSBOW);

        // Armor
        putMap(enchantability, 15, Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);
        putMap(enchantability, 12, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS);
        putMap(enchantability, 9, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS);
        putMap(enchantability, 25, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS);
        putMap(enchantability, 10, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS);
        putMap(enchantability, 15, Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS);
        putMap(enchantability, 9, Material.TURTLE_HELMET);

        // Tools
        putMap(enchantability, 15, Material.WOODEN_SWORD, Material.WOODEN_PICKAXE, Material.WOODEN_AXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE);
        putMap(enchantability, 5, Material.STONE_SWORD, Material.STONE_PICKAXE, Material.STONE_AXE, Material.STONE_SHOVEL, Material.STONE_HOE);
        putMap(enchantability, 14, Material.IRON_SWORD, Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_SHOVEL, Material.IRON_HOE);
        putMap(enchantability, 10, Material.DIAMOND_SWORD, Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_HOE);
        putMap(enchantability, 15, Material.NETHERITE_SWORD, Material.NETHERITE_PICKAXE, Material.NETHERITE_AXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_HOE);

        DEFAULT_ENCHANTABILITY = Object2IntMaps.unmodifiable(enchantability);
    }

    private static void putMap(@NotNull Object2IntMap<Material> map, int value, @NotNull Material @NotNull ... materials) {
        for (var material : materials) {
            map.put(material, value);
        }
    }
}