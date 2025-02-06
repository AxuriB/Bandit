package cn.elytra.mod.bandit.common.mining

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.common.DimensionManager

class VeinMiningSavedData(name: String) : WorldSavedData(name) {

	private var veinModeData: NBTTagCompound = NBTTagCompound()

	override fun readFromNBT(nbt: NBTTagCompound) {
		veinModeData = nbt.getCompoundTag("VeinModeData")
	}

	override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound = compound.apply {
		setTag("VeinModeData", veinModeData)
	}

	fun getVeinMiningMode(player: EntityPlayer): VeinMiningMode {
		val modeOrdinal = veinModeData.getInteger(player.uniqueID.toString())
		return VeinMiningMode.getByOrdinal(modeOrdinal) ?: VeinMiningMode.Plain
	}

	fun setVeinMiningMode(player: EntityPlayer, mode: VeinMiningMode) {
		veinModeData.setInteger(player.uniqueID.toString(), mode.ordinal)
		markDirty()
	}

	fun hasVeinMiningMode(player: EntityPlayer): Boolean {
		return veinModeData.hasKey(player.uniqueID.toString())
	}

	companion object {

		fun get(): VeinMiningSavedData {
			return get(DimensionManager.getWorld(0))
		}

		fun get(world: World): VeinMiningSavedData {
			require(world is WorldServer) { "must be called from server" }

			var data = world.loadData(VeinMiningSavedData::class.java, "BanditVeinMiningData") as VeinMiningSavedData?
			if(data == null) {
				data = VeinMiningSavedData("BanditVeinMiningData")
				world.setData("BanditVeinMiningData", data)
			}
			return data
		}

	}
}