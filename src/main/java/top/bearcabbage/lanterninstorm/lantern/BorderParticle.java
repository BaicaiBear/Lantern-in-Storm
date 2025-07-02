package top.bearcabbage.lanterninstorm.lantern;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
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
        public static void drawBox(World world, BlockPos pos ,Random random, double amplitude, int radius) {
            double centerX = pos.getX() + 0.5;
            double centerY = pos.getY() + 0.5;
            double centerZ = pos.getZ() + 0.5;

            double velocityX = -0.1 + (0.1 - (-0.1)) * random.nextDouble();
            double velocityY = -0.1 + (0.1 - (-0.1)) * random.nextDouble();
            double velocityZ = -0.1 + (0.1 - (-0.1)) * random.nextDouble();
            /*
            double velocityX = 0;
            double velocityY = 0;
            double velocityZ = 0;*/
            double[][] points = getPoints(centerX, centerY, centerZ, amplitude, radius);
            drawLine(world, points[0], points[1],velocityX,velocityY,velocityZ);
            drawLine(world, points[1], points[2],velocityX,velocityY,velocityZ);
            drawLine(world, points[2], points[3],velocityX,velocityY,velocityZ);
            drawLine(world, points[3], points[0],velocityX,velocityY,velocityZ);
            drawLine(world, points[4], points[5],velocityX,velocityY,velocityZ);
            drawLine(world, points[5], points[6],velocityX,velocityY,velocityZ);
            drawLine(world, points[6], points[7],velocityX,velocityY,velocityZ);
            drawLine(world, points[7], points[4],velocityX,velocityY,velocityZ);
            drawLine(world, points[0], points[4],velocityX,velocityY,velocityZ);
            drawLine(world, points[1], points[5],velocityX,velocityY,velocityZ);
            drawLine(world, points[2], points[6],velocityX,velocityY,velocityZ);
            drawLine(world, points[3], points[7],velocityX,velocityY,velocityZ);
        }

        private static double[][] getPoints(double centerX, double centerY, double centerZ, double amplitude, int radius) {
            if (radius==0) return new double[][] {
                    {centerX - (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerY - (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerZ - (double) amplitude * LanternInStorm.LANTERN_RADIUS},
                    {centerX + (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerY - (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerZ - (double) amplitude * LanternInStorm.LANTERN_RADIUS},
                    {centerX + (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerY + (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerZ - (double) amplitude * LanternInStorm.LANTERN_RADIUS},
                    {centerX - (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerY + (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerZ - (double) amplitude * LanternInStorm.LANTERN_RADIUS},
                    {centerX - (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerY - (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerZ + (double) amplitude * LanternInStorm.LANTERN_RADIUS},
                    {centerX + (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerY - (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerZ + (double) amplitude * LanternInStorm.LANTERN_RADIUS},
                    {centerX + (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerY + (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerZ + (double) amplitude * LanternInStorm.LANTERN_RADIUS},
                    {centerX - (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerY + (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerZ + (double) amplitude * LanternInStorm.LANTERN_RADIUS}
            };
            else return new double[][] {
                    {centerX - (double) amplitude * radius, centerY - (double) amplitude * radius, centerZ - (double) amplitude * radius},
                    {centerX + (double) amplitude * radius, centerY - (double) amplitude * radius, centerZ - (double) amplitude * radius},
                    {centerX + (double) amplitude * radius, centerY + (double) amplitude * radius, centerZ - (double) amplitude * radius},
                    {centerX - (double) amplitude * radius, centerY + (double) amplitude * radius, centerZ - (double) amplitude * radius},
                    {centerX - (double) amplitude * radius, centerY - (double) amplitude * radius, centerZ + (double) amplitude * radius},
                    {centerX + (double) amplitude * radius, centerY - (double) amplitude * radius, centerZ + (double) amplitude * radius},
                    {centerX + (double) amplitude * radius, centerY + (double) amplitude * radius, centerZ + (double) amplitude * radius},
                    {centerX - (double) amplitude * radius, centerY + (double) amplitude * radius, centerZ + (double) amplitude * radius}
            };
        }

        private static void drawLine(World world, double[] start, double[] end,double velocityX ,double velocityY ,double velocityZ) {
            double steps = 8; // 控制线条的分辨率
            for (int i = 0; i <= steps; i++) {
                double t = i / steps;
                double x = start[0] + (end[0] - start[0]) * t;
                double y = start[1] + (end[1] - start[1]) * t;
                double z = start[2] + (end[2] - start[2]) * t;
                world.addParticle(BorderParticle.border, x, y, z, velocityX, velocityY, velocityZ);
            }
        }
    }

    public static class DrawSoulCubicBorderParticle {
        public static void drawBox(World world, BlockPos pos ,Random random, double amplitude) {
            double centerX = pos.getX() + 0.5;
            double centerY = pos.getY() + 0.5;
            double centerZ = pos.getZ() + 0.5;

            double velocityX = -0.1 + (0.1 - (-0.1)) * random.nextDouble();
            double velocityY = -0.1 + (0.1 - (-0.1)) * random.nextDouble();
            double velocityZ = -0.1 + (0.1 - (-0.1)) * random.nextDouble();
            /*
            double velocityX = 0;
            double velocityY = 0;
            double velocityZ = 0;*/
            double[][] points = getPoints(centerX, centerY, centerZ, amplitude);
            drawLine(world, points[0], points[1],velocityX,velocityY,velocityZ);
            drawLine(world, points[1], points[2],velocityX,velocityY,velocityZ);
            drawLine(world, points[2], points[3],velocityX,velocityY,velocityZ);
            drawLine(world, points[3], points[0],velocityX,velocityY,velocityZ);
            drawLine(world, points[4], points[5],velocityX,velocityY,velocityZ);
            drawLine(world, points[5], points[6],velocityX,velocityY,velocityZ);
            drawLine(world, points[6], points[7],velocityX,velocityY,velocityZ);
            drawLine(world, points[7], points[4],velocityX,velocityY,velocityZ);
            drawLine(world, points[0], points[4],velocityX,velocityY,velocityZ);
            drawLine(world, points[1], points[5],velocityX,velocityY,velocityZ);
            drawLine(world, points[2], points[6],velocityX,velocityY,velocityZ);
            drawLine(world, points[3], points[7],velocityX,velocityY,velocityZ);
        }

        private static double[][] getPoints(double centerX, double centerY, double centerZ, double amplitude) {
            return new double[][] {
                    {centerX - (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerY - (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerZ - (double) amplitude * LanternInStorm.LANTERN_RADIUS},
                    {centerX + (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerY - (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerZ - (double) amplitude * LanternInStorm.LANTERN_RADIUS},
                    {centerX + (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerY + (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerZ - (double) amplitude * LanternInStorm.LANTERN_RADIUS},
                    {centerX - (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerY + (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerZ - (double) amplitude * LanternInStorm.LANTERN_RADIUS},
                    {centerX - (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerY - (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerZ + (double) amplitude * LanternInStorm.LANTERN_RADIUS},
                    {centerX + (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerY - (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerZ + (double) amplitude * LanternInStorm.LANTERN_RADIUS},
                    {centerX + (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerY + (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerZ + (double) amplitude * LanternInStorm.LANTERN_RADIUS},
                    {centerX - (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerY + (double) amplitude * LanternInStorm.LANTERN_RADIUS, centerZ + (double) amplitude * LanternInStorm.LANTERN_RADIUS}
            };
        }

        private static void drawLine(World world, double[] start, double[] end,double velocityX ,double velocityY ,double velocityZ) {
            double steps = 30; // 控制线条的分辨率
            for (int i = 0; i <= steps; i++) {
                double t = i / steps;
                double x = start[0] + (end[0] - start[0]) * t;
                double y = start[1] + (end[1] - start[1]) * t;
                double z = start[2] + (end[2] - start[2]) * t;
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, velocityX, velocityY, velocityZ);
            }
        }
    }

}