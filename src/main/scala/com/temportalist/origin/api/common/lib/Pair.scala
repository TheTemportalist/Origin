package com.temportalist.origin.api.common.lib

/**
 *
 *
 * @author TheTemportalist
 */
class Pair[A, B](private var key: A, private var value: B) {

	def getKey: A = key

	def getValue: B = value

	override def hashCode: Int = {
		var hash: Int = 1
		hash = hash * 37 + (if (this.key == null) 0 else this.key.hashCode)
		hash = hash * 37 + (if (this.value == null) 0 else this.value.hashCode)
		hash
	}

	override def equals(obj: Any): Boolean = obj match {
		case pair: Pair[A, B] =>
			this.key.equals(pair.key) && this.value.equals(pair.value)
		case _ => false
	}

}
