package me.frostedsnowman.asyncpvp.v1_14_4.processors;

import lombok.RequiredArgsConstructor;
import me.frostedsnowman.asyncpvp.api.AsyncPvpApi;
import me.frostedsnowman.asyncpvp.api.damage.DamageEvaluation;
import me.frostedsnowman.asyncpvp.api.damage.DamageProcessor;
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
import net.minecraft.server.v1_14_R1.GenericAttributes;
import net.minecraft.server.v1_14_R1.ItemStack;
import net.minecraft.server.v1_14_R1.ItemSword;
import net.minecraft.server.v1_14_R1.MobEffects;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_14_R1.Particles;
import net.minecraft.server.v1_14_R1.SoundEffects;
import net.minecraft.server.v1_14_R1.StatisticList;
import net.minecraft.server.v1_14_R1.Vec3D;
import net.minecraft.server.v1_14_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftVector;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public class DamageProcessor1_14_4 implements DamageProcessor {

    private final AsyncPvpApi asyncPvpApi;

    private double s(int current, double attackSpeed, float offset) {
        return Riven.maxOf((float) current + offset / this.cooldownTime(attackSpeed), 0.0F, 1.0F);
    }

    public float cooldownTime(double attackSpeed) {
        return (float) (1.0D / attackSpeed * 20.0D);
    }


    //0.2 + ((t + 0.5) / T) ^ 2 * 0.8
    public double damageMultiplier(int lastAttack, float cooldownTime) {
        double calculated = ((lastAttack + 0.5) / cooldownTime);
        return 0.2 + calculated * calculated * 0.8;
    }

    @Override
    public DamageEvaluation process(LivingEntity damager, LivingEntity victim) {

        DamageEvaluation damageEvaluation = new DamageEvaluation();

        //this.getAttributeInstance(GenericAttributes.ATTACK_SPEED).getValue()

        double baseDamage = damager.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();

        EntityHuman damagerHandle = ((CraftHumanEntity) damager).getHandle();
        EntityHuman victimHandle = ((CraftHumanEntity) victim).getHandle();

        double extraDamage = EnchantmentManager.a(damagerHandle.getItemInMainHand(), victimHandle.getMonsterType());

        int speedDamage = Reflections.getField(damagerHandle.getClass().getSuperclass(), "aD", int.class).get(damagerHandle);

        double modifiedSpeed = this.s(speedDamage, damagerHandle.getAttributeInstance(GenericAttributes.ATTACK_SPEED).getValue(), 0.5F);
        baseDamage *= 0.2F + modifiedSpeed * modifiedSpeed * 0.8F;
        extraDamage *= modifiedSpeed;
        damagerHandle.dZ();
        if (baseDamage > 0.0F || extraDamage > 0.0F) {
            boolean flag = modifiedSpeed > 0.9F;
            boolean flag1 = false;
            byte b0 = 0;
            int i = b0 + EnchantmentManager.b(damagerHandle);
            if (damagerHandle.isSprinting() && flag) {
                damagerHandle.world.a((EntityHuman) null, damagerHandle.locX, damagerHandle.locY, damagerHandle.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_KNOCKBACK, damagerHandle.getSoundCategory(), 1.0F, 1.0F);
                ++i;
                flag1 = true;
            }

            boolean flag2 = flag && damagerHandle.fallDistance > 0.0F && !damagerHandle.onGround && !damagerHandle.isClimbing() && !damagerHandle.isInWater() && !damagerHandle.hasEffect(MobEffects.BLINDNESS) && !damagerHandle.isPassenger();
            flag2 = flag2 && !damagerHandle.isSprinting();
            if (flag2) {
                baseDamage *= 1.5F;
            }

            baseDamage += extraDamage;
            boolean flag3 = false;


            double h = (float)damagerHandle.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();

            double d0 = (damagerHandle.E - damagerHandle.D);
            if (flag && !flag2 && !flag1 && damagerHandle.onGround && d0 < (double) h) {
                ItemStack itemstack = damagerHandle.b((EnumHand) EnumHand.MAIN_HAND);
                if (itemstack.getItem() instanceof ItemSword) {
                    flag3 = true;
                }
            }

            float f3 = 0.0F;
            boolean flag4 = false;
            int j = EnchantmentManager.getFireAspectEnchantmentLevel(damagerHandle);
            if (victimHandle instanceof EntityLiving) {
                f3 = victimHandle.getHealth();
                if (j > 0 && !victimHandle.isBurning()) {

                   damageEvaluation.setFireTicks(20);

                    damageEvaluation.addExternalVelocityChange(() -> {
                        EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(damagerHandle.getBukkitEntity(), victimHandle.getBukkitEntity(), 1);
                        Bukkit.getPluginManager().callEvent(combustEvent);
                        if (!combustEvent.isCancelled()) {
                            victimHandle.setOnFire(combustEvent.getDuration(), false);
                        }
                    });

                    //call event

/*                    EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), 1);
                    Bukkit.getPluginManager().callEvent(combustEvent);
                    if (!combustEvent.isCancelled()) {
                        flag4 = true;
                        entity.setOnFire(combustEvent.getDuration(), false);
                    }*/
                }
            }

            Vec3D vec3d = victimHandle.getMot();

            //boolean flag5 = victimHandle.noDamageTicks > 10;

            boolean flag5 = (float)victimHandle.noDamageTicks > 10.0F;

           // baseDamage = flag5 && baseDamage > victimHandle.lastDamage ? baseDamage - victimHandle.lastDamage : baseDamage;

/*            if (baseDamage > victimHandle.lastDamage) {
                victimHandle.lastDamage = (float) baseDamage;
            }*/
/*            if (!flag5) {
                victimHandle.noDamageTicks = 20;
            }*/
            System.out.println("Attempted Damage: " + baseDamage);

            //boolean flag5 = victimHandle.damageEntity(DamageSource.playerAttack(damagerHandle), (float) baseDamage);
            if (true/*flag5 && baseDamage > victimHandle.lastDamage*/) {
                /*baseDamage = baseDamage - victimHandle.lastDamage;*/
                if (i > 0) {
                    final int knockBackLevel = i;
                    if (victimHandle instanceof EntityLiving) {
                        damageEvaluation.addInternalVelocityChange(() -> {
                            ((EntityLiving) victimHandle).a(damagerHandle, (float) knockBackLevel * 0.5F, (double) Riven.sin(damagerHandle.yaw * 0.017453292F), (double) (-Riven.cos(damagerHandle.yaw * 0.017453292F)));
                        });
                    } else {
                        damageEvaluation.addInternalVelocityChange(() -> {
                            victimHandle.f((double) (-Riven.sin(damagerHandle.yaw * 0.017453292F) * (float) knockBackLevel * 0.5F), 0.1D, (double) (Riven.cos(damagerHandle.yaw * 0.017453292F) * (float) knockBackLevel * 0.5F));
                        });
                    }
                    damageEvaluation.addInternalVelocityChange(() -> {
                        damagerHandle.setMot(damagerHandle.getMot().d(0.6D, 1.0D, 0.6D));
                        damagerHandle.setSprinting(false);
                    });
                }

                if (flag3) {
                    double f4 = 1.0F + EnchantmentManager.a(damagerHandle) * baseDamage;
                    List<EntityLiving> list = damagerHandle.world.a(EntityLiving.class, victimHandle.getBoundingBox().grow(1.0D, 0.25D, 1.0D));
                    Iterator iterator = list.iterator();

                    label179:
                    while (true) {
                        EntityLiving entityliving;
                        do {
                            do {
                                do {
                                    do {
                                        if (!iterator.hasNext()) {
                                            damagerHandle.world.a((EntityHuman) null, damagerHandle.locX, damagerHandle.locY, damagerHandle.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_SWEEP, damagerHandle.getSoundCategory(), 1.0F, 1.0F);
                                            damagerHandle.dE();
                                            break label179;
                                        }

                                        entityliving = (EntityLiving) iterator.next();
                                    } while (entityliving == damagerHandle);
                                } while (entityliving == victimHandle);
                            } while (damagerHandle.r(entityliving));
                        } while (entityliving instanceof EntityArmorStand && ((EntityArmorStand) entityliving).isMarker());

                        if (damagerHandle.h(entityliving) < 9.0D && entityliving.damageEntity(DamageSource.playerAttack(damagerHandle).sweep(), (float) f4)) {
                            entityliving.a(damagerHandle, 0.4F, (double) Riven.sin(damagerHandle.yaw * 0.017453292F), (double) (-Riven.cos(damagerHandle.yaw * 0.017453292F)));
                        }
                    }
                }

                if (victimHandle instanceof EntityPlayer && victimHandle.velocityChanged) {
                    /* boolean cancelled = false;*/
                    Player player = (Player) victimHandle.getBukkitEntity();
                    Vector velocity = CraftVector.toBukkit(vec3d).clone();
                   /* PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity.clone());
                    damagerHandle.world.getServer().getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        cancelled = true;
                    } else if (!velocity.equals(event.getVelocity())) {
                        player.setVelocity(event.getVelocity());
                    }*/

                    damageEvaluation.addExternalVelocityChange(() -> {

                        boolean cancelled = false;

                        PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity);
                        damagerHandle.world.getServer().getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            cancelled = true;
                        } else if (!velocity.equals(event.getVelocity())) {
                            player.setVelocity(event.getVelocity());
                        }

                        if (!cancelled) {
                            ((EntityPlayer) victimHandle).playerConnection.sendPacket(new PacketPlayOutEntityVelocity(victimHandle));
                            victimHandle.velocityChanged = false;
                            victimHandle.setMot(vec3d);
                        }

                    });


                 /*   if (!cancelled) {
                        ((EntityPlayer)entity).playerConnection.sendPacket(new PacketPlayOutEntityVelocity(entity));
                        entity.velocityChanged = false;
                        entity.setMot(vec3d);
                    }*/
                }

                if (flag2) {
                   // damagerHandle.world.a((EntityHuman) null, damagerHandle.locX, damagerHandle.locY, damagerHandle.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_CRIT, damagerHandle.getSoundCategory(), 1.0F, 1.0F);
                    damagerHandle.a(victimHandle);
                }

                if (!flag2 && !flag3) {
                    if (flag) {
                    //    damagerHandle.world.a((EntityHuman) null, damagerHandle.locX, damagerHandle.locY, damagerHandle.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_STRONG, damagerHandle.getSoundCategory(), 1.0F, 1.0F);
                    } else {
                  //      damagerHandle.world.a((EntityHuman) null, damagerHandle.locX, damagerHandle.locY, damagerHandle.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_WEAK, damagerHandle.getSoundCategory(), 1.0F, 1.0F);
                    }
                }

                if (extraDamage > 0.0F) {
                    damagerHandle.b(victimHandle);
                }

                damagerHandle.z(victimHandle);
                if (victimHandle instanceof EntityLiving) {
                    EnchantmentManager.a((EntityLiving) victimHandle, damagerHandle);
                }

                EnchantmentManager.b(damagerHandle, victimHandle);
                ItemStack itemstack1 = damagerHandle.getItemInMainHand();
                Object object = victim;
                if (victim instanceof EntityComplexPart) {
                    object = ((EntityComplexPart) victim).owner;
                }

                if (!damagerHandle.world.isClientSide && !itemstack1.isEmpty() && object instanceof EntityLiving) {
                    itemstack1.a((EntityLiving) object, damagerHandle);
                    if (itemstack1.isEmpty()) {
                        damagerHandle.a((EnumHand) EnumHand.MAIN_HAND, (ItemStack) ItemStack.a);
                    }
                }

                if (victimHandle instanceof EntityLiving) {
                    float f5 = f3 - ((EntityLiving) victimHandle).getHealth();
                    damageEvaluation.addExternalVelocityChange(() ->  damagerHandle.a(StatisticList.DAMAGE_DEALT, Math.round(f5 * 10.0F)));
                   // damagerHandle.a(StatisticList.DAMAGE_DEALT, Math.round(f5 * 10.0F));
                    if (j > 0) {
                        damageEvaluation.addExternalVelocityChange(() -> {
                            EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(damagerHandle.getBukkitEntity(), victimHandle.getBukkitEntity(), j * 4);
                            Bukkit.getPluginManager().callEvent(combustEvent);
                            if (!combustEvent.isCancelled()) {
                                victimHandle.setOnFire(combustEvent.getDuration());
                            }
                        });
                    }

                    if (damagerHandle.world instanceof WorldServer && f5 > 2.0F) {
                        int k = (int) ((double) f5 * 0.5D);
                        ((WorldServer) damagerHandle.world).a(Particles.DAMAGE_INDICATOR, victimHandle.locX, victimHandle.locY + (double) (victimHandle.getHeight() * 0.5F), victimHandle.locZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                    }
                }

                damagerHandle.applyExhaustion(damagerHandle.world.spigotConfig.combatExhaustion);
            } else {
                damagerHandle.world.a((EntityHuman) null, damagerHandle.locX, damagerHandle.locY, damagerHandle.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_NODAMAGE, damagerHandle.getSoundCategory(), 1.0F, 1.0F);
                if (damageEvaluation.getFireTicks().isPresent()) {
                    victimHandle.extinguish();
                }

                if (damagerHandle instanceof EntityPlayer) {
                    ((EntityPlayer) damagerHandle).getBukkitEntity().updateInventory();
                }
                damageEvaluation.setBaseDamage(0);
                damageEvaluation.setFinalDamage(0);
            }
        }
        damageEvaluation.setBaseDamage(baseDamage);
        System.out.println("Net: " + baseDamage);
        return damageEvaluation;
    }
}
