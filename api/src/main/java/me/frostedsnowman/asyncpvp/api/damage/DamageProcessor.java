package me.frostedsnowman.asyncpvp.api.damage;

import me.frostedsnowman.asyncpvp.commons.processors.BiProcessor;
import org.bukkit.entity.LivingEntity;

public interface DamageProcessor extends BiProcessor<LivingEntity, LivingEntity, DamageEvaluation> {}
