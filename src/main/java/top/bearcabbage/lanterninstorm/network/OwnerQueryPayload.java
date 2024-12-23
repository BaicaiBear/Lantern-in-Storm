package top.bearcabbage.lanterninstorm.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public record OwnerQueryPayload(UUID lantern, UUID owner) implements CustomPayload {
    public static final Id<OwnerQueryPayload> ID = new Id<>(NetworkingConstants.OWNER_QUERY_PAYLOAD);
    public static final PacketCodec<PacketByteBuf, OwnerQueryPayload> CODEC = PacketCodec.of((value, buf) -> buf.writeUuid(value.lantern).writeUuid(value.owner), buf -> new OwnerQueryPayload(buf.readUuid(), buf.readUuid()));


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
