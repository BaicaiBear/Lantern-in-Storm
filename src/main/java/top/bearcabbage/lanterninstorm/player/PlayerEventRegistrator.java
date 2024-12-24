package top.bearcabbage.lanterninstorm.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import org.lwjgl.glfw.GLFW;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.lanterninstorm.entity.PrivateLanternEntity;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;
import top.bearcabbage.lanterninstorm.item.PublicLanternItem;
import top.bearcabbage.lanterninstorm.item.SpirirtFragItem;
import top.bearcabbage.lanterninstorm.network.DistributingSpiritsPayload;

import static top.bearcabbage.lanterninstorm.item.PublicLanternItem.PUBLIC_LANTERN_ITEM;
import static top.bearcabbage.lanterninstorm.item.SpirirtFragItem.SPIRIT_FRAG_ITEM;

public abstract class PlayerEventRegistrator extends LanternInStorm {

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if ((!(player.getPassengerList().stream().anyMatch(entity1 -> entity1 instanceof SpiritLanternEntity) || player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT))) && entity instanceof SpiritLanternEntity lantern) {
                // 玩家与灯笼右键交互的操作
                if (lantern instanceof PrivateLanternEntity privateLantern && !privateLantern.getOwner().equals(player.getUuid())) {
                    return ActionResult.FAIL;
                }
                ClientPlayNetworking.send(new DistributingSpiritsPayload(lantern.getUuid(), 1));
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof SpiritLanternEntity lantern && (!player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT))) {
                // 玩家与灯笼左键交互的操作
                if (lantern instanceof PrivateLanternEntity privateLantern && !privateLantern.getOwner().equals(player.getUuid())) {
                    return ActionResult.FAIL;
                }
                ClientPlayNetworking.send(new DistributingSpiritsPayload(lantern.getUuid(), -1));
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }

    public static void register() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.getPassengerList().stream().anyMatch(entity1 -> entity1 instanceof SpiritLanternEntity) && player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT)) {
                SpiritLanternEntity lantern = (SpiritLanternEntity) player.getPassengerList().stream().filter(entity1 -> entity1 instanceof SpiritLanternEntity).findFirst().get();
                // 将灯笼放下来的操作
                lantern.stopRiding();
                return ActionResult.SUCCESS;
            } else if (entity instanceof SpiritLanternEntity lantern
                    && player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT)) {
                // 将灯笼捡起来的操作
                if (lantern instanceof PrivateLanternEntity privateLantern && !privateLantern.getOwner().equals(player.getUuid())) {
                    return ActionResult.FAIL;
                }
                if (lantern.startRiding(player, true)) return ActionResult.SUCCESS;
                return ActionResult.PASS;
            }
            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            if (player.getPassengerList().stream().anyMatch(entity1 -> entity1 instanceof SpiritLanternEntity) && player.getMainHandStack().isOf(Items.POPPED_CHORUS_FRUIT)) {
                SpiritLanternEntity spiritLanternEntity = (SpiritLanternEntity) player.getPassengerList().stream().filter(entity1 -> entity1 instanceof SpiritLanternEntity).findFirst().get();
                // 将灯笼放下来的操作
                spiritLanternEntity.stopRiding();
                return ActionResult.SUCCESS;
            } else return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            // 从item放置公共灯笼
            if (player.getMainHandStack().isOf(PUBLIC_LANTERN_ITEM)) return PublicLanternItem.useOnBlock(world, player, hitResult);
            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack itemStack = player.getStackInHand(hand);
            // 使用手中的灵魂碎片，转化为玩家数值属性灵魂
            if (itemStack.isOf(SPIRIT_FRAG_ITEM)) return SpirirtFragItem.use(world, player, itemStack);
            return TypedActionResult.pass(itemStack);
        });

//        //注册了滚轮事件绑定 但不知道怎么调用
//        KeyBinding scrollUpBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
//                "key.lanterninstorm.scroll.up",
//                InputUtil.Type.KEYSYM,
//                GLFW.GLFW_KEY_KP_ADD, // 假设使用小键盘的加号键模拟向上滚动
//                "category.lanterninstorm.keys"
//        ));
//
//        KeyBinding scrollDownBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
//                "key.lanterninstorm.scroll.down",
//                InputUtil.Type.KEYSYM,
//                GLFW.GLFW_KEY_KP_SUBTRACT, // 假设使用小键盘的减号键模拟向下滚动
//                "category.lanterninstorm.keys"
//        ));
    }
}
