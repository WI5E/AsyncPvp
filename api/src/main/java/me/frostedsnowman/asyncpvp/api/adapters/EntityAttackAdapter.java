package me.frostedsnowman.asyncpvp.api.adapters;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.frostedsnowman.asyncpvp.api.AsyncPvpApi;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public abstract class EntityAttackAdapter extends PacketAdapter {

    private static final Set<GameMode> PVP_GAMEMODES = EnumSet.of(
            GameMode.SURVIVAL,
            GameMode.ADVENTURE
    );

    protected final AsyncPvpApi asyncPvpApi;

    public EntityAttackAdapter(@Nonnull AsyncPvpApi asyncPvpApi) {
        super(
                new AdapterParameteters()
                        .plugin(asyncPvpApi.getPlugin())
                        .clientSide()
                        .optionAsync()
                        .types(PacketType.Play.Client.USE_ENTITY)
        );
        this.asyncPvpApi = asyncPvpApi;
    }

    public abstract void onHit(PacketEvent event, Player attacker, Player victim);

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketContainer packet = event.getPacket();

        if (packet.getEntityUseActions().read(0) != EnumWrappers.EntityUseAction.ATTACK) {
            return;
        }
        int victimId = packet.getIntegers().read(0);

        try {
            Entity victim = this.asyncPvpApi.getEntityCache().get(victimId);

            if (victim == null || victim.getType() != EntityType.PLAYER || victim.isDead() || !PVP_GAMEMODES.contains(((Player) victim).getGameMode())) {
                return;
            }
            event.setCancelled(true);
            this.onHit(event, event.getPlayer(), (Player) victim);
        } catch (ExecutionException exception) {
            exception.printStackTrace();
        }
    }
}
