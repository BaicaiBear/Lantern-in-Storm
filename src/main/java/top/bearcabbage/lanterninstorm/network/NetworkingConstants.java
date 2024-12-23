package top.bearcabbage.lanterninstorm.network;

import net.minecraft.util.Identifier;

import java.util.UUID;

public class NetworkingConstants {
    public static final UUID NULL_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public static final Identifier DISTRIBUTING_SPIRITS_PAYLOAD = Identifier.of("lanterninstorm", "distributing_spirits_payload");
    public static final Identifier SPIRIT_MASS_PAYLOAD = Identifier.of("lanterninstorm", "spirit_mass_payload");
    public static final Identifier LANTERN_POS_PAYLOAD = Identifier.of("lanterninstorm", "lantern_pos_payload");
    public static final Identifier OWNER_QUERY_PAYLOAD = Identifier.of("lanterninstorm", "owner_query_payload");
}
