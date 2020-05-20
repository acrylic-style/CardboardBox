package xyz.acrylicstyle.cardboard.utils;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.cardboard.CardboardBoxPlugin;
import xyz.acrylicstyle.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import xyz.acrylicstyle.minecraft.v1_15_R1.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CardboardBoxUtils {
    public static boolean isCardboardBox(@NotNull ItemStack item) {
        if (item.getType() != CardboardBoxPlugin.BLOCK) return false;
        return CraftItemStack.asNMSCopy(item).getOrCreateTag().hasKey("cardboardData");
    }

    @NotNull
    public static NBTTagCompound getCardboardBoxTag(@NotNull ItemStack item) {
        if (!isCardboardBox(item)) return new NBTTagCompound();
        return CraftItemStack.asNMSCopy(item).getOrCreateTag().getCompound("cardboardData");
    }

    public static CardboardBox getCardboardBox(@NotNull ItemStack itemStack) {
        if (!isCardboardBox(itemStack)) throw new IllegalArgumentException("This item isn't cardboard box!");
        return new CardboardBox(itemStack);
    }

    public static ItemStack updateCardboardBox(@NotNull ItemStack itemStack) {
        if (!isCardboardBox(itemStack)) return itemStack;
        CardboardBox cardboardBox = getCardboardBox(itemStack);
        String type = cardboardBox.getType().name();
        boolean empty = cardboardBox.getTag().isEmpty();
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "段ボール箱");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Type: " + type);
        lore.add(ChatColor.GRAY + "Has tag: " + (empty ? "No" : "Yes"));
        meta.setLore(lore);
        if (cardboardBox.hasData()) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0, true);
        } else {
            meta.removeEnchant(Enchantment.PROTECTION_ENVIRONMENTAL);
        }
        itemStack.setItemMeta(meta);
        xyz.acrylicstyle.minecraft.v1_15_R1.ItemStack handle = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = handle.getOrCreateTag();
        tag.setString("cardboardUUID", UUID.randomUUID().toString());
        handle.setTag(tag);
        return CraftItemStack.asBukkitCopy(handle);
    }
}
