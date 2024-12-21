package top.bearcabbage.lanterninstorm.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record SpiritMassPayload(Map<UUID, Map<UUID, Integer>> spirit_mass) implements CustomPayload {
    public static final Id<SpiritMassPayload> ID = new Id<>(NetworkingConstants.SPIRIT_MASS_PAYLOAD);
    public static final PacketCodec<PacketByteBuf, SpiritMassPayload> CODEC = PacketCodec.of((value, buf) -> {
        buf.write
            }
    );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
