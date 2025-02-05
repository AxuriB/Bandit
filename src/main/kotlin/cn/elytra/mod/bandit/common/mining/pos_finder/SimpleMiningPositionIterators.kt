package cn.elytra.mod.bandit.common.mining.pos_finder

import cn.elytra.mod.bandit.common.mining.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import java.util.*

object SimpleMiningPositionIterators {

	/**
	 * Create a block pos iterator by scanning the neighbors one by one, which the Manhattan distance is slightly increasing.
	 * It is totally not filtered, except the airs. You'll need to filter by yourself.
	 */
	fun createManhattan(context: Context, distance: Int): Flow<BlockPos> {
		val centerPos = context.centerPos
		val distanceSqLimitFromCenter = (distance * distance)

		return flow {
			// block positions that has been visited once
			val visitedBlockPos = HashSet<BlockPos>()

			// block positions to be visited later
			val blockPosToVisit =
				PriorityQueue<BlockPos> { x1, x2 -> centerPos.distanceSq(x1) compareTo centerPos.distanceSq(x2) }
			blockPosToVisit.add(centerPos)

			while(true) {
				val p = blockPosToVisit.poll() ?: break // if polled nothing, it means we are done

				if(p.distanceSq(centerPos) >= distanceSqLimitFromCenter) {
					// discard this because of exceeding the limit,
					// but we continue to see if there are valid ones left.
					continue
				}

				// mark visited
				visitedBlockPos += p

				// add to queue
				blockPosToVisit += p.getNeighbors()
					.filter { it !in visitedBlockPos }

				// submit the value
				emit(p)
			}
		}
	}

	fun createCube(context: Context, range: Int): Flow<BlockPos> {
		val centerPos = context.centerPos
		return flow {
			for(j in centerPos.y + range downTo centerPos.y - range) { // from up to down, to remove something like sands
				for(k in centerPos.z - range..centerPos.z + range) {
					for(i in centerPos.x - range..centerPos.x + range) {
						emit(BlockPos(i, j, k))
					}
				}
			}
		}
	}

}

/**
 * Get the directly connected neighbor block positions.
 */
internal fun BlockPos.getNeighbors(): List<BlockPos> {
	return EnumFacing.entries.map { facing ->
		offset(facing, 1)
	}
}