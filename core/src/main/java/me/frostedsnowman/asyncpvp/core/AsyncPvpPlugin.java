package me.frostedsnowman.asyncpvp.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.async.AsyncListenerHandler;
import com.comphenix.protocol.events.PacketAdapter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.frostedsnowman.asyncpvp.api.AsyncPvpApi;
import me.frostedsnowman.asyncpvp.api.DamagePrerequisites;
import me.frostedsnowman.asyncpvp.api.adapters.EntityAttackAdapter;
import me.frostedsnowman.asyncpvp.api.damage.DamageProcessor;
import me.frostedsnowman.asyncpvp.api.monsters.MonsterProcessor;
import me.frostedsnowman.asyncpvp.v1_14_4.adapters.EntityAttackAdapter1_14_4;
import me.frostedsnowman.asyncpvp.v1_14_4.processors.DamageProcessor1_14_4;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.TimeUnit;

public class AsyncPvpPlugin extends JavaPlugin implements AsyncPvpApi {

    private final LoadingCache<Integer, LivingEntity> entityCache = CacheBuilder.newBuilder()
            .expireAfterAccess(20, TimeUnit.SECONDS)
            .weakValues()

            .build(new CacheLoader<Integer, LivingEntity>() {
                @ParametersAreNonnullByDefault
                @Override
                public LivingEntity load(final Integer key) {
                    for (final World world : Bukkit.getWorlds()) {
                        for (final LivingEntity livingEntity : world.getLivingEntities()) {
                            if (livingEntity.getEntityId() == key) {
                                return livingEntity;
                            }
                        }
                    }
                    return null;
                }
            });

    private EntityAttackAdapter packetAdapter;
    private DamageProcessor<Player, Player> damageProcessor;

    private AsyncListenerHandler asyncListenerHandler;

    @Override
    public void onEnable() {
        this.packetAdapter = new EntityAttackAdapter1_14_4(this);
        this.damageProcessor = new DamageProcessor1_14_4(this);

        this.asyncListenerHandler = ProtocolLibrary.getProtocolManager()
                .getAsynchronousManager()
                .registerAsyncHandler(this.packetAdapter);
        this.asyncListenerHandler.start();
    }

    @Override
    public void onDisable() {
        if (this.asyncListenerHandler != null) {
            this.asyncListenerHandler.stop();
        }
    }

    @Nonnull
    @Override
    public Plugin getPlugin() {
        return this;
    }

    @Nonnull
    @Override
    public LoadingCache<Integer, LivingEntity> getEntityCache() {
        return this.entityCache;
    }

    @Nonnull
    @Override
    public EntityAttackAdapter getPacketAdapter() {
        return this.packetAdapter;
    }

    @Nonnull
    @Override
    public MonsterProcessor getMonsterProcessor() {
        return null;
    }

    @Nonnull
    @Override
    public DamageProcessor getDamageProcessor() {
        return this.damageProcessor;
    }

    @Nonnull
    @Override
    public DamagePrerequisites getDamagePrerequisites() {
        return null;
    }
}
