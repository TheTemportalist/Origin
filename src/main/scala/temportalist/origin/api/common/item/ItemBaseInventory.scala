package temportalist.origin.api.common.item

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import temportalist.origin.api.common.inventory.ContainerBase

/**
 *
 *
 * @author TheTemportalist
 */
class ItemBaseInventory(pluginID: String, name: String) extends ItemBase(pluginID, name) {

	def basicDataKey = "BasicData"

	def inventoryDataKey = "InventoryData"

	// Default Constructor
	this.setMaxStackSize(1)

	// End Constructor

	// Other Constructors

	// End Constructors

	override def getMaxItemUseDuration(itemstack: ItemStack): Int = {
		1
	}

	override def onUpdate(itemstack: ItemStack, world: World, entity: Entity, par4: Int,
			isCurrentItem: Boolean): Unit = {
		// Check if a tag compound does not exist
		if (!itemstack.hasTagCompound) {
			// Create a new compound
			val tagCom: NBTTagCompound = new NBTTagCompound()
			// Set the basic data key to a new compound
			tagCom.setTag(this.basicDataKey, new NBTTagCompound())
			// Set the inventory data key to a new compound
			tagCom.setTag(this.inventoryDataKey, new NBTTagCompound())
			// set the tag compound of the item stack to the new total nbt tag
			itemstack.setTagCompound(tagCom)
		}
		// If client side and entity is a player
		if (!world.isRemote && entity.isInstanceOf[EntityPlayer]) {
			// Cast entity as player
			val player: EntityPlayer = entity.asInstanceOf[EntityPlayer]
			// if player has a container open and it is of the type ContainerBase, and it requires
			// and update
			if (player.openContainer != null &&
					player.openContainer.isInstanceOf[ContainerBase] &&
					player.openContainer.asInstanceOf[ContainerBase].needsUpdate) {
				// save the nbt data from the container to the itemstack
				player.openContainer.asInstanceOf[ContainerBase].writeToNBT()
				// reset the status of the container requiring an update
				player.openContainer.asInstanceOf[ContainerBase].needsUpdate = false
			}
		}

	}

}
