package cn.elytra.mod.bandit.client

import cn.elytra.mod.bandit.BanditMod
import cn.elytra.mod.bandit.common.mining.VeinMiningHandler
import cn.elytra.mod.bandit.common.network.BanditNetwork
import cn.elytra.mod.bandit.common.network.packet.C2SChangeModePacket
import cn.elytra.mod.bandit.common.network.packet.C2SChangeStatusPacket
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import net.minecraftforge.fml.relauncher.Side

@EventBusSubscriber(Side.CLIENT)
object ClientEventListeners {

	private var clientStatus = false
	private var currentMode = 0

	@JvmStatic
	@SubscribeEvent
	fun onKey(e: KeyInputEvent) {
		val keyStatus = BanditKeyBindings.trigger.isKeyDown
		if(keyStatus != clientStatus) {
			clientStatus = keyStatus
			BanditNetwork.sendMessageToServer(C2SChangeStatusPacket().apply { status = clientStatus })
			BanditMod.logger.debug("Client - Updating Status to $clientStatus")
		}

		if(BanditKeyBindings.changeMode.isPressed) {
			currentMode = (currentMode + 1) % VeinMiningHandler.executorCount
			BanditNetwork.sendMessageToServer(C2SChangeModePacket().apply { mode = currentMode })
			BanditMod.logger.debug("Client - Updating Mode to $currentMode")
		}
	}

	internal fun handleUpdateModePacket(mode: Int) {
		currentMode = mode
		BanditMod.logger.info("Mode updated by server to $mode")
	}

}