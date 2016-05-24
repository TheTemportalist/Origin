package temportalist.origin.api.common.enchant

import net.minecraft.enchantment.{Enchantment, EnumEnchantmentType}
import net.minecraft.util.ResourceLocation

/**
 *
 *
 * @author TheTemportalist
 */
class EnchantmentBase(name: String, weight: Int, reloc: ResourceLocation)
		extends Enchantment(EnchantmentHelper.getNewID, reloc, weight, EnumEnchantmentType.ALL) {

	this.setName(name)

}
