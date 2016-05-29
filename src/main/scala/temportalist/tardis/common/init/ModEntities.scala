package temportalist.tardis.common.init

import temportalist.origin.foundation.common.registers.EntityRegister
import temportalist.tardis.common.Tardis
import temportalist.tardis.common.entity.EntityTardis

/**
  *
  * Created by TheTemportalist on 5/27/2016.
  *
  * @author TheTemportalist
  */
object ModEntities extends EntityRegister {

	override def register(): Unit = {
		this.addEntity(classOf[EntityTardis], "tardis", Tardis, 80, 3, true)
	}

}
