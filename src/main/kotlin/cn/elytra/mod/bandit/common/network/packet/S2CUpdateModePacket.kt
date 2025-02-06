package cn.elytra.mod.bandit.common.network.packet

import cn.elytra.mod.bandit.client.ClientEventListeners
import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class S2CUpdateModePacket : IMessage {

	var mode = 0

	override fun fromBytes(buf: ByteBuf) {
		mode = buf.readInt()
	}

	override fun toBytes(buf: ByteBuf) {
		buf.writeInt(mode)
	}

	class Handler : IMessageHandler<S2CUpdateModePacket, IMessage> {
		override fun onMessage(message: S2CUpdateModePacket, ctx: MessageContext): IMessage? {
			ClientEventListeners.handleUpdateModePacket(message.mode)
			return null
		}
	}
}