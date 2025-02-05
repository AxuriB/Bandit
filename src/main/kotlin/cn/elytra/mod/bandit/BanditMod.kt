package cn.elytra.mod.bandit

import cn.elytra.mod.bandit.common.CommonProxy
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(
	modid = "bandit",
	name = "Bandit",
	version = "1.0.0",
	modLanguageAdapter = "io.github.chaosunity.forgelin.KotlinAdapter"
)
object BanditMod {

	@JvmStatic
	val logger: Logger = LogManager.getLogger()

	@SidedProxy(serverSide = "cn.elytra.mod.bandit.common.CommonProxy", clientSide = "cn.elytra.mod.bandit.client.ClientProxy")
	lateinit var proxy: CommonProxy

	@EventHandler
	fun preInit(e: FMLPreInitializationEvent) = proxy.preInit(e)

	@EventHandler
	fun onServerStarting(e: FMLServerStartingEvent) {
		proxy.serverStarting(e)
	}

}