package temportalist.origin.api.common.utility

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.util.MovingObjectPosition.MovingObjectType
import net.minecraft.util._
import net.minecraft.world.World
import temportalist.origin.api.common.lib.V3O

import scala.collection.JavaConversions

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
		val vec = new V3O(entity) + new V3O(0, entity.getEyeHeight, 0)
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
		val head = this.getHeadPos(entity)
		val cursorPosVec = this.getCursorPosVec(entity, reachLength, head)
		val mop = world.rayTraceBlocks(head.toVec3, cursorPosVec.toVec3, false)
		if (mop == null) return null
		var pos: V3O = null
		var side: EnumFacing = null
		if (mop.typeOfHit == MovingObjectType.BLOCK) {
			pos = new V3O(mop.getBlockPos)
			side = mop.sideHit
		}
		else {
			pos = new V3O(
				mop.hitVec.xCoord.asInstanceOf[Int],
				mop.hitVec.yCoord.asInstanceOf[Int],
				mop.hitVec.zCoord.asInstanceOf[Int]
			)
			side = mop.sideHit
		}
		pos + side
	}

	def raytraceWorld(player: EntityPlayer): MovingObjectPosition = {
		this.raytraceWorld(player, Players.getReachDistance(player))
	}

	def raytraceWorld(player: EntityPlayer, reach: Double): MovingObjectPosition = {
		this.raytraceWorld(player.getEntityWorld, player, reach)
	}

	def raytraceWorld(world: World, player: EntityPlayer, reach: Double): MovingObjectPosition = {
		val head = Cursor.getHeadPos(player)
		val look = new V3O(player.getLook(1f))
		val lookReach = look * reach
		val cursorPos = head + lookReach

		var retMop = world.rayTraceBlocks(head.toVec3, cursorPos.toVec3, false, false, false)

		// the rest is for entities

		val expansion: Float = 1f
		val entities = JavaConversions.asScalaBuffer(world.getEntitiesWithinAABBExcludingEntity(
			player, player.getEntityBoundingBox.addCoord(lookReach.x, lookReach.y, lookReach.z)
					.expand(expansion, expansion, expansion)
		))
		var lastDistance: Double = reach

		var pointedEntity: Entity = null
		var entityVector: Vec3 = null

		for (entity <- entities) {
			if (entity.canBeCollidedWith) {
				val entityExpansion: Double = entity.getCollisionBorderSize
				val aabb = entity.getEntityBoundingBox.expand(
					entityExpansion, entityExpansion, entityExpansion)
				val mop = aabb.calculateIntercept(head.toVec3, cursorPos.toVec3)

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
		}

		if (pointedEntity != null && (lastDistance < reach || retMop == null))
			retMop = new MovingObjectPosition(pointedEntity, entityVector)

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
	def getTargetBlock(user: EntityPlayer): (IBlockState, EnumFacing, BlockPos) = {
		this.getTargetBlock(user.getEntityWorld, this.getTarget(user))
	}

	/**
	 * Gets the stats of the block at the cursor within reach
	 * @param user The player
	 * @param reach the distance for reach
	 * @return null if no block at cursor within reach, or tuple of (Block, Metadata, Side, tuple of (X, Y, Z))
	 */
	def getTargetBlock(user: EntityPlayer,
			reach: Double): (IBlockState, EnumFacing, BlockPos) = {
		this.getTargetBlock(user.getEntityWorld, this.getTarget(user, reach))
	}

	private final def getTargetBlock(world: World,
			mop: MovingObjectPosition): (IBlockState, EnumFacing, BlockPos) = {
		mop match {
			case mop: MovingObjectPosition =>
				if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
					(world.getBlockState(mop.getBlockPos), mop.sideHit, mop.getBlockPos)
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
