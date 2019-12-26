package me.frostedsnowman.asyncpvp.commons.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class ItemStacks {

    private final ItemStack itemStack;

    private ItemStacks(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    private ItemStacks(Material material) {
        this(new ItemStack(material));
    }

    public static ItemStacks of(Material material) {
        return new ItemStacks(Objects.requireNonNull(material, "material"));
    }

    public static ItemStacks of(ItemStack itemStack) {
        return new ItemStacks(Objects.requireNonNull(itemStack, "itemStack"));
    }

    public ItemStacks amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemStacks enchant(Map<Enchantment, Integer> enchantments) {
        enchantments.forEach(this::enchant);
        return this;
    }

    public ItemStacks enchant(Enchantment enchantment, int level) {
        this.itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemStacks durability(int data) {
        this.itemStack.setDurability((short) data);
        return this;
    }

    @SafeVarargs
    public final <T extends ItemMeta> ItemStacks meta(Class<T> itemMetaType, Consumer<T>... itemMeta) {
        ItemMeta meta = this.itemStack.getItemMeta();
        if (itemMetaType.isInstance(Objects.requireNonNull(meta, "itemMeta"))) {
            Arrays.stream(itemMeta).forEach(tConsumer -> tConsumer.accept(itemMetaType.cast(meta)));


            this.itemStack.setItemMeta(meta);
        }
        return this;
    }

    @SafeVarargs
    public final ItemStacks meta(Consumer<ItemMeta>... itemMeta) {
        return this.meta(ItemMeta.class, itemMeta);
    }

    public ItemStack build() {
        return this.itemStack;
    }

    public static boolean isValid(@Nullable ItemStack itemStack) {
        return itemStack != null && itemStack.getType() != Material.AIR && itemStack.getAmount() >= 1;
    }
}
