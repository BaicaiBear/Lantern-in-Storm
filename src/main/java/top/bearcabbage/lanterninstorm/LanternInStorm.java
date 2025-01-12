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
	public static final String MOD_ID = "lanterninstorm";
	public static final Logger LOGGER = LoggerFactory.getLogger("lantern-in-storm");
	public static final NbtDataStorage LSData = new NbtDataStorage("LS_Data");

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