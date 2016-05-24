package temportalist.origin.api.common.lib

import java.io._
import java.util
import java.util.Map.Entry

import com.google.gson.{JsonArray, JsonElement, JsonObject, JsonPrimitive}
import net.minecraftforge.common.config.{ConfigCategory, Configuration, Property}
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import temportalist.origin.api.common.utility.Json.Config
import temportalist.origin.api.common.utility.{Json, Scala}

/**
 *
 *
 * @author TheTemportalist 1/31/15
 */
class ConfigJson(file: File) extends Configuration(file, true) {

	def this(parentFile: File, name: String) {
		this(new File(parentFile, name))
	}

	var PARENT: Configuration = null
	var categories: util.Map[String, ConfigCategory] = null
	var children: util.Map[String, Configuration] = null

	var configJson: JsonObject = null

	def fetchPrivates(): Unit = {
		PARENT = ObfuscationReflectionHelper.getPrivateValue(classOf[Configuration], null, 11)
		categories = ObfuscationReflectionHelper.getPrivateValue(classOf[Configuration], this, 13)
		children = ObfuscationReflectionHelper.getPrivateValue(classOf[Configuration], this, 14)
	}

	override def save(): Unit = {
		this.fetchPrivates()
		if (PARENT != null && PARENT != this) {
			PARENT.save()
			return
		}

		try {
			if (file.getParentFile != null) {
				file.getParentFile.mkdirs
			}
			if (!file.exists && !file.createNewFile) {
				return
			}
			if (file.canWrite) {
				val categories: util.HashMap[String, util.HashMap[String, JsonElement]] =
					new util.HashMap[String, util.HashMap[String, JsonElement]]
				val comments: util.HashMap[String, String] = new util.HashMap[String, String]
				Scala.iterateCol(this.getCategoryNames, (categoryName: String) => {
					val cate: ConfigCategory = this.getCategory(categoryName)
					val options: util.HashMap[String, JsonElement] =
						new util.HashMap[String, JsonElement]

					if (cate.getComment != null && !cate.getComment.isEmpty)
						comments.put(categoryName, cate.getComment)

					Scala.iterateCol(cate.getValues.entrySet(), (entry: Entry[String, Property]) => {
						val prop: Property = entry.getValue
						val element: JsonElement = Config.toJson(prop)
						if (element != null) {
							options.put(entry.getKey, element)
							if (prop.comment != null && !prop.comment.isEmpty)
								comments.put(entry.getKey, prop.comment)
						}
					})
					categories.put(categoryName, options)
				})

				val fos: FileOutputStream = new FileOutputStream(file)
				val buffer: BufferedWriter = new BufferedWriter(
					new OutputStreamWriter(fos, defaultEncoding)
				)
				buffer.write(Config.toString(Config.toJson(categories), comments, 0))
				buffer.close()
				fos.close()
			}
		}
		catch {
			case e: Exception => e.printStackTrace()
		}
	}

	override def load(): Unit = {
		this.fetchPrivates()
		if (PARENT != null && PARENT != this) {
			return
		}

		try {
			if (file.getParentFile != null) {
				file.getParentFile.mkdirs()
			}

			if (!file.exists()) {
				// Either a previous load attempt failed or the file is new; clear maps
				this.categories.clear()
				this.children.clear()
				if (!file.createNewFile()) return
			}

			if (file.canRead) {
				try {
					this.configJson = Json.getJson(this.file).getAsJsonObject
				}
				catch {
					case e: Exception => this.configJson = new JsonObject
				}
				Scala.iterateCol(this.configJson.entrySet(),
					(entry: Entry[String, JsonElement]) => {
						val cate: ConfigCategory = this.getCategory(entry.getKey)
						Scala.iterateCol(entry.getValue.getAsJsonObject.entrySet(),
							(propEntry: Entry[String, JsonElement]) => {
								val name: String = propEntry.getKey
								val jsonElement: JsonElement = propEntry.getValue
								if (!jsonElement.isInstanceOf[JsonObject])
									cate.put(name, this.getProperty(name, jsonElement))

							}
						)
					}
				)
			}
		}
		catch {
			case e: Exception => e.printStackTrace()
		}

	}

	private def fromJson(element: JsonElement): Any = {
		element match {
			case prim: JsonPrimitive =>
				if (prim.isBoolean) prim.getAsBoolean
				else if (prim.isNumber) prim.getAsNumber
				else prim.getAsString
			case array: JsonArray =>
				val str: Array[String] = new Array[String](array.size())
				for (i <- 0 until array.size()) {
					str(i) = array.get(i).getAsString
				}
				str
			case _ =>
				null
		}
	}

	private def getProperty(name: String, element: JsonElement): Property = {
		val datatype: Property.Type = this.getType(element)
		element match {
			case array: JsonArray =>
				val con = classOf[Property].getDeclaredConstructor(
					classOf[String], classOf[Array[String]], classOf[Property.Type],
					classOf[Boolean]
				)
				con.setAccessible(true)
				con.newInstance(
					name, this.fromJson(array).asInstanceOf[AnyRef], datatype,
					Boolean.box(x = true)
				)
			case obj: JsonObject =>
				new Property(name, element.getAsString, datatype, true)
			case _ =>
				new Property(name, element.getAsString, datatype, true)
		}
	}

	private def getType(prop: JsonElement): Property.Type = {
		prop match {
			case prim: JsonPrimitive =>
				//println (prim.getAsString + ":" + prim.isBoolean)
				if (prim.isBoolean)
					Property.Type.BOOLEAN
				else if (prim.isNumber) {
					if (prim.getAsString.contains('.'))
						Property.Type.DOUBLE
					else
						Property.Type.INTEGER
				}
				else Property.Type.STRING
			case array: JsonArray =>
				if (array.size() > 0)
					this.getType(array.get(0))
				else
					Property.Type.STRING
			case obj: JsonObject =>
				Property.Type.STRING
			case _ =>
				if (prop.getAsString.startsWith("#"))
					Property.Type.COLOR
				else
					Property.Type.STRING
		}
	}

}
