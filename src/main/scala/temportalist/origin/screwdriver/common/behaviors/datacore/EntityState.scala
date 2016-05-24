package temportalist.origin.screwdriver.common.behaviors.datacore

import net.minecraft.entity.{EntityLivingBase, Entity, EntityList}
import net.minecraft.nbt.{NBTTagCompound, JsonToNBT}
import net.minecraft.world.World

/**
 * Created by TheTemportalist on 12/24/2015.
 */
class EntityState(private val className: String, private val name: String,
		private val description: String, private val nbtString: String) {

	private var modName: String = null
	private var author: String = null
	private var entityInstance: EntityLivingBase = null

	def setModName(str: String): Unit = this.modName = str

	def setAuthor(str: String): Unit = this.author = str

	def getClassString: String = this.className

	def getName: String = this.name

	def getDescription: String = this.description

	def createEntityInstance(world: World): Unit = {
		val nbt = JsonToNBT.func_150315_a(this.nbtString).asInstanceOf[NBTTagCompound]
		nbt.setString("id",	DataCoreHandler.canonicalNameToStringMap(this.className))
		this.entityInstance = EntityList.createEntityFromNBT(
			nbt, world).asInstanceOf[EntityLivingBase]
	}

	def getEntity: EntityLivingBase = {
		this.entityInstance
	}

}
