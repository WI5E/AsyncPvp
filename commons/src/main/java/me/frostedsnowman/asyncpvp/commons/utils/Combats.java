package me.frostedsnowman.asyncpvp.commons.utils;

import me.frostedsnowman.asyncpvp.commons.maths.Riven;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public final class Combats {

    private Combats() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static boolean canCriticalHit(@Nonnull LivingEntity livingEntity) {
        Material on = livingEntity.getLocation().getBlock().getType();
        return livingEntity.getFallDistance() > 0.0F && !livingEntity.isOnGround() && on != Material.LADDER && on != Material.WATER && !livingEntity.hasPotionEffect(PotionEffectType.BLINDNESS) && livingEntity.getVehicle() == null;
    }

    public static Vector getKnockbackVelocity(float yaw, int knockbackLevel) {
        return new Vector((-Riven.sin(yaw * (float) Math.PI / 180.0F) * (float) knockbackLevel * 0.5F), 0.1D, (Riven.cos(yaw * (float) Math.PI / 180.0F) * (float) knockbackLevel * 0.5F));
    }

    public static int getEnchantmentLevelArmor(LivingEntity livingEntity, Enchantment enchantment) {
        EntityEquipment entityEquipment = livingEntity.getEquipment();
        if (entityEquipment == null) {
            return 0;
        }
        for (ItemStack itemStack : entityEquipment.getArmorContents()) {
            if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasEnchant(enchantment)) {
                continue;
            }
            return itemStack.getEnchantmentLevel(enchantment);
        }
        return 0;
    }

    /**
     * Defines the type of damage of the enchantment, 0 = all, 1 = undead, 3 = arthropods
     * @param enchantment
     * @return
     */
/*    public static double getDamageEnchants(ItemStack itemStack, EnumMonsterType enumMonsterType) {
        int damageAllLevel = itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
        if (damageAllLevel > 0) {
            return 1.0F + (float)Math.max(0, damageAllLevel - 1) * 0.5F;
        }

        int damageUndeadLevel = itemStack.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD);
        if (damageUndeadLevel > 0 && enumMonsterType == EnumMonsterType.UNDEAD) {
            return damageUndeadLevel * 2.5D;
        }

        int damageArothropodsLevel = itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS);
        if (damageArothropodsLevel > 0 && enumMonsterType == EnumMonsterType.ARTHROPOD) {
            return damageArothropodsLevel * 2.5D;
        }
        return 0.0D;
    }*/
}
