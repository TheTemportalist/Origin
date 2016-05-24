package com.temportalist.origin.api.common.lib

import com.google.common.io.ByteArrayDataInput
import com.temportalist.origin.api.client.utility.TessRenderer
import com.temportalist.origin.api.common.general.INBTSaver
import com.temportalist.origin.api.common.utility.MathFuncs
import cpw.mods.fml.relauncher.{Side, SideOnly}
import io.netty.buffer.ByteBuf
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util._
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.{ChunkCoordIntPair, EnumSkyBlock, World}
import net.minecraftforge.common.util.{BlockSnapshot, ForgeDirection}

/**
 *
 *
 * @author TheTemportalist
 */
class V3O(var x: Double, var y: Double, var z: Double) extends INBTSaver {

	def this(array: Array[Double]) {
		this(array(0), array(1), array(2))
	}

	def this(vec: Vec3) {
		this(vec.xCoord, vec.yCoord, vec.zCoord)
	}

	def this(amount: Double) {
		this(amount, amount, amount)
	}

	def this(ent: Entity) {
		this(ent.posX, ent.posY, ent.posZ)
	}

	def this(tile: TileEntity) {
		this(tile.xCoord, tile.yCoord, tile.zCoord)
	}

	def this(mop: MovingObjectPosition) {
		this(mop.hitVec)
	}

	def this(chunk: ChunkCoordIntPair) {
		this(chunk.chunkXPos, 0, chunk.chunkZPos)
	}

	def this(chunk: Chunk) {
		this(chunk.getChunkCoordIntPair)
	}

	def this(snap: BlockSnapshot) {
		this(snap.x, snap.y, snap.z)
	}

	def this(dir: ForgeDirection) {
		this(dir.offsetX, dir.offsetY, dir.offsetZ)
	}

	def this(nbt: NBTTagCompound) {
		this(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"))
	}

	def this(data: ByteArrayDataInput) {
		this(data.readDouble(), data.readDouble(), data.readDouble())
	}

	def this(data: ByteBuf) {
		this(data.readDouble(), data.readDouble(), data.readDouble())
	}

	def this(u: Int, v: Int) {
		this(u, v, 0)
	}

	def u(): Int = this.x_i()

	def v(): Int = this.y_i()

	def x_i(): Int = this.x.asInstanceOf[Int]

	def y_i(): Int = this.y.asInstanceOf[Int]

	def z_i(): Int = this.z.asInstanceOf[Int]

	def x_f(): Float = this.x.asInstanceOf[Float]

	def y_f(): Float = this.y.asInstanceOf[Float]

	def z_f(): Float = this.z.asInstanceOf[Float]

	def toData(data: ByteBuf): Unit = {
		data.writeDouble(this.x)
		data.writeDouble(this.y)
		data.writeDouble(this.z)
	}

	override def writeTo(tag: NBTTagCompound): Unit = {
		tag.setDouble("x", this.x)
		tag.setDouble("y", this.y)
		tag.setDouble("z", this.z)
	}

	override def readFrom(tag: NBTTagCompound): Unit = {
		this.x = tag.getInteger("x")
		this.y = tag.getInteger("y")
		this.z = tag.getInteger("z")
	}

	def toBlockPos: BlockPos = {
		new BlockPos(
			MathHelper.floor_double(this.x),
			MathHelper.floor_double(this.y),
			MathHelper.floor_double(this.z)
		)
	}

	def toBlockCoord(world: World): BlockCoord = new BlockCoord(this, world.provider.dimensionId)

	def getBlockState(world: World): BlockState =
		new BlockState(this.getBlock(world), this.getBlockMeta(world))

	def markForUpdate(world: World): Unit = this.toBlockCoord(world).markForUpdate()

	def markChunkModified(tile: TileEntity): Unit =
		tile.getWorldObj.markTileEntityChunkModified(this.x_i(), this.y_i(), this.z_i(), tile)

	def toVec3: Vec3 = Vec3.createVectorHelper(this.x, this.y, this.z)

	def toChunkPair: ChunkCoordIntPair = new ChunkCoordIntPair(this.x_i(), this.z_i())

	def toChunkCoords: ChunkCoordinates = new ChunkCoordinates(this.x_i(), this.y_i(), this.z_i())

	def getChunk(world: World): Chunk = world.getChunkFromChunkCoords(this.x_i(), this.z_i())

	def getChunkAsBlock(world: World): Chunk = world.getChunkFromBlockCoords(this.x_i(), this.z_i())

	def getBlock(world: World): Block = world.getBlock(this.x_i(), this.y_i(), this.z_i())

	def getBlockMeta(world: World): Int = world.getBlockMetadata(this.x_i(), this.y_i(), this.z_i())

	def getTile(world: World): TileEntity = world.getTileEntity(this.x_i(), this.y_i(), this.z_i())

	def getLightBrightnes(world: World): Float =
		world.getLightBrightness(this.x_i(), this.y_i(), this.z_i())

	def getSavedLightValue(world: World, skyBlock: EnumSkyBlock): Int = {
		world.getSavedLightValue(skyBlock, this.x_i(), this.y_i(), this.z_i())
	}

	def getLightValue(world: World): Int = {
		world.getBlockLightValue(this.x_i(), this.y_i(), this.z_i())
	}

	def isAir(world: World): Boolean =
		this.getBlock(world).isAir(world, this.x_i(), this.y_i(), this.z_i())

	def isReplaceable(world: World): Boolean =
		this.getBlock(world).isReplaceable(world, this.x_i(), this.y_i(), this.z_i())

	def getHardness(world: World): Float =
		this.getBlock(world).getBlockHardness(world, this.x_i(), this.y_i(), this.z_i())

	def setBlock(world: World, block: Block, meta: Int, notify: Int): Boolean =
		world.setBlock(this.x_i(), this.y_i(), this.z_i(), block, meta, notify)

	def setBlock(world: World, block: Block, meta: Int): Boolean =
		this.setBlock(world, block, meta, 3)

	def setBlock(world: World, block: Block): Unit = {
		this.setBlock(world, block, 0)
	}

	def setBlockToAir(world: World): Unit = {
		this.setBlock(world, Blocks.air)
	}

	def setBlockMeta(world: World, meta: Int, notify: Int): Unit =
		world.setBlockMetadataWithNotify(this.x_i(), this.y_i(), this.z_i(), meta, notify)

	def getDir: ForgeDirection =
		if (this.x_i() < 0) ForgeDirection.WEST
		else if (this.x_i() > 0) ForgeDirection.EAST
		else if (this.y_i() < 0) ForgeDirection.DOWN
		else if (this.y_i() > 0) ForgeDirection.UP
		else if (this.z_i() < 0) ForgeDirection.NORTH
		else if (this.z_i() > 0) ForgeDirection.SOUTH
		else ForgeDirection.UNKNOWN

	def toNBT(nbt: NBTTagCompound) {
		nbt.setDouble("x", this.x)
		nbt.setDouble("y", this.y)
		nbt.setDouble("z", this.z)
	}

	@SideOnly(Side.CLIENT)
	def addVecUV(u: Double, v: Double): Unit = {
		TessRenderer.addVertex(this.x, this.y, this.z, u, v)
	}

	@SideOnly(Side.CLIENT)
	def setTranslation(): Unit = {
		TessRenderer.getTess().setTranslation(this.x, this.y, this.z)
	}

	def openGui(mod: AnyRef, id: Int, player: EntityPlayer): Unit = {
		player.openGui(mod, id, player.getEntityWorld, this.x_i(), this.y_i(), this.z_i())
	}

	def set(x1: Double, y1: Double, z1: Double): Unit = {
		this.x = x1
		this.y = y1
		this.z = z1
	}

	def set(vec: V3O): Unit = this.set(vec.x, vec.y, vec.z)

	// Additive things. Plus(+) & Add(+=) http://stackoverflow.com/questions/16644988/why-is-the-unary-prefix-needed-in-scala

	// Additive +, does not modify this vector

	def plus(x1: Double, y1: Double, z1: Double): V3O = new V3O(
		this.x + x1, this.y + y1, this.z + z1
	)

	def +(v: V3O): V3O = this.plus(v.x, v.y, v.z)

	def +(d: Double): V3O = this.plus(d, d, d)

	def +(dir: ForgeDirection): V3O = this + new V3O(dir)

	// Additive +=

	def add(x1: Double, y1: Double, z1: Double): Unit = {
		this.set(this.plus(x1, y1, z1))
	}

	def +=(vec: V3O): Unit = this.add(vec.x, vec.y, vec.z)

	def +=(d: Double): Unit = this.add(d, d, d)

	def add(dir: ForgeDirection, amount: Double): Unit =
		this.+=(new V3O(dir) * amount)

	def +=(dir: ForgeDirection): Unit = this.add(dir, 1)

	def down(amount: Double): V3O = {
		this.add(ForgeDirection.DOWN, amount)
		this
	}

	def down(): V3O = this.down(1)

	def up(amount: Double): V3O = {
		this.add(ForgeDirection.UP, amount)
		this
	}

	def up(): V3O = this.up(1)

	def north(amount: Double): V3O = {
		this.add(ForgeDirection.NORTH, amount)
		this
	}

	def north(): V3O = this.north(1)

	def south(amount: Double): V3O = {
		this.add(ForgeDirection.SOUTH, amount)
		this
	}

	def south(): V3O = this.south(1)

	def east(amount: Double): V3O = {
		this.add(ForgeDirection.EAST, amount)
		this
	}

	def east(): V3O = this.east(1)

	def west(amount: Double): V3O = {
		this.add(ForgeDirection.WEST, amount)
		this
	}

	def west(): V3O = this.west(1)

	// Subtractive +, does not modify

	def minus(x1: Double, y1: Double, z1: Double): V3O = new V3O(
		this.x - x1, this.y - y1, this.z - z1
	)

	def -(vec: V3O): V3O = this.minus(vec.x, vec.y, vec.z)

	def -(d: Double): V3O = this.minus(d, d, d)

	// Subtractive +=

	def subtract(x1: Double, y1: Double, z1: Double): Unit = this.set(this.minus(x1, y1, z1))

	def -=(vec: V3O): Unit = this.subtract(vec.x, vec.y, vec.z)

	def -=(d: Double): Unit = this.subtract(d, d, d)

	// Multiplicitive *

	def times(x1: Double, y1: Double, z1: Double): V3O = new V3O(
		this.x * x1, this.y * y1, this.z * z1
	)

	def *(vec: V3O): V3O = this.times(vec.x, vec.y, vec.z)

	def *(d: Double): V3O = this.times(d, d, d)

	// Multiplicitive *=

	def multiply(x1: Double, y1: Double, z1: Double): Unit = this.set(this.times(x1, y1, z1))

	def *=(vec: V3O): Unit = this.multiply(vec.x, vec.y, vec.z)

	def *=(d: Double): Unit = this.multiply(d, d, d)

	def invert(): Unit = this.+=(-1)

	// 3D Functions

	def suppressedYAxis(): V3O = {
		new V3O(this.x, 0, this.z)
	}

	def magSquared(): Double = {
		this.x * this.x + this.y * this.y + this.z * this.z
	}

	def magnitude(): Double = {
		Math.sqrt(this.magSquared())
	}

	def distance(vec: V3O): Double = {
		(this - vec).magnitude()
	}

	def distance(x: Double, y: Double, z: Double): Double = {
		this.distance(new V3O(x, y, z))
	}

	def normalize(): V3O = {
		val mag: Double = this.magnitude()
		this * (if (mag != 0.0D) 1.0d / mag else 1d)
	}

	def dotProduct(vec: V3O): Double = {
		var d: Double = vec.x * this.x + vec.y * this.y + vec.z * this.z
		d = MathFuncs.bind(1, d, 1.00001, 1)
		d = MathFuncs.bind(-1.00001, d, -1, -1)
		d
	}

	def dotProduct(x1: Double, y1: Double, z1: Double): Double = {
		x1 * this.x + y1 * this.y + z1 * this.z
	}

	def crossProduct(vec: V3O): V3O = {
		val (x1, y1, z1) = (
				this.y * vec.z - this.z * vec.y,
				this.z * vec.x - this.x * vec.z,
				this.x * vec.y - this.y * vec.x
				)
		x = x1
		y = y1
		z = z1
		this
	}

	def crossProduct(axis: Axis): V3O = {
		val (x1, y1, z1) = (this.x, this.y, this.z)
		this.x = 0
		this.y = 0
		this.z = 0
		axis match {
			case Axis.X =>
				this.y = z1
				this.z = -y1
			case Axis.Y =>
				this.x = -z1
				this.z = x1
			case Axis.Z =>
				this.x = y1
				this.y = -x1
			case _ =>
		}
		this
	}

	def xCrossProduct(): V3O = this.crossProduct(Axis.X)

	def zCrossProduct(): V3O = this.crossProduct(Axis.Z)

	def yCrossProduct(): V3O = this.crossProduct(Axis.Y)

	def perpendicular(): V3O =
		if (this.z == 0.0D) this.zCrossProduct() else this.xCrossProduct()

	//def rotate(angle: Double, axis: Vector3O): Vector3O = new Vector3O(super.rotate(angle, axis))

	//def rotate(rotator: Quat): Vector3O = new Vector3O(super.rotate(rotator))

	def intercept(axis: Axis, end: V3O, p: Double): V3O = {
		val (dx, dy, dz) = (end.x - this.x, end.y - this.y, end.z - this.z)
		var d: Double = 0.0D
		axis match {
			case Axis.X =>
				if (dx == 0.0D) return null
				d = (p - this.x) / dx

				if (MathFuncs.between_eq(-1E-5, d, 1E-5)) return this
				if (!MathFuncs.between_eq(0, d, 1)) return null

				x = p
				y += d * dy
				z += d * dz
			case Axis.Y =>
				if (dy == 0.0D) return null
				d = (p - this.y) / dy

				if (MathFuncs.between_eq(-1E-5, d, 1E-5)) return this
				if (!MathFuncs.between_eq(0, d, 1)) return null

				x += d * dx
				y = p
				z += d * dz
			case Axis.Z =>
				if (dz == 0.0D) return null
				d = (p - this.z) / dz

				if (MathFuncs.between_eq(-1E-5, d, 1E-5)) return this
				if (!MathFuncs.between_eq(0, d, 1)) return null

				x += d * dx
				y += d * dy
				z = p
			case _ =>
		}
		this
	}

	def YZintercept(end: V3O, px: Double): V3O =
		this.intercept(Axis.X, end, px)

	def XZintercept(end: V3O, py: Double): V3O =
		this.intercept(Axis.Y, end, py)

	def XYintercept(end: V3O, pz: Double): V3O =
		this.intercept(Axis.Z, end, pz)

	def unary_$tilde(): V3O = this.normalize()

	def $tilde(): V3O = this.normalize()

	def /(d: Double): V3O = this * (1 / d)

	def copy(): V3O = new V3O(this.x, this.y, this.z)

	override def equals(o: scala.Any): Boolean = {
		o match {
			case vec: V3O =>
				return vec.x == this.x && vec.y == this.y && vec.z == this.z
			case _ =>
		}
		false
	}

	override def hashCode(): Int = {
		var hash: Int = 1
		hash = hash * 31 + this.x.hashCode()
		hash = hash * 31 + this.y.hashCode()
		hash = hash * 31 + this.z.hashCode()
		hash
	}

	override def toString: String = "V3O{" + this.x + "|" + this.y + "|" + this.z + "}"

}

object V3O {

	def from(x: Double, y: Double, z: Double, dir: ForgeDirection): V3O = {
		new V3O(x, y, z) + new V3O(dir)
	}

	def from(aabb: AxisAlignedBB): V3O = {
		new V3O(aabb.maxX, aabb.maxY, aabb.maxZ) - new V3O(aabb.minX, aabb.minY, aabb.minZ)
	}

	def readFrom(tag: NBTTagCompound, key: String): V3O = {
		val vec: V3O = V3O.ZERO
		vec.readFrom(tag, key)
		vec
	}

	def fromCoordinate(entity: Entity): V3O = {
		new V3O(
			MathHelper.floor_double(entity.posX),
			MathHelper.floor_double(entity.posY),
			MathHelper.floor_double(entity.posZ)
		)
	}

	def UP: V3O = new V3O(ForgeDirection.UP)

	def DOWN: V3O = new V3O(ForgeDirection.DOWN)

	def NORTH: V3O = new V3O(ForgeDirection.NORTH)

	def SOUTH: V3O = new V3O(ForgeDirection.SOUTH)

	def EAST: V3O = new V3O(ForgeDirection.EAST)

	def WEST: V3O = new V3O(ForgeDirection.WEST)

	def ZERO: V3O = new V3O(0, 0, 0)

	def CENTER: V3O = new V3O(0.5, 0.5, 0.5)

	def SINGLE: V3O = new V3O(1, 1, 1)

}
