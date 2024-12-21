package top.bearcabbage.lanterninstorm.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public record LanternBoundaryPayload(UUID lantern, int radius) implements CustomPayload {
    public static final Id<LanternBoundaryPayload> ID = new Id<>(NetworkingConstants.LANTERN_BOUNDARY_PAYLOAD);
    public static final PacketCodec<PacketByteBuf, LanternBoundaryPayload> CODEC = PacketCodec.of((value, buf) -> buf.writeUuid(value.lantern).writeInt(value.radius), buf -> new LanternBoundaryPayload(buf.readUuid(), buf.readInt()));


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
