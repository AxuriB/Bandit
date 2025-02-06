package cn.elytra.mod.bandit.common.command

import cn.elytra.mod.bandit.common.mining.VeinMiningHandler
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
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
					?: return sender.sendMessage(
						TextComponentTranslation(
							"command.bandit-admin.invalid-arg-type",
							arg1,
							"integer"
						)
					)

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

			"kill-drops" -> {
				var totalCount = 0

				getEntityList(server, sender, "@e[type=item]").forEach {
					it.onKillCommand()
					totalCount++
				}

				getEntityList(server, sender, "@e[type=xp_orb]").forEach {
					it.onKillCommand()
					totalCount++
				}

				sender.sendMessage(TextComponentTranslation("command.bandit-admin.kill-drops", totalCount))
			}

			"unbreakable!" -> {
				if(sender is EntityPlayer) {
					val pickaxe = ItemStack(Items.DIAMOND_PICKAXE)

					// set unbreakable
					pickaxe.tagCompound = (pickaxe.tagCompound?:NBTTagCompound()).apply { setBoolean("Unbreakable", true) }

					// set display name
					pickaxe.setTranslatableName("item.bandit.unbreakable-pickaxe")

					sender.inventory.addItemStackToInventory(pickaxe)
				}
			}

			else -> {
				sender.sendMessage(TextComponentTranslation("command.bandit-admin.help"))
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
			"vm-max", "vm-chunk" -> listOf("0")
			"", null -> listOf("vm-max", "vm-chunk", "kill-drops", "unbreakable!")
			else -> listOf()
		}
	}
}