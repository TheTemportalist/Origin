package temportalist.origin.foundation.client.modTraits

import temportalist.origin.api.common.IModDetails
import temportalist.origin.foundation.common.IModPlugin
import temportalist.origin.foundation.common.modTraits.IHasDetails

/**
  *
  * Created by TheTemportalist on 4/9/2016.
  *
  * @author TheTemportalist
  */
trait IHasMod extends IHasDetails {

	def getMod: IModPlugin

	override def getDetails: IModDetails = this.getMod.getDetails

}
