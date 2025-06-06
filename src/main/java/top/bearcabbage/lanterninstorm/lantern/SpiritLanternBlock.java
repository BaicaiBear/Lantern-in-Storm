package top.bearcabbage.lanterninstorm.lantern;


import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import top.bearcabbage.lanterninstorm.player.LiSPlayer;
import top.bearcabbage.lanterninstorm.player.Player;


public class SpiritLanternBlock extends LanternBlock implements Waterloggable {
    public static final MapCodec<SpiritLanternBlock> CODEC = createCodec(SpiritLanternBlock::new);
    public static final BooleanProperty STARTUP;
    public static final DirectionProperty FACING;


    public SpiritLanternBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(STARTUP, false));
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
        if(state.get(STARTUP)) BorderParticle.DrawCubicBorderParticle.drawBox(world, pos, random, 1);
        super.randomDisplayTick(state, world, pos, random);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        Player lsplayer = ((LiSPlayer) player).getLS();
        if (!state.get(STARTUP)) {
            if (lsplayer.addLantern(world.getRegistryKey(), pos)) {
                state = state.cycle(STARTUP);
                world.setBlockState(pos, state, 3);
                return ActionResult.SUCCESS;
            }
            else return ActionResult.FAIL;
        } else {
            if (lsplayer.removeLantern(world.getRegistryKey(), pos)) {
                state = state.cycle(STARTUP);
                world.setBlockState(pos, state, 3);
                return ActionResult.SUCCESS;
            }
            else return ActionResult.FAIL;
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HANGING, WATERLOGGED, STARTUP, FACING);
    }

    static {
        STARTUP = BooleanProperty.of("startup");
        FACING = HorizontalFacingBlock.FACING;
    }

}
