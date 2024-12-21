package top.bearcabbage.lanterninstorm.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record SpiritMassPayload(Map<UUID, Map<UUID, Integer>> spirit_mass) implements CustomPayload {

    public static final Id<SpiritMassPayload> ID = new Id<>(NetworkingConstants.SPIRIT_MASS_PAYLOAD);
    public static final PacketCodec<PacketByteBuf, SpiritMassPayload> CODEC = PacketCodec.of(
        (value, buf) -> {
            buf.writeMap(
                    value.spirit_mass,
                    (buff, uuid) -> buff.writeUuid(uuid),
                    (buff, map) -> {
                        if (map == null || map.isEmpty()) {
                            buff.writeInt(0); // 写入空映射的大小
                        } else {
                            buff.writeInt(map.size());
                            map.forEach((uuid1, mass) -> {
                                buff.writeUuid(uuid1);
                                buff.writeInt(mass);
                            });
                        }
                    }
            );
        },
        buf -> new SpiritMassPayload(buf.readMap(
                    (buff) -> buff.readUuid(),
                    (buff) -> {
                        int size = buff.readInt(); // 读取映射的大小
                        if (size == 0) {
                            return new HashMap<>(); // 返回空映射
                        } else {
                            Map<UUID, Integer> map = new HashMap<>(size);
                            for (int i = 0; i < size; i++) {
                                UUID uuid1 = buff.readUuid();
                                int mass = buff.readInt();
                                map.put(uuid1, mass);
                            }
                            return map;
                        }
                    }
            )
        )
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
