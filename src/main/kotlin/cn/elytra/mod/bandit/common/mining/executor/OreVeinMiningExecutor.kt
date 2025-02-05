package cn.elytra.mod.bandit.common.mining.executor

import cn.elytra.mod.bandit.common.mining.Context
import cn.elytra.mod.bandit.common.mining.pos_finder.SimpleMiningPositionIterators
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import net.minecraft.util.math.BlockPos

class OreVeinMiningExecutor(override val context: Context) : SimpleVeinMiningExecutor() {

	private val blockState = context.world.getBlockState(context.centerPos)

	override val positions: Flow<BlockPos> = SimpleMiningPositionIterators.createCube(context, 32)
		.filter { context.world.getBlockState(it) == blockState }

	init {
		// un-limit the size for this mode
		context.veinMiningMaxCountLimit = Int.MAX_VALUE
	}
}