package top.bearcabbage.lanterninstorm.lantern;

import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BeginningLanternEntity extends Entity {

    public int Age;
    public static Map<UUID, BeginningLanternEntity> lantern_list = new HashMap<>();

    public static final EntityType<BeginningLanternEntity> BEGINNING_LANTERN =  Registry.register(Registries.ENTITY_TYPE, Identifier.of("lanterninstorm","beginning_lantern"), EntityType.Builder.create(BeginningLanternEntity::new, SpawnGroup.MISC)
            .makeFireImmune()
            .dimensions(2.0F, 2.0F)
            .maxTrackingRange(64)
            .trackingTickInterval(Integer.MAX_VALUE).build("beginning_lantern"));

    public static void create(World world, ServerPlayerEntity player) {
        BeginningLanternEntity lantern = new BeginningLanternEntity(BEGINNING_LANTERN, world);
        lantern.setPos(player.getX(), player.getY(), player.getZ());
        world.spawnEntity(lantern);
        lantern.setCustomName(Text.of("入梦点["+player.getName().getLiteralString()+"]"));
        player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("大鹏的梦").withColor(0x525288)));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("这个「灯笼」是你的起点").withColor(0xFFFFFF)));
    }

    public static void initialize(){}

    public BeginningLanternEntity(EntityType<? extends BeginningLanternEntity> entityType, World world) {
        super(entityType, world);
        this.intersectionChecked = true;
        this.Age = this.random.nextInt(100000);
    }

    protected void initDataTracker(DataTracker.Builder builder) {}

    public void tick() {
        if (this.getY() < this.getWorld().getBottomY() && !this.getWorld().isClient) {
            this.discard();
        }
        ++this.Age;
        this.checkBlockCollision();
        this.tickPortalTeleportation();
    }

    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }

    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }



    public boolean canHit() {
        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return true;
    }
}