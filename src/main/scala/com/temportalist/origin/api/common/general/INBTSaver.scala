package com.temportalist.origin.api.common.general

import net.minecraft.nbt.NBTTagCompound

/**
 *
 *
 * @author TheTemportalist
 */
trait INBTSaver {

	def writeTo(tag: NBTTagCompound, key: String): Unit = {
		val selfTag: NBTTagCompound = new NBTTagCompound
		this.writeTo(selfTag)
		tag.setTag(key, selfTag)
	}

	def writeTo(tag: NBTTagCompound): Unit

	def readFrom(tag: NBTTagCompound, key: String): Unit = {
		this.readFrom(tag.getCompoundTag(key))
	}

	def readFrom(tag: NBTTagCompound): Unit

}
