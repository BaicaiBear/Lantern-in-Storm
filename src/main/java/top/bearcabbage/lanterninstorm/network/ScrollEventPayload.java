package top.bearcabbage.lanterninstorm.network;

import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

//payload的模板？但是我不知道该怎么写（）
public class ScrollEventPayload implements CustomPayload {
    public static final Identifier ID = new Identifier("lanterninstorm", "scroll_payload");
    @Override
    public Id<? extends CustomPayload> getId() {
        return null;
    }
}
