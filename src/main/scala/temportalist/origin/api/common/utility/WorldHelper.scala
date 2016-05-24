package temportalist.origin.api.common.utility

import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{EnumFacing, Vec3}
import net.minecraft.world.World
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.origin.api.common.lib.V3O

/**
 *
 *
 * @author TheTemportalist
 */
object WorldHelper {

	def isClientMC: Boolean = FMLCommonHandler.instance().getEffectiveSide.isClient

	def isServerDedicated: Boolean = FMLCommonHandler.instance().getEffectiveSide.isServer

	def isClient(ent: Entity): Boolean = ent.worldObj.isRemote

	def isServer(ent: Entity): Boolean = !this.isClient(ent)

	def isClient(w: World): Boolean = w.isRemote

	def isServer(w: World): Boolean = !this.isClient(w)

	def getWorld(dim: Int): World = DimensionManager.getWorld(dim)

	def isOverWorld(world: World): Boolean = world.provider.getDimensionId == 0

	@SideOnly(Side.CLIENT)
	def getWorld_client: World = Minecraft.getMinecraft.theWorld

	def isBlock(item: Item): Boolean = Block.getBlockFromItem(item) != Blocks.air

	def getBlock(world: World, x: Int, y: Int, z: Int, dir: EnumFacing): Block = {
		V3O.from(x, y, z, dir).getBlock(world)
	}

	def getTileEntity(world: World, x: Int, y: Int, z: Int, dir: EnumFacing): TileEntity = {
		V3O.from(x, y, z, dir).getTile(world)
	}

	def isInFieldOfView(viewer: Entity, viewee: Entity): Boolean = {
		val look: Vec3 = viewer.getLookVec
		if (look == null) return false
		val entityLookVec: V3O = new V3O(look) //.normalize()
		val differenceVec: V3O = new V3O(
				viewee.posX - viewer.posX,
				viewee.posY + viewee.height.asInstanceOf[Double] -
						(viewer.posY + viewer.getEyeHeight.asInstanceOf[Double]),
				viewee.posZ - viewer.posZ
			)

		val lengthVec: Double = differenceVec.toVec3.lengthVector()

		val differenceVec_normal: Vec3 = differenceVec.toVec3.normalize()

		val d1: Double = entityLookVec.toVec3.dotProduct(differenceVec_normal)

		if (d1 > (1.0D - 0.025D) / lengthVec && WorldHelper.canEntityBeSeen(viewer, viewee)) {
			true
		}
		else {
			false
		}
	}

	def canEntityBeSeen(viewer: Entity, viewee: Entity): Boolean = {
		viewee.worldObj.rayTraceBlocks(
			new Vec3(
				viewee.posX, viewee.posY + viewee.getEyeHeight.asInstanceOf[Double], viewee.posZ)
			,
			new Vec3(
				viewer.posX, viewer.posY + viewer.getEyeHeight.asInstanceOf[Double], viewer.posZ
			)
		) == null
	}

	def getVectorForEntity(entity: Entity): V3O =
		new V3O(entity.posX, entity.getEntityBoundingBox.minY, entity.posZ)

	def getLightLevel(entity: Entity): Int =
		this.getLightLevel(entity.worldObj, this.getVectorForEntity(entity))

	def getLightLevel(world: World, pos: V3O): Int = {
		if (!world.isThundering) world.getLightFromNeighbors(pos.toBlockPos)
		else {
			var ret = 0
			val skylight = world.getSkylightSubtracted
			world.setSkylightSubtracted(10)
			ret = world.getLightFromNeighbors(pos.toBlockPos)
			world.setSkylightSubtracted(skylight)
			ret
		}
	}

	def isValidLightLevelForMobSpawn(entity: Entity, minLightLevel: Int): Boolean =
		this.isValidLightLevelForMobSpawn(
			entity.worldObj, this.getVectorForEntity(entity), minLightLevel)

	def isValidLightLevelForMobSpawn(world: World, pos: V3O, minLightLevel: Int): Boolean = {
		this.getLightLevel(world, pos) <= minLightLevel
	}

}
