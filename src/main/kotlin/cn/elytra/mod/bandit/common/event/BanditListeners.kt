package cn.elytra.mod.bandit.common.event

import cn.elytra.mod.bandit.common.mining.VeinMiningHandler
import net.minecraft.entity.player.EntityPlayerMP
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

}