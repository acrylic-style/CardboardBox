package xyz.acrylicstyle.cardboard.utils;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.cardboard.CardboardBoxPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CardboardBoxUtils {
    public static @Nullable CompoundTag getCustomData(@NotNull ItemStack item) {
        net.minecraft.world.item.ItemStack minecraftItem = CraftItemStack.asNMSCopy(item);
        CustomData customData = minecraftItem.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return null;
        return customData.copyTag();
    }

    public static @NotNull CompoundTag getCustomDataOrThrow(@NotNull ItemStack item) {
        CompoundTag tag = getCustomData(item);
        if (tag == null) throw new IllegalArgumentException("This item doesn't have custom data!");
        return tag;
    }

    public static @NotNull ItemStack setCustomData(@NotNull ItemStack item, @Nullable CompoundTag tag) {
        net.minecraft.world.item.ItemStack minecraftItem = CraftItemStack.asNMSCopy(item);
        minecraftItem.set(DataComponents.CUSTOM_DATA, tag == null ? CustomData.EMPTY : CustomData.of(tag));
        return CraftItemStack.asBukkitCopy(minecraftItem);
    }

    public static boolean isNotCardboardBox(ItemStack item) {
        if (item.getType() != CardboardBoxPlugin.BLOCK) return true;
        CompoundTag tag = getCustomData(item);
        if (tag == null) return true;
        return !tag.contains("cardboardData");
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
            meta.addEnchant(Enchantment.UNBREAKING, 0, true);
        } else {
            meta.removeEnchant(Enchantment.UNBREAKING);
        }
        itemStack.setItemMeta(meta);
        CompoundTag tag = getCustomData(itemStack);
        if (tag == null) tag = new CompoundTag();
        tag.putString("cardboardUUID", UUID.randomUUID().toString());
        return setCustomData(itemStack, tag);
    }
}
