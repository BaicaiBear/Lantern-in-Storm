package top.bearcabbage.lanterninstorm.player;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlock;

import static top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlock.STARTUP;

public abstract class PlayerEventHandler extends LanternInStorm {

    public static void register() {

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            BlockState state = player.getWorld().getBlockState(pos);
            if (state.getBlock() instanceof SpiritLanternBlock && state.get(STARTUP)) {
                if (!world.isClient) ((ServerPlayerEntity)player).networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal("§c你不能破坏一个点亮了的路灯（/正义）")));
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }

    // 处理玩家每个tick的事件
    public static void onTick(ServerPlayerEntity player) {
        if (player instanceof LiSPlayer lsPlayer) lsPlayer.getLS().onTick();
    }
}
