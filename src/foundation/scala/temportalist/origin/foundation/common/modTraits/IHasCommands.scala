package temportalist.origin.foundation.common.modTraits

import temportalist.origin.foundation.server.ICommand

/**
  *
  * Created by TheTemportalist on 4/9/2016.
  *
  * @author TheTemportalist
  */
trait IHasCommands {

	def getCommands: Seq[ICommand]

}
