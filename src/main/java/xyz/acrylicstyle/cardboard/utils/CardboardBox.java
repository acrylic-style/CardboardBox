package xyz.acrylicstyle.cardboard.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.paper.Paper;
import xyz.acrylicstyle.paper.inventory.ItemStackUtils;
import xyz.acrylicstyle.paper.nbt.NBTTagCompound;

public class CardboardBox {
    @NotNull
    private final ItemStackUtils handle;
    @NotNull
    private NBTTagCompound tag;
    @NotNull
    private Material material;

    public CardboardBox(@NotNull ItemStack itemStack) {
        this(itemStack,
                Material.getMaterial(Paper.itemStack(itemStack).getOrCreateTag().getString("cardboardMaterial")),
                Paper.itemStack(itemStack).getOrCreateTag().getCompound("cardboardData"));
    }

    public CardboardBox(@NotNull ItemStack itemStack, @Nullable Material material, @NotNull NBTTagCompound tag) {
        if (material == null) material = Material.AIR;
        this.handle = Paper.itemStack(itemStack);
        this.material = material;
        this.tag = tag;
        NBTTagCompound nbt = handle.getOrCreateTag();
        nbt.set("cardboardData", tag);
        nbt.setString("cardboardMaterial", material.name());
        handle.setTag(nbt);
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
        return CardboardBoxUtils.updateCardboardBox(handle.getItemStack());
    }

    public boolean hasData() {
        return getType() != Material.AIR;
    }
}
