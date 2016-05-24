package com.temportalist.origin.foundation.common.register

import java.io.File
import java.util

import com.temportalist.origin.api.common.register.Register
import cpw.mods.fml.client.IModGuiFactory
import cpw.mods.fml.client.IModGuiFactory.{RuntimeOptionCategoryElement, RuntimeOptionGuiHandler}
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.item.Item
import net.minecraftforge.common.config.{Configuration, Property}

/**
 *
 *
 * @author TheTemportalist
 */
class OptionRegister() extends Register.Unusual with IModGuiFactory {

	/**
	 * Stores the configuration data
	 */
	var config: Configuration = null

	def getExtension: String = "cfg"

	def getConfigDirectory(configDir: File): File = {
		configDir
	}

	/**
	 * Do custom things to create/use a config file(s).
	 * @see OptionHandler.handleConfiguration
	 * @param event
	 */
	def customizeConfiguration(event: FMLPreInitializationEvent): Unit = {
	}

	def loadConfiguration(): Unit = {
		try {
			this.register()
		}
		catch {
			case e: Exception =>
		}
		finally {
			if (this.config != null && this.config.hasChanged) {
				this.config.save()
			}
		}
	}

	def hasDefaultConfig: Boolean = {
		true
	}

	override def register(): Unit = {}

	// Gui Factory Things

	@SideOnly(Side.CLIENT)
	override def initialize(minecraftInstance: Minecraft): Unit = {

	}

	@SideOnly(Side.CLIENT)
	override def runtimeGuiCategories(): util.Set[RuntimeOptionCategoryElement] = {
		null
	}

	@SideOnly(Side.CLIENT)
	override def getHandlerFor(element: RuntimeOptionCategoryElement): RuntimeOptionGuiHandler = {
		null
	}

	@SideOnly(Side.CLIENT)
	override def mainConfigGuiClass(): Class[_ <: GuiScreen] = {
		null
	}

	// Configuration Things
	def getAndComment(cate: String, name: String, comment: String, value: Int): Int = {
		val property: Property = this.config.get(cate, name, value)
		if (comment != null && !comment.isEmpty) property.comment = comment
		property.getInt
	}

	def getAndComment(cate: String, name: String, comment: String, value: String): String = {
		val property: Property = this.config.get(cate, name, value)
		if (comment != null && !comment.isEmpty) property.comment = comment
		property.getString
	}

	def getAndComment(cate: String, name: String, comment: String, value: Boolean): Boolean = {
		val property: Property = this.config.get(cate, name, value)
		if (comment != null && !comment.isEmpty) property.comment = comment
		property.getBoolean(false)
	}

	def getAndComment(cate: String, name: String, comment: String, value: Double): Double = {
		val property: Property = this.config.get(cate, name, value)
		if (comment != null && !comment.isEmpty) property.comment = comment
		property.getDouble
	}

	def getAndComment(cate: String, name: String, comment: String,
			value: Array[Boolean]): Array[Boolean] = {
		val property: Property = this.config.get(cate, name, value)
		if (comment != null && !comment.isEmpty) property.comment = comment
		property.getBooleanList
	}

	def getAndComment(cate: String, name: String, comment: String,
			value: Array[Int]): Array[Int] = {
		val property: Property = this.config.get(cate, name, value)
		if (comment != null && !comment.isEmpty) property.comment = comment
		property.getIntList
	}

	def getAndComment(cate: String, name: String, comment: String,
			value: Array[Double]): Array[Double] = {
		val property: Property = this.config.get(cate, name, value)
		if (comment != null && !comment.isEmpty) property.comment = comment
		property.getDoubleList
	}

	def getAndComment(cate: String, name: String, comment: String,
			value: Array[String]): Array[String] = {
		val property: Property = this.config.get(cate, name, value, comment)
		//if (comment != null && !comment.isEmpty) property.comment = comment
		property.getStringList
	}

	def getItemsFromArray(names: Array[String]): util.List[Item] = {
		val ret: util.List[Item] = new util.ArrayList[Item]
		for (name <- names) {
			if (name != null) {
				val obj: AnyRef = Item.itemRegistry.getObject(name)
				if (obj != null && obj.isInstanceOf[Item]) {
					ret.add(obj.asInstanceOf[Item])
				}
			}
		}
		ret
	}

	def getBlocksFromArray(names: Array[String]): util.List[Block] = {
		val ret: util.List[Block] = new util.ArrayList[Block]
		for (name <- names) {
			if (name != null) {
				val obj: Block = Block.getBlockFromName(name)
				if (obj != null) {
					ret.add(obj)
				}
			}
		}
		ret
	}

}
