package me.frostedsnowman.asyncpvp.api;

import com.google.common.cache.LoadingCache;
import me.frostedsnowman.asyncpvp.api.adapters.EntityAttackAdapter;
import me.frostedsnowman.asyncpvp.api.damage.DamageProcessor;
import me.frostedsnowman.asyncpvp.api.monsters.MonsterProcessor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;

public interface AsyncPvpApi {

    @Nonnull
    Plugin getPlugin();

    @Nonnull
    LoadingCache<Integer, LivingEntity> getEntityCache();

    @Nonnull
    EntityAttackAdapter getPacketAdapter();

    @Nonnull
    MonsterProcessor getMonsterProcessor();

    @Nonnull
    DamageProcessor getDamageProcessor();

    @Nonnull
    DamagePrerequisites getDamagePrerequisites();
}
