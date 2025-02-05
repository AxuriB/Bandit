package cn.elytra.mod.bandit.common.command

import cn.elytra.mod.bandit.common.mining.VeinMiningHandler
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.TextComponentTranslation

object BanditAdminCommand : CommandBase() {

	override fun getName(): String = "bandit-admin"

	override fun getUsage(sender: ICommandSender): String = "command.bandit-admin.usage"

	override fun execute(server: MinecraftServer, sender: ICommandSender, argsRaw: Array<out String>) {
		val args = argsRaw.toMutableList()

		when(val arg0 = args.removeFirstOrNull()) {
			"vm-chunk", "vm-max" -> {
				val arg1 = args.removeFirstOrNull()
					?: return sender.sendMessage(TextComponentTranslation("command.bandit-admin.missing-args", 1))
				val arg1Val = arg1.toIntOrNull()
					?: return sender.sendMessage(TextComponentTranslation("command.bandit-admin.invalid-arg-type", arg1, "integer"))

				when(arg0) {
					"vm-chunk" -> {
						VeinMiningHandler.DefaultChunkSize = arg1Val
						sender.sendMessage(TextComponentTranslation("command.bandit-admin.ok"))
					}
					"vm-max" -> {
						VeinMiningHandler.DefaultMaxSize = arg1Val
						sender.sendMessage(TextComponentTranslation("command.bandit-admin.ok"))
					}
				}
			}

			else -> {
				sender.sendMessage(TextComponentTranslation("command.bandit-admin.help"))
			}
		}
	}
}