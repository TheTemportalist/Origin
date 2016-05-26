package temportalist.origin.api.common.helper

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import temportalist.origin.api.common.lib.Vect
import temportalist.origin.api.common.utility.Players

/**
  *
  * Created by TheTemportalist on 5/24/2016.
  *
  * @author TheTemportalist
  */
object HelperRayTrace {

	def getStartVect(entity: EntityLivingBase): Vect = {
		var vect = new Vect(entity) + Vect.UP * entity.getEyeHeight
		entity match {
			case player: EntityPlayer =>
				if (player.getEntityWorld.isRemote)
					vect += Vect.DOWN * player.getDefaultEyeHeight
				else if (player.isInstanceOf[EntityPlayerMP] && player.isSneaking)
					vect += Vect.DOWN * 0.08D
			case _ =>
		}
		vect
	}

	def getEndVect(entity: EntityLivingBase, reachDistanceNonPlayer: Double = 5D,
			startIn: Vect = null, partialTicks: Float = 1F): Vect = {
		val reach =
			entity match {
				case player: EntityPlayer => Players.getReachDistance(player)
				case _ => reachDistanceNonPlayer
			}
		(if (startIn != null) startIn else this.getStartVect(entity)) +
				new Vect(entity.getLook(partialTicks)) * reach
	}

	// TODO https://github.com/TheTemportalist/Origin/blob/1.8.8/src/main/scala/temportalist/origin/api/common/utility/Cursor.scala#L62

}
