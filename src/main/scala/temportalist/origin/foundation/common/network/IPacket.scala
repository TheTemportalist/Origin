package temportalist.origin.foundation.common.network

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, DataInputStream, DataOutputStream}
import java.util.UUID

import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{CompressedStreamTools, NBTTagCompound}
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.{ChunkCoordIntPair, World}
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.relauncher.Side
import temportalist.origin.api.common.general.INBTSaver
import temportalist.origin.api.common.lib.{Crash, LogHelper, NameParser, V3O}
import temportalist.origin.foundation.common.NetworkMod

import scala.reflect.runtime.universe._

/**
 *
 *
 * @author  TheTemportalist  5/3/15
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
		if (any == null) return this
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
				this.addAny(array.length)
				array.foreach(this.addAny)
			case uuid: UUID =>
				this.addAny(uuid.getMostSignificantBits)
				this.addAny(uuid.getLeastSignificantBits)
			case nbt: NBTTagCompound =>
				CompressedStreamTools.writeCompressed(nbt, this.writeData)
			case stack: ItemStack =>
				this.addAny(NameParser.getName(stack))
				this.addAny(stack.hasTagCompound)
				if (stack.hasTagCompound) this.addAny(stack.getTagCompound)
			case v: V3O =>
				this.addAny(v.x)
				this.addAny(v.y)
				this.addAny(v.z)
			case tile: TileEntity =>
				this.addAny(new V3O(tile))
			case saver: INBTSaver =>
				val tag: NBTTagCompound = new NBTTagCompound
				saver.writeTo(tag)
				this.addAny(tag)
			case array: Array[Byte] =>
				this.addAny(array.length)
				array.foreach(this.addAny)
			case array: Array[Int] =>
				this.addAny(array.length)
				array.foreach(this.addAny)
			case chunkPair: ChunkCoordIntPair =>
				this.addAny(chunkPair.chunkXPos)
				this.addAny(chunkPair.chunkZPos)
			case chunks: Seq[_] =>
				this.addAny(chunks.length)
				for (chunk <- chunks) this.addAny(chunk)
			case chunks: Array[ChunkCoordIntPair] =>
				this.addAny(chunks.length)
				for (chunk <- chunks) this.addAny(chunk)
			case _ =>
				throw new IllegalArgumentException("Origin|API: Packets cannot add \"" + any +
						"\" of class " + any.getClass.getCanonicalName + " objects")
		}
		this
	}

	final def add(all: Any*): IPacket = {
		if (all != null) all.foreach(this.addAny)
		this
	}

	final def get[T: TypeTag]: T = {
		val t = typeOf[T]
		(if (t =:= typeOf[Boolean]) this.readData.readBoolean()
		else if (t =:= typeOf[Byte]) this.readData.readByte()
		else if (t =:= typeOf[Short]) this.readData.readShort()
		else if (t =:= typeOf[Int]) this.readData.readInt()
		else if (t =:= typeOf[Char]) this.readData.readChar()
		else if (t =:= typeOf[Float]) this.readData.readFloat()
		else if (t =:= typeOf[Double])this.readData.readDouble()
		else if (t =:= typeOf[Long])this.readData.readLong()
		else if (t =:= typeOf[String])this.readData.readUTF()
		else if (t =:= typeOf[Array[Double]]) {
			val array = new Array[Double](this.get[Int])
			for (i <- array.indices) array(i) = this.get[Double]
			array
		}
		else if (t =:= typeOf[UUID]) new UUID(this.get[Long], this.get[Long])
		else if (t =:= typeOf[NBTTagCompound]) CompressedStreamTools.readCompressed(this.readData)
		else if (t =:= typeOf[ItemStack]) {
			val stack: ItemStack = NameParser.getItemStack(this.get[String])
			if (this.get[Boolean]) stack.setTagCompound(this.get[NBTTagCompound])
			stack
		}
		else if (t =:= typeOf[V3O]) new V3O(this.get[Double], this.get[Double], this.get[Double])
		else if (t =:= typeOf[Array[Byte]]) {
			val array = new Array[Byte](this.get[Int])
			for (i <- array.indices) array(i) = this.get[Byte]
			array
		}
		else if (t =:= typeOf[Array[Int]]) {
			val array = new Array[Int](this.get[Int])
			for (i <- array.indices) array(i) = this.get[Int]
			array
		}
		else if (t =:= typeOf[ChunkCoordIntPair]) new ChunkCoordIntPair(this.get[Int], this.get[Int])
		else if (t =:= typeOf[Seq[ChunkCoordIntPair]]) this.get[Array[ChunkCoordIntPair]].toSeq
		else if (t =:= typeOf[Array[ChunkCoordIntPair]]) {
			val array = new Array[ChunkCoordIntPair](this.get[Int])
			for (i <- array.indices) array(i) = this.get[ChunkCoordIntPair]
			array
		}
		else {
			LogHelper.error("Origin|API", "Packets cannot get type: " + typeOf[T])
			null
		}).asInstanceOf[T]
	}

	final def getTile(world: World): TileEntity = this.get[V3O].getTile(world)

	// ~~~~~~~~~~~ Packet Sending ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	def getReceivableSide: Side

	final def throwSideCrash(mod: NetworkMod, targetSide: Side): Unit = {
		new Crash(mod.getDetails.getModName, "Cannot send " + this.getClass.getCanonicalName +
				" to " + targetSide.toString + " side. Check packet.getReceivableSide & " +
				"where're the packet is registered to change this.", "")
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
