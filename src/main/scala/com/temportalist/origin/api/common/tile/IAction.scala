package com.temportalist.origin.api.common.tile

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

/**
 *
 *
 * @author TheTemportalist
 */
trait IAction extends TileEntity {

	var action: ActivatedAction = ActivatedAction.PULSE

	def setAction(action: ActivatedAction): Unit = {
		this.action = action
	}

	def getAction: ActivatedAction = {
		this.action
	}

	override def writeToNBT(compound: NBTTagCompound): Unit = {
		super.writeToNBT(compound)
		compound.setInteger("IAction_activatedActionID", ActivatedAction.getInt(this.action))
	}


	override def readFromNBT(compound: NBTTagCompound): Unit = {
		super.readFromNBT(compound)
		this.action = ActivatedAction.getState(compound.getInteger("IAction_activatedAction"))
	}

}
