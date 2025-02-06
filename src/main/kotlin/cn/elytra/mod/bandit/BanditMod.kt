package cn.elytra.mod.bandit

import cn.elytra.mod.bandit.BanditMod.MOD_ID
import cn.elytra.mod.bandit.bandit.Tags
import cn.elytra.mod.bandit.common.CommonProxy
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(
	modid = MOD_ID,
	name = "Bandit",
	version = Tags.VERSION,
	modLanguageAdapter = "io.github.chaosunity.forgelin.KotlinAdapter"
)
object BanditMod {

	const val MOD_ID = "bandit"

	@JvmStatic
	val logger: Logger = LogManager.getLogger()

	@SidedProxy(serverSide = "cn.elytra.mod.bandit.common.CommonProxy", clientSide = "cn.elytra.mod.bandit.client.ClientProxy")
	lateinit var proxy: CommonProxy

	@EventHandler
	fun preInit(e: FMLPreInitializationEvent) = proxy.preInit(e)

	@EventHandler
	fun onServerStarting(e: FMLServerStartingEvent) = proxy.serverStarting(e)

}