package xyz.acrylicstyle.cardboard.utils;

import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CardboardBox {
    @NotNull
    private final net.minecraft.server.v1_15_R1.ItemStack handle;
    @NotNull
    private NBTTagCompound tag;
    @NotNull
    private Material material;

    public CardboardBox(@NotNull ItemStack itemStack) {
        this(itemStack,
                Material.getMaterial(CraftItemStack.asNMSCopy(itemStack).getOrCreateTag().getString("cardboardMaterial")),
                CraftItemStack.asNMSCopy(itemStack).getOrCreateTag().getCompound("cardboardData"));
    }

    public CardboardBox(@NotNull ItemStack itemStack, @Nullable Material material, @NotNull NBTTagCompound tag) {
        if (material == null) material = Material.AIR;
        this.handle = CraftItemStack.asNMSCopy(itemStack);
        this.material = material;
        this.tag = tag;
        NBTTagCompound nbt = handle.getOrCreateTag();
        nbt.set("cardboardData", tag);
        nbt.setString("cardboardMaterial", material.name());
        handle.setTag(nbt);
    }

    @NotNull
    public net.minecraft.server.v1_15_R1.ItemStack getHandle() {
        return handle;
    }

    @NotNull
    public NBTTagCompound getTag() {
        return tag;
    }

    public void setTag(@Nullable NBTTagCompound tag) {
        if (tag == null) tag = new NBTTagCompound();
        NBTTagCompound nbt = handle.getOrCreateTag();
        nbt.set("cardboardData", tag);
        handle.setTag(nbt);
        this.tag = tag;
    }

    @NotNull
    public Material getType() {
        return material;
    }

    public void setType(@Nullable Material material) {
        if (material == null) material = Material.AIR;
        NBTTagCompound nbt = handle.getOrCreateTag();
        nbt.setString("cardboardMaterial", material.name());
        handle.setTag(nbt);
        this.material = material;
    }

    public void store(@Nullable Material material, @Nullable NBTTagCompound tag) {
        this.setTag(tag);
        this.setType(material);
    }

    public ItemStack getItemStack() {
        ItemStack item = CraftItemStack.asBukkitCopy(handle);
        return CardboardBoxUtils.updateCardboardBox(item);
    }

    public boolean hasData() {
        return getType() != Material.AIR;
    }
}
