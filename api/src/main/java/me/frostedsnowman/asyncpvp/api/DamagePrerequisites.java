package me.frostedsnowman.asyncpvp.api;

import me.frostedsnowman.asyncpvp.api.monsters.MonsterType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface DamagePrerequisites {

    boolean canCriticalHit(@Nonnull LivingEntity livingEntity);

    boolean isInWater(@Nonnull LivingEntity livingEntity);

    boolean isClimbing(@Nonnull LivingEntity livingEntity);

    static double getDamageEnchants(ItemStack itemStack, MonsterType monsterType) {
        final int damageAllLevel = itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
        if (damageAllLevel > 0) {
            return 1.0D + Math.max(0, damageAllLevel - 1) * 0.5D;
        }
        final int damageUndeadLevel = itemStack.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD);
        if (damageUndeadLevel > 0 && monsterType == MonsterType.UNDEAD) {
            return damageUndeadLevel * 2.5D;
        }
        final int damageArothropodsLevel = itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS);
        if (damageArothropodsLevel > 0 && monsterType == MonsterType.ARTHROPOD) {
            return damageArothropodsLevel * 2.5D;
        }
        return 0.0D;
    }
}
