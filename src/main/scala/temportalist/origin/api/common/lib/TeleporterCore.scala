package temportalist.origin.api.common.lib

import net.minecraft.entity.Entity
import net.minecraft.world.{Teleporter, WorldServer}

/**
 *
 *
 * @author TheTemportalist
 */
class TeleporterCore(ws: WorldServer) extends Teleporter(ws) {

	override def makePortal(entity: Entity): Boolean = {
		true
	}

	override def placeInExistingPortal(entityIn: Entity, rotationYaw: Float): Boolean = true

}
