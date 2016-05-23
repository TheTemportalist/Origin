package temportalist.origin.foundation.common

import temportalist.origin.api.common.IModDetails
import temportalist.origin.foundation.common.registers.OptionRegister

/**
  *
  * Created by TheTemportalist on 4/9/2016.
 *
  * @author TheTemportalist
  */
abstract class ModBase extends IMod with IModDetails {

	override def getDetails: IModDetails = this

	override def getOptions: OptionRegister = null

}
