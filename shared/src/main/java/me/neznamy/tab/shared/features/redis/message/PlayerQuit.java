package me.neznamy.tab.shared.features.redis.message;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.neznamy.tab.shared.TAB;
import me.neznamy.tab.shared.features.redis.RedisPlayer;
import me.neznamy.tab.shared.features.redis.RedisSupport;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class PlayerQuit extends RedisMessage {

    private UUID playerId;

    @Override
    public void write(@NotNull ByteArrayDataOutput out) {
        writeUUID(out, playerId);
    }

    @Override
    public void read(@NotNull ByteArrayDataInput in) {
        playerId = readUUID(in);
    }

    @Override
    public void process(@NotNull RedisSupport redisSupport) {
        RedisPlayer target = redisSupport.getRedisPlayers().get(playerId);
        if (target == null) {
            TAB.getInstance().getErrorManager().printError("Unable to process quit of redis player " + playerId + ", because no such player exists", null);
            return;
        }
        TAB.getInstance().debug("Processing quit of redis player " + target.getName());
        TAB.getInstance().getFeatureManager().onQuit(target);
        redisSupport.getRedisPlayers().remove(target.getUniqueId());
    }
}
