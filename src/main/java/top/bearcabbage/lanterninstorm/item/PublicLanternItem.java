package top.bearcabbage.lanterninstorm.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import top.bearcabbage.lanterninstorm.entity.PublicLanternEntity;

import java.util.Objects;

import static top.bearcabbage.lanterninstorm.LanternInStormClient.MOD_NAMESPACE;

public class PublicLanternItem {

    public static final Item PUBLIC_LANTERN_ITEM = register("public_lantern_item", new Item(new Item.Settings().maxCount(1)));


    public static <T extends Item> T register(String path, T item) {
        return Registry.register(Registries.ITEM, Identifier.of(MOD_NAMESPACE, path), item);
    }

    public static ActionResult useOnBlock(World world, PlayerEntity player1, BlockHitResult hitResult) {
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        } else {
            ServerPlayerEntity player = (ServerPlayerEntity) player1;
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
            } else player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("地方太窄，放不下公共灯笼了……")));
            return ActionResult.CONSUME;
        }
    }

    public static void initialize() {
    }

}
