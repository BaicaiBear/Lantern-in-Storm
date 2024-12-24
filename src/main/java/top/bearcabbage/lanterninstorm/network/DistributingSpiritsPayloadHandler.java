package top.bearcabbage.lanterninstorm.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import top.bearcabbage.lanterninstorm.LanternInStormSpiritManager;

import java.util.UUID;

import static top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity.lantern_list;


public class DistributingSpiritsPayloadHandler {

    // 收到加减灵魂的包
    public static void onDistributingSpiritsPayload(DistributingSpiritsPayload payload, ServerPlayNetworking.Context context)  {
        ServerPlayerEntity serverPlayer = context.player();
        UUID player = serverPlayer.getUuid();
        UUID lantern = payload.lantern();
        int spirits = payload.spirits();

        int player_left = LanternInStormSpiritManager.get_left(player);
        int lantern_left = LanternInStormSpiritManager.get(player, lantern);
        if(spirits > player_left){
            serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("灵魂不够了……")));
            return;
        }
        if(spirits + lantern_left < 0){
            serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("灯笼里没有你的灵魂了……")));
            return;
        }
        LanternInStormSpiritManager.increase_left(player, -spirits);
        LanternInStormSpiritManager.increase(player, lantern, spirits);
        serverPlayer.getServer().getPlayerManager().getPlayerList().forEach(LanternInStormSpiritManager::sendAll);
        if (spirits>0) serverPlayer.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("放入了"+spirits+"点灵魂").withColor(0xFCA106)));
        else serverPlayer.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("取回了"+(-spirits)+"点灵魂").withColor(0x815C94)));
        if (LanternInStormSpiritManager.get(player, lantern) > 0) serverPlayer.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("半径"+lantern_list.get(lantern).getRadius()+"的球内已被这个灯笼稳定").withColor(0xBBBBBB)));
        serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal("你的灵魂剩余："+LanternInStormSpiritManager.get_left(player)+"点")));
    }

}