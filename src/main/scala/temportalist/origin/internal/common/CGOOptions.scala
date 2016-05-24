package temportalist.origin.internal.common

import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.origin.foundation.common.register.OptionRegister
import temportalist.origin.internal.client.gui.GuiConfig

import scala.collection.mutable

/**
 *
 *
 * @author TheTemportalist
 */
object CGOOptions extends OptionRegister {

	var secretPumpkin: Boolean = true
	var coloredHearts: Boolean = true
	var heartColors: Array[String] = Array[String](
		"#ff1313",
		"#f26c00", "#eabd00", "#00ce00", "#0097ed", "#aa7eff",
		"#ea77fb", "#fb77a0", "#fbd177", "#fbd177", "#fbd177",
		"#fbd177"
	)
	var enableSoundControl: Boolean = false
	var volumeControls: mutable.Map[String, Float] = mutable.Map[String, Float]()

	override def register(): Unit = {

		///*
		this.secretPumpkin = this.getAndComment(
			"general",
			"Secret Pumpkin",
			"Shhhhh!",
			value = this.secretPumpkin
		)
		//*/

		if (FMLCommonHandler.instance().getEffectiveSide.isClient) // if the jar is a client jar
			this.registerClient()

	}

	@SideOnly(Side.CLIENT)
	def registerClient() {
		this.coloredHearts = this.getAndComment(
			"client",
			// coloured because hilburn
			"Coloured Hearts",
			"Collapses the vanilla multiple rows of hearts into a colour coded single layer",
			value = this.coloredHearts
		)
		this.heartColors = this.getAndComment(
			"client",
			"Heart Colours",
			"The colors of the hearts. The quantity of colors here represents the tiers of hearts." +
					"(quantity * 20 + 20 = total max health accounted for)",
			value = this.heartColors
		)

		/*
		this.enableSoundControl = this.getAndComment(
			"client.music", "Enable Sound Control", "", value = false)
		SoundCategory.values().foreach((sound: SoundCategory) => {
			this.volumeControls(sound.getCategoryName) = this.getAndComment(
				"client.music", sound.getCategoryName + " volume", "", 100D
			).toFloat / 100f
			if (this.enableSoundControl) Rendering.mc.gameSettings.setSoundLevel(sound,
					this.volumeControls(sound.getCategoryName))
		})
		if (this.enableSoundControl) Rendering.mc.gameSettings.saveOptions()
		*/

	}

	override def getExtension: String = "json"

	@SideOnly(Side.CLIENT)
	override def mainConfigGuiClass(): Class[_ <: GuiScreen] = {
		classOf[GuiConfig]
	}

}
