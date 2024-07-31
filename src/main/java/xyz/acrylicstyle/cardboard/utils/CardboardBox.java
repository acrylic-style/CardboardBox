package xyz.acrylicstyle.cardboard.utils;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class CardboardBox {
    private final net.minecraft.world.item.ItemStack handle;
    private CompoundTag tag;
    private Material material;

    public CardboardBox(ItemStack itemStack) {
        this(itemStack,
                Material.getMaterial(CraftItemStack.asNMSCopy(itemStack).getOrCreateTag().getString("cardboardMaterial")),
                CraftItemStack.asNMSCopy(itemStack).getOrCreateTag().getCompound("cardboardData"));
    }

    public CardboardBox(ItemStack itemStack, Material material, CompoundTag tag) {
        if (material == null) material = Material.AIR;
        this.handle = CraftItemStack.asNMSCopy(itemStack);
        this.material = material;
        this.tag = tag;
        CompoundTag nbt = handle.getOrCreateTag();
        nbt.put("cardboardData", tag);
        nbt.putString("cardboardMaterial", material.name());
        handle.setTag(nbt);
    }

    public CompoundTag getTag() {
        return tag;
    }

    public void setTag(CompoundTag tag) {
        if (tag == null) tag = new CompoundTag();
        CompoundTag nbt = handle.getOrCreateTag();
        nbt.put("cardboardData", tag);
        handle.setTag(nbt);
        this.tag = tag;
    }

    public Material getType() {
        return material;
    }

    public void setType(Material material) {
        if (material == null) material = Material.AIR;
        CompoundTag nbt = handle.getOrCreateTag();
        nbt.putString("cardboardMaterial", material.name());
        handle.setTag(nbt);
        this.material = material;
    }

    public void store(Material material, CompoundTag tag) {
        this.setTag(tag);
        this.setType(material);
    }

    public ItemStack getItemStack() {
        return CardboardBoxUtils.updateCardboardBox(CraftItemStack.asBukkitCopy(handle));
    }

    public boolean hasData() {
        return getType() != Material.AIR;
    }
}
