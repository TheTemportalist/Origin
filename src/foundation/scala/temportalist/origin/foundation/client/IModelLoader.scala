package temportalist.origin.foundation.client

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import temportalist.origin.api.common.block.BlockBase
import temportalist.origin.api.common.item.ItemBase
import temportalist.origin.foundation.common.IModPlugin
import temportalist.origin.foundation.common.registers.{BlockRegister, ItemRegister, Register}

/**
  *
  * Created by TheTemportalist on 4/28/2016.
  *
  * @author TheTemportalist
  */
trait IModelLoader {

	def autoLoadModels(mod: IModPlugin): Unit = {

		for (reg <- mod.getRegisters) {
			reg match {
				case regObj: BlockRegister =>
					for (obj <- regObj.getObjects) this.registerModel(mod, obj)
				case regObj: ItemRegister =>
					for (obj <- regObj.getObjects) this.registerModel(mod, obj)
				case _ =>
					this.registerOtherObjects(mod, reg)
			}
		}

	}

	def registerOtherObjects(mod: IModPlugin, reg: Register): Unit = {}

	final def registerModel(mod: IModPlugin, obj: ItemBase): Unit = {
		this.registerModel(obj, obj.getItemMetaRange, mod, obj.name, "inventory")
	}

	final def registerModel(mod: IModPlugin, obj: BlockBase): Unit = {
		if (obj.hasItemBlock)
			this.registerModel(obj.getItemBlock, obj.getItemMetaRange, mod, obj.name, "inventory")
	}

	final def registerModel(item: Item, metas: Range, mod: IModPlugin, name: String, variant: String = null): Unit = {
		for (meta <- metas)
			ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(
				mod.getDetails.getModId + ":" + name, variant
			))
	}

}
