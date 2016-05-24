package com.temportalist.origin.api.common.utility

import java.util

import com.temportalist.origin.api.common.general.INBTSaver
import cpw.mods.fml.common.ObfuscationReflectionHelper
import net.minecraft.nbt.NBTBase.NBTPrimitive
import net.minecraft.nbt._

import scala.collection.mutable
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

	@Deprecated
	def getTagList[T](nbt: NBTTagCompound, key: String, f: T => Unit)
			(implicit t: TypeTag[T]): Unit = {
		val list = nbt.getTagList(key, this.nbtTypes(t.tpe))
		for (i <- 0 until list.tagCount()) f(this.getTagValueAt(list, i).asInstanceOf[T])
	}

	def getTagList[T: TypeTag](nbt: NBTTagCompound, key: String, f: Any => Unit)
			(implicit t: TypeTag[T]): Unit = {
		val list = nbt.getTagList(key, this.nbtTypes(t.tpe))
		for (i <- 0 until list.tagCount()) f(this.getTagValueAt(list, i))
	}

	def getTagValueAt(nbtList: NBTTagList, index: Int): Any = {
		this.getTagValue(ObfuscationReflectionHelper
				.getPrivateValue(classOf[NBTTagList], nbtList, 0).asInstanceOf[util.List[NBTBase]]
				.get(index))
	}

	def getTagValue(tag: NBTBase): Any = {
		if (tag == null) return null
		tag.getId match {
			case 1 => tag.asInstanceOf[NBTPrimitive].func_150290_f
			case 2 => tag.asInstanceOf[NBTPrimitive].func_150289_e
			case 3 => tag.asInstanceOf[NBTPrimitive].func_150287_d
			case 4 => tag.asInstanceOf[NBTPrimitive].func_150291_c
			case 5 => tag.asInstanceOf[NBTPrimitive].func_150288_h
			case 6 => tag.asInstanceOf[NBTPrimitive].func_150286_g
			case 7 => tag.asInstanceOf[NBTTagByteArray].func_150292_c
			case 8 => tag.asInstanceOf[NBTTagString].func_150285_a_
			case 9 =>
				val list = tag.asInstanceOf[NBTTagList]
				val byteType = list.func_150303_d()
				val retList = new util.ArrayList[Any]()
				for (i <- 0 until list.tagCount())
					retList.add(byteType match {
						case 5 => list.func_150308_e(i)
						case 6 => list.func_150309_d(i)
						case 8 => list.getStringTagAt(i)
						case 10 => list.getCompoundTagAt(i)
						case 11 => list.func_150306_c(i)
						case _ => null
					})
				retList
			case 10 =>
				val compound = tag.asInstanceOf[NBTTagCompound]
				val map = mutable.Map[String, Any]()
				compound.func_150296_c().toArray.foreach({
					case key: String => map(key) = this.getTagValue(compound.getTag(key))
				})
				map
			case 11 => tag.asInstanceOf[NBTTagIntArray].func_150302_c
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
			case _ =>
				throw new IllegalArgumentException("Invalid parameter type " +
						any.getClass.getCanonicalName + " with value " + any.toString)
				null
		}
	}

	def getNBT[A](vars: A*): NBTTagList = {
		val tag: NBTTagList = new NBTTagList
		for (num <- vars) tag.appendTag(this.asTag(num))
		tag
	}

}
