package xyz.acrylicstyle.cardboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import util.reflect.Ref;
import xyz.acrylicstyle.cardboard.utils.CardboardBox;
import xyz.acrylicstyle.cardboard.utils.CardboardBoxUtils;
import xyz.acrylicstyle.paper.Paper;
import xyz.acrylicstyle.paper.block.TileEntity;
import xyz.acrylicstyle.paper.inventory.ItemStackUtils;
import xyz.acrylicstyle.paper.nbt.NBTTagCompound;
import xyz.acrylicstyle.tomeito_api.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CardboardBoxPlugin extends JavaPlugin implements Listener {
    public static final Material BLOCK = Material.HONEYCOMB_BLOCK;
    public static final List<Material> disallowedMaterials = new ArrayList<>();
    public static final List<UUID> cooltime = new ArrayList<>();

    static {
        // disallowed blocks
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
        // these blocks will causes chunk/block corruption and server crash
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
        ItemStack result = new ItemStack(BLOCK);
        ItemMeta meta = result.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "段ボール箱");
        result.setItemMeta(meta);
        ItemStackUtils util = Paper.itemStack(result);
        NBTTagCompound tag = util.getOrCreateTag();
        tag.set("cardboardData", new NBTTagCompound());
        util.setTag(tag);
        result = util.getItemStack();
        CardboardBoxUtils.updateCardboardBox(result);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "cardboardBox"), result);
        recipe.shape("XXX", "X X", "XXX");
        recipe.setIngredient('X', Material.CHEST);
        Bukkit.addRecipe(recipe);
    }

    @Override
    public void onDisable() {
        Bukkit.removeRecipe(new NamespacedKey(this, "cardboardBox"));
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent e) {
        ItemStack item = e.getInventory().getResult();
        if (item == null || !CardboardBoxUtils.isCardboardBox(item)) return;
        e.getInventory().setResult(CardboardBoxUtils.updateCardboardBox(item));
    }

    @EventHandler
    public void onBlockMultiPlace(BlockMultiPlaceEvent e) {
        //e.getPlayer().sendMessage(ChatColor.DARK_GRAY + "BlockMultiPlaceEvent");
        if (e.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if (!CardboardBoxUtils.isCardboardBox(item)) return;
        e.setCancelled(true);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if (!CardboardBoxUtils.isCardboardBox(item)) return;
        e.setCancelled(true);
        CardboardBox cardboardBox = CardboardBoxUtils.getCardboardBox(item);
        if (cooltime.contains(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendActionBar(ChatColor.RED + "現在クールタイム中です。");
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
                e.getPlayer().sendActionBar(ChatColor.RED + "このブロックは回収できません。");
                return;
            }
            TileEntity te = e.getBlockPlaced().getWorld().getTileEntity(l.getLocation());
            NBTTagCompound tag = new NBTTagCompound();
            if (te != null) {
                Object nms = Ref.getClass(te.getClass()).getMethod("getHandle").invokeObj(te);
                Object nmsTag = Ref.forName("xyz.acrylicstyle.paper.nbt.CraftNBT")
                        .getMethod("asNMSCompound", NBTTagCompound.class)
                        .invoke(null, tag);
                Ref.getClass(nms.getClass()).getMethod("save", xyz.acrylicstyle.minecraft.NBTTagCompound.CLASS).invokeObj(nms, nmsTag);
                tag = (NBTTagCompound) Ref.forName("xyz.acrylicstyle.paper.nbt.CraftNBT")
                        .getMethod("asBukkitCompound", xyz.acrylicstyle.minecraft.NBTTagCompound.CLASS)
                        .invoke(null, nmsTag);
                te.save(tag);
            }
            cardboardBox.store(e.getBlockAgainst().getType(), tag);
            e.getPlayer().getInventory().setItemInMainHand(cardboardBox.getItemStack());
            Log.debug("Removing TileEntity at " + l.getX() + "," + l.getY() + "," + l.getZ());
            e.getBlockPlaced().getWorld().removeTileEntity(l.getLocation());
            if (l.getState() instanceof Container) {
                l.setType(Material.AIR, false);
                Bukkit.getOnlinePlayers().forEach(p -> p.sendBlockChange(l.getLocation(), Material.AIR, (byte) 0));
            } else l.setType(Material.AIR);
            e.getPlayer().sendActionBar(ChatColor.GREEN + "段ボール箱の中にブロックを入れました。");
        } else {
            Block l = e.getBlockPlaced();
            l.getWorld().removeTileEntity(l.getLocation());
            new BukkitRunnable() {
                @Override
                public void run() {
                    l.setType(cardboardBox.getType());
                    TileEntity te = l.getWorld().getTileEntity(l.getLocation());
                    NBTTagCompound tag = (NBTTagCompound) cardboardBox.getTag().clone();
                    if (te != null) {
                        tag.setInt("x", l.getX());
                        tag.setInt("y", l.getY());
                        tag.setInt("z", l.getZ());
                        te.load(tag);
                        te.update();
                    }
                    cardboardBox.store(null, null);
                    e.getPlayer().getInventory().setItemInMainHand(cardboardBox.getItemStack());
                    e.getPlayer().sendActionBar(ChatColor.GREEN + "段ボール箱の中からブロックを出しました。");
                }
            }.runTaskLater(this, 1);
        }
    }
}
