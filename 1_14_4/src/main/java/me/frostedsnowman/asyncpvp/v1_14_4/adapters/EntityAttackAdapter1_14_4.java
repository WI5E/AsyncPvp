package me.frostedsnowman.asyncpvp.v1_14_4.adapters;

import com.comphenix.protocol.events.PacketEvent;
import me.frostedsnowman.asyncpvp.api.AsyncPvpApi;
import me.frostedsnowman.asyncpvp.api.adapters.EntityAttackAdapter;
import me.frostedsnowman.asyncpvp.api.damage.DamageEvaluation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class EntityAttackAdapter1_14_4 extends EntityAttackAdapter {

    public EntityAttackAdapter1_14_4(@Nonnull AsyncPvpApi asyncPvpApi) {
        super(asyncPvpApi);
    }

    @Override
    public void onHit(PacketEvent event, Player attacker, Player victim) {
        this.asyncPvpApi.getDamageProcessor().process(victim, victim, damageEvaluation -> {
            Bukkit.getScheduler().runTask(this.plugin, damageEvaluation::runSyncedActions);
        });
    }
}
