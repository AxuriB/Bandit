package cn.elytra.mod.bandit.common.command

import cn.elytra.mod.bandit.common.mining.VeinMiningHandler
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation

object BanditCommand :CommandBase() {

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
			else -> listOf("halt")
		}
	}
}