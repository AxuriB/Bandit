package cn.elytra.mod.bandit.common.network

import cn.elytra.mod.bandit.common.network.packet.C2SChangeModePacket
import cn.elytra.mod.bandit.common.network.packet.C2SChangeStatusPacket
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side

object BanditNetwork {

	private val networkW = SimpleNetworkWrapper("bandit")

	fun register(e: FMLPreInitializationEvent) {
		networkW.registerMessage(C2SChangeStatusPacket.Handler::class.java, C2SChangeStatusPacket::class.java, 0, Side.SERVER)
		networkW.registerMessage(C2SChangeModePacket.Handler::class.java, C2SChangeModePacket::class.java, 1, Side.SERVER)
	}

	fun sendMessageToPlayer(message: IMessage, player: EntityPlayerMP) {
		networkW.sendTo(message, player)
	}

	fun sendMessageToServer(message: IMessage) {
		networkW.sendToServer(message)
	}

}