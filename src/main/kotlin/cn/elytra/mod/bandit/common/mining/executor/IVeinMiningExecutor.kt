package cn.elytra.mod.bandit.common.mining.executor

import cn.elytra.mod.bandit.common.BanditCoroutines
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

interface IVeinMiningExecutor {

	suspend fun start()

	/**
	 * Runs the vein mining task in [BanditCoroutines.BanditCoroutineScope].
	 */
	fun startAsync(lazy: Boolean): Job {
		return BanditCoroutines.BanditCoroutineScope.launch(start = if(lazy) CoroutineStart.LAZY else CoroutineStart.DEFAULT) { start() }
	}

}