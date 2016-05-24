package com.temportalist.origin.foundation.client.gui

import java.util

import com.temportalist.origin.api.common.utility.Scala
import com.temportalist.origin.foundation.common.IMod
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement
import cpw.mods.fml.client.config.{GuiConfig, IConfigElement}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.common.config.{ConfigElement, Configuration}

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

	def getConfigElements(plugin: IMod): java.util.List[IConfigElement[_]] = {
		GuiConfigBase.getConfigElements(plugin.options.config)
	}

	def getConfigElements(configuration: Configuration): util.List[IConfigElement[_]] = {
		val elements: util.List[IConfigElement[_]] = new util.ArrayList[IConfigElement[_]]()

		Scala.iterateCol(configuration.getCategoryNames, (categoryName: String) => {
			val category: ConfigElement[_] = new ConfigElement(configuration.getCategory(categoryName))
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
