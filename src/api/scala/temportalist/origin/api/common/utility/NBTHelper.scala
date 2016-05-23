package temportalist.origin.api.common.utility

import java.util
import java.util.UUID

import net.minecraft.nbt.NBTBase.NBTPrimitive
import net.minecraft.nbt._
import net.minecraftforge.fml.common.{FMLLog, ObfuscationReflectionHelper}
import temportalist.origin.api.common.INBTSaver

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

/**
 *
 *
 * @author TheTemportalist
 */
object NBTHelper {

	private val nbtTypes = Map[Type, Int](
		typeOf[Byte] -> 1,
		typeOf[Short] -> 2,
		typeOf[Int] -> 3,
		typeOf[Long] -> 4,
		typeOf[Float] -> 5,
		typeOf[Double] -> 6,
		typeOf[Array[Byte]] -> 7,
		typeOf[String] -> 8,
		typeOf[NBTTagList] -> 9,
		typeOf[NBTTagCompound] -> 10,
		typeOf[Array[Int]] -> 11
	)

	def getNBTType[T: TypeTag]: Int = this.nbtTypes(typeOf[T])

	def getTagList[T: TypeTag](nbt: NBTTagCompound, key: String): NBTTagList = {
		nbt.getTagList(key, this.getNBTType[T])
	}

	def getTagList[T: TypeTag](nbt: NBTTagCompound, key: String, f: Any => Unit)
			(implicit t: TypeTag[T]): Unit = {
		val list = nbt.getTagList(key, this.nbtTypes(t.tpe))
		for (i <- 0 until list.tagCount()) f(this.getTagValueAt(list, i))
	}

	def getTagValueAt(nbtList: NBTTagList, index: Int): Any = {
		this.getTagValue(ObfuscationReflectionHelper.getPrivateValue(
			classOf[NBTTagList], nbtList, 0).asInstanceOf[util.List[NBTBase]].get(index))
	}

	def getTagValue(tag: NBTBase): Any = {
		if (tag == null) return null
		tag.getId match {
			case 1 => tag.asInstanceOf[NBTPrimitive].getByte
			case 2 => tag.asInstanceOf[NBTPrimitive].getShort
			case 3 => tag.asInstanceOf[NBTPrimitive].getInt
			case 4 => tag.asInstanceOf[NBTPrimitive].getLong
			case 5 => tag.asInstanceOf[NBTPrimitive].getFloat
			case 6 => tag.asInstanceOf[NBTPrimitive].getDouble
			case 7 => tag.asInstanceOf[NBTTagByteArray].getByteArray
			case 8 => tag.asInstanceOf[NBTTagString].getString
			case 9 =>
				val list = tag.asInstanceOf[NBTTagList]
				val byteType = list.getTagType
				val retList = new util.ArrayList[Any]()
				for (i <- 0 until list.tagCount())
					retList.add(byteType match {
						case 5 => list.getFloatAt(i)
						case 6 => list.getDoubleAt(i)
						case 8 => list.getStringTagAt(i)
						case 10 => list.getCompoundTagAt(i)
						case 11 => list.getIntArrayAt(i)
						case _ => null
					})
				retList
			case 10 =>
				val compound = tag.asInstanceOf[NBTTagCompound]
				val map = mutable.Map[String, Any]()
				compound.getKeySet.toArray.foreach({
					case key: String => map(key) = this.getTagValue(compound.getTag(key))
				})
				map
			case 11 => tag.asInstanceOf[NBTTagIntArray].getIntArray
			case _ => tag
		}
	}

	def asTag(any: Any): NBTBase = {
		any match {
			case b: Boolean => new NBTTagByte(if (b) 1 else 0)
			case b: Byte => new NBTTagByte(b)
			case s: Short => new NBTTagShort(s)
			case i: Int => new NBTTagInt(i)
			case l: Long => new NBTTagLong(l)
			case f: Float => new NBTTagFloat(f)
			case d: Double => new NBTTagDouble(d)
			case ab: Array[Byte] => new NBTTagByteArray(ab)
			case s: String => new NBTTagString(s)
			case ai: Array[Int] => new NBTTagIntArray(ai)
			case map: mutable.Map[_, _] =>
				val tag = new NBTTagCompound
				map.foreach(f => tag.setTag(f._1.toString, this.asTag(f._2)))
				tag
			case saver: INBTSaver =>
				val tag: NBTTagCompound = new NBTTagCompound()
				saver.writeTo(tag)
				tag
			case uuidList: ListBuffer[_] =>
				val tagList = new NBTTagList
				for (id <- uuidList) tagList.appendTag(this.asTag(id))
				tagList
			case uuid: UUID =>
				new NBTTagString(uuid.getMostSignificantBits + "|" + uuid.getLeastSignificantBits)
			case _ =>
				throw new IllegalArgumentException("Invalid parameter type " +
						any.getClass.getCanonicalName + " with value " + any.toString)
				null
		}
	}

	def get[T: TypeTag](nbt: NBTBase): T = {
		val t = typeOf[T]
		(if (t =:= typeOf[Boolean]) this.get[Byte](nbt) > 0
		else if (t =:= typeOf[Byte]) nbt.asInstanceOf[NBTPrimitive].getByte
		else if (t =:= typeOf[Short]) nbt.asInstanceOf[NBTPrimitive].getShort
		else if (t =:= typeOf[Int]) nbt.asInstanceOf[NBTPrimitive].getInt
		else if (t =:= typeOf[Float]) nbt.asInstanceOf[NBTPrimitive].getFloat
		else if (t =:= typeOf[Double]) nbt.asInstanceOf[NBTPrimitive].getDouble
		else if (t =:= typeOf[Long]) nbt.asInstanceOf[NBTPrimitive].getLong
		else if (t =:= typeOf[String]) nbt.asInstanceOf[NBTTagString].getString
		else if (t =:= typeOf[UUID]) {
			val str = this.get[String](nbt)
			val bits = str.split("|")
			new UUID(bits(0).toLong, bits(1).toLong)
		}
		else if (t =:= typeOf[ListBuffer[UUID]]) {
			val list = ListBuffer[UUID]()
			val tagList = nbt.asInstanceOf[NBTTagList]
			for (i <- 0 until tagList.tagCount()) list += this.get[UUID](tagList.get(i))
			list
		}
		else {
			FMLLog.info("[Origin|API] NBTHelper cannot get type: " + t)
			null
		}).asInstanceOf[T]
	}

	def getNBT[A](vars: A*): NBTTagList = {
		val tag: NBTTagList = new NBTTagList
		for (num <- vars) tag.appendTag(this.asTag(num))
		tag
	}

}
