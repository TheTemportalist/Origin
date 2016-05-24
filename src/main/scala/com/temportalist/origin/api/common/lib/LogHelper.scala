package com.temportalist.origin.api.common.lib

import cpw.mods.fml.common.FMLLog
import org.apache.logging.log4j.Level

/**
 * Used for easy logging
 *
 * @author TheTemportalist
 */
object LogHelper {

	def log(level: Level, pluginName: String, obj: Object): Unit = {
		FMLLog.log(pluginName, level, String.valueOf(obj))
	}

	def all(pluginName: String, obj: Object): Unit = {
		LogHelper.log(Level.ALL, pluginName, obj)
	}

	def debug(pluginName: String, obj: Object): Unit = {
		LogHelper.log(Level.DEBUG, pluginName, obj)
	}

	def error(pluginName: String, obj: Object): Unit = {
		LogHelper.log(Level.ERROR, pluginName, obj)
	}

	def fatal(pluginName: String, obj: Object): Unit = {
		LogHelper.log(Level.FATAL, pluginName, obj)
	}

	def info(pluginName: String, obj: Object): Unit = {
		LogHelper.log(Level.INFO, pluginName, obj)
	}

	def off(pluginName: String, obj: Object): Unit = {
		LogHelper.log(Level.OFF, pluginName, obj)
	}

	def trace(pluginName: String, obj: Object): Unit = {
		LogHelper.log(Level.TRACE, pluginName, obj)
	}

	def warn(pluginName: String, obj: Object): Unit = {
		LogHelper.log(Level.WARN, pluginName, obj)
	}

}
