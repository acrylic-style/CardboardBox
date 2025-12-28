package xyz.acrylicstyle.cardboard.utils;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CardboardBox {
    private ItemStack handle;
    private CompoundTag tag;
    private Material material;

    public CardboardBox(@NotNull ItemStack itemStack) {
        this(itemStack,
                Material.getMaterial(CardboardBoxUtils.getCustomDataOrThrow(itemStack).getString("cardboardMaterial").orElse("AIR")),
                CardboardBoxUtils.getCustomDataOrThrow(itemStack).getCompound("cardboardData").orElseGet(CompoundTag::new));
    }

    public CardboardBox(@NotNull ItemStack itemStack, @Nullable Material material, @Nullable CompoundTag tag) {
        if (material == null) material = Material.AIR;
        this.material = material;
        this.tag = tag == null ? new CompoundTag() : tag;
        CompoundTag nbt = CardboardBoxUtils.getCustomData(itemStack);
        if (nbt == null) nbt = new CompoundTag();
        nbt.put("cardboardData", this.tag);
        nbt.putString("cardboardMaterial", material.name());
        this.handle = CardboardBoxUtils.setCustomData(itemStack, nbt);
    }

    public @NotNull CompoundTag getTag() {
        return tag;
    }

    public void setTag(@Nullable CompoundTag tag) {
        if (tag == null) tag = new CompoundTag();
        CompoundTag nbt = CardboardBoxUtils.getCustomData(handle);
        if (nbt == null) nbt = new CompoundTag();
        nbt.put("cardboardData", tag);
        handle = CardboardBoxUtils.setCustomData(handle, nbt);
        this.tag = tag;
    }

    public @NotNull Material getType() {
        return material;
    }

    public void setType(@Nullable Material material) {
        if (material == null) material = Material.AIR;
        CompoundTag nbt = CardboardBoxUtils.getCustomData(handle);
        if (nbt == null) nbt = new CompoundTag();
        nbt.putString("cardboardMaterial", material.name());
        handle = CardboardBoxUtils.setCustomData(handle, nbt);
        this.material = material;
    }

    public void store(@Nullable Material material, @Nullable CompoundTag tag) {
        this.setTag(tag);
        this.setType(material);
    }

    public @NotNull ItemStack getItemStack() {
        return CardboardBoxUtils.updateCardboardBox(handle);
    }

    public boolean hasData() {
        return getType() != Material.AIR;
    }
}
