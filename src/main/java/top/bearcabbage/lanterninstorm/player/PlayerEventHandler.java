package top.bearcabbage.lanterninstorm.player;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlock;

import static top.bearcabbage.lanterninstorm.LanternInStormItems.SPIRIT_FRAG_ITEM;
import static top.bearcabbage.lanterninstorm.LanternInStormItems.useSpiritFrag;
import static top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlock.STARTUP;

public abstract class PlayerEventHandler extends LanternInStorm {

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack itemStack = player.getStackInHand(hand);
            // 使用手中的灵魂碎片，转化为玩家数值属性灵魂
            if (itemStack.isOf(SPIRIT_FRAG_ITEM)) return useSpiritFrag(world, player, itemStack);
            return TypedActionResult.pass(itemStack);
        });
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            BlockState state = player.getWorld().getBlockState(pos);
            if (state.getBlock() instanceof SpiritLanternBlock && state.get(STARTUP)) {
                if (!world.isClient) ((ServerPlayerEntity)player).networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal("§c你不能破坏一个有灵魂的路灯（/正义）")));
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }

    // 处理玩家每个tick的事件
    public static void onTick(ServerPlayerEntity player) {
        if (player instanceof PlayerAccessor lsPlayer  && lsPlayer.getLS().onTick()){
            // 疲惫的状态：扛灵魂灯笼
            if (!lsPlayer.getLS().getSafety()){
                lsPlayer.getLS().onUnstableTick();
            }
        }
    }
}
