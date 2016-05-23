package temportalist.origin.api.common.lib

import com.google.common.io.ByteArrayDataInput
import io.netty.buffer.ByteBuf
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math._
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.common.util.{BlockSnapshot, INBTSerializable}

/**
  * A vector class to rule them all
  *
  * Created by TheTemportalist on 4/9/2016.
  * @author TheTemportalist
  */
class Vect(var x: Double, var y: Double, var z: Double) extends INBTSerializable[NBTTagCompound] {

	def this(array: Array[Double]) {
		this(array(0), array(1), array(2))
	}

	def this(vec: Vec3d) {
		this(vec.xCoord, vec.yCoord, vec.zCoord)
	}

	def this(pos: BlockPos) {
		this(pos.getX, pos.getY, pos.getZ)
	}

	def this(amount: Double) {
		this(amount, amount, amount)
	}

	def this(vect: Vect) {
		this(vect.x, vect.y, vect.z)
	}

	def this(ent: Entity) {
		this(ent.posX, ent.posY, ent.posZ)
	}

	def this(tile: TileEntity) {
		this(tile.getPos)
	}

	def this(mop: RayTraceResult) {
		this(mop.hitVec)
	}

	def this(chunk: ChunkPos) {
		this(chunk.chunkXPos, 0, chunk.chunkZPos)
	}

	def this(chunk: Chunk) {
		this(chunk.getChunkCoordIntPair)
	}

	def this(snap: BlockSnapshot) {
		this(snap.getPos)
	}

	def this(dir: EnumFacing) {
		this(dir.getFrontOffsetX, dir.getFrontOffsetY, dir.getFrontOffsetZ)
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

	/**
	  * @return the integer representation of the x component
	  */
	def x_i(): Int = this.x.asInstanceOf[Int]

	/**
	  * @return the integer representation of the y component
	  */
	def y_i(): Int = this.y.asInstanceOf[Int]

	/**
	  * @return the integer representation of the z component
	  */
	def z_i(): Int = this.z.asInstanceOf[Int]

	/**
	  * @return the float representation of the x component
	  */
	def x_f(): Float = this.x.asInstanceOf[Float]

	/**
	  * @return the float representation of the y component
	  */
	def y_f(): Float = this.y.asInstanceOf[Float]

	/**
	  * @return the float representation of the z component
	  */
	def z_f(): Float = this.z.asInstanceOf[Float]

	/**
	  * Writes the xyz data to the buffer
	  *
	  * @param data A buffer
	  */
	def writeToData(data: ByteBuf): Unit = {
		data.writeDouble(this.x)
		data.writeDouble(this.y)
		data.writeDouble(this.z)
	}

	/**
	  * Writes xyz to the tag using appropriate keys
	  */
	override def serializeNBT(): NBTTagCompound = {
		val nbt = new NBTTagCompound
		nbt.setDouble("x", this.x)
		nbt.setDouble("y", this.y)
		nbt.setDouble("z", this.z)
		nbt
	}

	/**
	  * Reads xyx from the tag using appropriate keys
	  *
	  * @param nbt The tag which contains the data
	  */
	override def deserializeNBT(nbt: NBTTagCompound): Unit = {
		this.x = nbt.getInteger("x")
		this.y = nbt.getInteger("y")
		this.z = nbt.getInteger("z")
	}

	/**
	  * @return This vector as a block position (rounding down the xyz)
	  */
	def toBlockPos: BlockPos = {
		new BlockPos(
			MathHelper.floor_double(this.x),
			MathHelper.floor_double(this.y),
			MathHelper.floor_double(this.z)
		)
	}

	def getBlockState(world: World): IBlockState = world.getBlockState(this.toBlockPos)

	def getTile(world: World): TileEntity = world.getTileEntity(this.toBlockPos)

	def isAir(world: World): Boolean = world.isAirBlock(this.toBlockPos)

	def setBlockState(world: World, state: IBlockState, notify: Int): Boolean =
		world.setBlockState(this.toBlockPos, state, notify)

	def setBlock(world: World, block: Block, meta: Int, notify: Int): Boolean =
		this.setBlockState(world, block.getStateFromMeta(meta), notify)

	def setBlock(world: World, block: Block, meta: Int): Boolean =
		this.setBlock(world, block, meta, 3)

	def setBlock(world: World, block: Block): Unit = this.setBlock(world, block, 0)

	def setBlockToAir(world: World): Unit = world.setBlockToAir(this.toBlockPos)

	def getDirection: EnumFacing =
		if (this.x < 0) EnumFacing.WEST
		else if (this.x > 0) EnumFacing.EAST
		else if (this.z < 0) EnumFacing.NORTH
		else if (this.z > 0) EnumFacing.SOUTH
		else if (this.y > 0) EnumFacing.UP
		else EnumFacing.DOWN

	/**
	  * Sets the xyz values to the passed values
	  *
	  * @param x1 the x
	  * @param y1 the y
	  * @param z1 the z
	  */
	def set(x1: Double, y1: Double, z1: Double): Unit = {
		this.x = x1
		this.y = y1
		this.z = z1
	}

	/**
	  * Set this vector with the data of the passed vector
	  *
	  * @param vect A vector
	  */
	def set(vect: Vect): Unit = this.set(vect.x, vect.y, vect.z)

	// Additive things. Plus(+) & Add(+=) http://stackoverflow.com/questions/16644988/why-is-the-unary-prefix-needed-in-scala

	// Additive +, does not modify this vector

	def plus(x1: Double, y1: Double, z1: Double): Vect = new Vect(
		this.x + x1, this.y + y1, this.z + z1
	)

	def +(v: Vect): Vect = this.plus(v.x, v.y, v.z)

	def +(dir: EnumFacing): Vect = this + new Vect(dir)

	def plus(dir: EnumFacing, amount: Double): Vect =
		this.+(new Vect(dir) * amount)

	// Additive +=

	def add(x1: Double, y1: Double, z1: Double): Unit = {
		this.set(this.plus(x1, y1, z1))
	}

	def +=(vec: Vect): Unit = this.add(vec.x, vec.y, vec.z)

	def add(dir: EnumFacing, amount: Double): Unit =
		this.+=(new Vect(dir) * amount)

	def +=(dir: EnumFacing): Unit = this.add(dir, 1)

	def getDown(amount: Double = 1): Vect = this.plus(EnumFacing.DOWN, amount)

	def getUp(amount: Double = 1): Vect = this.plus(EnumFacing.UP, amount)

	def getNorth(amount: Double = 1): Vect = this.plus(EnumFacing.NORTH, amount)

	def getSouth(amount: Double = 1): Vect = this.plus(EnumFacing.SOUTH, amount)

	def getEast(amount: Double = 1): Vect = this.plus(EnumFacing.EAST, amount)

	def getWest(amount: Double = 1): Vect = this.plus(EnumFacing.WEST, amount)

	def down(amount: Double = 1): Vect = {
		this.add(EnumFacing.DOWN, amount)
		this
	}

	def up(amount: Double = 1): Vect = {
		this.add(EnumFacing.UP, amount)
		this
	}

	def north(amount: Double = 1): Vect = {
		this.add(EnumFacing.NORTH, amount)
		this
	}

	def south(amount: Double = 1): Vect = {
		this.add(EnumFacing.SOUTH, amount)
		this
	}

	def east(amount: Double = 1): Vect = {
		this.add(EnumFacing.EAST, amount)
		this
	}

	def west(amount: Double = 1): Vect = {
		this.add(EnumFacing.WEST, amount)
		this
	}

	// Subtractive +, does not modify

	def minus(x1: Double, y1: Double, z1: Double): Vect = new Vect(
		this.x - x1, this.y - y1, this.z - z1
	)

	def -(vec: Vect): Vect = this.minus(vec.x, vec.y, vec.z)

	// Subtractive +=

	def subtract(x1: Double, y1: Double, z1: Double): Unit = this.set(this.minus(x1, y1, z1))

	def -=(vec: Vect): Unit = this.subtract(vec.x, vec.y, vec.z)

	// Multiplicitive *

	def *(d: Double): Vect = new Vect(
		this.x * d, this.y * d, this.z * d
	)

	def *=(d: Double): Unit = this.set(this * d)

	def invert(): Unit = this *= -1

	def /(d: Double): Vect = new Vect(
		this.x / d, this.y / d, this.z / d
	)

	def /=(d: Double): Unit = this.set(this / d)

	// 3d Functions

	/**
	  * Sets the specified axis to 0
	  *
	  * @param axis An axis
	  */
	def suppressAxis(axis: EnumFacing.Axis): Unit = {
		this.set(
			if (axis.ordinal() == 0) 0 else this.x,
			if (axis.ordinal() == 1) 0 else this.y,
			if (axis.ordinal() == 2) 0 else this.z
		)
	}

	/**
	  * Sets the specified axis to 0 and returns the value
	  *
	  * @param axis An axis
	  * @return The vector copy with the suppressed axis
	  */
	def suppressAxisGet(axis: EnumFacing.Axis): Vect = {
		val vect = this.clone()
		vect.suppressAxis(axis)
		vect
	}

	/**
	  * Gets the dot product of this vector with itself
	  *
	  * @return x*x + y*y + z*z
	  */
	def magnitude: Double = Vect.dot(this, this)

	/**
	  * @return The length of this vector (sqrt(x*x + y*y + z*z))
	  */
	def length: Double = Math.sqrt(this.magnitude)

	/**
	  * Normalizes this vector so its length is 1
	  *
	  * @return The normalized copy of this vector
	  */
	def norm: Vect = this / this.length

	/**
	  * Normalizes this vector so its length is 1
	  */
	def normalize(): Unit = this /= this.length

	/**
	  * Shorthand notation for [[normalize]]
	  */
	def ~(): Unit = this.normalize()

	override def clone(): Vect = new Vect(this)

	override def equals(o: scala.Any): Boolean = {
		o match {
			case vec: Vect => vec.x == this.x && vec.y == this.y && vec.z == this.z
			case _ => false
		}
	}

	override def hashCode(): Int = {
		var hash: Int = 1
		hash *= 31 + this.x.hashCode()
		hash *= 31 + this.y.hashCode()
		hash *= 31 + this.z.hashCode()
		hash
	}

	override def toString: String = "Vect{" + this.x + '|' + this.y + '|' + this.z + '}'

}
object Vect {

	/**
	  * The the point in the middle of two points
	  * @param p1 The first point
	  * @param p2 The second point
	  * @return (p1x + p2x) / 2, (p1y + p2y) / 2, (p1z + p2z) / 2
	  */
	def midpoint(p1: Vect, p2: Vect): Vect = new Vect(
		(p1.x + p2.x) / 2,
		(p1.y + p2.y) / 2,
		(p1.z + p2.z) / 2
	)

	/**
	  * Get the dot product of two vectors
	  * @param u The first vector
	  * @param v The second vector
	  * @return (ux)(vx) + (uy)(vy) + (uz)(vz)
	  */
	def dot(u: Vect, v: Vect): Double = {
		u.x * v.x + u.y * v.y + u.z * v.z
	}

	/**
	  * Project u onto v (Proj_v(u))
	  * @param u The vector to be projected onto
	  * @param v The vector being projected onto u
	  * @return [dot(u, v) / dot(v, v)] * v
	  */
	def proj(u: Vect, v: Vect): Vect = {
		val dotUV = Vect.dot(u, v)
		val dotVV = v.magnitude
		v * (dotUV / dotVV)
	}

	/**
	  * Get the cross product (u x v)
	  * @param u The first vector
	  * @param v The second vector
	  * @return <(uy)(vz) - (uz)(vy), -(ux)(vz) + (uz)(vx), (ux)(vy) - (uy)(vx)>
	  */
	def cross(u: Vect, v: Vect): Vect = new Vect(
		u.y * v.z - u.z * v.y,
		-(u.x * v.z - u.z * v.x),
		u.x * v.y - u.y * v.x
	)

	/**
	  * Get the distance between a point and a line
	  * @param Q The point
	  * @param v The line vector
	  * @param P A point on the line
	  * @return ||PQ x v|| / ||v||
	  */
	def distancePointLine(Q: Vect, v: Vect, P: Vect): Double = {
		Vect.cross(Q - P, v).length / v.length
	}

	/**
	  * Get the distance between a point and a plane
	  * @param Q The point
	  * @param n The plane vector
	  * @param P A point on the plane
	  * @return |dot(PQ, n)| / ||n||
	  */
	def distancePointPlane(Q: Vect, n: Vect, P: Vect): Double = {
		Vect.dot(Q - P, n).abs / n.length
	}

	/**
	  * Calculates the size of a box and returns it as a vector
	  * @param aabb The bounding box
	  * @return The size of the bounding box as < xLength, yLength, zLength >
	  */
	def from(aabb: AxisAlignedBB): Vect = {
		new Vect(aabb.maxX, aabb.maxY, aabb.maxZ) - new Vect(aabb.minX, aabb.minY, aabb.minZ)
	}

	/**
	  * Gets the position of the entity as a block coordinate
	  * @param entity The entity
	  * @return A floored/integer version of the entity's position
	  */
	def fromCoordinate(entity: Entity): Vect = {
		new Vect(
			MathHelper.floor_double(entity.posX),
			MathHelper.floor_double(entity.posY),
			MathHelper.floor_double(entity.posZ)
		)
	}

	def readFrom(tag: NBTTagCompound, key: String): Vect = {
		val vec: Vect = Vect.ZERO
		vec.deserializeNBT(tag.getCompoundTag(key))
		vec
	}

	val UP = new Vect(EnumFacing.UP)
	val DOWN = new Vect(EnumFacing.DOWN)
	val NORTH = new Vect(EnumFacing.NORTH)
	val SOUTH = new Vect(EnumFacing.SOUTH)
	val EAST = new Vect(EnumFacing.EAST)
	val WEST = new Vect(EnumFacing.WEST)
	val ZERO = new Vect(0, 0, 0)
	val CENTER = new Vect(0.5, 0.5, 0.5)
	val SINGLE = new Vect(1, 1, 1)

}
