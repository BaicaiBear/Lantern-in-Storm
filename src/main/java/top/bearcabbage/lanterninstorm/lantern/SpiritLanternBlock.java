package top.bearcabbage.lanterninstorm.lantern;


import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.util.math.GlobalPos;



public class SpiritLanternBlock extends LanternBlock implements Waterloggable {
    public static final MapCodec<SpiritLanternBlock> CODEC = createCodec(SpiritLanternBlock::new);
    public static final BooleanProperty STARTUP;
    public static final DirectionProperty FACING;
    public static final IntProperty RADIUS;
    protected static final VoxelShape HANGING_SPIRIT_LANTERN_SHAPE;
    protected static final VoxelShape STANDING_SPIRIT_LANTERN_SHAPE;

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

        // 非主世界且半径为0时阻止点亮
        if (!world.getRegistryKey().getValue().getNamespace().equals("minecraft") && state.get(RADIUS) == 0) {
            ((ServerPlayerEntity) player).networkHandler.sendPacket(
                    new OverlayMessageS2CPacket(Text.of("这种彩灯抵御不住球球世界的噩梦侵蚀"))
            );
            return ActionResult.SUCCESS;
        }

        // 切换彩灯状态
        state = state.cycle(STARTUP);
        world.setBlockState(pos, state, 3);

        if (state.get(STARTUP)) {
            ((ServerPlayerEntity) player).networkHandler.sendPacket(
                    new TitleS2CPacket(Text.literal("又点亮了一盏彩灯！").withColor(0xFCA106))
            );
            ((ServerPlayerEntity) player).networkHandler.sendPacket(
                    new SubtitleS2CPacket(
                            Text.literal("附近半径" + (state.get(RADIUS) == 0 ? 16 : state.get(RADIUS)) + "的立方体稳定下来了")
                                    .withColor(0xBBBBBB)
                    )
            );
        } else {
            ((ServerPlayerEntity) player).networkHandler.sendPacket(
                    new OverlayMessageS2CPacket(Text.of("这盏彩灯被关掉了"))
            );
        }

        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HANGING, WATERLOGGED, STARTUP, FACING, RADIUS);
    }


    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (Boolean)state.get(HANGING) ? HANGING_SPIRIT_LANTERN_SHAPE : STANDING_SPIRIT_LANTERN_SHAPE;
    }

    static {
        STARTUP = BooleanProperty.of("startup");
        FACING = HorizontalFacingBlock.FACING;
        RADIUS = IntProperty.of("radius",0,16);
        HANGING_SPIRIT_LANTERN_SHAPE = VoxelShapes.union(Block.createCuboidShape(3.0, 2.0, 3.0, 13.0, 12.0, 13.0));
        STANDING_SPIRIT_LANTERN_SHAPE = VoxelShapes.union(Block.createCuboidShape(3.0, 3.0, 3.0, 13.0, 13.0, 13.0));
    }

}
