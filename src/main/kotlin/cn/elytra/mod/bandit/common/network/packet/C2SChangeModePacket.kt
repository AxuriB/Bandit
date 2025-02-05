package cn.elytra.mod.bandit.common.network.packet

import cn.elytra.mod.bandit.common.mining.VeinMiningHandler
import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class C2SChangeModePacket : IMessage {
	var mode: Int = 0

	override fun fromBytes(buf: ByteBuf) {
		mode = buf.readInt()
	}

	override fun toBytes(buf: ByteBuf) {
		buf.writeInt(mode)
	}

	class Handler : IMessageHandler<C2SChangeModePacket, IMessage> {
		override fun onMessage(message: C2SChangeModePacket, ctx: MessageContext): IMessage? {
			VeinMiningHandler.setModeByInt(ctx.serverHandler.player, message.mode)
			return null
		}
	}
}
