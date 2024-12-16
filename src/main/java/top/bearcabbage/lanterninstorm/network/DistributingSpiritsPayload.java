package top.bearcabbage.lanterninstorm.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import java.util.UUID;

public record DistributingSpiritsPayload (UUID lantern, int spirits) implements CustomPayload {
    public static final Id<DistributingSpiritsPayload> ID = new CustomPayload.Id<>(NetworkingConstants.DISTRIBUTING_SPIRITS_PAYLOAD);
    public static final PacketCodec<PacketByteBuf, DistributingSpiritsPayload> CODEC = PacketCodec.of((value, buf) -> buf.writeUuid(value.lantern).writeInt(value.spirits),  buf -> new DistributingSpiritsPayload(buf.readUuid(), buf.readInt()));


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
