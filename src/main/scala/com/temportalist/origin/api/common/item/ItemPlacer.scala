package com.temportalist.origin.api.common.item

import com.temportalist.origin.api.common.lib.V3O
import com.temportalist.origin.api.common.utility.WorldHelper
import net.minecraft.block.{Block, BlockFence}
import net.minecraft.entity._
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.{MobSpawnerBaseLogic, TileEntityMobSpawner}
import net.minecraft.util.{MathHelper, StatCollector}
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection

/**
 *
 *
 * @author TheTemportalist
 */
class ItemPlacer(id: String, n: String, private var entClass: Class[_ <: Entity])
		extends ItemBase(id, n) {

	override def getItemStackDisplayName(stack: ItemStack): String = {
		"Spawn " + StatCollector.translateToLocal(
			"entity." + this.getEntityName(stack) + ".name"
		)
	}

	def getEntityName(stack: ItemStack): String =
		EntityList.classToStringMapping.get(this.entClass).asInstanceOf[String]

	override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
		val pos: V3O = new V3O(x, y, z)
		val pos_side: V3O = pos + ForgeDirection.getOrientation(side)
		if (WorldHelper.isClient ||
				!player.canPlayerEdit(pos_side.x_i(), pos_side.y_i(), pos_side.z_i(), side, stack))
			return false
		val entityName: String = this.getEntityName(stack)
		val state: Block = pos.getBlock(world)

		if (state == Blocks.mob_spawner) {
			pos.getTile(world) match {
				case spawner: TileEntityMobSpawner =>
					val logic: MobSpawnerBaseLogic = spawner.func_145881_a()
					logic.setEntityName(entityName)
					spawner.markDirty()
					pos.markForUpdate(world)
					if (!player.capabilities.isCreativeMode)
						stack.stackSize -= 1
					return true
				case _ =>
			}
		}

		val placePos: V3O = pos_side + new V3O(
			0.5,
			if (side == 1 && state.isInstanceOf[BlockFence]) 0.5 else 0,
			0.5
		)

		val entity: Entity = this.spawnEntity(world, entityName, placePos)

		if (entity != null) {
			/* todo fix the name tagging
			if (entity.isInstanceOf[EntityLivingBase] && stack.hasDisplayName) {
				entity.setCustomNameTag(stack.getDisplayName)
			}
			*/
			if (!player.capabilities.isCreativeMode) {
				stack.stackSize -= 1
			}
			return true
		}

		false
	}

	def spawnEntity(world: World, name: String, pos: V3O): Entity = {
		val entity: Entity = EntityList.createEntityByName(name, world)
		this.spawnEntity(entity, pos)
		entity
	}

	def spawnEntity(entity: Entity, pos: V3O): Boolean = {
		entity.setLocationAndAngles(
			pos.x, pos.y, pos.z,
			MathHelper.wrapAngleTo180_float(entity.worldObj.rand.nextFloat * 360.0F),
			0.0F
		)
		entity match {
			case living: EntityLivingBase =>

				living.rotationYawHead = living.rotationYaw
				living.renderYawOffset = living.rotationYaw
				/*
				living.asInstanceOf[EntityLiving].onInitialSpawn(
					entity.worldObj.getDifficultyForLocation(pos.toBlockPos()),
					null
				)
				*/
				living.asInstanceOf[EntityLiving].playLivingSound()
			case _ =>
		}
		this.preSpawn(entity)
		entity.worldObj.spawnEntityInWorld(entity)
		this.playSummonSound(pos, entity)
		true
	}

	def playSummonSound(pos: V3O, entity: Entity): Unit = {}

	def preSpawn(entity: Entity): Unit = {}

	/**
	 * Returns true if the item can be used on the given entity, e.g. shears on sheep.
	 *
	 * @param stack
	 * @param player
	 * @param entity
	 * @return
	 */
	override def itemInteractionForEntity(stack: ItemStack, player: EntityPlayer,
			entity: EntityLivingBase): Boolean = {
		if (stack.hasTagCompound) {
			val entityName: String = this.getEntityName(stack)
			val thatEntityName: String = EntityList.classToStringMapping.get(
				entity.getClass
			).asInstanceOf[String]
			if (thatEntityName.equals(entityName)) {
				entity match {
					case ageable: EntityAgeable =>
						return this.spawnEntity(
							ageable.createChild(null), new V3O(entity)
						)
				}
			}
		}
		false
	}

}

object ItemPlacer {

	def createEntity(entClass: Class[_ <: Entity], world: World, pos: V3O, rotZ: Float): Entity = {
		var entity: Entity = null
		try {
			if (entClass != null) {
				entity = entClass.getConstructor(classOf[World]).newInstance(world)
				entity.setLocationAndAngles(pos.x, pos.y, pos.z, rotZ, 0.0F)
			}
		}
		catch {
			case e: Exception =>
				e.printStackTrace()
		}
		entity
	}

}
