package top.bearcabbage.lanterninstorm.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.GlobalPos;

import java.util.Map;
import java.util.UUID;

public record LanternPosPayload(Map<UUID, GlobalPos> lantern_pos) implements CustomPayload {
    public static final Id<LanternPosPayload> ID = new Id<>(NetworkingConstants.LANTERN_POS_PAYLOAD);
    public static final PacketCodec<PacketByteBuf, LanternPosPayload> CODEC = PacketCodec.of(
            (value, buf) -> buf.writeMap(value.lantern_pos,
            (buff, uuid) -> buff.writeUuid(uuid),
            PacketByteBuf::writeGlobalPos
            ),
            buf -> new LanternPosPayload(buf.readMap(
                    (buff) -> buff.readUuid(),
                    PacketByteBuf::readGlobalPos
            ))
    );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
