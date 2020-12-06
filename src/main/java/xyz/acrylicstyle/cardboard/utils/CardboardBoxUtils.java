package xyz.acrylicstyle.cardboard.utils;

import net.minecraft.server.v1_17_R0.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_17_R0.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.acrylicstyle.cardboard.CardboardBoxPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CardboardBoxUtils {
    public static boolean isNotCardboardBox(ItemStack item) {
        if (item.getType() != CardboardBoxPlugin.BLOCK) return true;
        return !CraftItemStack.asNMSCopy(item).getOrCreateTag().hasKey("cardboardData");
    }

    public static CardboardBox getCardboardBox(ItemStack itemStack) {
        if (isNotCardboardBox(itemStack)) throw new IllegalArgumentException("This item isn't cardboard box!");
        return new CardboardBox(itemStack);
    }

    public static ItemStack updateCardboardBox(ItemStack itemStack) {
        if (isNotCardboardBox(itemStack)) return itemStack;
        CardboardBox cardboardBox = getCardboardBox(itemStack);
        String type = cardboardBox.getType().name();
        boolean empty = cardboardBox.getTag().isEmpty();
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
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
        net.minecraft.server.v1_17_R0.ItemStack util = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = util.getOrCreateTag();
        tag.setString("cardboardUUID", UUID.randomUUID().toString());
        util.setTag(tag);
        return CraftItemStack.asBukkitCopy(util);
    }
}
