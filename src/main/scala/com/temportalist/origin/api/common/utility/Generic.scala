package com.temportalist.origin.api.common.utility

import java.util

/**
 *
 *
 * @author TheTemportalist
 */
object Generic {

	def getR(color: Int): Int = ((0xff000000 | color) >> 16) & 0xFF

	def getG(color: Int): Int = ((0xff000000 | color) >> 8) & 0xFF

	def getB(color: Int): Int = ((0xff000000 | color) >> 0) & 0xFF

	def addToList[T](list: util.List[_], obj: T): Unit = {
		list.asInstanceOf[util.List[T]].add(obj)
	}

	def round(value: Double, step: Double): Double = {
		val mult: Double = 1d / step
		Math.floor(value * mult) / mult
	}

}
