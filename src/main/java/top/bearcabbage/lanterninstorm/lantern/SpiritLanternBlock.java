package top.bearcabbage.lanterninstorm.lantern;


import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.lanterninstorm.LanternInStormItems;
import net.minecraft.util.math.GlobalPos;


import java.util.HashMap;
import java.util.Map;

import static top.bearcabbage.lanterninstorm.LanternInStorm.LANTERN_RADIUS;


public class SpiritLanternBlock extends LanternBlock implements Waterloggable {
    public static final MapCodec<SpiritLanternBlock> CODEC = createCodec(SpiritLanternBlock::new);
    public static final BooleanProperty STARTUP;
    public static final DirectionProperty FACING;
    public static final IntProperty RADIUS;
    public static final IntProperty REMAIN_TIME_IN_SECOND;
    protected static final VoxelShape HANGING_SPIRIT_LANTERN_SHAPE;
    protected static final VoxelShape STANDING_SPIRIT_LANTERN_SHAPE;

    public static final int TimePerItem = 5 * 60;

    public SpiritLanternBlock(Settings settings, int radius) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(STARTUP, false).with(RADIUS, radius));
    }

    public SpiritLanternBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(STARTUP, false).with(RADIUS, 0));
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if(state.get(STARTUP)) BorderParticle.DrawCubicBorderParticle.drawBox(world, pos, random, 1, state.get(RADIUS));
        super.randomDisplayTick(state, world, pos, random);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        GlobalPos gpos = GlobalPos.create(world.getRegistryKey(), pos);
        if (!world.getRegistryKey().getValue().getNamespace().equals("starry_skies")) {
            state = state.cycle(STARTUP);
            world.setBlockState(pos, state, 3);
            if (state.get(STARTUP)) {
                LanternTimeManager.addLantern(gpos, 0); // 0 means infinite or not tracked
                ((ServerPlayerEntity)player).networkHandler.sendPacket(new TitleS2CPacket(Text.literal("又点亮了一盏彩灯！").withColor(0xFCA106)));
                ((ServerPlayerEntity)player).networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("附近半径"+(state.get(RADIUS)==0?16:state.get(RADIUS))+"的立方体稳定下来了").withColor(0xBBBBBB)));
            } else {
                LanternTimeManager.removeLantern(gpos);
                ((ServerPlayerEntity)player).networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("这盏彩灯被关掉了")));
            }
            return ActionResult.SUCCESS;
        } else {
            if (state.get(RADIUS)==0) {
                ((ServerPlayerEntity)player).networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("这种彩灯抵御不住球球世界的噩梦侵蚀")));
                return ActionResult.FAIL;
            }
            else if (!state.get(STARTUP) && !player.getMainHandStack().isOf(Registries.ITEM.get(Identifier.of("numismatic-overhaul","silver_coin")))) {
                ((ServerPlayerEntity)player).networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("在可怕的世界里你需要花费银币才可以点亮彩灯")));
                return ActionResult.SUCCESS;
            }
            else if (state.get(STARTUP) && player.getMainHandStack().isEmpty()) {
                LanternTimeManager.removeLantern(gpos);
                state.cycle(STARTUP);
                ((ServerPlayerEntity)player).networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("这盏彩灯被关掉了")));
                return ActionResult.SUCCESS;
            }
            else if (state.get(STARTUP) && !(player.getMainHandStack().isEmpty() || player.getMainHandStack().isOf(Registries.ITEM.get(Identifier.of("numismatic-overhaul","silver_coin"))))) {
                ((ServerPlayerEntity)player).networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("继续加入银币可以增加彩灯时间，空手右键会关掉彩灯（但不会返还银币）")));
                return ActionResult.SUCCESS;
            }
            else {
                player.getMainHandStack().setCount(player.getMainHandStack().getCount() - 1);
                int current = LanternTimeManager.getTime(gpos) == null ? 0 : LanternTimeManager.getTime(gpos);
                int newTime = current + TimePerItem;
                LanternTimeManager.setTime(gpos, newTime);
                if (!state.get(STARTUP)) {
                    state = state.cycle(STARTUP);
                    world.setBlockState(pos, state, 3);
                    ((ServerPlayerEntity)player).networkHandler.sendPacket(new TitleS2CPacket(Text.literal("又点亮了一盏彩灯！").withColor(0xFCA106)));
                } else ((ServerPlayerEntity)player).networkHandler.sendPacket(new TitleS2CPacket(Text.literal("彩灯的时间增加了！").withColor(0x06BDFC)));
                ((ServerPlayerEntity)player).networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("附近半径"+state.get(RADIUS)+"的立方体稳定下来了").withColor(0xBBBBBB)));
                return ActionResult.SUCCESS;
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HANGING, WATERLOGGED, STARTUP, FACING, RADIUS);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        if (!world.isClient && state.getBlock() != newState.getBlock()) {
            GlobalPos gpos = GlobalPos.create(world.getRegistryKey(), pos);
            LanternTimeManager.removeLantern(gpos);
        }
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (Boolean)state.get(HANGING) ? HANGING_SPIRIT_LANTERN_SHAPE : STANDING_SPIRIT_LANTERN_SHAPE;
    }


    static {
        STARTUP = BooleanProperty.of("startup");
        FACING = HorizontalFacingBlock.FACING;
        RADIUS = IntProperty.of("radius",0,16);
        REMAIN_TIME_IN_SECOND = null; // No longer used
        HANGING_SPIRIT_LANTERN_SHAPE = VoxelShapes.union(Block.createCuboidShape(3.0, 2.0, 3.0, 13.0, 12.0, 13.0));
        STANDING_SPIRIT_LANTERN_SHAPE = VoxelShapes.union(Block.createCuboidShape(3.0, 3.0, 3.0, 13.0, 13.0, 13.0));
    }

}
