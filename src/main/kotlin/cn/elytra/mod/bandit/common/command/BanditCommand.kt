package cn.elytra.mod.bandit.common.command

import cn.elytra.mod.bandit.common.mining.VeinMiningHandler
import cn.elytra.mod.bandit.common.mining.VeinMiningMode
import cn.elytra.mod.bandit.common.network.BanditNetwork
import cn.elytra.mod.bandit.common.network.packet.S2CUpdateModePacket
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting

object BanditCommand : CommandBase() {

	override fun getName(): String = "bandit"

	override fun getUsage(sender: ICommandSender): String = "command.bandit.usage"

	override fun getRequiredPermissionLevel(): Int = 0

	override fun execute(server: MinecraftServer, sender: ICommandSender, argsRaw: Array<out String>) {
		val args = argsRaw.toMutableList()

		when(args.removeFirstOrNull()) {
			"halt" -> {
				if(sender is EntityPlayerMP) {
					VeinMiningHandler.haltVeinMining(sender)
					sender.sendMessage(TextComponentTranslation("command.bandit.halt.halted"))
				} else {
					sender.sendMessage(TextComponentTranslation("command.bandit.halt.invalid-sender"))
				}
			}

			"mode" -> {
				if(sender is EntityPlayerMP) {
					val modeOrdinal = (args.removeFirstOrNull()
						?: return sender.sendMessage(TextComponentTranslation("command.bandit.mode.missing-argument"))).toIntOrNull()
						?: return sender.sendMessage(TextComponentTranslation("command.bandit.mode.invalid-argument"))
					val mode = VeinMiningHandler.setModeByInt(sender, modeOrdinal)
					if(mode != null) {
						sender.sendMessage(
							TextComponentTranslation(
								"command.bandit.mode.mode-changed",
								mode.displayText.apply { style = Style().setColor(TextFormatting.AQUA) }
							)
						)

						// update mode on client side
						BanditNetwork.sendMessageToPlayer(
							S2CUpdateModePacket().apply { this.mode = modeOrdinal },
							sender
						)
					} else {
						sender.sendMessage(
							TextComponentTranslation(
								"command.bandit.mode.mode-not-found",
								modeOrdinal
							)
						)
					}
				} else {
					sender.sendMessage(TextComponentTranslation("command.bandit.mode.invalid-sender"))
				}
			}

			else -> {
				sender.sendMessage(TextComponentTranslation("command.bandit.usage"))
			}
		}
	}

	override fun getTabCompletions(
		server: MinecraftServer,
		sender: ICommandSender,
		argsRaw: Array<out String>,
		targetPos: BlockPos?,
	): List<String> {
		val args = argsRaw.toMutableList()

		return when(args.removeFirstOrNull()) {
			"mode" -> VeinMiningMode.entries.map { it.ordinal.toString() }
			"", null -> listOf("halt", "mode")
			else -> listOf()
		}
	}
}