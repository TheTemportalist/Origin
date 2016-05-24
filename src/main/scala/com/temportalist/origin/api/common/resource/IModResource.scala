package com.temportalist.origin.api.common.resource

import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation

import scala.collection.mutable

/**
 *
 *
 * @author TheTemportalist
 */
trait IModResource extends IModDetails {

	private val loadedResources = mutable.Map[String, ResourceLocation]()

	final def loadResource(resourceType: String, name: String): ResourceLocation = {
		this.loadResource(EnumResource.valueOf(resourceType), name)
	}

	final def loadResource(resourceType: EnumResource, name: String): ResourceLocation = {
		new ResourceLocation(this.getModid, resourceType.getPath + "/" + name)
	}

	final def loadResource(key: String, res: (EnumResource, String)): Unit = {
		this.setResource(key, this.loadResource(res._1, res._2))
	}

	final def setResource(key: String, resourceLocation: ResourceLocation): Unit =
		this.loadedResources(key) = resourceLocation

	final def getResource(key: String): ResourceLocation = this.loadedResources(key)

	final def translate(prefix: String = null, str: String): String =
		I18n.format((if (prefix == null) "" else prefix + ".") + this.getModid + "." + str)

}
