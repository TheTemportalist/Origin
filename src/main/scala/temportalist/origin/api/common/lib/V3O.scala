package temportalist.origin.api.common.lib

import com.google.common.io.ByteArrayDataInput
import io.netty.buffer.ByteBuf
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util._
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.{ChunkCoordIntPair, World}
import net.minecraftforge.common.util.BlockSnapshot
import temportalist.origin.api.common.general.INBTSaver
import temportalist.origin.api.common.utility.MathFuncs

/**
  *
  *
  * @author TheTemportalist
  */
class V3O(var x: Double, var y: Double, var z: Double) extends INBTSaver {

	def this(xyz: (Double, Double, Double)) {
		this(xyz._1, xyz._2, xyz._3)
	}

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

	def this(vec: Vec3i) {
		this(vec.getX, vec.getY, vec.getZ)
	}

	def this(tile: TileEntity) {
		this(tile.getPos)
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
		this(snap.pos)
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

	def this(uv: (Int, Int)) {
		this(uv._1, uv._2)
	}

	def this(u: Double, v: Double) {
		this(u, v, 0D)
	}

	def u(): Int = this.x_i()

	def v(): Int = this.y_i()

	def u_f(): Float = this.x_f()

	def v_f(): Float = this.y_f()

	def x_i(): Int = this.x.asInstanceOf[Int]

	def y_i(): Int = this.y.asInstanceOf[Int]

	def z_i(): Int = this.z.asInstanceOf[Int]

	def x_f(): Float = this.x.asInstanceOf[Float]

	def y_f(): Float = this.y.asInstanceOf[Float]

	def z_f(): Float = this.z.asInstanceOf[Float]

	def writeData(data: ByteBuf): Unit = {
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

	def markBlockForUpdate(world: World): Unit = world.markBlockForUpdate(this.toBlockPos)

	def markChunkModified(tile: TileEntity): Unit = {
		//tile.getWorld.markTileEntityChunkModified(this.x_i(), this.y_i(), this.z_i(), tile)
		tile.getWorld.markChunkDirty(this.toBlockPos, tile)
	}

	def notifyAllOfStateChange(world: World): Unit = {
		world.notifyNeighborsOfStateChange(this.toBlockPos, this.getBlock(world))
	}

	def toVec3: Vec3 = new Vec3(this.x, this.y, this.z)

	def toChunkPair: ChunkCoordIntPair = new ChunkCoordIntPair(this.x_i(), this.z_i())

	def getChunk(world: World): Chunk = world.getChunkFromChunkCoords(this.x_i(), this.z_i())

	def getChunkAsBlock(world: World): Chunk = world.getChunkFromBlockCoords(this.toBlockPos)

	def getBlockState(world: World): IBlockState = world.getBlockState(this.toBlockPos)

	def getBlock(world: World): Block = this.getBlockState(world).getBlock

	def getTile(world: World): TileEntity = world.getTileEntity(this.toBlockPos)


	def getLightBrightnes(world: World): Float = world.getLightBrightness(this.toBlockPos)

	def getLightValue(world: World): Int = world.getLight(this.toBlockPos)

	def isAir(world: World): Boolean = this.getBlock(world).isAir(world, this.toBlockPos)

	def isReplaceable(world: World): Boolean =
		this.getBlock(world).isReplaceable(world, this.toBlockPos)

	def getHardness(world: World): Float =
		this.getBlock(world).getBlockHardness(world, this.toBlockPos)

	def setBlockState(world: World, state: IBlockState): Boolean =
		world.setBlockState(this.toBlockPos, state)

	def setBlockState(world: World, state: IBlockState, notify: Int): Boolean =
		world.setBlockState(this.toBlockPos, state, notify)

	def setBlockToAir(world: World): Unit = world.setBlockToAir(this.toBlockPos)

	def getDir: EnumFacing = {
		if (this.x_i() < 0) EnumFacing.WEST
		else if (this.x_i() > 0) EnumFacing.EAST
		else if (this.y_i() < 0) EnumFacing.DOWN
		else if (this.y_i() > 0) EnumFacing.UP
		else if (this.z_i() < 0) EnumFacing.NORTH
		else if (this.z_i() > 0) EnumFacing.SOUTH
		else null
	}

	def writeNBT(nbt: NBTTagCompound) {
		nbt.setDouble("x", this.x)
		nbt.setDouble("y", this.y)
		nbt.setDouble("z", this.z)
	}

	/*
	@SideOnly(Side.CLIENT)
	def addVecUV(u: Double, v: Double): Unit = {
		TessRenderer.addVertex(this.x, this.y, this.z, u, v)
	}

	@SideOnly(Side.CLIENT)
	def setTranslation(): Unit = {
		TessRenderer.getTess().setTranslation(this.x, this.y, this.z)
	}
	*/

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

	def +(pos: (Double, Double, Double)): V3O = this.plus(pos._1, pos._2, pos._3)

	def +(x1: Double, y1: Double, z1: Double): V3O = this.plus(x1, y1, z1)

	def +(v: V3O): V3O = this.plus(v.x, v.y, v.z)

	def +(d: Double): V3O = this.plus(d, d, d)

	def +(dir: EnumFacing): V3O = this + new V3O(dir)

	// Additive +=

	def add(x1: Double, y1: Double, z1: Double): Unit = {
		this.set(this.plus(x1, y1, z1))
	}

	def +=(vec: V3O): Unit = this.add(vec.x, vec.y, vec.z)

	def +=(d: Double): Unit = this.add(d, d, d)

	def add(dir: EnumFacing, amount: Double): Unit =
		this.+=(new V3O(dir) * amount)

	def +=(dir: EnumFacing): Unit = this.add(dir, 1)

	def down(amount: Double): V3O = {
		this.add(EnumFacing.DOWN, amount)
		this
	}

	def down(): V3O = this.down(1)

	def up(amount: Double): V3O = {
		this.add(EnumFacing.UP, amount)
		this
	}

	def up(): V3O = this.up(1)

	def north(amount: Double): V3O = {
		this.add(EnumFacing.NORTH, amount)
		this
	}

	def north(): V3O = this.north(1)

	def south(amount: Double): V3O = {
		this.add(EnumFacing.SOUTH, amount)
		this
	}

	def south(): V3O = this.south(1)

	def east(amount: Double): V3O = {
		this.add(EnumFacing.EAST, amount)
		this
	}

	def east(): V3O = this.east(1)

	def west(amount: Double): V3O = {
		this.add(EnumFacing.WEST, amount)
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

	// Division

	def /(vec: V3O): V3O = new V3O(this.x / vec.x, this.y / vec.y, this.z / vec.z)

	def /=(vec: V3O): Unit = {
		this.x /= vec.x
		this.y /= vec.y
		this.z /= vec.z
	}

	// 3D Functions

	def iterate[U](radius: V3O, f: (V3O) => U): Unit = {
		for {x <- -radius.x_i() to radius.x_i()
		     y <- -radius.y_i() to radius.y_i()
		     z <- -radius.z_i() to radius.z_i()}
			f(this + (x, y, z))
	}

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

	def from(x: Double, y: Double, z: Double, dir: EnumFacing): V3O = {
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

	def toAABB(min: V3O, max: V3O): AxisAlignedBB =
		AxisAlignedBB.fromBounds(min.x, min.y, min.z, max.x, max.y, max.z)

	val UP = new V3O(EnumFacing.UP)

	val DOWN = new V3O(EnumFacing.DOWN)

	val NORTH = new V3O(EnumFacing.NORTH)

	val SOUTH = new V3O(EnumFacing.SOUTH)

	val EAST = new V3O(EnumFacing.EAST)

	val WEST = new V3O(EnumFacing.WEST)

	val ZERO = new V3O(0, 0, 0)

	val CENTER = new V3O(0.5, 0.5, 0.5)

	val SINGLE = new V3O(1, 1, 1)

	val UV_NEG = new V3O(-1, -1, 0)

	val HORIZONTAL = Array[V3O](EAST, NORTH, WEST, SOUTH)

	val AXIS = Map[EnumFacing.Axis, Array[V3O]](
		(EnumFacing.Axis.X, Array[V3O](EAST, WEST)),
		(EnumFacing.Axis.Y, Array[V3O](UP, DOWN)),
		(EnumFacing.Axis.Z, Array[V3O](SOUTH, NORTH))
	)

	val AXIS_FACING = Map[EnumFacing.Axis, Array[EnumFacing]](
		(EnumFacing.Axis.X, Array[EnumFacing](EnumFacing.EAST, EnumFacing.WEST)),
		(EnumFacing.Axis.Y, Array[EnumFacing](EnumFacing.UP, EnumFacing.DOWN)),
		(EnumFacing.Axis.Z, Array[EnumFacing](EnumFacing.SOUTH, EnumFacing.NORTH))
	)

	val ENUMFACING = Map[EnumFacing, V3O] (
		(EnumFacing.NORTH, NORTH),
		(EnumFacing.SOUTH, SOUTH),
		(EnumFacing.EAST, EAST),
		(EnumFacing.WEST, WEST),
		(EnumFacing.UP, UP),
		(EnumFacing.DOWN, DOWN)
	)

}
