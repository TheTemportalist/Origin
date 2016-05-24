package temportalist.origin.api.common.utility

import java.util
import java.util.Random

import net.minecraft.block.Block
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import temportalist.origin.api.common.lib.V3O

/**
 *
 *
 * @author TheTemportalist
 */
object Stacks {

	def spawnDrops(world: World, pos: V3O, drops: util.List[ItemStack]): Unit = {
		if (!drops.isEmpty) {
			val random: Random = new Random
			var drop: ItemStack = null
			for (i <- 0 until drops.size()) {
				drop = drops.get(i)
				if (drop != null && drop.getItem != null) {
					Stacks.spawnItemStack(world, pos, drop, random)
				}
			}
		}
	}

	def spawnItemStack(world: World, pos: V3O, itemStack: ItemStack, random: Random): EntityItem = {
		if (itemStack != null) {
			val entityitem: EntityItem = new EntityItem(world,
				pos.x + random.nextFloat * 0.8F + 0.1F,
				pos.y + random.nextFloat * 0.8F + 0.1F,
				pos.z + random.nextFloat * 0.8F + 0.1F,
				itemStack.copy
			)
			val f3: Float = 0.05F
			entityitem.motionX = (random.nextGaussian.asInstanceOf[Float] * f3).asInstanceOf[Double]
			entityitem.motionY = (random.nextGaussian.asInstanceOf[Float] * f3 + 0.2F)
					.asInstanceOf[Double]
			entityitem.motionZ = (random.nextGaussian.asInstanceOf[Float] * f3).asInstanceOf[Double]
			entityitem.setDefaultPickupDelay()
			if (itemStack.hasTagCompound) {
				entityitem.getEntityItem
						.setTagCompound(itemStack.getTagCompound.copy.asInstanceOf[NBTTagCompound])
			}
			if (!world.isRemote) world.spawnEntityInWorld(entityitem)
			entityitem
		} else null
	}

	def createStack(obj: AnyRef, data: Map[String, Any]): ItemStack =
		this.createStack(obj, 1, 0, data)

	def createStack(obj: AnyRef, size: Int, meta: Int, data: Map[String, Any]): ItemStack = {
		val stack: ItemStack = obj match {
			case b: Block => new ItemStack(b, size, meta)
			case i: Item => new ItemStack(i, size, meta)
			case _ => throw new IllegalArgumentException(
				"Illegal argument " + obj.toString + " of class " + obj.getClass.getCanonicalName)
		}
		if (data != null) {
			val nbt: NBTTagCompound = new NBTTagCompound
			for ((key: String, value: Any) <- data) {
				value match {
					case i: Int => nbt.setInteger(key, i)
					case stack: ItemStack =>
						val stackTag: NBTTagCompound = new NBTTagCompound
						stack.writeToNBT(stackTag)
						nbt.setTag(key, stackTag)
					case _ =>
				}
			}
			stack.setTagCompound(nbt)
		}
		stack
	}

	def doStacksMatch(a: ItemStack, b: ItemStack, meta: Boolean = true, size: Boolean = false,
			nbt: Boolean = false, nil: Boolean = true): Boolean = {
		if ((a == null && b != null) || (b == null && a != null)) nil
		else if (a != null && b != null) {
			a.getItem == b.getItem &&
					(!meta || a.getItemDamage == b.getItemDamage) &&
					(!size || a.stackSize == b.stackSize) &&
					(!nbt || ItemStack.areItemStackTagsEqual(a, b))
		}
		else true
	}

	def canFit(a: ItemStack, b: ItemStack): Boolean = this.canFit(a, b, a.stackSize)

	def canFit(a: ItemStack, b: ItemStack, amt: Int): Boolean = {
		a != null && this.doStacksMatch(a, b, meta = true, size = false, nbt = true, nil = true) &&
				(b == null || b.stackSize + amt <= b.getMaxStackSize)
	}

	def tossItem(stack: ItemStack, player: EntityPlayer): Unit = {
		player.captureDrops = true
		// third par specifies whether to indicate the thrower
		val entityItem = player.dropItem(stack, false, true)
		player.capturedDrops.clear()
		player.captureDrops = false
		if (entityItem != null) player.joinEntityItemWithWorld(entityItem)
	}

}
