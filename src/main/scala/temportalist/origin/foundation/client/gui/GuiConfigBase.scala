package temportalist.origin.foundation.client.gui

import java.util

import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.common.config.{ConfigElement, Configuration}
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement
import net.minecraftforge.fml.client.config.{IConfigElement, GuiConfig}
import net.minecraftforge.fml.relauncher.{SideOnly, Side}
import temportalist.origin.api.common.utility.Scala
import temportalist.origin.foundation.common.IMod

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class GuiConfigBase(guiParent: GuiScreen, mod: IMod, modid: String) extends GuiConfig(
	guiParent, GuiConfigBase.getConfigElements(mod), modid,
	null, false, false, GuiConfigBase.getTitle(mod)
) {

}

@SideOnly(Side.CLIENT)
object GuiConfigBase {

	def getTitle(plugin: IMod): String = {
		GuiConfig.getAbridgedConfigPath(plugin.options.config.toString)
	}

	def getConfigElements(plugin: IMod): java.util.List[IConfigElement] = {
		GuiConfigBase.getConfigElements(plugin.options.config)
	}

	def getConfigElements(configuration: Configuration): util.List[IConfigElement] = {
		val elements = new util.ArrayList[IConfigElement]()

		Scala.iterateCol(configuration.getCategoryNames, (categoryName: String) => {
			val category = new ConfigElement(configuration.getCategory(categoryName))
			if (categoryName.equals(Configuration.CATEGORY_GENERAL)) {
				elements.addAll(category.getChildElements)
			}
			else {
				elements.add(new DummyCategoryElement(categoryName, categoryName, category.getChildElements))
			}
		})

		elements
	}

}
