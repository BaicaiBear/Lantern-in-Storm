package top.bearcabbage.lanterninstorm.player;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;

public class PlayerEventRegistrator extends LanternInStorm {

    public static void register() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.getPassengerList().stream().anyMatch(entity1 -> entity1 instanceof SpiritLanternEntity) && player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT)) {
                SpiritLanternEntity spiritLanternEntity = (SpiritLanternEntity) player.getPassengerList().stream().filter(entity1 -> entity1 instanceof SpiritLanternEntity).findFirst().get();
                // 将灯笼放下来的操作
                return ActionResult.FAIL;
            } else if (entity instanceof SpiritLanternEntity) {
                if (player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT)) {
                    // 将灯笼捡起来的操作
                    return ActionResult.FAIL;
                } else {
                    // 玩家与灯笼右键交互的操作
                    return ActionResult.FAIL;
                }
            } else return ActionResult.PASS;
        });


        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof SpiritLanternEntity && (!player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT))) {
                    // 玩家与灯笼左键交互的操作
                    return ActionResult.FAIL;
            } else return ActionResult.PASS;
        });
    }
}
