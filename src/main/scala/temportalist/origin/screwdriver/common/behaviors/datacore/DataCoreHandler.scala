package temportalist.origin.screwdriver.common.behaviors.datacore

import java.io.InputStreamReader
import java.net.URL

import com.google.gson.JsonElement
import com.temportalist.origin.api.common.utility.Json
import com.temportalist.origin.screwdriver.common.AddonScrewdriver
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.EntityList
import net.minecraftforge.event.world.WorldEvent

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Created by TheTemportalist on 12/24/2015.
 */
object DataCoreHandler {

	/**
	 * Derived from the EntityList maps
	 */
	val canonicalNameToStringMap = mutable.Map[String, String]()

	private val commitsLogURL = "https://api.github.com/repos/TheTemportalist/Origin-DataCore-Resources/commits"
	private val prefixURL_Files = "https://raw.githubusercontent.com/TheTemportalist/Origin-DataCore-Resources/"
	private var prefixURL_mods = this.prefixURL_Files
	private var commitSHA: String = null
	/**
	 * Maps a class' canonical name to its EntityState
	 */
	private val className = mutable.Map[String, EntityState]()

	def getEntityState(canonicalName: String): EntityState = this.className(canonicalName)

	def refreshData(): Unit = {
		new Thread(new Runnable {
			override def run(): Unit = {

				val entryIterator = EntityList.stringToClassMapping.
						asInstanceOf[java.util.Map[String, Class[_]]].entrySet().iterator()
				while (entryIterator.hasNext) {
					val entry = entryIterator.next()
					canonicalNameToStringMap(entry.getValue.getCanonicalName) = entry.getKey
				}

				refreshCommitSHA()
				className.clear()
				fetchCloudData()
			}
		}).start()
	}

	private def fetchAndParseJson(url: String): JsonElement = {
		val fileIn = new InputStreamReader(new URL(url).openStream())
		val contents = Json.getJson(fileIn)
		fileIn.close()
		contents
	}

	private def refreshCommitSHA(): Unit = {
		this.commitSHA = this.fetchAndParseJson(this.commitsLogURL).
				getAsJsonArray.get(0).getAsJsonObject.get("sha").getAsString
		this.prefixURL_mods += this.commitSHA + "/mods/"
	}

	private def fetchCloudData(): Unit = {
		this.fetchModsManifest.foreach(modIdentifier => {
			val prefixURL_modIdentifier = this.prefixURL_mods + modIdentifier + "/"
			val manifestURL = prefixURL_modIdentifier + "manifest.json"
			val nameAuthorManifest = this.fetchEntityManifest(manifestURL)
			nameAuthorManifest._3.foreach(entityFileName => {
				val modFileURL = prefixURL_modIdentifier + entityFileName + ".json"
				val entityState = this.fetchEntityState(modFileURL)
				if (this.canonicalNameToStringMap contains entityState.getClassString) {
					entityState.setModName(nameAuthorManifest._1)
					entityState.setAuthor(nameAuthorManifest._2)
					this.className(entityState.getClassString) = entityState
				}
			})
		})
	}

	private def fetchModsManifest: Array[String] = {
		val manifest_mods_URL = this.prefixURL_mods + "manifest.json"
		val manifest_mods_JSON = this.fetchAndParseJson(manifest_mods_URL).getAsJsonArray
		val manifest_mods = ListBuffer[String]()
		for (i <- 0 until manifest_mods_JSON.size())
			manifest_mods += manifest_mods_JSON.get(i).getAsString
		manifest_mods.toArray
	}

	private def fetchEntityManifest(url: String): (String, String, Array[String]) = {
		val manifest_JSON = this.fetchAndParseJson(url).getAsJsonObject
		val modName = manifest_JSON.get("mod id").getAsString
		val modAuthor = manifest_JSON.get("author").getAsString
		val manifestFiles = manifest_JSON.get("entity files").getAsJsonArray

		val manifest = ListBuffer[String]()
		for (i <- 0 until manifestFiles.size()) manifest += manifestFiles.get(i).getAsString

		(modName, modAuthor, manifest.toArray)
	}

	private def fetchEntityState(url: String): EntityState = {
		val entityJson = this.fetchAndParseJson(url).getAsJsonObject
		val classString = entityJson.get("class").getAsString
		val name = entityJson.get("name").getAsString
		val description = entityJson.get("description").getAsString
		val nbtString = if (entityJson.has("nbt")) entityJson.get("NBT data").getAsString else "{}"
		new EntityState(classString, name, description, nbtString)
	}

	@SubscribeEvent
	def onWorldLoad(worldEvent: WorldEvent.Load): Unit = {
		val world = worldEvent.world
		if (world.provider.dimensionId != 0) return
		this.className.values.foreach(entityState => entityState.createEntityInstance(world))
	}

}
