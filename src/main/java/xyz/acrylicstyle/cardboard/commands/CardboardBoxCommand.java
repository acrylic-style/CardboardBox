package xyz.acrylicstyle.cardboard.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.cardboard.CardboardBoxPlugin;
import xyz.acrylicstyle.cardboard.utils.CardboardBoxUtils;

import java.util.List;

public class CardboardBoxCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /cardboardbox args...");
            return true;
        }
        if (args[0].equals("give")) {
            if (args.length < 2) {
                sender.sendMessage("Usage: /cardboardbox give <player>");
                return true;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage("Player not found.");
                return true;
            }
            ItemStack result = new ItemStack(CardboardBoxPlugin.BLOCK);
            ItemMeta meta = result.getItemMeta();
            assert meta != null;
            meta.displayName(Component.text("段ボール箱", NamedTextColor.LIGHT_PURPLE));
            result.setItemMeta(meta);
            net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(result);
            CompoundTag tag = nms.getOrCreateTag();
            tag.put("cardboardData", new CompoundTag());
            nms.setTag(tag);
            result = CraftItemStack.asBukkitCopy(nms);
            result = CardboardBoxUtils.updateCardboardBox(result);
            player.getInventory().addItem(result);
            sender.sendMessage("Gave " + player.getName() + " a cardboard box.");
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("give");
        }
        if (args.length == 2) {
            if (args[0].equals("give")) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            }
        }
        return List.of();
    }
}
