package top.bearcabbage.lanterninstorm.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.impl.screenhandler.client.ClientNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Spawner;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.lwjgl.glfw.GLFW;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.lanterninstorm.entity.PrivateLanternEntity;
import top.bearcabbage.lanterninstorm.entity.PublicLanternEntity;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;
import top.bearcabbage.lanterninstorm.interfaces.PlayerAccessor;
import top.bearcabbage.lanterninstorm.item.PublicLanternItem;
import top.bearcabbage.lanterninstorm.network.DistributingSpiritsPayload;
import top.bearcabbage.lanterninstorm.network.NetworkingConstants;

import java.util.Objects;

import static top.bearcabbage.lanterninstorm.item.PublicLanternItem.PUBLIC_LANTERN_ITEM;

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
            if (player.getMainHandStack().isOf(PUBLIC_LANTERN_ITEM)) {
                if (!(world instanceof ServerWorld)) {
                    return ActionResult.SUCCESS;
                } else {
                    ItemStack itemStack = player.getMainHandStack();
                    BlockPos blockPos = hitResult.getBlockPos();
                    Direction direction = hitResult.getSide();
                    BlockState blockState = world.getBlockState(blockPos);
                    BlockEntity var8 = world.getBlockEntity(blockPos);
                    EntityType entityType;
                    BlockPos blockPos2;
                    if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
                        blockPos2 = blockPos;
                    } else {
                        blockPos2 = blockPos.offset(direction);
                    }

                    entityType = PublicLanternEntity.PUBLIC_LANTERN;
                    if (entityType.spawnFromItemStack((ServerWorld) world, itemStack, player, blockPos2, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP) != null) {
                        itemStack.decrement(1);
                        world.emitGameEvent(player, GameEvent.ENTITY_PLACE, blockPos);
                    }
                    return ActionResult.CONSUME;
                }
            }
            return ActionResult.PASS;
        });

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
}
