package cn.elytra.mod.bandit.common

import cn.elytra.mod.bandit.BanditMod
import cn.elytra.mod.bandit.common.command.BanditAdminCommand
import cn.elytra.mod.bandit.common.command.BanditCommand
import cn.elytra.mod.bandit.common.network.BanditNetwork
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent

open class CommonProxy {

	open fun preInit(e: FMLPreInitializationEvent) {
		e.modLog.info("Bandit is invading!")

		BanditMod.logger.info("Registering network")
		BanditNetwork.register(e)
	}

	open fun serverStarting(e: FMLServerStartingEvent) {
		BanditMod.logger.info("Linking server ${e.server}")
		BanditCoroutines.MinecraftServerRef = e.server

		BanditMod.logger.info("Registering commands")
		e.registerServerCommand(BanditCommand)
		e.registerServerCommand(BanditAdminCommand)
	}

}