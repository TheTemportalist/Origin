package temportalist.tardis.client

import net.minecraft.client.renderer.entity.{Render, RenderManager}
import net.minecraftforge.fml.client.registry.{IRenderFactory, RenderingRegistry}
import temportalist.tardis.common.ProxyCommon
import temportalist.tardis.common.entity.EntityTardis

/**
  *
  * Created by TheTemportalist on 5/27/2016.
  *
  * @author TheTemportalist
  */
class ProxyClient extends ProxyCommon {

	override def preInit(): Unit = {

		RenderingRegistry.registerEntityRenderingHandler(classOf[EntityTardis],
			new IRenderFactory[EntityTardis] {
				override def createRenderFor(manager: RenderManager): Render[_ >: EntityTardis] = {
					new RenderTardis(manager)
				}
			}
		)

	}

}
