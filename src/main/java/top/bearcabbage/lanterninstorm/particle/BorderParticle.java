package top.bearcabbage.lanterninstorm.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.BlockState;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.bearcabbage.lanterninstorm.LanternInStorm;

public class BorderParticle {
    public static final SimpleParticleType border = register("border", FabricParticleTypes.simple());
    private static SimpleParticleType register(String name, SimpleParticleType type) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(LanternInStorm.MOD_ID,name),type);
    }
    public static void registerModParticles(){
    }

    public static class DrawCubicBorderParticle {
        public static void drawBox(BlockState state, World world, BlockPos pos) {
            double centerX = pos.getX() + 0.5;
            double centerY = pos.getY() + 0.5;
            double centerZ = pos.getZ() + 0.5;
            double[][] points = getPoints(centerX, centerY, centerZ);
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

        private static double[][] getPoints(double centerX, double centerY, double centerZ) {
            return new double[][] {
                    {centerX - (double) LanternInStorm.LANTERN_RADIUS, centerY - (double) LanternInStorm.LANTERN_RADIUS, centerZ - (double) LanternInStorm.LANTERN_RADIUS},
                    {centerX + (double) LanternInStorm.LANTERN_RADIUS, centerY - (double) LanternInStorm.LANTERN_RADIUS, centerZ - (double) LanternInStorm.LANTERN_RADIUS},
                    {centerX + (double) LanternInStorm.LANTERN_RADIUS, centerY + (double) LanternInStorm.LANTERN_RADIUS, centerZ - (double) LanternInStorm.LANTERN_RADIUS},
                    {centerX - (double) LanternInStorm.LANTERN_RADIUS, centerY + (double) LanternInStorm.LANTERN_RADIUS, centerZ - (double) LanternInStorm.LANTERN_RADIUS},
                    {centerX - (double) LanternInStorm.LANTERN_RADIUS, centerY - (double) LanternInStorm.LANTERN_RADIUS, centerZ + (double) LanternInStorm.LANTERN_RADIUS},
                    {centerX + (double) LanternInStorm.LANTERN_RADIUS, centerY - (double) LanternInStorm.LANTERN_RADIUS, centerZ + (double) LanternInStorm.LANTERN_RADIUS},
                    {centerX + (double) LanternInStorm.LANTERN_RADIUS, centerY + (double) LanternInStorm.LANTERN_RADIUS, centerZ + (double) LanternInStorm.LANTERN_RADIUS},
                    {centerX - (double) LanternInStorm.LANTERN_RADIUS, centerY + (double) LanternInStorm.LANTERN_RADIUS, centerZ + (double) LanternInStorm.LANTERN_RADIUS}
            };
        }

        private static void drawLine(World world, double[] start, double[] end) {
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
    }

    public static class DrawHexagonBorderParticle {
        public static void drawBox(BlockState state, World world, BlockPos pos) {
            double centerX = pos.getX() + 0.5;
            double centerY = pos.getY() + 0.5;
            double centerZ = pos.getZ() + 0.5;
            double[][] points = getPoints(centerX, centerY, centerZ);
            drawLine(world, points[0], points[1]);
            drawLine(world, points[1], points[2]);
            drawLine(world, points[2], points[3]);
            drawLine(world, points[3], points[4]);
            drawLine(world, points[4], points[5]);
            drawLine(world, points[5], points[0]);
            drawLine(world, points[6], points[7]);
            drawLine(world, points[7], points[8]);
            drawLine(world, points[8], points[9]);
            drawLine(world, points[9], points[10]);
            drawLine(world, points[10], points[11]);
            drawLine(world, points[11], points[6]);
            drawLine(world, points[0], points[6]);
            drawLine(world, points[1], points[7]);
            drawLine(world, points[2], points[8]);
            drawLine(world, points[3], points[9]);
            drawLine(world, points[4], points[10]);
            drawLine(world, points[5], points[11]);

        }

        private static double[][] getPoints(double centerX, double centerY, double centerZ) {
            return new double[][] {
                    {centerX - (double) LanternInStorm.LANTERN_RADIUS, centerY - (double) LanternInStorm.LANTERN_HEIGHT, centerZ},
                    {centerX - (double) LanternInStorm.LANTERN_RADIUS * 0.5, centerY - (double) LanternInStorm.LANTERN_HEIGHT, centerZ + (double) LanternInStorm.LANTERN_RADIUS * 0.8660254038},
                    {centerX + (double) LanternInStorm.LANTERN_RADIUS * 0.5, centerY - (double) LanternInStorm.LANTERN_HEIGHT, centerZ + (double) LanternInStorm.LANTERN_RADIUS * 0.8660254038},
                    {centerX + (double) LanternInStorm.LANTERN_RADIUS, centerY - (double) LanternInStorm.LANTERN_HEIGHT, centerZ},
                    {centerX + (double) LanternInStorm.LANTERN_RADIUS * 0.5, centerY - (double) LanternInStorm.LANTERN_HEIGHT, centerZ - (double) LanternInStorm.LANTERN_RADIUS * 0.8660254038},
                    {centerX - (double) LanternInStorm.LANTERN_RADIUS * 0.5, centerY - (double) LanternInStorm.LANTERN_HEIGHT, centerZ - (double) LanternInStorm.LANTERN_RADIUS * 0.8660254038},

                    {centerX - (double) LanternInStorm.LANTERN_RADIUS, centerY + (double) LanternInStorm.LANTERN_HEIGHT, centerZ},
                    {centerX - (double) LanternInStorm.LANTERN_RADIUS * 0.5, centerY + (double) LanternInStorm.LANTERN_HEIGHT, centerZ + (double) LanternInStorm.LANTERN_RADIUS * 0.8660254038},
                    {centerX + (double) LanternInStorm.LANTERN_RADIUS * 0.5, centerY + (double) LanternInStorm.LANTERN_HEIGHT, centerZ + (double) LanternInStorm.LANTERN_RADIUS * 0.8660254038},
                    {centerX + (double) LanternInStorm.LANTERN_RADIUS, centerY + (double) LanternInStorm.LANTERN_HEIGHT, centerZ},
                    {centerX + (double) LanternInStorm.LANTERN_RADIUS * 0.5, centerY + (double) LanternInStorm.LANTERN_HEIGHT, centerZ - (double) LanternInStorm.LANTERN_RADIUS * 0.8660254038},
                    {centerX - (double) LanternInStorm.LANTERN_RADIUS * 0.5, centerY + (double) LanternInStorm.LANTERN_HEIGHT, centerZ - (double) LanternInStorm.LANTERN_RADIUS * 0.8660254038}
            };
        }

        private static void drawLine(World world, double[] start, double[] end) {
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
    }
}