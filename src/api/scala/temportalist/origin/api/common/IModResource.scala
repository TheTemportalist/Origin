package temportalist.origin.api.common

import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation

import scala.collection.mutable

/**
  * Provides a route to load [[net.minecraft.util.ResourceLocation]] objects,
  * in order to minimize the amount created during runtime.
  * This is mainly used when rendering textures or models.
  *
  * Created by TheTemportalist on 4/13/2016.
  * @author TheTemportalist
  */
trait IModResource extends IModDetails {

	/**
	  * Map of keys to ResourceLocation objects
	  */
	private val loadedResources = mutable.Map[String, ResourceLocation]()

	/**
	  * Load a [[net.minecraft.util.ResourceLocation]] object.
	  * Set this resource using [[temportalist.origin.api.common.IModResource#setResource]]
	  * @param resourceType The [[temportalist.origin.api.common.EnumResource]] value as a string
	  * @param name The name under the EnumResource path
	  * @return The [[net.minecraft.util.ResourceLocation]] created by
	  *         [[temportalist.origin.api.common.IModDetails#getModId]] and the EnumResource path and name.
	  */
	final def loadResource(resourceType: String, name: String): ResourceLocation = {
		this.loadResource(EnumResource.valueOf(resourceType), name)
	}

	/**
	  * Load a [[net.minecraft.util.ResourceLocation]] object.
	  * Set this resource using [[temportalist.origin.api.common.IModResource#setResource]]
	  * @param resourceType The [[temportalist.origin.api.common.EnumResource]] value
	  * @param name The name under the EnumResource path
	  * @return The [[net.minecraft.util.ResourceLocation]] created by
	  *         [[temportalist.origin.api.common.IModDetails#getModId]] and the EnumResource path and name.
	  */
	final def loadResource(resourceType: EnumResource, name: String): ResourceLocation = {
		new ResourceLocation(this.getModId, resourceType.getPath + "/" + name)
	}

	/**
	  * Create a [[net.minecraft.util.ResourceLocation]] object and sets it
	  * @param key The key to save the object by
	  * @param res The resource pair of [[temportalist.origin.api.common.EnumResource]]
	  *            and name at the resource path
	  */
	final def loadResource(key: String, res: (EnumResource, String)): Unit = {
		this.setResource(key, this.loadResource(res._1, res._2))
	}

	/**
	  * Saves a [[net.minecraft.util.ResourceLocation]] by a key
	  * @param key The key
	  * @param resourceLocation The [[net.minecraft.util.ResourceLocation]] to save
	  */
	final def setResource(key: String, resourceLocation: ResourceLocation): Unit =
		this.loadedResources(key) = resourceLocation

	/**
	  * Fetches a [[net.minecraft.util.ResourceLocation]] by key
	  * @param key They key set by [[temportalist.origin.api.common.IModResource#setResource]]
	  * @return The ResourceLocation, or null if the key does not exist
	  */
	final def getResource(key: String): ResourceLocation = this.loadedResources.getOrElse(key, null)

	/**
	  * Translates a string using a prefix and this [[temportalist.origin.api.common.IModDetails#getModId]]
	  * @param prefix A prefix to be inserted prior to the mod ID. Can be null.
	  * @param str The string key to translate
	  * @return The translated string using [[net.minecraft.client.resources.I18n#format]]
	  */
	final def translate(prefix: String = null, str: String): String =
		I18n.format((if (prefix == null) "" else prefix + ".") + this.getModId + "." + str)

}
