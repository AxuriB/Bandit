package cn.elytra.mod.bandit.common.event

import cn.elytra.mod.bandit.common.mining.VeinMiningHandler
import cn.elytra.mod.bandit.common.mining.VeinMiningSavedData
import cn.elytra.mod.bandit.common.network.BanditNetwork
import cn.elytra.mod.bandit.common.network.packet.S2CUpdateModePacket
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@EventBusSubscriber
object BanditListeners {

	@JvmStatic
	@SubscribeEvent
	fun onBlockBreaking(e: BlockEvent.BreakEvent) {
		val player = e.player
		if(player is EntityPlayerMP) {
			if(VeinMiningHandler.getStatus(player)) {
				VeinMiningHandler.runVeinMining(e.world, e.pos, player)
			}
		}
	}

	@JvmStatic
	@SubscribeEvent
	fun onPlayerJoin(e: net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent) {
		val player = e.player
		if(player is EntityPlayerMP) {
			if(VeinMiningSavedData.get().hasVeinMiningMode(player)) {
				val mode = VeinMiningSavedData.get().getVeinMiningMode(player)
				BanditNetwork.sendMessageToPlayer(S2CUpdateModePacket().apply { this.mode = mode.ordinal }, player)
				player.sendMessage(TextComponentTranslation("message.bandit.mode-updated-by-server", mode.displayText))
			}
		}
	}

}