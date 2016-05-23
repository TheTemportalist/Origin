package temportalist.origin.foundation.common.network

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, DataInputStream, DataOutputStream}
import java.util.UUID

import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{CompressedStreamTools, NBTTagCompound}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.relauncher.Side
import temportalist.origin.api.common.helper.Names
import temportalist.origin.api.common.lib.Vect

import scala.reflect.runtime.universe._

/**
  *
  *
  * Created by TheTemportalist on 4/9/2016.
  *
  * @author TheTemportalist
  */
trait IPacket extends IMessage {

	// ~~~~~~~~~~~ Default IMessage ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def toBytes(buffer: ByteBuf): Unit = buffer.writeBytes(this.writeStream.toByteArray)

	override def fromBytes(buffer: ByteBuf): Unit = {
		this.readData = new DataInputStream(new ByteArrayInputStream(buffer.array()))
		try {
			this.readData.skipBytes(1)
		}
		catch {
			case e: Exception =>
		}
	}

	// ~~~~~~~~~~~ Data Wrapper ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private val writeStream: ByteArrayOutputStream = new ByteArrayOutputStream()
	private val writeData: DataOutputStream = new DataOutputStream(this.writeStream)
	private var readData: DataInputStream = null

	final def addAny(any: Any): IPacket = {
		any match {
			case bool: Boolean => this.writeData.writeBoolean(bool)
			case byte: Byte => this.writeData.writeByte(byte)
			case short: Short => this.writeData.writeShort(short)
			case int: Int => this.writeData.writeInt(int)
			case char: Char => this.writeData.writeChar(char)
			case float: Float => this.writeData.writeFloat(float)
			case double: Double => this.writeData.writeDouble(double)
			case long: Long => this.writeData.writeLong(long)
			case str: String => this.writeData.writeUTF(str)
			case array: Array[Double] =>
				this.add(array.length)
				for (d: Double <- array)
					this.add(d)
			case uuid: UUID =>
				this.add(uuid.getMostSignificantBits)
				this.add(uuid.getLeastSignificantBits)
			case nbt: NBTTagCompound =>
				CompressedStreamTools.writeCompressed(nbt, this.writeData)
			//if (nbt == null) this.add((-1).toShort)
			//else this.add(CompressedStreamTools.compress(nbt))

			case stack: ItemStack =>
				this.add(Names.getName(stack))
				this.add(stack.hasTagCompound)
				if (stack.hasTagCompound) this.add(stack.getTagCompound)
			case v: Vect =>
				this.add(v.x)
				this.add(v.y)
				this.add(v.z)
			case tile: TileEntity =>
				this.add(new Vect(tile))
			case array: Array[Byte] =>
				this.add(array.length.toShort)
				this.writeData.write(array)
			case chunk: ChunkPos =>
				this.add(chunk.chunkXPos)
				this.add(chunk.chunkZPos)
			case array: Array[ChunkPos] =>
				this.add(array.length.toShort)
				array.foreach(f => this.add(f))
			case _ =>
				throw new IllegalArgumentException("Origin|API: Packets cannot add " +
						any.getClass.getCanonicalName + " objects")
		}
		this
	}

	final def add(all: Any*): IPacket = {
		if (all == null) return this
		all.foreach(any => {
			if (any != null) {
				this.addAny(any)
			}
		})
		this
	}

	final def get[T: TypeTag]: T = {
		(try {
			typeOf[T] match {
				case t if t =:= typeOf[Boolean] =>
					this.readData.readBoolean()
				case t if t =:= typeOf[Byte] =>
					this.readData.readByte()
				case t if t =:= typeOf[Short] =>
					this.readData.readShort()
				case t if t =:= typeOf[Int] =>
					this.readData.readInt()
				case t if t =:= typeOf[Char] =>
					this.readData.readChar()
				case t if t =:= typeOf[Float] =>
					this.readData.readFloat()
				case t if t =:= typeOf[Double] =>
					this.readData.readDouble()
				case t if t =:= typeOf[Long] =>
					this.readData.readLong()
				case t if t =:= typeOf[String] =>
					this.readData.readUTF()
				case t if t =:= typeOf[Array[Double]] =>
					val array: Array[Double] = new Array[Double](this.get[Int])
					for (i <- array.indices) array(i) = this.get[Double]
				case t if t =:= typeOf[UUID] =>
					new UUID(this.get[Long], this.get[Long])
				case t if t =:= typeOf[NBTTagCompound] =>
					CompressedStreamTools.readCompressed(this.readData)
					/*
					val array: Array[Byte] = this.get[Array[Byte]]
					if (array != null) {
						CompressedStreamTools.func_152457_a(array,
							new NBTSizeTracker(2097152L)
						)
					}
					else null
					*/
				case t if t =:= typeOf[ItemStack] =>
					val stack: ItemStack = Names.getItemStack(this.get[String])
					if (this.get[Boolean]) stack.setTagCompound(this.get[NBTTagCompound])
					stack
				case t if t =:= typeOf[Vect] =>
					new Vect(this.get[Double], this.get[Double], this.get[Double])
				case t if t =:= typeOf[Array[Byte]] =>
					val length: Short = this.get[Short]
					if (length < 0) null
					else {
						val array: Array[Byte] = new Array[Byte](length)
						this.readData.read(array)
						array
					}
				case t if t =:= typeOf[ChunkPos] =>
					new ChunkPos(this.get[Int], this.get[Int])
				case t if t =:= typeOf[Array[ChunkPos]] =>
					val array = new Array[ChunkPos](this.get[Short])
					for (i <- array.indices) array(i) = this.get[ChunkPos]
					array
				case _ =>
					FMLCommonHandler.instance().getFMLLogger.error(
						"Origin|API", "Packets cannot get type: " + typeOf[T])
					null
			}
		}
		catch {
			case e: Exception =>
				e.printStackTrace()
				null
		}).asInstanceOf[T] // wrap what ever returns to make compiler happy
	}

	final def getTile(world: World): TileEntity = this.get[Vect].getTile(world)

	// ~~~~~~~~~~~ Packet Sending ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	def getReceivableSide: Side

	final def throwSideCrash(mod: NetworkMod, targetSide: Side): Unit = {
		throw new IllegalArgumentException(
			"[" + mod.getDetails.getModName + "]" +
					"Cannot send " + this.getClass.getCanonicalName +
					" to " + targetSide.toString + " side. Check packet.getReceivableSide & " +
					"where're the packet is registered to change this."
		)
	}

	/**
	  * SERVER RECEIVER
	  * Sends the packet to the server
	  */
	final def sendToServer(mod: NetworkMod): Unit = {
		if (this.getReceivableSide != Side.CLIENT) mod.getNetwork.sendToServer(this)
		else this.throwSideCrash(mod, Side.CLIENT)
	}

	/**
	  * CLIENT RECEIVER
	  * Send the packet once to every single player on the current server,
	  * no matter what location or dimension they are in.
	  */
	final def sendToAll(mod: NetworkMod): Unit = {
		if (this.getReceivableSide != Side.SERVER) mod.getNetwork.sendToAll(this)
		else this.throwSideCrash(mod, Side.SERVER)
	}

	/**
	  * CLIENT RECEIVER
	  * Send the packet to all players currently in the given dimension.
	  * @param dimension The dimension to send the packet to
	  */
	final def sendToDimension(mod: NetworkMod, dimension: Int): Unit = {
		if (this.getReceivableSide != Side.SERVER) mod.getNetwork.sendToDimension(this, dimension)
		else this.throwSideCrash(mod, Side.SERVER)
	}

	/**
	  * CLIENT RECEIVER
	  * All players within the TargetPoint will have the packet sent to them.
	  * @param point The point to send the packet to; requires a dimension, x/y/z coordinates,
	  *              and a range. It represents a cube in a world.
	  */
	final def sendToAllAround(mod: NetworkMod, point: NetworkRegistry.TargetPoint): Unit = {
		if (this.getReceivableSide != Side.SERVER) mod.getNetwork.sendToAllAround(this, point)
		else this.throwSideCrash(mod, Side.SERVER)
	}

	/**
	  * Send the packet to a single client.
	  * @param player The server-side player
	  */
	final def sendToPlayer(mod: NetworkMod, player: EntityPlayerMP): Unit = {
		if (this.getReceivableSide != Side.SERVER) mod.getNetwork.sendTo(this, player)
		else this.throwSideCrash(mod, Side.SERVER)
	}

	final def sendToOpposite(mod: NetworkMod, side: Side, player: EntityPlayer): Unit = {
		side match {
			case Side.CLIENT => this.sendToServer(mod)
			case Side.SERVER => this.sendToPlayer(mod, player.asInstanceOf[EntityPlayerMP])
			case _ =>
		}
	}

	final def sendToBoth(mod: NetworkMod, player: EntityPlayerMP): Unit = {
		this.sendToServer(mod)
		this.sendToPlayer(mod, player)
	}

}
