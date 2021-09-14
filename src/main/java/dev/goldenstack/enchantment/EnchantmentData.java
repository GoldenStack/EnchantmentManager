package dev.goldenstack.enchantment;

import net.minestom.server.item.Enchantment;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static dev.goldenstack.enchantment.LevelProvider.*;

/**
 * <p>Represents extra data about an enchantment that is specific to the EnchantmentManager it is represented in.</p>
 * <p>Most people will want to use the default data or enchantability values from here, but you are always free to use
 * your own.</p>
 */
public record EnchantmentData(@NotNull Enchantment enchantment, int weight, @NotNull SlotType slotType,
                              @NotNull LevelProvider minLP, @NotNull LevelProvider maxLP, @NotNull Enchantment@NotNull... incompatible) {
    public int getMinimumLevel(int level){
        return this.minLP.getLevel(this, level);
    }
    public int getMaximumLevel(int level){
        return this.maxLP.getLevel(this, level);
    }

    /**
     * Tests if this EnchantmentData's enchantment collides in some way with the other object's enchantment. An
     * enchantment is considered to be colliding with another one's enchantment if:
     * <ul>
     *     <li>The enchantment is equal to the other object's enchantment</li>
     *     <li>The enchantment is contained on the other object's list of incompatible enchantments</li>
     *     <li>or the other object's enchantment is contained on this object's list of incompatible enchantments.</li>
     * </ul>
     * @param data The other EnchantmentData
     * @return If this object's enchantment collides with the other's enchantment
     */
    public boolean collidesWith(@NotNull EnchantmentData data){
        if (this.enchantment.equals(data.enchantment)){
            return true;
        }
        if (this.incompatible.length != 0){
            for (Enchantment enchantment : this.incompatible){
                if (enchantment.equals(data.enchantment)){
                    return true;
                }
            }
        }
        if (data.incompatible.length != 0){
            for (Enchantment enchantment : data.incompatible){
                if (enchantment.equals(this.enchantment)){
                    return true;
                }
            }
        }
        return false;
    }

    private static final @NotNull Map<Enchantment, EnchantmentData> DEFAULT_DATA = Map.ofEntries(
            Map.entry(Enchantment.FIRE_PROTECTION, new EnchantmentData(Enchantment.FIRE_PROTECTION, 5, SlotType::ARMOR, adjusted(10, 8), addToMin(8), Enchantment.PROTECTION, Enchantment.BLAST_PROTECTION, Enchantment.PROJECTILE_PROTECTION)),
            Map.entry(Enchantment.KNOCKBACK, new EnchantmentData(Enchantment.KNOCKBACK, 5, SlotType::WEAPON, adjusted(5, 20), addToDefault(50))),
            Map.entry(Enchantment.MENDING, new EnchantmentData(Enchantment.MENDING, 2, SlotType::BREAKABLE, multiply(25), addToMin(50))),
            Map.entry(Enchantment.POWER, new EnchantmentData(Enchantment.POWER, 10, SlotType::BOW, adjusted(1, 10), addToMin(15))),
            Map.entry(Enchantment.LUCK_OF_THE_SEA, new EnchantmentData(Enchantment.LUCK_OF_THE_SEA, 2, SlotType::FISHING_ROD, adjusted(15, 9), addToDefault(50))),
            Map.entry(Enchantment.THORNS, new EnchantmentData(Enchantment.THORNS, 1, SlotType::ARMOR, adjusted(10, 20), addToDefault(50))),
            Map.entry(Enchantment.SWEEPING, new EnchantmentData(Enchantment.SWEEPING, 2, SlotType::WEAPON, adjusted(5, 9), addToMin(15))),
            Map.entry(Enchantment.FEATHER_FALLING, new EnchantmentData(Enchantment.FEATHER_FALLING, 5, SlotType::ARMOR_FEET, adjusted(5, 6), addToMin(6))),
            Map.entry(Enchantment.FLAME, new EnchantmentData(Enchantment.FLAME, 2, SlotType::BOW, multiply(20), constant(50))),
            Map.entry(Enchantment.BINDING_CURSE, new EnchantmentData(Enchantment.BINDING_CURSE, 1, SlotType::ARMOR, multiply(25), constant(50))),
            Map.entry(Enchantment.AQUA_AFFINITY, new EnchantmentData(Enchantment.AQUA_AFFINITY, 2, SlotType::ARMOR_HELMET, multiply(1), addToMin(40))),
            Map.entry(Enchantment.PROJECTILE_PROTECTION, new EnchantmentData(Enchantment.PROJECTILE_PROTECTION, 5, SlotType::ARMOR, adjusted(3, 6), addToMin(6), Enchantment.PROTECTION, Enchantment.BLAST_PROTECTION, Enchantment.FIRE_PROTECTION)),
            Map.entry(Enchantment.SMITE, new EnchantmentData(Enchantment.SMITE, 5, SlotType::WEAPON, adjusted(5, 8), addToMin(20), Enchantment.SHARPNESS, Enchantment.BANE_OF_ARTHROPODS)),
            Map.entry(Enchantment.FROST_WALKER, new EnchantmentData(Enchantment.FROST_WALKER, 2, SlotType::ARMOR_FEET, multiply(10), addToMin(15), Enchantment.DEPTH_STRIDER)),
            Map.entry(Enchantment.VANISHING_CURSE, new EnchantmentData(Enchantment.VANISHING_CURSE, 1, SlotType::ALL, multiply(25), constant(50))),
            Map.entry(Enchantment.PUNCH, new EnchantmentData(Enchantment.PUNCH, 2, SlotType::BOW, adjusted(12, 20), addToMin(25))),
            Map.entry(Enchantment.BLAST_PROTECTION, new EnchantmentData(Enchantment.BLAST_PROTECTION, 2, SlotType::ARMOR, adjusted(5, 8), addToMin(8), Enchantment.PROTECTION, Enchantment.FIRE_PROTECTION, Enchantment.PROJECTILE_PROTECTION)),
            Map.entry(Enchantment.IMPALING, new EnchantmentData(Enchantment.IMPALING, 2, SlotType::TRIDENT, adjusted(1, 8), addToMin(20))),
            Map.entry(Enchantment.BANE_OF_ARTHROPODS, new EnchantmentData(Enchantment.BANE_OF_ARTHROPODS, 5, SlotType::WEAPON, adjusted(5, 8), addToMin(20), Enchantment.SHARPNESS, Enchantment.SMITE)),
            Map.entry(Enchantment.SHARPNESS, new EnchantmentData(Enchantment.SHARPNESS, 10, SlotType::WEAPON, adjusted(1, 11), addToMin(20), Enchantment.SMITE, Enchantment.BANE_OF_ARTHROPODS)),
            Map.entry(Enchantment.EFFICIENCY, new EnchantmentData(Enchantment.EFFICIENCY, 10, SlotType::TOOL, adjusted(1, 10), addToDefault(50))),
            Map.entry(Enchantment.SILK_TOUCH, new EnchantmentData(Enchantment.SILK_TOUCH, 1, SlotType::TOOL, multiply(15), addToDefault(50), Enchantment.FORTUNE)),
            Map.entry(Enchantment.LOOTING, new EnchantmentData(Enchantment.LOOTING, 2, SlotType::WEAPON, adjusted(15, 9), addToDefault(50))),
            Map.entry(Enchantment.LURE, new EnchantmentData(Enchantment.LURE, 2, SlotType::FISHING_ROD, adjusted(15, 9), addToDefault(50))),
            Map.entry(Enchantment.DEPTH_STRIDER, new EnchantmentData(Enchantment.DEPTH_STRIDER, 2, SlotType::ARMOR_FEET, multiply(10), addToMin(15), Enchantment.FROST_WALKER)),
            Map.entry(Enchantment.SOUL_SPEED, new EnchantmentData(Enchantment.SOUL_SPEED, 1, SlotType::ARMOR_FEET, multiply(10), addToMin(15))),
            Map.entry(Enchantment.RESPIRATION, new EnchantmentData(Enchantment.RESPIRATION, 2, SlotType::ARMOR_HELMET, multiply(10), addToMin(30))),
            Map.entry(Enchantment.FIRE_ASPECT, new EnchantmentData(Enchantment.FIRE_ASPECT, 2, SlotType::WEAPON, adjusted(10, 20), addToDefault(50))),
            Map.entry(Enchantment.PIERCING, new EnchantmentData(Enchantment.PIERCING, 10, SlotType::CROSSBOW, adjusted(1, 10), constant(50), Enchantment.MULTISHOT)),
            Map.entry(Enchantment.LOYALTY, new EnchantmentData(Enchantment.LOYALTY, 5, SlotType::TRIDENT, basic(5, 7), constant(50))),
            Map.entry(Enchantment.UNBREAKING, new EnchantmentData(Enchantment.UNBREAKING, 5, SlotType::BREAKABLE, adjusted(5, 8), addToDefault(50))),
            Map.entry(Enchantment.RIPTIDE, new EnchantmentData(Enchantment.RIPTIDE, 2, SlotType::TRIDENT, basic(10, 7), constant(50), Enchantment.LOYALTY, Enchantment.CHANNELING)),
            Map.entry(Enchantment.QUICK_CHARGE, new EnchantmentData(Enchantment.QUICK_CHARGE, 5, SlotType::CROSSBOW, adjusted(12, 20), constant(50))),
            Map.entry(Enchantment.PROTECTION, new EnchantmentData(Enchantment.PROTECTION, 10, SlotType::ARMOR, adjusted(1, 11), addToMin(11), Enchantment.BLAST_PROTECTION, Enchantment.FIRE_PROTECTION, Enchantment.PROJECTILE_PROTECTION)),
            Map.entry(Enchantment.INFINITY, new EnchantmentData(Enchantment.INFINITY, 1, SlotType::BOW, multiply(20), constant(50), Enchantment.MENDING)),
            Map.entry(Enchantment.FORTUNE, new EnchantmentData(Enchantment.FORTUNE, 2, SlotType::TOOL, adjusted(15, 9), addToDefault(50), Enchantment.SILK_TOUCH)),
            Map.entry(Enchantment.MULTISHOT, new EnchantmentData(Enchantment.MULTISHOT, 2, SlotType::CROSSBOW, multiply(20), constant(50), Enchantment.PIERCING)),
            Map.entry(Enchantment.CHANNELING, new EnchantmentData(Enchantment.CHANNELING, 1, SlotType::TRIDENT, multiply(25), constant(50)))
    );

    // IntelliJ helpfully informed me that not including these supposedly "redundant" type arguments could slow down
    // compilation and analysis!
    @SuppressWarnings("RedundantTypeArguments")
    private static final @NotNull Map<Material, Integer> DEFAULT_ENCHANTABILITY = Map.<Material, Integer>ofEntries(
            Map.entry(Material.TRIDENT, 1),
            Map.entry(Material.BOOK, 1),
            Map.entry(Material.FISHING_ROD, 1),
            Map.entry(Material.BOW, 1),
            Map.entry(Material.CROSSBOW, 1),
            Map.entry(Material.LEATHER_HELMET, 15),
            Map.entry(Material.LEATHER_CHESTPLATE, 15),
            Map.entry(Material.LEATHER_LEGGINGS, 15),
            Map.entry(Material.LEATHER_BOOTS, 15),
            Map.entry(Material.CHAINMAIL_HELMET, 12),
            Map.entry(Material.CHAINMAIL_CHESTPLATE, 12),
            Map.entry(Material.CHAINMAIL_LEGGINGS, 12),
            Map.entry(Material.CHAINMAIL_BOOTS, 12),
            Map.entry(Material.IRON_HELMET, 9),
            Map.entry(Material.IRON_CHESTPLATE, 9),
            Map.entry(Material.IRON_LEGGINGS, 9),
            Map.entry(Material.IRON_BOOTS, 9),
            Map.entry(Material.GOLDEN_HELMET, 25),
            Map.entry(Material.GOLDEN_CHESTPLATE, 25),
            Map.entry(Material.GOLDEN_LEGGINGS, 25),
            Map.entry(Material.GOLDEN_BOOTS, 25),
            Map.entry(Material.DIAMOND_HELMET, 10),
            Map.entry(Material.DIAMOND_CHESTPLATE, 10),
            Map.entry(Material.DIAMOND_LEGGINGS, 10),
            Map.entry(Material.DIAMOND_BOOTS, 10),
            Map.entry(Material.TURTLE_HELMET, 9),
            Map.entry(Material.NETHERITE_HELMET, 15),
            Map.entry(Material.NETHERITE_CHESTPLATE, 15),
            Map.entry(Material.NETHERITE_LEGGINGS, 15),
            Map.entry(Material.NETHERITE_BOOTS, 15),
            Map.entry(Material.WOODEN_SWORD, 15),
            Map.entry(Material.WOODEN_PICKAXE, 15),
            Map.entry(Material.WOODEN_AXE, 15),
            Map.entry(Material.WOODEN_SHOVEL, 15),
            Map.entry(Material.WOODEN_HOE, 15),
            Map.entry(Material.STONE_SWORD, 5),
            Map.entry(Material.STONE_PICKAXE, 5),
            Map.entry(Material.STONE_AXE, 5),
            Map.entry(Material.STONE_SHOVEL, 5),
            Map.entry(Material.STONE_HOE, 5),
            Map.entry(Material.IRON_SWORD, 14),
            Map.entry(Material.IRON_PICKAXE, 14),
            Map.entry(Material.IRON_AXE, 14),
            Map.entry(Material.IRON_SHOVEL, 14),
            Map.entry(Material.IRON_HOE, 14),
            Map.entry(Material.DIAMOND_SWORD, 10),
            Map.entry(Material.DIAMOND_PICKAXE, 10),
            Map.entry(Material.DIAMOND_AXE, 10),
            Map.entry(Material.DIAMOND_SHOVEL, 10),
            Map.entry(Material.DIAMOND_HOE, 10),
            Map.entry(Material.NETHERITE_SWORD, 15),
            Map.entry(Material.NETHERITE_PICKAXE, 15),
            Map.entry(Material.NETHERITE_AXE, 15),
            Map.entry(Material.NETHERITE_SHOVEL, 15),
            Map.entry(Material.NETHERITE_HOE, 15)
    );

    public static @NotNull Map<Enchantment, EnchantmentData> getDefaultData(){
        return DEFAULT_DATA;
    }

    public static @NotNull Map<Material, Integer> getDefaultEnchantability(){
        return DEFAULT_ENCHANTABILITY;
    }
}