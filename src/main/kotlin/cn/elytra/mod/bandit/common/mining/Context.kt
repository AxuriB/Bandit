package cn.elytra.mod.bandit.common.mining

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

data class Context(
	val world: World,
	val centerPos: BlockPos,
	val blockState: IBlockState,
	val player: EntityPlayerMP,

	var veinMiningMaxCountLimit: Int = VeinMiningHandler.DefaultMaxSize,
	var veinMiningChunkedSize: Int = VeinMiningHandler.DefaultChunkSize,

	val blocksMined: AtomicInteger = AtomicInteger(0),
	val itemsDropped: AtomicInteger = AtomicInteger(0),
	val startedAt: LocalDateTime = LocalDateTime.now(),
)
