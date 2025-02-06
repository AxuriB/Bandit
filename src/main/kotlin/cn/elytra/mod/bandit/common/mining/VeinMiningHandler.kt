package cn.elytra.mod.bandit.common.mining

import cn.elytra.mod.bandit.BanditMod
import cn.elytra.mod.bandit.common.mining.executor.IVeinMiningExecutor
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
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

	val executorCount: Int get() = VeinMiningMode.entries.size

	/**
	 * The map that records the enable/disable status for each player.
	 */
	private val playerStatusVeinMiningKey = ConcurrentHashMap<UUID, Boolean>()

	/**
	 * The map that records the running vein mining jobs for each player.
	 */
	private val playerJobs = ConcurrentHashMap<UUID, Job>()

	/**
	 * The integer id generator for the jobs.
	 */
	private val executorCounter = AtomicInteger(0)

	/**
	 * The linked block states.
	 *
	 * For example, we should consider lit redstone ores are redstone ores, vice versa.
	 */
	internal val AdditionalBlockState = mutableMapOf<IBlockState, MutableList<IBlockState>>()

	/**
	 * Register the linked block states, so that they are considered as the same blocks when vein mining.
	 */
	fun linkBlockStates(blockState1: IBlockState, blockState2: IBlockState) {
		AdditionalBlockState.computeIfAbsent(blockState1) { mutableListOf() }.add(blockState2)
		AdditionalBlockState.computeIfAbsent(blockState2) { mutableListOf() }.add(blockState1)
	}

	fun isInAdditionalBlockStates(originalBlockState: IBlockState, targetBlockState: IBlockState): Boolean {
		return AdditionalBlockState[originalBlockState]?.contains(targetBlockState) == true
	}

	init {
		linkBlockStates(Blocks.REDSTONE_ORE.defaultState, Blocks.LIT_REDSTONE_ORE.defaultState)
	}

	fun setStatus(player: EntityPlayerMP, status: Boolean) {
		playerStatusVeinMiningKey[player.uniqueID] = status
	}

	fun getStatus(player: EntityPlayerMP): Boolean {
		return playerStatusVeinMiningKey[player.uniqueID] ?: false
	}

	fun setMode(player: EntityPlayer, mode: VeinMiningMode) {
		VeinMiningSavedData.get().setVeinMiningMode(player, mode)
	}

	/**
	 * Update the activating vein mining mode for the player, and return the [VeinMiningMode] instance.
	 * `null` if the mode was not found, and the activating mode will not be updated.
	 */
	fun setModeByInt(player: EntityPlayerMP, modeOrdinal: Int): VeinMiningMode? {
		val mode = VeinMiningMode.getByOrdinal(modeOrdinal)
		if(mode != null) {
			setMode(player, mode)
			return mode
		} else {
			// report invalid mode
			BanditMod.logger.info("Player ${player.name} attempts to set mode to ${modeOrdinal}, but was not found.")
			return null
		}
	}

	/**
	 * Update the Vein Mode status by packets sent from clients by keybindings.
	 */
	internal fun handleModeUpdatePacket(player: EntityPlayerMP, modeOrdinal: Int) {
		val mode = setModeByInt(player, modeOrdinal)
		if(mode != null) {
			player.sendMessage(TextComponentTranslation("message.bandit.mode-changed", mode.displayText))
		} else {
			player.sendMessage(TextComponentTranslation("message.bandit.mode-not-found"))
		}
	}

	private fun getExecutor(context: Context): IVeinMiningExecutor {
		val player = context.player
		return VeinMiningSavedData.get().getVeinMiningMode(player).create(context)
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