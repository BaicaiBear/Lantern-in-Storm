package top.bearcabbage.lanterninstorm.player;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;

public abstract class PlayerEventRegistrator extends LanternInStorm {

    public static void register() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.getPassengerList().stream().anyMatch(entity1 -> entity1 instanceof SpiritLanternEntity) && player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT)) {
                SpiritLanternEntity spiritLanternEntity = (SpiritLanternEntity) player.getPassengerList().stream().filter(entity1 -> entity1 instanceof SpiritLanternEntity).findFirst().get();
                // 将灯笼放下来的操作
                player.sendMessage(Text.of("Down"));
                spiritLanternEntity.stopRiding();
                return ActionResult.FAIL;
            } else if (entity instanceof SpiritLanternEntity) {
                if (player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT)) {
                    // 将灯笼捡起来的操作
                    player.sendMessage(Text.of("Up"));
                    entity.startRiding(player, true);
                    return ActionResult.FAIL;
                } else {
                    // 玩家与灯笼右键交互的操作
                    player.sendMessage(Text.of("Right"));
                    return ActionResult.FAIL;
                }
            } else return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            if (player.getPassengerList().stream().anyMatch(entity1 -> entity1 instanceof SpiritLanternEntity) && player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT)) {
                SpiritLanternEntity spiritLanternEntity = (SpiritLanternEntity) player.getPassengerList().stream().filter(entity1 -> entity1 instanceof SpiritLanternEntity).findFirst().get();
                // 将灯笼放下来的操作
                player.sendMessage(Text.of("Down"));
                spiritLanternEntity.stopRiding();
                return ActionResult.FAIL;
            } else return ActionResult.PASS;
        });


        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof SpiritLanternEntity && (!player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT))) {
                    // 玩家与灯笼左键交互的操作
                player.sendMessage(Text.of("Left"));
                return ActionResult.FAIL;
            } else return ActionResult.PASS;
        });
    }
}
