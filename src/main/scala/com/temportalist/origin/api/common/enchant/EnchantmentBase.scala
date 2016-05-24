package com.temportalist.origin.api.common.enchant

import net.minecraft.enchantment.{Enchantment, EnumEnchantmentType}
import net.minecraft.util.ResourceLocation

/**
 *
 *
 * @author TheTemportalist
 */
class EnchantmentBase(name: String, weight: Int, reloc: ResourceLocation)
		extends Enchantment(EnchantmentHelper.getNewID, weight, EnumEnchantmentType.all) {

	this.setName(name)

}
