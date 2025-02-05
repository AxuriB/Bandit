package cn.elytra.mod.bandit.common.mining.executor

import cn.elytra.mod.bandit.common.mining.Context
import cn.elytra.mod.bandit.common.mining.pos_finder.SimpleMiningPositionIterators
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import net.minecraft.util.math.BlockPos

class DummyVeinMiningExecutor(
	override val context: Context,
) : SimpleVeinMiningExecutor() {
	override val positions: Flow<BlockPos> = SimpleMiningPositionIterators
		.createManhattan(context, 32)
		.filterNot { context.world.isAirBlock(it) }
}