package temportalist.origin.internal.client

import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.origin.api.client.EnumHUDOverlay
import temportalist.origin.foundation.client.IModClient
import temportalist.origin.foundation.common.IModPlugin
import temportalist.origin.internal.common.Origin

/**
  *
  * Created by TheTemportalist on 4/9/2016.
  *
  * @author TheTemportalist
  */
@SideOnly(Side.CLIENT)
object Client extends IModClient {

	override def getMod: IModPlugin = Origin

	/**
	  * This needs to be called in [[temportalist.origin.foundation.common.IProxy.preInit]]
	  */
	override def preInit(): Unit = {
		super.preInit()
		this.registerOverlay(OverlayHealth, EnumHUDOverlay.PRE)
	}

}
