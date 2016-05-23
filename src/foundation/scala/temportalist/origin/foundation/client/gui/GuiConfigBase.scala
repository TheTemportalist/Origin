package temportalist.origin.foundation.client.gui

import java.util

import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.common.config.{ConfigElement, Configuration}
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement
import net.minecraftforge.fml.client.config.{GuiConfig, IConfigElement}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.origin.api.common.utility.Scala
import temportalist.origin.foundation.common.IModPlugin
import temportalist.origin.foundation.common.registers.OptionRegister

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class GuiConfigBase(guiParent: GuiScreen, mod: IModPlugin) extends GuiConfig(
	guiParent, GuiConfigBase.getConfigElements(mod), mod.getDetails.getModId,
	null, false, false, GuiConfigBase.getTitle(mod)
) {

}

@SideOnly(Side.CLIENT)
object GuiConfigBase {

	def getTitle(mod: IModPlugin): String = {
		mod.getOptions match {
			case options: OptionRegister => GuiConfig.getAbridgedConfigPath(options.config.toString)
			case _ => null
		}
	}

	def getConfigElements(mod: IModPlugin): java.util.List[IConfigElement] = {
		GuiConfigBase.getConfigElements(mod.getOptions.config)
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
