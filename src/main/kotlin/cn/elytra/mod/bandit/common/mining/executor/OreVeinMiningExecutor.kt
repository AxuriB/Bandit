package cn.elytra.mod.bandit.common.mining.executor

import cn.elytra.mod.bandit.common.mining.Context
import cn.elytra.mod.bandit.common.mining.VeinMiningHandler
import cn.elytra.mod.bandit.common.mining.pos_finder.SimpleMiningPositionIterators
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import net.minecraft.util.math.BlockPos

class OreVeinMiningExecutor(override val context: Context) : SimpleVeinMiningExecutor() {

	/**
	 * The original block state from the center pos.
	 */
	private val blockState = context.blockState

	override val positions: Flow<BlockPos> = SimpleMiningPositionIterators.createCube(context, 32)
		.filter { blockPos ->
			val thatBlockState = context.world.getBlockState(blockPos)
			thatBlockState == blockState || VeinMiningHandler.isInAdditionalBlockStates(blockState, thatBlockState)
		}

	init {
		// un-limit the size for this mode
		context.veinMiningMaxCountLimit = Int.MAX_VALUE
	}
}