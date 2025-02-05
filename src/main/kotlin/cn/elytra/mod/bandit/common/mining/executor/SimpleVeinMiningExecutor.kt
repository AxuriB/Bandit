package cn.elytra.mod.bandit.common.mining.executor

import cn.elytra.mod.bandit.common.BanditCoroutines
import cn.elytra.mod.bandit.common.mining.Context
import cn.elytra.mod.bandit.common.mining.VeinMiningHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.chunked
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import net.minecraft.block.Block
import net.minecraft.util.math.BlockPos

abstract class SimpleVeinMiningExecutor : IVeinMiningExecutor {

	abstract val context: Context
	abstract val positions: Flow<BlockPos>

	@OptIn(ExperimentalCoroutinesApi::class)
	override suspend fun start() = positions
		.take(context.veinMiningMaxCountLimit)
		.chunked(context.veinMiningChunkedSize)
		.collect { chunk ->
			withContext(BanditCoroutines.ServerThreadDispatcher) {
				chunk.forEach { pos ->
					val drops = VeinMiningHandler.harvestBlockAndCollectDrops(context.player, pos)
					drops.forEach {
						Block.spawnAsEntity(context.world, context.player.position, it)
					}

					// update stats
					context.blocksMined.addAndGet(1)
					context.itemsDropped.addAndGet(drops.sumOf { it.count })
				}
			}
		}
}