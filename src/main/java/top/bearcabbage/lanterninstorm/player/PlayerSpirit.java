package top.bearcabbage.lanterninstorm.player;


import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;

import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;


import java.util.Map;
import java.util.UUID;

import static top.bearcabbage.lanterninstorm.utils.Math.HorizontalDistance;

public class PlayerSpirit {
    public static final int SPIRIT_RADIUS = 10;
    public static final int MAX_SPIRIT_RADIUS = 200;
    private final ServerPlayerEntity player;

    public PlayerSpirit(ServerPlayerEntity player, boolean firstTime) {
        this.player = player;
    }

    // 检查玩家是否在安全区内
    public boolean isStabilized() {
        Map<UUID, Integer> lanterns_and_spirits = SpiritManager.spirit_mass.getOrDefault(player.getUuid(), null);
        if (lanterns_and_spirits == null) {
            return false;
        }
        for (SpiritLanternEntity lantern : player.getEntityWorld().getNonSpectatingEntities(SpiritLanternEntity.class,
                Box.of(player.getPos(), MAX_SPIRIT_RADIUS * 2, MAX_SPIRIT_RADIUS * 2, MAX_SPIRIT_RADIUS * 2))) {
            int spirit_mass = SpiritManager.get(player.getUuid(), lantern.getUuid());
            if (spirit_mass == 0) continue;
            if (HorizontalDistance(player.getPos(), lantern.getPos()) < spirit_mass * SPIRIT_RADIUS) return true;
        }
        return false;
    }

    public boolean setSpiritToLantern(SpiritLanternEntity lantern, int i) {
        if (lantern.getEntityWorld().isClient) {
            return false;
        }
        int old_in_lantern = SpiritManager.get(player.getUuid(), lantern.getUuid());
        int old_left = SpiritManager.get_left(player.getUuid());
        int moved_mass = i - old_in_lantern;

        if (moved_mass > old_left) {
            // 剩余的不足以填充这么多的灵魂进入灯笼
            return false;
        }

        SpiritManager.increase_left(player.getUuid(), -moved_mass);
        SpiritManager.increase(player.getUuid(), lantern.getUuid(), moved_mass);
        return true;
    }

    // 灵魂灯笼死亡
    public boolean removeSpiritFromLantern(SpiritLanternEntity lantern) {
        if (lantern.getEntityWorld().isClient) {
            return false;
        }
        SpiritManager.set(player.getUuid(), lantern.getUuid(), 0);
        return true;
    }

    public int getMass() {
        return SpiritManager.get_sum(player.getUuid());
    }

    public boolean addMass(int mass) {
        SpiritManager.increase_left(player.getUuid(), mass);
        return true;
    }
}
