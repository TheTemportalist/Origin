package temportalist.origin.api.common.tile

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

/**
 * Used by TileEntities for handling usage of power
 *
 * @author TheTemportalist
 */
trait IPowerable extends TileEntity {

	var redstoneState: RedstoneState = RedstoneState.HIGH
	var isRecievingPower: Boolean = false

	def setRedstoneState(state: RedstoneState): Unit = {
		this.redstoneState = state

	}

	def getRedstoneState: RedstoneState = {
		this.redstoneState
	}

	/**
	 * Calls @see onPowerChanged() after setting of power
	 */
	def setPowered(isRecievingPower: Boolean): Unit = {
		if (this.isRecievingPower != isRecievingPower) {
			this.isRecievingPower = isRecievingPower
			this.onPowerChanged()
		}
	}

	/**
	 * Checks if self is powered.
	 *
	 * @param checkState
	 * If true, checks the redstone state and then decides
	 * If false, returns if self is recieving power
	 * @return
	 */
	def isPowered(checkState: Boolean): Boolean = {
		if (!checkState) {
			return this.isRecievingPower
		}
		else {
			if (this.redstoneState eq RedstoneState.IGNORE) {
				return true
			}
			else if (this.redstoneState eq RedstoneState.LOW) {
				return !this.isRecievingPower
			}
			else if (this.redstoneState eq RedstoneState.HIGH) {
				return this.isRecievingPower
			}
		}
		false
	}

	/**
	 * Called when the power is not what it was
	 */
	def onPowerChanged(): Unit = {
		this.markDirty()
	}

	/**
	 * Checks if self is powered with regards to redstone state
	 * @return
	 */
	def canRun: Boolean = {
		this.isPowered(checkState = true)
	}

	override def writeToNBT(compound: NBTTagCompound): Unit = {
		super.writeToNBT(compound)
		compound.setInteger("IPowerable_redstoneState",
			RedstoneState.getIntFromState(this.redstoneState))
		compound.setBoolean("IPowerable_isRecievingPower", this.isRecievingPower)
	}

	override def readFromNBT(compound: NBTTagCompound): Unit = {
		super.readFromNBT(compound)
		this.redstoneState = RedstoneState.getStateFromInt(
			compound.getInteger("IPowerable_redstoneState")
		)
		this.isRecievingPower = compound.getBoolean("IPowerable_isRecievingPower")
	}

}
