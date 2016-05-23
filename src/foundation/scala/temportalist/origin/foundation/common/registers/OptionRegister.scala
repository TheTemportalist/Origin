package temportalist.origin.foundation.common.registers

import java.io.File
import java.util

import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.common.config.{Configuration, Property}
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.IModGuiFactory.{RuntimeOptionCategoryElement, RuntimeOptionGuiHandler}
import net.minecraftforge.fml.common.FMLLog
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author TheTemportalist
 */
class OptionRegister extends Register.Unusual {

	/**
	 * Stores the configuration data
	 */
	var config: Configuration = null

	/**
	  * Determines if configuration should be handled automatically
	  * @return Whether or not this file should handle the files for configuration. If false, [[customizeConfiguration]] MUST be implemented
	  */
	def hasDefaultConfig: Boolean = {
		true
	}

	/**
	  * If [[hasDefaultConfig]] returns false, this method must handle the file, load the
	  * configuration object, and set that object to [[config]].
	  * @param event The Pre-Init event
	  */
	def customizeConfiguration(event: FMLPreInitializationEvent): Unit = {}

	/**
	  * If [[hasDefaultConfig]] returns true, this is used to determine the directory for the config file.
	  * @param configDir The mods directory
	  * @return The file directory in which to put the config file
	  */
	def getConfigDirectory(configDir: File): File = {
		configDir
	}

	/**
	  * If [[hasDefaultConfig]] returns true, this is used to determine the config's file extension
	  * @return The extension for the file. 'cfg' and 'json' are supported.
	  */
	def getExtension: String = "cfg"

	final def loadConfiguration(): Unit = {
		try {
			this.register()
		}
		catch {
			case e: Exception => FMLLog.info(e.toString)
		}
		finally {
			if (this.config != null && this.config.hasChanged) {
				this.config.save()
			}
		}
	}

	override def register(): Unit = {}

	// ~~~~~~~~~~ Configuration ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final def getAndComment(cate: String, name: String, comment: String, value: Int): Int = {
		val property: Property = this.config.get(cate, name, value)
		if (comment != null && !comment.isEmpty) property.setComment(comment)
		property.getInt
	}

	final def getAndComment(cate: String, name: String, comment: String, value: String): String = {
		val property: Property = this.config.get(cate, name, value)
		if (comment != null && !comment.isEmpty) property.setComment(comment)
		property.getString
	}

	final def getAndComment(cate: String, name: String, comment: String, value: Boolean): Boolean = {
		val property: Property = this.config.get(cate, name, value)
		if (comment != null && !comment.isEmpty) property.setComment(comment)
		property.getBoolean(false)
	}

	final def getAndComment(cate: String, name: String, comment: String, value: Double): Double = {
		val property: Property = this.config.get(cate, name, value)
		if (comment != null && !comment.isEmpty) property.setComment(comment)
		property.getDouble
	}

	final def getAndComment(cate: String, name: String, comment: String,
			value: Array[Boolean]): Array[Boolean] = {
		val property: Property = this.config.get(cate, name, value)
		if (comment != null && !comment.isEmpty) property.setComment(comment)
		property.getBooleanList
	}

	final def getAndComment(cate: String, name: String, comment: String,
			value: Array[Int]): Array[Int] = {
		val property: Property = this.config.get(cate, name, value)
		if (comment != null && !comment.isEmpty) property.setComment(comment)
		property.getIntList
	}

	final def getAndComment(cate: String, name: String, comment: String,
			value: Array[Double]): Array[Double] = {
		val property: Property = this.config.get(cate, name, value)
		if (comment != null && !comment.isEmpty) property.setComment(comment)
		property.getDoubleList
	}

	final def getAndComment(cate: String, name: String, comment: String,
			value: Array[String]): Array[String] = {
		val property: Property = this.config.get(cate, name, value, comment)
		if (comment != null && !comment.isEmpty) property.setComment(comment)
		property.getStringList
	}

	final def getBlocksFromArray(names: Array[String]): util.List[Block] = {
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
