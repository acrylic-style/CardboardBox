package xyz.acrylicstyle.cardboard.utils;

import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class CardboardBox {
    private final net.minecraft.server.v1_16_R3.ItemStack handle;
    private NBTTagCompound tag;
    private Material material;

    public CardboardBox(ItemStack itemStack) {
        this(itemStack,
                Material.getMaterial(CraftItemStack.asNMSCopy(itemStack).getOrCreateTag().getString("cardboardMaterial")),
                CraftItemStack.asNMSCopy(itemStack).getOrCreateTag().getCompound("cardboardData"));
    }

    public CardboardBox(ItemStack itemStack, Material material, NBTTagCompound tag) {
        if (material == null) material = Material.AIR;
        this.handle = CraftItemStack.asNMSCopy(itemStack);
        this.material = material;
        this.tag = tag;
        NBTTagCompound nbt = handle.getOrCreateTag();
        nbt.set("cardboardData", tag);
        nbt.setString("cardboardMaterial", material.name());
        handle.setTag(nbt);
    }

    public NBTTagCompound getTag() {
        return tag;
    }

    public void setTag(NBTTagCompound tag) {
        if (tag == null) tag = new NBTTagCompound();
        NBTTagCompound nbt = handle.getOrCreateTag();
        nbt.set("cardboardData", tag);
        handle.setTag(nbt);
        this.tag = tag;
    }

    public Material getType() {
        return material;
    }

    public void setType(Material material) {
        if (material == null) material = Material.AIR;
        NBTTagCompound nbt = handle.getOrCreateTag();
        nbt.setString("cardboardMaterial", material.name());
        handle.setTag(nbt);
        this.material = material;
    }

    public void store(Material material, NBTTagCompound tag) {
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
