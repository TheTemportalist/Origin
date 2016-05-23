package temportalist.origin.foundation.common
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
abstract class IMod extends IModPlugin {

	def getProxy: IProxy

	override protected def preInitialize(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(event)

		this.registerHandler(this, this.getProxy)
		this.getProxy.preInit()
		this.registerGuiHandler(this, this.getProxy)
	}

	override protected def initialize(event: FMLInitializationEvent): Unit = {
		super.initialize(event)
		this.getProxy.register()
	}

	override protected def postInitialize(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)
		this.getProxy.postInit()
	}

}
