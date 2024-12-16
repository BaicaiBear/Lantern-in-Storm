package top.bearcabbage.lanterninstorm.player;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.lwjgl.glfw.GLFW;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;
import top.bearcabbage.lanterninstorm.interfaces.PlayerAccessor;

public abstract class PlayerEventRegistrator extends LanternInStorm {

    public static void register() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.getPassengerList().stream().anyMatch(entity1 -> entity1 instanceof SpiritLanternEntity) && player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT)) {
                SpiritLanternEntity lantern = (SpiritLanternEntity) player.getPassengerList().stream().filter(entity1 -> entity1 instanceof SpiritLanternEntity).findFirst().get();
                // 将灯笼放下来的操作
                player.sendMessage(Text.of("Down"));
                lantern.stopRiding();
                return ActionResult.SUCCESS;
            } else if (entity instanceof SpiritLanternEntity lantern) {
                if (player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT)) {
                    // 将灯笼捡起来的操作
                    player.sendMessage(Text.of("Up"));
                    if (lantern.startRiding(player,true)) return ActionResult.SUCCESS;
                    return ActionResult.PASS;
                } else if (player instanceof ServerPlayerEntity serverPlayer) {
                    // 玩家与灯笼右键交互的操作
                    player.sendMessage(Text.of("Right"));
                    return ((PlayerAccessor)serverPlayer).getLS().distributeSpirits(lantern, 1);
                }
            }
            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            if (player.getPassengerList().stream().anyMatch(entity1 -> entity1 instanceof SpiritLanternEntity) && player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT)) {
                SpiritLanternEntity spiritLanternEntity = (SpiritLanternEntity) player.getPassengerList().stream().filter(entity1 -> entity1 instanceof SpiritLanternEntity).findFirst().get();
                // 将灯笼放下来的操作
                player.sendMessage(Text.of("Down"));
                spiritLanternEntity.stopRiding();
                return ActionResult.SUCCESS;
            } else return ActionResult.PASS;
        });


        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof SpiritLanternEntity lantern && player instanceof ServerPlayerEntity serverPlayer && (!player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT))) {
                    // 玩家与灯笼左键交互的操作
                player.sendMessage(Text.of("Left"));
                return ((PlayerAccessor)serverPlayer).getLS().distributeSpirits(lantern, -1);
            } else return ActionResult.PASS;
        });
    }
    //注册了滚轮事件绑定 但不知道怎么调用
        KeyBinding scrollUpBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lanterninstorm.scroll.up",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_ADD, // 假设使用小键盘的加号键模拟向上滚动
                "category.lanterninstorm.keys"
        ));

        KeyBinding scrollDownBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lanterninstorm.scroll.down",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_SUBTRACT, // 假设使用小键盘的减号键模拟向下滚动
                "category.lanterninstorm.keys"
        ));
}
