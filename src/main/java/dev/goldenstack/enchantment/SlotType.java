package dev.goldenstack.enchantment;

import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

public interface SlotType {
    boolean canEnchant(ItemStack itemStack);

    static boolean ARMOR(@NotNull ItemStack itemStack){
        return itemStack.getMaterial().isArmor();
    }

    static boolean ARMOR_HELMET(@NotNull ItemStack itemStack){
        return itemStack.getMaterial().registry().equipmentSlot() == EquipmentSlot.HELMET;
    }

    static boolean ARMOR_CHESTPLATE(@NotNull ItemStack itemStack){
        return itemStack.getMaterial().registry().equipmentSlot() == EquipmentSlot.CHESTPLATE;
    }

    static boolean ARMOR_LEGGINGS(@NotNull ItemStack itemStack){
        return itemStack.getMaterial().registry().equipmentSlot() == EquipmentSlot.LEGGINGS;
    }

    static boolean ARMOR_FEET(@NotNull ItemStack itemStack){
        return itemStack.getMaterial().registry().equipmentSlot() == EquipmentSlot.BOOTS;
    }

    static boolean WEAPON(@NotNull ItemStack itemStack){
        Material material = itemStack.getMaterial();
        return material == Material.WOODEN_SWORD || material == Material.STONE_SWORD || material == Material.IRON_SWORD ||
               material == Material.GOLDEN_SWORD || material == Material.DIAMOND_SWORD || material == Material.NETHERITE_SWORD;
    }

    static boolean TOOL(@NotNull ItemStack itemStack){
        Material material = itemStack.getMaterial();
        return  material == Material.WOODEN_PICKAXE || material == Material.WOODEN_AXE || material == Material.WOODEN_SHOVEL || material == Material.WOODEN_HOE ||
                material == Material.STONE_PICKAXE || material == Material.STONE_AXE || material == Material.STONE_SHOVEL || material == Material.STONE_HOE ||
                material == Material.IRON_PICKAXE || material == Material.IRON_AXE || material == Material.IRON_SHOVEL || material == Material.IRON_HOE ||
                material == Material.GOLDEN_PICKAXE || material == Material.GOLDEN_AXE || material == Material.GOLDEN_SHOVEL || material == Material.GOLDEN_HOE ||
                material == Material.DIAMOND_PICKAXE || material == Material.DIAMOND_AXE || material == Material.DIAMOND_SHOVEL || material == Material.DIAMOND_HOE ||
                material == Material.NETHERITE_PICKAXE || material == Material.NETHERITE_AXE || material == Material.NETHERITE_SHOVEL || material == Material.NETHERITE_HOE;
    }

    static boolean FISHING_ROD(@NotNull ItemStack itemStack){
        return itemStack.getMaterial() == Material.FISHING_ROD;
    }

    static boolean TRIDENT(@NotNull ItemStack itemStack){
        return itemStack.getMaterial() == Material.TRIDENT;
    }

    static boolean BREAKABLE(@NotNull ItemStack itemStack){
        return itemStack.getMaterial().registry().maxDamage() != 0;
    }

    static boolean BOW(@NotNull ItemStack itemStack){
        return itemStack.getMaterial() == Material.BOW;
    }

    static boolean WEARABLE(@NotNull ItemStack itemStack){
        Material material = itemStack.getMaterial();
        return material.isArmor() || material == Material.CARVED_PUMPKIN || material == Material.ELYTRA ||
                material == Material.PLAYER_HEAD || material == Material.ZOMBIE_HEAD || material == Material.SKELETON_SKULL ||
                material == Material.WITHER_SKELETON_SKULL || material == Material.CREEPER_HEAD || material == Material.DRAGON_HEAD;
    }

    static boolean CROSSBOW(@NotNull ItemStack itemStack){
        return itemStack.getMaterial() == Material.CROSSBOW;
    }

    static boolean ALL(@NotNull ItemStack itemStack){
        return BREAKABLE(itemStack) || WEARABLE(itemStack);
    }

    // Default MC has another type named "VANISHABLE" which literally just calls BREAKABLE, but that's unnecessary here
}
