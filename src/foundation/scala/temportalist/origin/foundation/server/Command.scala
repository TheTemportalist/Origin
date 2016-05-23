package temportalist.origin.foundation.server

import net.minecraft.command.CommandBase
import temportalist.origin.foundation.common.modTraits.IHasDetails

/**
  *
  * Created by TheTemportalist on 4/10/2016.
  *
  * @author TheTemportalist
  */
abstract class Command extends CommandBase with ICommand with IHasDetails {

	override def getUsage: String = this.getDetails.getModId + ".commands." + this.getCommandName + ".usage"

}
