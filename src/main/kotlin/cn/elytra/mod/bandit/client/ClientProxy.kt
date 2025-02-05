package cn.elytra.mod.bandit.client

import cn.elytra.mod.bandit.BanditMod
import cn.elytra.mod.bandit.common.CommonProxy
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

class ClientProxy : CommonProxy() {

	override fun preInit(e: FMLPreInitializationEvent) {
		super.preInit(e)

		BanditMod.logger.info("Registering keybindings")
		ClientRegistry.registerKeyBinding(BanditKeyBindings.trigger)
		ClientRegistry.registerKeyBinding(BanditKeyBindings.changeMode)
	}
}