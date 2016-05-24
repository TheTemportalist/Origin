package temportalist.origin.api.common.utility

import java.util.Random

import net.minecraft.block.Block
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.init.Blocks
import net.minecraft.server.MinecraftServer
import net.minecraft.util.AxisAlignedBB
import net.minecraft.world.{World, WorldServer}
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.EnderTeleportEvent
import temportalist.origin.api.common.lib.{V3O, TeleporterCore}

/**
 *
 *
 * @author TheTemportalist
 */
object Teleport {

	/**
	 * Teleports players to inputted dimensionID. Returns true if player is
	 * successfully teleported.
	 *
	 * @param player The player being teleported
	 * @param dimID The dimension's ID
	 */
	def toDimension(player: EntityPlayer, dimID: Int): Boolean = {
		if (player.dimension != dimID) {
			player match {
				case player: EntityPlayerMP =>
					val world: WorldServer = player.worldObj.asInstanceOf[WorldServer]
					if (player.ridingEntity == null && player.riddenByEntity == null) {
						MinecraftServer.getServer.getConfigurationManager.transferPlayerToDimension(
							player, dimID, new TeleporterCore(world)
						)
						if (player.dimension == dimID) {
							return true
						}
					}
				case _ =>
			}
		}
		false
	}

	/**
	 * Teleports player based on where their crosshair lays. Max distance of 500.0D (500 blocks)
	 *
	 * @param entityPlayer
	 */
	def toCursorPosition(entityPlayer: EntityPlayer): Boolean = {
		this.toCursorPosition(entityPlayer, 500.0D)
	}

	/**
	 * Teleports player based on where their crosshair lays.
	 *
	 * @param entityPlayer
	 * @param maxDistance
	 */
	def toCursorPosition(entityPlayer: EntityPlayer, maxDistance: Double): Boolean = {
		val point: V3O = Cursor.getRaytracedBlock(
			entityPlayer.worldObj, entityPlayer, maxDistance
		)
		if (point == null) return false
		Teleport.toPoint(
			entityPlayer, point.plus(0.5D, 0.0D, 0.5D)
		)
	}

	def toPointRandom(entity: EntityLivingBase, minRadius: Int, maxRadius: Int): Unit = {
		val entityPos = V3O.fromCoordinate(entity)
		var newPosCoordinate: V3O = null
		var centeredNewPos: V3O = null
		var loop: Int = 0
		newPosCoordinate = this.getRandomPoint(
			entity.worldObj.rand, minRadius, maxRadius) + entityPos
		centeredNewPos = newPosCoordinate + V3O.CENTER
		var safePos: (Boolean, Block) = this.isSafePosition(entity.worldObj, newPosCoordinate)
		var isSaveAndValidPos: Boolean = safePos._1 &&
				this.isValidPosition(entity.worldObj, centeredNewPos, entity)
		while (!isSaveAndValidPos) {
			loop += 1
			// world height is 128
			if (loop > 128) {
				this.toPointRandom(entity, minRadius, maxRadius)
				return
			}

			if (safePos._2 != Blocks.air) {
				newPosCoordinate.up()
				centeredNewPos.up()
			}
			else {
				newPosCoordinate.down()
				centeredNewPos.down()
			}

			safePos = this.isSafePosition(entity.worldObj, newPosCoordinate)
			isSaveAndValidPos = safePos._1 &&
					this.isValidPosition(entity.worldObj, centeredNewPos, entity)

		}

		this.toPoint(entity, centeredNewPos)
	}

	def getRandomPoint(rand: Random, minRadius: Int, maxRadius: Int): V3O = {
		new V3O(
			MathFuncs.getRandomBetweenBounds(rand, minRadius, maxRadius),
			MathFuncs.getRandomBetweenBounds(rand, minRadius, maxRadius),
			MathFuncs.getRandomBetweenBounds(rand, minRadius, maxRadius)
		)
	}

	def isSafePosition(world: World, position: V3O): (Boolean, Block) = {
		val block = position.copy().down().getBlock(world)
		(block != Blocks.air && block.isOpaqueCube, block)
	}

	def isValidPosition(world: World, centeredPos: V3O, entity: EntityLivingBase): Boolean = {
		val entityHalfWidth: Float = entity.width / 2
		entity.height
		val posBoundingBox: AxisAlignedBB = AxisAlignedBB.fromBounds(
			centeredPos.x - entityHalfWidth,
			centeredPos.y - entity.getYOffset,// + entity.ySize,
			centeredPos.z - entityHalfWidth,
			centeredPos.x + entityHalfWidth,
			centeredPos.y - entity.getYOffset + entity.height,// + entity.ySize,
			centeredPos.z + entityHalfWidth
		)
		world.getCollidingBoundingBoxes(entity, posBoundingBox).isEmpty &&
				!world.isAnyLiquid(posBoundingBox)
	}

	private def canLandOnBlock(block: Block): Boolean = {
		/*
		block == Blocks.lava ||
				block == Blocks.flowing_lava ||
				block == Blocks.water ||
				block == Blocks.flowing_water
		*/
		!block.getMaterial.isLiquid
	}

	/**
	 * Teleports player to the xyz parameter coordinates. If fallDamage is
	 * false, height player was at before teleportation will not be calculated
	 * into fall damage. This does not apply to post teleportation fall damage.
	 * If particles is true, will spawn particles after teleportation.
	 *
	 * @param player
	 * @param x
	 * @param y
	 * @param z
	 */
	def toPoint(player: EntityPlayer, x: Double, y: Double, z: Double): Boolean = {
		this.toPoint(player, new V3O(x, y, z))
	}

	/**
	 * Teleports player to the xyz parameter coordinates. If fallDamage is
	 * false, height player was at before teleportation will not be calculated
	 * into fall damage. This does not apply to post teleportation fall damage.
	 * If particles is true, will spawn particles after teleportation.
	 *
	 * @param entity
	 * @param point
	 */
	def toPoint(entity: Entity, point: V3O): Boolean = {
		entity match {
			case player: EntityPlayer =>
				val event: EnderTeleportEvent = new EnderTeleportEvent(
					player, point.x_i(), point.y_i(), point.z_i(), 0.0F
				)
				if (MinecraftForge.EVENT_BUS.post(event)) return false
			case _ =>
		}

		// todo make sure spot is chunk loaded (setup chunkloader for Origin mod?)

		entity match {
			case elb: EntityLivingBase =>
				elb.setPositionAndUpdate(point.x, point.y, point.z)
				elb match {
					case mp: EntityPlayerMP =>
						mp.playerNetServerHandler.setPlayerLocation(point.x, point.y, point.z,
							mp.rotationYaw, mp.rotationPitch)
					case _ =>
				}
			case _ =>
				entity.setPosition(point.x, point.y, point.z)
		}

		// todo optional particles

		true
	}

	def toDimensionPoint(player: EntityPlayer, pos: V3O, dimid: Int): Unit = {
		this.toDimension(player, dimid)
		this.toPoint(player, pos)
	}

}
