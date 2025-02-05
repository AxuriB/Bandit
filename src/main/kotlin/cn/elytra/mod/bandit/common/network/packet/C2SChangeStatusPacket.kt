package cn.elytra.mod.bandit.common.network.packet

import cn.elytra.mod.bandit.common.mining.VeinMiningHandler
import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class C2SChangeStatusPacket : IMessage {
	var status: Boolean = false

	override fun fromBytes(buf: ByteBuf) {
		status = buf.readBoolean()
	}

	override fun toBytes(buf: ByteBuf) {
		buf.writeBoolean(status)
	}

	class Handler : IMessageHandler<C2SChangeStatusPacket, IMessage> {
		override fun onMessage(message: C2SChangeStatusPacket, ctx: MessageContext): IMessage? {
			VeinMiningHandler.setStatus(ctx.serverHandler.player, message.status)
			return null
		}
	}
}
