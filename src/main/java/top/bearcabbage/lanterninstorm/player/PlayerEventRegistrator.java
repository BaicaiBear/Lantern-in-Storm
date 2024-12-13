package top.bearcabbage.lanterninstorm.player;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResult;
import top.bearcabbage.lanterninstorm.LanternInStorm;

public class PlayerEventRegistrator extends LanternInStorm {

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {

            return ActionResult.PASS;
        });
    }
}
