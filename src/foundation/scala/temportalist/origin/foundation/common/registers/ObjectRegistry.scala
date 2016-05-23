package temportalist.origin.foundation.common.registers

import scala.collection.mutable.ListBuffer

/**
  *
  * Created by TheTemportalist on 4/28/2016.
  *
  * @author TheTemportalist
  */
trait ObjectRegistry[T] {

	private val blocks = ListBuffer[T]()

	final def addObject(block: T): Unit = this.blocks += block

	final def getObjects: ListBuffer[T] = this.blocks

	final def registerObject[U <: T](obj: U): U = {
		this.addObject(obj)
		obj
	}

}
