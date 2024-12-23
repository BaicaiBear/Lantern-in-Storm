package top.bearcabbage.lanterninstorm.entity;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import top.bearcabbage.lanterninstorm.network.OwnerQueryPayload;

import java.util.UUID;

import static top.bearcabbage.lanterninstorm.network.NetworkingConstants.NULL_UUID;


/*
    玩家一个人用的私有灯笼
*/
public class PrivateLanternEntity extends SpiritLanternEntity {
    private UUID owner;

    public PrivateLanternEntity(EntityType<? extends SpiritLanternEntity> entityType, World world) {
        super(entityType, world);
    }

    public static void create(World world, ServerPlayerEntity player) {
        PrivateLanternEntity lantern = new PrivateLanternEntity(PRIVATE_LANTERN, world);
        lantern.setOwner(player.getUuid());
        lantern.setPos(player.getX(), player.getY(), player.getZ());
        world.spawnEntity(lantern);
        player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("大鹏的梦").withColor(0x525288)));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("你在颤抖，右键面前的「灯笼」试试吧").withColor(0xFFFFFF)));
    }

    public void setOwner(UUID owner) {
        if(this.owner == null || this.owner.equals(NULL_UUID)) {
            this.owner = owner;
        }
    }

    public UUID getOwner() {
        return owner==null ? NULL_UUID : owner;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.getOwner() != null) nbt.put("owner", NbtHelper.fromUuid(this.getOwner()));
        else nbt.put("owner", NbtHelper.fromUuid(NULL_UUID));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("owner")) this.setOwner(NbtHelper.toUuid(nbt.get("owner")));
        else this.setOwner(NULL_UUID);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient && (this.getOwner() == null || this.getOwner().equals(NULL_UUID))) {
            ClientPlayNetworking.send(new OwnerQueryPayload(this.getUuid(), NULL_UUID));
        }
    }


    @Override
    public boolean shouldRender(double distance) {
        // Private lanterns should only render for their owner
        if (MinecraftClient.getInstance().player != null && (this.getOwner() == null || this.getOwner().equals(NULL_UUID) || !this.getOwner().equals(MinecraftClient.getInstance().player.getUuid()))) {
            return false;
        }

        double d = this.getBoundingBox().getAverageSideLength();
        if (Double.isNaN(d)) {
            d = 1.0;
        }

        d *= 64.0;
        return distance < d * d;
    }
}