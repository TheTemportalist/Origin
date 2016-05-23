package temportalist.origin.api.common

import net.minecraft.nbt.NBTTagCompound

/**
  * Provides an easy interface to access read and write functions for an object
  *
  * Created by TheTemportalist on 4/9/2016.
  * @author TheTemportalist
  */
trait INBTSaver {

	/**
	  * Write data to a tag to store in the pass tag
	  * @param tag The tag to store information in
	  * @param key The key to store the information at
	  */
	def writeTo(tag: NBTTagCompound, key: String): Unit = {
		val selfTag: NBTTagCompound = new NBTTagCompound
		this.writeTo(selfTag)
		tag.setTag(key, selfTag)
	}

	/**
	  * Write this class's data to a tag
	  * @param tag The tag to store the information in
	  */
	def writeTo(tag: NBTTagCompound): Unit

	/**
	  * Read data from a tag at the passed key
	  * @param tag The tag which contains the data
	  * @param key The key where the data can be found (expects a NBTTagCompound as the value)
	  */
	def readFrom(tag: NBTTagCompound, key: String): Unit = {
		this.readFrom(tag.getCompoundTag(key))
	}

	/**
	  * Read this class's data from a tag
	  * @param tag The tag which contains the data
	  */
	def readFrom(tag: NBTTagCompound): Unit

}
