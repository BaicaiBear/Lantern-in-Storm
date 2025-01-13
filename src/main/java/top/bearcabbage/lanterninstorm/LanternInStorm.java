package top.bearcabbage.lanterninstorm;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.NbtDataStorage;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlocks;
import top.bearcabbage.lanterninstorm.lantern.BeginningLanternEntity;
import top.bearcabbage.lanterninstorm.particle.BorderParticle;
import top.bearcabbage.lanterninstorm.player.PlayerEventHandler;


public class LanternInStorm implements ModInitializer {
	public static final String MOD_ID = "lantern-in-storm";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final NbtDataStorage LSData = new NbtDataStorage("LS_Data");

	public static final int INIT_SPIRIT = 5;
	public static final int UPGRADE_FRAG = 1;
	public static final int TICK_INTERVAL = 20;
	public static final int LANTERN_RADIUS = 8;
	public static final int LANTERN_HEIGHT = 8;

	@Override
	public void onInitialize() {
		// 获取配置文件
		PlayerDataApi.register(LSData);
		// 注册内容
		SpiritLanternBlocks.initialize();
		BeginningLanternEntity.initialize();
		LanternInStormItems.initialize();
		// 注册事件
		PlayerEventHandler.register();
		//注册边界粒子
		BorderParticle.registerModParticles();
	}
}