package cn.elytra.mod.bandit.common

import com.google.common.util.concurrent.ThreadFactoryBuilder
import kotlinx.coroutines.*
import net.minecraft.server.MinecraftServer
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

object BanditCoroutines {

	private val BanditDispatcher = ThreadPoolExecutor(
		4,
		6,
		60L,
		TimeUnit.SECONDS,
		ArrayBlockingQueue(6),
		ThreadFactoryBuilder()
			.setNameFormat("QZMThread-%d")
			.setDaemon(true)
			.build(),
		ThreadPoolExecutor.DiscardPolicy()
	).asCoroutineDispatcher()

	val BanditCoroutineScope = CoroutineScope(BanditDispatcher)

	lateinit var MinecraftServerRef: MinecraftServer

	val ServerThreadDispatcher = object : CoroutineDispatcher() {
		override fun dispatch(context: CoroutineContext, block: Runnable) {
			if(MinecraftServerRef.isCallingFromMinecraftThread) {
				block.run()
			} else {
				MinecraftServerRef.addScheduledTask { block.run() }
			}
		}
	}

}