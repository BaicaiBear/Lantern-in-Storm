package top.bearcabbage.lanterninstorm.lantern;


import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import top.bearcabbage.lanterninstorm.particle.BorderParticle;
import top.bearcabbage.lanterninstorm.player.LiSPlayer;
import top.bearcabbage.lanterninstorm.player.Player;

public class SpiritLanternBlock extends LanternBlock implements Waterloggable {
    public static final MapCodec<SpiritLanternBlock> CODEC = createCodec(SpiritLanternBlock::new);
    public static final BooleanProperty STARTUP;


    public SpiritLanternBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(STARTUP, false));
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        drawBox(state, world, pos);
        super.randomDisplayTick(state, world, pos, random);
    }

    private void drawBox(BlockState state, World world, BlockPos pos) {
        double offset = 8;
        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.5;
        double centerZ = pos.getZ() + 0.5;
        double[][] points = getPoints(centerX, centerY, centerZ, offset);
        drawLine(world, points[0], points[1]);
        drawLine(world, points[1], points[2]);
        drawLine(world, points[2], points[3]);
        drawLine(world, points[3], points[0]);
        drawLine(world, points[4], points[5]);
        drawLine(world, points[5], points[6]);
        drawLine(world, points[6], points[7]);
        drawLine(world, points[7], points[4]);
        drawLine(world, points[0], points[4]);
        drawLine(world, points[1], points[5]);
        drawLine(world, points[2], points[6]);
        drawLine(world, points[3], points[7]);
    }

    private double[][] getPoints(double centerX, double centerY, double centerZ, double offset) {
        return new double[][] {
                {centerX - offset, centerY - offset, centerZ - offset},
                {centerX + offset, centerY - offset, centerZ - offset},
                {centerX + offset, centerY + offset, centerZ - offset},
                {centerX - offset, centerY + offset, centerZ - offset},
                {centerX - offset, centerY - offset, centerZ + offset},
                {centerX + offset, centerY - offset, centerZ + offset},
                {centerX + offset, centerY + offset, centerZ + offset},
                {centerX - offset, centerY + offset, centerZ + offset}
        };
    }

    private void drawLine(World world, double[] start, double[] end) {
        double steps = 10; // 控制线条的分辨率
        for (int i = 0; i <= steps; i++) {
            double t = i / steps;
            double x = start[0] + (end[0] - start[0]) * t;
            double y = start[1] + (end[1] - start[1]) * t;
            double z = start[2] + (end[2] - start[2]) * t;

            // 添加粒子
            world.addParticle(BorderParticle.border, x, y, z, 0, 0, 0);
        }
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
        builder.add(HANGING, WATERLOGGED, STARTUP);
    }

    static {
        STARTUP = BooleanProperty.of("startup");
    }

}
