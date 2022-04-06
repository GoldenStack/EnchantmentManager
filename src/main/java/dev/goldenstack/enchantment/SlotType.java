package dev.goldenstack.enchantment;

import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the slot type of an enchantment.<br>
 * Every slot type in default Minecraft is defined here, but you are free to use your own!
 */
public interface SlotType {

    /**
     * @return true if the SlotType can accept the provided ItemStack, otherwise false
     */
    boolean canEnchant(ItemStack itemStack);

    /**
     * @return true if the provided ItemStack's material is an armor piece
     */
    static boolean ARMOR(@NotNull ItemStack itemStack) {
        return itemStack.getMaterial().isArmor();
    }

    /**
     * @return true if the provided ItemStack's material can be worn in the player's helmet slot
     */
    static boolean ARMOR_HELMET(@NotNull ItemStack itemStack) {
        return itemStack.getMaterial().registry().equipmentSlot() == EquipmentSlot.HELMET;
    }

    /**
     * @return true if the provided ItemStack's material can be worn in the player's chestplate slot
     */
    static boolean ARMOR_CHESTPLATE(@NotNull ItemStack itemStack) {
        return itemStack.getMaterial().registry().equipmentSlot() == EquipmentSlot.CHESTPLATE;
    }

    /**
     * @return true if the provided ItemStack's material can be worn in the player's leggings slot
     */
    static boolean ARMOR_LEGGINGS(@NotNull ItemStack itemStack) {
        return itemStack.getMaterial().registry().equipmentSlot() == EquipmentSlot.LEGGINGS;
    }

    /**
     * @return true if the provided ItemStack's material can be worn in the player's boots slot
     */
    static boolean ARMOR_FEET(@NotNull ItemStack itemStack) {
        return itemStack.getMaterial().registry().equipmentSlot() == EquipmentSlot.BOOTS;
    }

    /**
     * @return true if the provided ItemStack's material is a sword. Yes, axes are weapons too, but this is an
     * enchantment manager, not an anvil manager.
     */
    static boolean WEAPON(@NotNull ItemStack itemStack) {
        Material material = itemStack.getMaterial();
        return material == Material.WOODEN_SWORD || material == Material.STONE_SWORD || material == Material.IRON_SWORD ||
                material == Material.GOLDEN_SWORD || material == Material.DIAMOND_SWORD || material == Material.NETHERITE_SWORD;
    }

    /**
     * @return true if the provided ItemStack's material is any type of tool (pickaxe, axe, shovel, or hoe)
     */
    static boolean TOOL(@NotNull ItemStack itemStack) {
        Material material = itemStack.getMaterial();
        return material == Material.WOODEN_PICKAXE || material == Material.WOODEN_AXE || material == Material.WOODEN_SHOVEL || material == Material.WOODEN_HOE ||
                material == Material.STONE_PICKAXE || material == Material.STONE_AXE || material == Material.STONE_SHOVEL || material == Material.STONE_HOE ||
                material == Material.IRON_PICKAXE || material == Material.IRON_AXE || material == Material.IRON_SHOVEL || material == Material.IRON_HOE ||
                material == Material.GOLDEN_PICKAXE || material == Material.GOLDEN_AXE || material == Material.GOLDEN_SHOVEL || material == Material.GOLDEN_HOE ||
                material == Material.DIAMOND_PICKAXE || material == Material.DIAMOND_AXE || material == Material.DIAMOND_SHOVEL || material == Material.DIAMOND_HOE ||
                material == Material.NETHERITE_PICKAXE || material == Material.NETHERITE_AXE || material == Material.NETHERITE_SHOVEL || material == Material.NETHERITE_HOE;
    }

    /**
     * @return true if the provided ItemStack's material is {@link Material#FISHING_ROD}
     */
    static boolean FISHING_ROD(@NotNull ItemStack itemStack) {
        return itemStack.getMaterial() == Material.FISHING_ROD;
    }

    /**
     * @return true if the provided ItemStack's material is {@link Material#TRIDENT}
     */
    static boolean TRIDENT(@NotNull ItemStack itemStack) {
        return itemStack.getMaterial() == Material.TRIDENT;
    }

    /**
     * @return true if the provided ItemStack's material is {@link Material#BOW}
     */
    static boolean BOW(@NotNull ItemStack itemStack) {
        return itemStack.getMaterial() == Material.BOW;
    }

    /**
     * @return true if the provided ItemStack's material is {@link Material#CROSSBOW}
     */
    static boolean CROSSBOW(@NotNull ItemStack itemStack) {
        return itemStack.getMaterial() == Material.CROSSBOW;
    }


    /**
     * @return true if the provided ItemStack's material is armor or is one of the following materials:
     * <ul>
     *     <li>{@link Material#CARVED_PUMPKIN}</li>
     *     <li>{@link Material#ELYTRA}</li>
     *     <li>{@link Material#PLAYER_HEAD}</li>
     *     <li>{@link Material#ZOMBIE_HEAD}</li>
     *     <li>{@link Material#SKELETON_SKULL}</li>
     *     <li>{@link Material#WITHER_SKELETON_SKULL}</li>
     *     <li>{@link Material#CREEPER_HEAD}</li>
     *     <li>{@link Material#DRAGON_HEAD}</li>
     * </ul>
     */
    static boolean WEARABLE(@NotNull ItemStack itemStack) {
        Material material = itemStack.getMaterial();
        return material.isArmor() || material == Material.CARVED_PUMPKIN || material == Material.ELYTRA ||
                material == Material.PLAYER_HEAD || material == Material.ZOMBIE_HEAD || material == Material.SKELETON_SKULL ||
                material == Material.WITHER_SKELETON_SKULL || material == Material.CREEPER_HEAD || material == Material.DRAGON_HEAD;
    }

    /**
     * @return true if the provided ItemStack's material has durability
     */
    static boolean BREAKABLE(@NotNull ItemStack itemStack) {
        return itemStack.getMaterial().registry().maxDamage() != 0;
    }

    /**
     * @return true if the ItemStack's material is breakable or wearable
     */
    static boolean ALL(@NotNull ItemStack itemStack) {
        return BREAKABLE(itemStack) || WEARABLE(itemStack);
    }

    // Vanilla MC has another type named "VANISHABLE" which is equivalent to BREAKABLE and so it's unnecessary to implement here
}
