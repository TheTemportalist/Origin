package com.temportalist.origin.api.common.utility

import java.{lang, util}

import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTBase, NBTTagCompound, NBTTagList}

import scala.collection.{JavaConversions, mutable}

/**
 *
 *
 * @author TheTemportalist 2/1/15
 */
object Scala {

	def iterateCol[T, U](collection: util.Collection[T], f: T => U): Unit = {
		if (collection == null) return
		collection match {
			case list: util.List[T] =>
				for (item <- JavaConversions.asScalaBuffer(list)) f(item)
			case set: util.Set[T] =>
				for (item <- JavaConversions.asScalaSet(set)) f(item)
			case _ =>
				val iter: util.Iterator[T] = collection.iterator()
				while (iter.hasNext) f(iter.next())
		}
	}

	def iterate[T](data: Array[T], f: ((Int, T)) => Unit): Unit = {
		var i: Int = 0
		data.foreach(cell => {f(i, cell); i += 1})
	}

	def foreach(inv: IInventory, callback: (Int, ItemStack) => Unit): Unit = {
		if (inv == null) return
		for (i <- 0 until inv.getSizeInventory) callback(i, inv.getStackInSlot(i))
	}

	def foreach[T, U](data: Array[T], callback: (Int, T) => U): Unit = {
		if (data == null) return
		for (i <- 0 until data.length) {
			callback(i, data(i))
		}
	}

	def foreach[T, U](iter: lang.Iterable[T], callback: (Int, T) => U): Unit = {
		if (iter == null) return
		this.foreach(iter.iterator(), callback)
	}

	def foreach[T, U](iter: util.Iterator[T], callback: (Int, T) => U): Unit = {
		if (iter == null) return
		var i: Int = 0
		JavaConversions.asScalaIterator(iter).foreach(
			(t: T) => {
				callback(i, t)
				i += 1
			}
		)
	}

	def foreach[T, U](tagList: NBTTagList, callback: (Int, Any) => U): Unit = {
		for (i <- 0 until tagList.tagCount()) {
			callback(i, NBTHelper.getTagValueAt(tagList, i))
		}
	}

	def foreach(tagCom: NBTTagCompound, callback: (String, NBTBase) => Unit): Unit = {
		Scala.foreach(tagCom.func_150296_c, (i: Int, key: Any) => {
			callback(key.asInstanceOf[String], tagCom.getTag(key.asInstanceOf[String]))
		}: Unit)
	}

	def fill[B](size: Int, obj: B): Map[Int, B] = {
		val map: mutable.Map[Int, B] = mutable.Map[Int, B]()
		for (i <- 0 until size)
			map(i) = obj
		map.toMap
	}

	def toCollection[T](seq: Seq[T]): util.Collection[T] = JavaConversions.asJavaCollection(seq)

	def toArrayList[T](seq: Seq[T]): util.List[T] = new util.ArrayList[T](this.toCollection(seq))

}
