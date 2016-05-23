package temportalist.origin.foundation.common.network

import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import scala.reflect.ClassTag
import scala.reflect._

/**
  *
  *
  * Created by TheTemportalist on 4/9/2016.
  *
  * @author TheTemportalist
  */
@Deprecated
abstract class IPacketDetails[T <: IPacket] {

	def getClassPacket(implicit C: ClassTag[T]): Class[T] = classTag(C).runtimeClass.asInstanceOf[Class[T]]

	def getClassHandler: Class[_ <: IMessageHandler[T, IMessage]] = {
		classOf[Handler]
	}

	class Handler extends IMessageHandler[T, IMessage] {
		override def onMessage(message: T, ctx: MessageContext): IMessage =  {
			onHandlerMessage(message, ctx)
		}
	}

	def onHandlerMessage(message: T, ctx: MessageContext): IMessage

}
