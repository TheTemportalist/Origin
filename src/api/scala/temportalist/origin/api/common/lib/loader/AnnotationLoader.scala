package temportalist.origin.api.common.lib.loader

import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

import scala.collection.mutable.ListBuffer
import scala.collection.{JavaConversions, mutable}

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
class AnnotationLoader[C, T](private val annotation: Class[C], private val instance: Class[T]) {

	private val classInstances = mutable.Map[Class[_ <: T], mutable.Map[String, AnyRef]]()
	private var mapKeys = ListBuffer[Class[_ <: T]]()

	final def loadAnnotations(event: FMLPreInitializationEvent): Unit = {
		this.findInstanceClasses(event.getAsmData)
	}

	final def findInstanceClasses(asmData: ASMDataTable): Unit = {
		val annotationName = this.annotation.getName
		val dataAnnotatedClasses = JavaConversions.asScalaSet(asmData.getAll(annotationName))
		for (dataAnnotatedClass <- dataAnnotatedClasses) {
			try {
				val annotatedClass = Class.forName(dataAnnotatedClass.getClassName)
				val annotatedClassAsSub = annotatedClass.asSubclass(this.instance)
				val annotationInfo = JavaConversions.mapAsScalaMap(dataAnnotatedClass.getAnnotationInfo)
				this.classInstances.put(annotatedClassAsSub, annotationInfo)
				this.onAnnotationClassFound(annotatedClassAsSub, annotationInfo)
			}
			catch {
				case e: Exception =>
					e.printStackTrace()
			}
		}
		this.mapKeys ++= this.classInstances.keySet.toSeq.sortWith(this.keySorter)
	}

	def keySorter(a: Class[_ <: T], b: Class[_ <: T]): Boolean = a.getSimpleName < b.getSimpleName

	def onAnnotationClassFound[I <: T](implementer: Class[I], info: mutable.Map[String, AnyRef]): Unit = {}

	final def getClassInstances: Iterable[Class[_ <: T]] = this.mapKeys

	final def getAnnotationInfo(clazz: Class[_ <: T]) =
		this.classInstances.getOrElse(clazz, Map[String, AnyRef]())

}
