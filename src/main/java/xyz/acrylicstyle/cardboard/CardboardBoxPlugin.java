package xyz.acrylicstyle.cardboard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.acrylicstyle.cardboard.commands.CardboardBoxCommand;
import xyz.acrylicstyle.cardboard.utils.CardboardBox;
import xyz.acrylicstyle.cardboard.utils.CardboardBoxUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CardboardBoxPlugin extends JavaPlugin implements Listener {
    public static final Material BLOCK = Material.HONEYCOMB_BLOCK;
    public static final List<Material> disallowedMaterials = new ArrayList<>();
    public static final List<UUID> cooltime = new ArrayList<>();

    static {
        // disallowed blocks (creative items, indestructible items etc.)
        disallowedMaterials.add(Material.BEDROCK);
        disallowedMaterials.add(Material.COMMAND_BLOCK);
        disallowedMaterials.add(Material.CHAIN_COMMAND_BLOCK);
        disallowedMaterials.add(Material.REPEATING_COMMAND_BLOCK);
        disallowedMaterials.add(Material.END_PORTAL);
        disallowedMaterials.add(Material.END_PORTAL_FRAME);
        disallowedMaterials.add(Material.END_GATEWAY);
        disallowedMaterials.add(Material.NETHER_PORTAL);
        disallowedMaterials.add(Material.JIGSAW);
        disallowedMaterials.add(Material.BARRIER);
        disallowedMaterials.add(Material.STRUCTURE_BLOCK);
        disallowedMaterials.add(Material.STRUCTURE_VOID);

        // bugged blocks
        // these blocks will cause chunk/block corruption and the server crashes when the server tries to load the affected chunk
        disallowedMaterials.add(Material.PISTON);
        disallowedMaterials.add(Material.PISTON_HEAD);
        disallowedMaterials.add(Material.MOVING_PISTON);
        disallowedMaterials.add(Material.STICKY_PISTON);

        // these blocks will just suck
        // doors
        disallowedMaterials.add(Material.DARK_OAK_DOOR);
        disallowedMaterials.add(Material.ACACIA_DOOR);
        disallowedMaterials.add(Material.BIRCH_DOOR);
        disallowedMaterials.add(Material.IRON_DOOR);
        disallowedMaterials.add(Material.OAK_DOOR);
        disallowedMaterials.add(Material.JUNGLE_DOOR);
        disallowedMaterials.add(Material.SPRUCE_DOOR);

        // beds
        disallowedMaterials.add(Material.BLACK_BED);
        disallowedMaterials.add(Material.BLUE_BED);
        disallowedMaterials.add(Material.BROWN_BED);
        disallowedMaterials.add(Material.CYAN_BED);
        disallowedMaterials.add(Material.GRAY_BED);
        disallowedMaterials.add(Material.GREEN_BED);
        disallowedMaterials.add(Material.LIGHT_BLUE_BED);
        disallowedMaterials.add(Material.LIGHT_GRAY_BED);
        disallowedMaterials.add(Material.LIME_BED);
        disallowedMaterials.add(Material.MAGENTA_BED);
        disallowedMaterials.add(Material.ORANGE_BED);
        disallowedMaterials.add(Material.PINK_BED);
        disallowedMaterials.add(Material.PURPLE_BED);
        disallowedMaterials.add(Material.RED_BED);
        disallowedMaterials.add(Material.WHITE_BED);
        disallowedMaterials.add(Material.YELLOW_BED);

        // other
        disallowedMaterials.add(Material.VINE);
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        // Register recipe
        // Recipe (where X are chest):
        // XXX
        // X X
        // XXX
        Objects.requireNonNull(getCommand("cardboardbox")).setExecutor(new CardboardBoxCommand());
        if (!getConfig().getBoolean("disable-recipe", false)) {
            ItemStack result = new ItemStack(BLOCK);
            ItemMeta meta = result.getItemMeta();
            assert meta != null;
            meta.displayName(Component.text("段ボール箱", NamedTextColor.LIGHT_PURPLE));
            result.setItemMeta(meta);
            net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(result);
            CompoundTag tag = nms.getOrCreateTag();
            tag.put("cardboardData", new CompoundTag());
            nms.setTag(tag);
            result = CraftItemStack.asBukkitCopy(nms);
            CardboardBoxUtils.updateCardboardBox(result);
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "cardboard_box"), result);
            recipe.shape("XXX", "X X", "XXX");
            recipe.setIngredient('X', Material.CHEST);
            Bukkit.addRecipe(recipe);
        }
    }

    @Override
    public void onDisable() {
        Bukkit.removeRecipe(new NamespacedKey(this, "cardboard_box"));
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent e) {
        ItemStack item = e.getInventory().getResult();
        if (item == null || CardboardBoxUtils.isNotCardboardBox(item)) return;
        e.getInventory().setResult(CardboardBoxUtils.updateCardboardBox(item));
    }

    @EventHandler
    public void onBlockMultiPlace(BlockMultiPlaceEvent e) {
        //e.getPlayer().sendMessage(ChatColor.DARK_GRAY + "BlockMultiPlaceEvent");
        if (e.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if (CardboardBoxUtils.isNotCardboardBox(item)) return;
        e.setCancelled(true);
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if (CardboardBoxUtils.isNotCardboardBox(item)) return;
        e.setCancelled(true);
        CardboardBox cardboardBox = CardboardBoxUtils.getCardboardBox(item);
        if (cooltime.contains(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendMessage(ChatColor.RED + "現在クールタイム中です。");
            return;
        }
        cooltime.add(e.getPlayer().getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                cooltime.remove(e.getPlayer().getUniqueId());
            }
        }.runTaskLater(this, 5);
        if (!cardboardBox.hasData()) {
            Block l = e.getBlockAgainst();
            if (disallowedMaterials.contains(l.getType())) {
                e.getPlayer().sendMessage(ChatColor.RED + "このブロックは回収できません。");
                return;
            }
            if (!new BlockBreakEvent(l, e.getPlayer()).callEvent()) {
                e.getPlayer().sendMessage(ChatColor.RED + "このブロックは回収できません。");
                return;
            }
            BlockEntity blockEntity = ((CraftWorld) e.getBlockPlaced().getWorld()).getHandle().getBlockEntity(blockPosition(l.getLocation()));
            CompoundTag tag = new CompoundTag();
            if (blockEntity != null) tag.merge(blockEntity.saveWithFullMetadata());
            cardboardBox.store(e.getBlockAgainst().getType(), tag);
            e.getPlayer().getInventory().setItemInMainHand(cardboardBox.getItemStack());
            getLogger().info("Removing TileEntity at " + l.getX() + "," + l.getY() + "," + l.getZ());
            ((CraftWorld) e.getBlockPlaced().getWorld()).getHandle().removeBlockEntity(blockPosition(l.getLocation()));
            if (l.getState() instanceof Container) {
                l.setType(Material.AIR, false);
                Bukkit.getOnlinePlayers().forEach(p -> p.sendBlockChange(l.getLocation(), Material.AIR, (byte) 0));
            } else l.setType(Material.AIR);
            e.getPlayer().sendMessage(ChatColor.GREEN + "段ボール箱の中にブロックを入れました。");
        } else {
            Block l = e.getBlockPlaced();
            ((CraftWorld) l.getWorld()).getHandle().removeBlockEntity(blockPosition(l.getLocation()));
            new BukkitRunnable() {
                @Override
                public void run() {
                    l.setType(cardboardBox.getType());
                    BlockEntity blockEntity = ((CraftWorld) l.getWorld()).getHandle().getBlockEntity(blockPosition(l.getLocation()));
                    CompoundTag tag = cardboardBox.getTag().copy();
                    if (blockEntity != null) {
                        tag.putInt("x", l.getX());
                        tag.putInt("y", l.getY());
                        tag.putInt("z", l.getZ());
                        blockEntity.load(tag);
                        blockEntity.setLevel(((CraftWorld) l.getWorld()).getHandle());
                        ((CraftWorld) l.getWorld()).getHandle().setBlockEntity(blockEntity);
                    } else {
                        getSLF4JLogger().warn("BlockEntity is null at {},{},{}", l.getX(), l.getY(), l.getZ());
                    }
                    cardboardBox.store(null, null);
                    e.getPlayer().getInventory().setItemInMainHand(cardboardBox.getItemStack());
                    e.getPlayer().sendMessage(ChatColor.GREEN + "段ボール箱の中からブロックを出しました。");
                }
            }.runTaskLater(this, 1);
        }
    }

    public static BlockPos blockPosition(Location location) {
        return new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
