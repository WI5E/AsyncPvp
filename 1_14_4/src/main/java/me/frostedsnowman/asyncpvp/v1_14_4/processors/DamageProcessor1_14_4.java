package me.frostedsnowman.asyncpvp.v1_14_4.processors;

import lombok.RequiredArgsConstructor;
import me.frostedsnowman.asyncpvp.api.AsyncPvpApi;
import me.frostedsnowman.asyncpvp.api.damage.DamageEvaluation;
import me.frostedsnowman.asyncpvp.api.damage.DamageProcessor;
import me.frostedsnowman.asyncpvp.api.damage.PlayerDamageProcessor;
import me.frostedsnowman.asyncpvp.commons.maths.Riven;
import me.frostedsnowman.asyncpvp.commons.reflections.Reflections;
import net.minecraft.server.v1_14_R1.DamageSource;
import net.minecraft.server.v1_14_R1.EnchantmentManager;
import net.minecraft.server.v1_14_R1.EntityArmorStand;
import net.minecraft.server.v1_14_R1.EntityComplexPart;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EnumHand;
import net.minecraft.server.v1_14_R1.EnumMonsterType;
import net.minecraft.server.v1_14_R1.GenericAttributes;
import net.minecraft.server.v1_14_R1.ItemStack;
import net.minecraft.server.v1_14_R1.ItemSword;
import net.minecraft.server.v1_14_R1.MathHelper;
import net.minecraft.server.v1_14_R1.MobEffects;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_14_R1.Particles;
import net.minecraft.server.v1_14_R1.SoundEffects;
import net.minecraft.server.v1_14_R1.StatisticList;
import net.minecraft.server.v1_14_R1.Vec3D;
import net.minecraft.server.v1_14_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftVector;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class DamageProcessor1_14_4 implements PlayerDamageProcessor {

    private final AsyncPvpApi asyncPvpApi;

    private float s(int current, double attackSpeed, float offset) {
        return Riven.maxOf((float) current + offset / this.cooldownTime(attackSpeed), 0.0F, 1.0F);
    }

    public float cooldownTime(double attackSpeed) {
        return (float) (1.0D / attackSpeed * 20.0D);
    }

    @Override
    public void process(Player player, Player player2, Consumer<DamageEvaluation> v) {
        DamageEvaluation damageEvaluation = new DamageEvaluation();

        EntityPlayer damager = ((CraftPlayer) player).getHandle();
        EntityPlayer victim = ((CraftPlayer) player).getHandle();

        /**
         * Entity::br returns true, unless Arrow, ExperienceOrb, EPSignal, etc.
         * Entity::t returns false, unless EntityHanging
         */

        if (victim.br() && !victim.t(damager)) {
            float attackDamage = (float) damager.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
            float enchantDamage;

            enchantDamage = EnchantmentManager.a(damager.getItemInMainHand(), victim.getMonsterType());

            float hitDelayDamage = this.s(Reflections.getField(damager.getClass(), "aD", int.class).get(damager), damager.getAttributeInstance(GenericAttributes.ATTACK_SPEED).getValue(), 0.5F);
            attackDamage *= 0.2F + hitDelayDamage * hitDelayDamage * 0.8F;
            enchantDamage *= hitDelayDamage;
            damager.dZ(); //sets aD to 0
            if (attackDamage > 0.0F || enchantDamage > 0.0F) {
                boolean flag = hitDelayDamage > 0.9F;
                boolean flag1 = false;
                byte b0 = 0;
                int knockBack = b0 + EnchantmentManager.b(damager);
                if (damager.isSprinting() && flag) {
                    //play sounds through Bukkit
                    //this.world.a((EntityHuman)null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_KNOCKBACK, this.getSoundCategory(), 1.0F, 1.0F);
                    ++knockBack;
                    flag1 = true;
                }

                boolean criticalHit = flag && damager.fallDistance > 0.0F && !damager.onGround && !damager.isClimbing() && !damager.isInWater() && !damager.hasEffect(MobEffects.BLINDNESS) && !damager.isPassenger();
                criticalHit = criticalHit && !damager.isSprinting();
                if (criticalHit) {
                    attackDamage *= 1.5F;
                }

                attackDamage += enchantDamage;
                boolean weaponIsSword = false;
                double d0 = (damager.E - damager.D);
                if (flag && !criticalHit && !flag1 && damager.onGround && d0 < (double) damager.da()) {
                    ItemStack itemstack = damager.b(EnumHand.MAIN_HAND);
                    if (itemstack.getItem() instanceof ItemSword) {
                        weaponIsSword = true;
                    }
                }

                float victimHealth = 0.0F;
                boolean flag4 = false;
                int fireAspect = EnchantmentManager.getFireAspectEnchantmentLevel(damager);

                victimHealth = victim.getHealth();
                if (fireAspect > 0 && !victim.isBurning()) {
                    EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(damager.getBukkitEntity(), victim.getBukkitEntity(), 1);
                    Bukkit.getPluginManager().callEvent(combustEvent);
                    if (!combustEvent.isCancelled()) {
                        flag4 = true;
                        victim.setOnFire(combustEvent.getDuration(), false);
                    }
                }

                Vec3D vec3d = victim.getMot();

                final float currentAttackDamage = attackDamage;
                final int currentKnockBack = knockBack;
                final boolean currentWeaponIsSword = weaponIsSword;
                final boolean currentCriticalHit = criticalHit;
                final float currentEnchantDamage = enchantDamage;
                final float currentVictimHealth = victimHealth;

                Bukkit.getScheduler().runTask(this.asyncPvpApi.getPlugin(), () -> {

                    CompletableFuture.completedFuture(victim.damageEntity(DamageSource.playerAttack(damager), currentAttackDamage))
                            .thenAcceptAsync(flag5 -> {

                                if (flag5) {
                                    if (currentKnockBack > 0) {
                                        damageEvaluation.addSyncedAction(() -> {
                                            victim.a(damager, (float) currentKnockBack * 0.5F, (double) MathHelper.sin(damager.yaw * 0.017453292F), (double) (-MathHelper.cos(damager.yaw * 0.017453292F)));

                                            damager.setMot(damager.getMot().d(0.6D, 1.0D, 0.6D));
                                            damager.setSprinting(false);
                                        });
                                    }

                                    if (currentWeaponIsSword) {
                                        float f4 = 1.0F + EnchantmentManager.a(damager) * currentAttackDamage;

                                        List<EntityLiving> list = damager.world.a(EntityLiving.class, victim.getBoundingBox().grow(1.0D, 0.25D, 1.0D));


                                        damageEvaluation.addSyncedAction(() -> {
                                            Iterator<EntityLiving> iterator = list.iterator();

                                            label179:
                                            while (true) {
                                                EntityLiving entityliving;
                                                do {
                                                    do {
                                                        do {
                                                            do {
                                                                if (!iterator.hasNext()) {
                                                                    //damager.world.a((EntityHuman)null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0F, 1.0F);
                                                                    damager.dE();
                                                                    break label179;
                                                                }

                                                                entityliving = (EntityLiving) iterator.next();
                                                            } while (entityliving == damager);
                                                        } while (entityliving == victim);
                                                    } while (damager.r(entityliving));
                                                } while (entityliving instanceof EntityArmorStand && ((EntityArmorStand) entityliving).isMarker());

                                                if (damager.h(entityliving) < 9.0D && entityliving.damageEntity(DamageSource.playerAttack(damager).sweep(), f4)) {
                                                    entityliving.a(damager, 0.4F, (double) MathHelper.sin(damager.yaw * 0.017453292F), (double) (-MathHelper.cos(damager.yaw * 0.017453292F)));
                                                }
                                            }
                                        });
                                    }

                                    if (victim.velocityChanged) {
/*                                        boolean cancelled = false;
                                        Player player = (Player)entity.getBukkitEntity();
                                        Vector velocity = CraftVector.toBukkit(vec3d);
                                        PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity.clone());
                                        this.world.getServer().getPluginManager().callEvent(event);
                                        if (event.isCancelled()) {
                                            cancelled = true;
                                        } else if (!velocity.equals(event.getVelocity())) {
                                            //velocity was changed in event
                                            player.setVelocity(event.getVelocity());
                                        }*/

                                        victim.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(victim));
                                        victim.velocityChanged = false;
                                        victim.setMot(vec3d);


/*                                        if (!cancelled) {
                                            ((EntityPlayer)entity).playerConnection.sendPacket(new PacketPlayOutEntityVelocity(entity));
                                            entity.velocityChanged = false;
                                            entity.setMot(vec3d);
                                        }*/
                                    }

                                    if (currentCriticalHit) {
                                        // this.world.a((EntityHuman)null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_CRIT, this.getSoundCategory(), 1.0F, 1.0F);
                                        damager.a(victim);
                                    }

                                    if (!currentCriticalHit && !currentWeaponIsSword) {
                                        if (flag) {
                                            // this.world.a((EntityHuman)null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0F, 1.0F);
                                        } else {
                                            // this.world.a((EntityHuman)null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_WEAK, this.getSoundCategory(), 1.0F, 1.0F);
                                        }
                                    }

                                    if (currentEnchantDamage > 0.0F) {
                                        damager.b(victim); //animation
                                    }

                                    damageEvaluation.addSyncedAction(() -> damager.z(victim)); //set something to do with ticks lived

                                    EnchantmentManager.a(victim, damager);

                                    EnchantmentManager.b(damager, victim);

                                    damageEvaluation.addSyncedAction(() -> {
                                        ItemStack itemstack1 = damager.getItemInMainHand();

                                        if (!damager.world.isClientSide && !itemstack1.isEmpty()) {
                                            itemstack1.a(victim, damager);
                                            if (itemstack1.isEmpty()) {
                                                damager.a(EnumHand.MAIN_HAND, ItemStack.a);
                                            }
                                        }
                                    });

                                    float f5 = currentVictimHealth - victim.getHealth();
                                    damageEvaluation.addSyncedAction(() -> {
                                        damager.a(StatisticList.DAMAGE_DEALT, Math.round(f5 * 10.0F));
                                    });
                                    if (fireAspect > 0) {
/*                                        EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), fireAspect * 4);
                                        Bukkit.getPluginManager().callEvent(combustEvent);
                                        if (!combustEvent.isCancelled()) {
                                            entity.setOnFire(combustEvent.getDuration());
                                        }*/
                                        victim.setOnFire(fireAspect * 4);
                                    }

                                    if (damager.world instanceof WorldServer && f5 > 2.0F) {
                                        int k = (int) ((double) f5 * 0.5D);
                                        ((WorldServer) damager.world).a(Particles.DAMAGE_INDICATOR, victim.locX, victim.locY + (double) (victim.getHeight() * 0.5F), victim.locZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                                    }


                                    damageEvaluation.addSyncedAction(() -> {
                                        damager.applyExhaustion(damager.world.spigotConfig.combatExhaustion);
                                    });
                                } else {
                                    //damager.world.a((EntityHuman)null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory(), 1.0F, 1.0F);
/*                                    if (flag4) {
                                        entity.extinguish();
                                    }*/

                                    damager.getBukkitEntity().updateInventory();
                                }
                                damageEvaluation.setFinalDamage(currentAttackDamage);
                                v.accept(damageEvaluation);

                            });

                });
            }
        }
    }
}