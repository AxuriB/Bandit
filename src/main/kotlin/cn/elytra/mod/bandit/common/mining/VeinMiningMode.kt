package cn.elytra.mod.bandit.common.mining

import cn.elytra.mod.bandit.common.mining.executor.*
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentTranslation

enum class VeinMiningMode(
	private val unlocalizedName: String,
	private val ctor: (Context) -> IVeinMiningExecutor,
) {
	Plain("bandit.mode.plain", ::PlainVeinMiningExecutor),
	Ore("bandit.mode.ore", ::OreVeinMiningExecutor),
	// Dummy("bandit.mode.dummy", ::DummyVeinMiningExecutor), // removed for test only
	;

	val displayText: ITextComponent get() = TextComponentTranslation(unlocalizedName)

	fun create(context: Context): IVeinMiningExecutor {
		return ctor(context)
	}

	companion object {
		fun getByOrdinal(ordinal: Int): VeinMiningMode? {
			return entries.getOrNull(ordinal)
		}
	}
}