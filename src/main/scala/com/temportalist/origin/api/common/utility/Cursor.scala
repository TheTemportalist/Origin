package com.temportalist.origin.api.common.utility

import java.util

import com.temportalist.origin.api.common.lib.V3O
import com.temportalist.origin.foundation.common.utility.Players
import net.minecraft.block.Block
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.util.MovingObjectPosition.MovingObjectType
import net.minecraft.util.{AxisAlignedBB, MovingObjectPosition, Vec3}
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection

/**
 *
 *
 * @author TheTemportalist
 */
object Cursor {

	/**
	 * Synonymous to codechicken.lib.raytracer.RayTracer.getCorrectedHeadVec(EntityPlayer player)
	 * @param entity
	 * @return
	 */
	def getHeadPos(entity: EntityLivingBase): V3O = {
		val vec: V3O = new V3O(entity) + new V3O(0, entity.getEyeHeight, 0)
		entity match {
			case player: EntityPlayer =>
				if (player.worldObj.isRemote)
					vec.add(0, -player.getDefaultEyeHeight, 0)
				else if (player.isInstanceOf[EntityPlayerMP] && player.isSneaking)
					vec.add(0, -0.08D, 0)
		}
		vec
	}

	/**
	 * Synonymous to codechicken.lib.raytracer.RayTracer.getEndVec(EntityPlayer player)
	 * @param entity
	 * @return
	 */
	def getCursorPosVec(
			entity: EntityLivingBase, reachDistance: Double, headVec: V3O
			): V3O = {
		// scale the look vector by the reach distance and translate it by the head/eye vector
		new V3O(entity.getLook(1.0F)) * reachDistance + headVec
	}

	def getCursorPosVec(entity: EntityLivingBase, reachDistance: Double): V3O = {
		this.getCursorPosVec(entity, reachDistance, this.getHeadPos(entity))
	}

	def getCursorPosVec(entity: EntityLivingBase): V3O = {
		entity match {
			case player: EntityPlayer =>
				this.getCursorPosVec(entity, Players.getReachDistance(player))
			case _ =>
				this.getCursorPosVec(entity, 5D)
		}
	}

	def getRaytracedBlock(world: World, entity: EntityLivingBase, reachLength: Double): V3O = {
		val head: V3O = this.getHeadPos(entity)
		val cursorPosVec: V3O = this.getCursorPosVec(entity, reachLength, head)
		val mop: MovingObjectPosition = world
				.rayTraceBlocks(head.toVec3, cursorPosVec.toVec3, false)
		if (mop == null) return null
		var blockX: Int = 0
		var blockY: Int = 0
		var blockZ: Int = 0
		var side: Int = 0
		if (mop.typeOfHit == MovingObjectType.BLOCK) {
			blockX = mop.blockX
			blockY = mop.blockY
			blockZ = mop.blockZ
			side = mop.sideHit
		}
		else {
			blockX = mop.hitVec.xCoord.asInstanceOf[Int]
			blockY = mop.hitVec.yCoord.asInstanceOf[Int]
			blockZ = mop.hitVec.zCoord.asInstanceOf[Int]
			side = 1
		}
		new V3O(blockX, blockY, blockZ) + ForgeDirection.getOrientation(side)
	}

	def raytraceWorld(player: EntityPlayer): MovingObjectPosition ={
		this.raytraceWorld(player, Players.getReachDistance(player))
	}

	def raytraceWorld(player: EntityPlayer, reach: Double): MovingObjectPosition = {
		this.raytraceWorld(player.getEntityWorld, player, reach)
	}

	def raytraceWorld(world: World, player: EntityPlayer, reach: Double): MovingObjectPosition = {
		val head: V3O = Cursor.getHeadPos(player)
		val look: V3O = new V3O(player.getLook(1f))
		val lookReach: V3O = look * reach
		val cursorPos: V3O = head + lookReach

		var retMop: MovingObjectPosition = world.func_147447_a(
			head.toVec3, cursorPos.toVec3, false, false, false
		)

		// the rest is for entities

		val expansion: Float = 1f
		val entities: util.List[Entity] = world.getEntitiesWithinAABBExcludingEntity(
			player, player.boundingBox.addCoord(lookReach.x, lookReach.y, lookReach.z)
					.expand(expansion, expansion, expansion)
		).asInstanceOf[util.List[Entity]]
		var lastDistance: Double = reach

		var pointedEntity: Entity = null
		var entityVector: Vec3 = null

		Scala.foreach(entities, (index: Int, entity: Entity) => {

			if (entity.canBeCollidedWith) {

				val entityExpansion: Double = entity.getCollisionBorderSize
				val aabb: AxisAlignedBB = entity.boundingBox.expand(entityExpansion,
					entityExpansion, entityExpansion)
				val mop: MovingObjectPosition = aabb.calculateIntercept(head.toVec3,
					cursorPos.toVec3)

				if (aabb.isVecInside(head.toVec3)) {
					if (lastDistance >= 0.0D) {
						pointedEntity = entity
						entityVector = if (mop == null) head.toVec3 else mop.hitVec
						lastDistance = 0.0D
					}
				}
				else if (mop != null) {
					val distanceToEntity: Double = head.toVec3.distanceTo(mop.hitVec)
					if (distanceToEntity < lastDistance || lastDistance == 0.0D) {
						if (entity == player.ridingEntity && !player.canRiderInteract) {
							if (lastDistance == 0.0D) {
								pointedEntity = entity
								entityVector = mop.hitVec
							}
						}
						else {
							pointedEntity = entity
							entityVector = mop.hitVec
							lastDistance = distanceToEntity
						}
					}
				}

			}

		})

		if (pointedEntity != null && (lastDistance < reach || retMop == null)) {
			retMop = new MovingObjectPosition(pointedEntity, entityVector)
		}

		retMop
	}

	def getTarget(user: EntityPlayer): MovingObjectPosition = {
		Cursor.raytraceWorld(user)
	}

	def getTarget(user: EntityPlayer, reach: Double): MovingObjectPosition = {
		Cursor.raytraceWorld(user, reach)
	}

	/**
	 * Gets the stats of the block at the cursor with player's reach
	 * @param user The player
	 * @return null if no block at cursor within reach, or tuple of (Block, Metadata, Side, tuple of (X, Y, Z))
	 */
	def getTargetBlock(user: EntityPlayer): (Block, Int, ForgeDirection, (Int, Int, Int)) = {
		this.getTargetBlock(user.getEntityWorld, this.getTarget(user))
	}

	/**
	 * Gets the stats of the block at the cursor within reach
	 * @param user The player
	 * @param reach the distance for reach
	 * @return null if no block at cursor within reach, or tuple of (Block, Metadata, Side, tuple of (X, Y, Z))
	 */
	def getTargetBlock(user: EntityPlayer, reach: Double): (Block, Int, ForgeDirection, (Int, Int, Int)) = {
		this.getTargetBlock(user.getEntityWorld, this.getTarget(user, reach))
	}

	private final def getTargetBlock(world: World,
			mop: MovingObjectPosition): (Block, Int, ForgeDirection, (Int, Int, Int)) = {
		mop match {
			case mop: MovingObjectPosition =>
				if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
					(world.getBlock(mop.blockX, mop.blockY, mop.blockZ),
							world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ),
							ForgeDirection.getOrientation(mop.sideHit),
							(mop.blockX, mop.blockY, mop.blockZ)
							)
				}
				else null
			case _ => null
		}
	}

	def getTargetEntity(user: EntityPlayer): EntityLivingBase = {
		this.getTargetEntity(this.getTarget(user))
	}

	def getTargetEntity(user: EntityPlayer, reach: Double): EntityLivingBase = {
		this.getTargetEntity(this.getTarget(user, reach))
	}

	private final def getTargetEntity(mop: MovingObjectPosition): EntityLivingBase = {
		mop.entityHit match {
			case e: EntityLivingBase => e
			case _ => null
		}
	}

}
