package me.frostedsnowman.asyncpvp.v1_14_4.processors;

import lombok.RequiredArgsConstructor;
import me.frostedsnowman.asyncpvp.api.AsyncPvpApi;
import me.frostedsnowman.asyncpvp.api.damage.DamageEvaluation;
import me.frostedsnowman.asyncpvp.api.damage.DamageProcessor;
import me.frostedsnowman.asyncpvp.commons.maths.Riven;
import org.bukkit.entity.LivingEntity;

@RequiredArgsConstructor
public class DamageProcessor1_14_4 implements DamageProcessor {

    private final AsyncPvpApi asyncPvpApi;

    private double s(int current, double attackSpeed, float offset) {
        return Riven.maxOf((float) current + offset / this.cooldownTime(attackSpeed), 0.0F, 1.0F);
    }

    public float cooldownTime(double attackSpeed) {
        return (float) (1.0D / attackSpeed * 20.0D);
    }

    @Override
    public DamageEvaluation process(LivingEntity damager, LivingEntity victim) {
        //todo
        return new DamageEvaluation();
    }
}
