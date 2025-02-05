package cn.elytra.mod.bandit.common.mining

import cn.elytra.mod.bandit.BanditMod
import cn.elytra.mod.bandit.common.mining.executor.DummyVeinMiningExecutor
import cn.elytra.mod.bandit.common.mining.executor.IVeinMiningExecutor
import cn.elytra.mod.bandit.common.mining.executor.OreVeinMiningExecutor
import cn.elytra.mod.bandit.common.mining.executor.PlainVeinMiningExecutor
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import java.lang.reflect.Method
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.toKotlinDuration

object VeinMiningHandler {

	var DefaultChunkSize = 64
	var DefaultMaxSize = 1024

	private val veinMiningExecutorMap = buildMap<Int, (Context) -> IVeinMiningExecutor> {
		this[0] = ::PlainVeinMiningExecutor
		this[1] = ::OreVeinMiningExecutor
		this[2] = ::DummyVeinMiningExecutor
	}

	val executorCount: Int get() = veinMiningExecutorMap.size

	private val playerStatusVeinMiningKey = ConcurrentHashMap<UUID, Boolean>()
	private val playerStatusVeinMiningMode = ConcurrentHashMap<UUID, Int>()

	private val playerJobs = ConcurrentHashMap<UUID, Job>()

	private val executorCounter = AtomicInteger(0)

	fun setStatus(player: EntityPlayerMP, status: Boolean) {
		playerStatusVeinMiningKey[player.uniqueID] = status
	}

	fun getStatus(player: EntityPlayerMP): Boolean {
		return playerStatusVeinMiningKey[player.uniqueID] ?: false
	}

	fun setModeByInt(player: EntityPlayer, mode: Int) {
		playerStatusVeinMiningMode[player.uniqueID] = mode
		player.sendMessage(TextComponentTranslation("message.bandit.mode-changed", mode))
	}

	private fun getExecutor(context: Context): IVeinMiningExecutor {
		val executorId = playerStatusVeinMiningMode[context.player.uniqueID] ?: 0
		val executorCtor = veinMiningExecutorMap[executorId] ?: ::PlainVeinMiningExecutor
		return executorCtor(context)
	}

	fun haltVeinMining(player: EntityPlayerMP) {
		playerJobs[player.uniqueID]?.cancel("halt by the player using command")
	}

	fun runVeinMining(world: World, pos: BlockPos, player: EntityPlayerMP) {
		if(playerJobs.containsKey(player.uniqueID)) return

		val context = Context(world, pos, world.getBlockState(pos), player)
		val executor = getExecutor(context)
		val executorCount = executorCounter.getAndIncrement()
		BanditMod.logger.info("Executing vein-mining task #${executorCount}")
		player.sendMessage(
			TextComponentTranslation(
				"message.bandit.task-started",
				TextComponentString("#${executorCount}").apply {
					style = Style().setColor(TextFormatting.GREEN)
				}
			)
		)
		player.sendMessage(TextComponentTranslation("message.bandit.task-started.halt-hint"))
		val job = executor.startAsync(true)
		playerJobs[player.uniqueID] = job
		job.invokeOnCompletion {
			BanditMod.logger.info("Finished executing vein-mining task #${executorCount}")
			if(it != null) BanditMod.logger.info("\tCaused by ${it.message}")

			player.sendMessage(
				TextComponentTranslation(
					"message.bandit.task-finished",
					TextComponentString("#${executorCount}").apply {
						style = Style().setColor(TextFormatting.GREEN)
					},
					TextComponentString(context.blocksMined.get().toString()).apply {
						style = Style().setColor(TextFormatting.GREEN)
					},
					TextComponentString(context.itemsDropped.get().toString()).apply {
						style = Style().setColor(TextFormatting.GREEN)
					},
					TextComponentString(
						Duration.between(context.startedAt, LocalDateTime.now()).toKotlinDuration().toString()
					).apply {
						style = Style().setColor(TextFormatting.YELLOW)
					},
				)
			)
			playerJobs.remove(player.uniqueID)
		}
		job.start()
	}

	/**
	 * Harvest the block and collect the drops as return. The items will not drop like default, and you'll need handle it manually.
	 *
	 * @return the drops
	 */
	fun harvestBlockAndCollectDrops(player: EntityPlayerMP, pos: BlockPos): List<ItemStack> {
		captureDrops(true) // start capturing drops and prevent them from dropping
		player.interactionManager.tryHarvestBlock(pos) // run the harvest logics
		return captureDrops(false) // collect the dropped items and return
	}

	private val captureDropsMethod: Method by lazy {
		Block::class.java.getDeclaredMethod("captureDrops", Boolean::class.java).apply { isAccessible = true }
	}

	@Suppress("UNCHECKED_CAST")
	private fun captureDrops(start: Boolean): NonNullList<ItemStack> {
		return captureDropsMethod.invoke(Blocks.AIR, start) as NonNullList<ItemStack>
	}

}