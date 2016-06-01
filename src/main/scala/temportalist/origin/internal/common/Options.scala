package temportalist.origin.internal.common

import temportalist.origin.foundation.common.registers.OptionRegister

/**
  *
  * Created by TheTemportalist on 4/9/2016.
  *
  * @author TheTemportalist
  */
object Options extends OptionRegister {

	var coloredHearts: Boolean = true
	var heartColors: Array[String] = Array[String](
		"#ff1313",
		"#f26c00", "#eabd00", "#00ce00", "#0097ed", "#aa7eff",
		"#ea77fb", "#fb77a0", "#fbd177", "#fbd177", "#fbd177",
		"#fbd177"
	)

	override def getExtension: String = "json"

	override def register(): Unit = {

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

	}

}
