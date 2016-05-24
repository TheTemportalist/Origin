package com.temportalist.origin.screwdriver.api;

import com.temportalist.origin.api.client.utility.Rendering;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import scala.Tuple2;

import java.util.List;
import java.util.Set;

/**
 * Created by TheTemportalist on 12/20/2015.
 */
public abstract class Behavior {

	private final String name;
	private final boolean isDefault;

	public Behavior(String name) {
		this(name, false);
	}

	public Behavior(String name, boolean isDefaultBehavior) {
		this.name = name;
		this.isDefault = isDefaultBehavior;
	}

	public int register() {
		return ApiOriginScrewdriver.registerBehavior(this);
	}

	public String getName() {
		return this.name;
	}

	public boolean isDefaultBehavior() {
		return this.isDefault;
	}

	public int getGlobalID() {
		return ApiOriginScrewdriver.getBehaviorGlobalID(this.getName());
	}

	public void postInit() {}

	abstract public BehaviorType getBehaviorType();

	abstract public boolean isValidStackForSimulation(ItemStack stack);

	/**
	 * Returns true if the item can be used on the given entity, e.g. shears on sheep.
	 * If the source is changed in any way, it is updated inside the container stack.
	 * @param container The screwdriver stack which contains the source item
	 * @param source The first ItemStack which this behavior is enable by.
	 *                  If isDefaultBehavior == true,
	 *                  then this stack is equivalent to the container stack.
	 * @param player The player
	 * @param entity The entity being looked at
	 */
	public boolean itemInteractionForEntity(ItemStack container, ItemStack source,
			EntityPlayer player, EntityLivingBase entity) {
		return false;
	}

	/**
	 * Called before a block is broken.  Return true to prevent default block harvesting.
	 *
	 * Note: In SMP, this is called on both client and server sides!
	 *
	 * If the source is changed in any way, it is updated inside the container stack.
	 *
	 * @param container The screwdriver stack which contains the source item
	 * @param source The first ItemStack which this behavior is enable by.
	 *                  If isDefaultBehavior == true,
	 *                  then this stack is equivalent to the container stack.
	 * @param x The X Position
	 * @param y The X Position
	 * @param z The X Position
	 * @param player The Player that is wielding the item
	 * @return True to prevent harvesting, false to continue as normal
	 */
	public boolean onBlockStartBreak(ItemStack container, ItemStack source,
			int x, int y, int z, EntityPlayer player) {
		return false;
	}

	/**
	 * This is called when the item is used, before the block is activated.
	 * If the source is changed in any way, it is updated inside the container stack.
	 * @param container The screwdriver stack which contains the source item
	 * @param source The first ItemStack which this behavior is enable by.
	 *                  If isDefaultBehavior == true,
	 *                  then this stack is equivalent to the container stack.
	 * @param player The Player that used the item
	 * @param world The Current World
	 * @param x Target X Position
	 * @param y Target Y Position
	 * @param z Target Z Position
	 * @param side The side of the target hit
	 * @return Return true to prevent any further processing.
	 */
	public boolean onItemUseFirst(ItemStack container, ItemStack source,
			EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ) {
		return false;
	}

	/**
	 * Gets and additional tool classes that might want to be implemented by the behavior.
	 * By default, this will add the behaviors of the source stack.
	 * @param container The screwdriver stack which contains the source item
	 * @param source The first ItemStack which this behavior is enable by.
	 *                  If isDefaultBehavior == true,
	 *                  then this stack is equivalent to the container stack.
	 * @param toolClasses The classes the container stack will identify as
	 */
	public void getBehaviorToolClasses(ItemStack container, ItemStack source,
			Set<String> toolClasses) {
		if (!this.isDefaultBehavior())
			toolClasses.addAll(source.getItem().getToolClasses(source));
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed.
	 * If the source is changed in any way, it is updated inside the container stack.
	 *
	 * @param container The screwdriver stack which contains the source item
	 * @param source The first ItemStack which this behavior is enable by.
	 *                  If isDefaultBehavior == true,
	 *                  then this stack is equivalent to the container stack.
	 * @param world The world object of the player
	 * @param player The EntityPlayer
	 * @return The source stack. If changed, it will be updated.
	 */
	public ItemStack onItemRightClick(ItemStack container, ItemStack source, World world,
			EntityPlayer player) {
		return source;
	}

	/**
	 * How long it takes to use or consume an item
	 * @param container The screwdriver stack which contains the source item
	 * @param source The first ItemStack which this behavior is enable by.
	 *                  If isDefaultBehavior == true,
	 *                  then this stack is equivalent to the container stack.
	 */
	public int getMaxItemUseDuration(ItemStack container, ItemStack source) {
		return 0;
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 * @param container The screwdriver stack which contains the source item
	 * @param source The first ItemStack which this behavior is enable by.
	 *                  If isDefaultBehavior == true,
	 *                  then this stack is equivalent to the container stack.
	 */
	public EnumAction getItemUseAction(ItemStack container, ItemStack source) {
		return EnumAction.none;
	}

	/**
	 * Called each tick while using an item.
	 * @param container The screwdriver stack which contains the source item
	 * @param source The first ItemStack which this behavior is enable by.
	 *                  If isDefaultBehavior == true,
	 *                  then this stack is equivalent to the container stack.
	 * @param player The Player using the item
	 * @param count The amount of time in tick the item has been used for continuously
	 */
	public void onUsingTick(ItemStack container, ItemStack source,
			EntityPlayer player, int count) {}

	/**
	 * Called when the player releases the use item button.
	 * If the source is changed in any way, it is updated inside the container stack.
	 *
	 * @param container The screwdriver stack which contains the source item
	 * @param source The first ItemStack which this behavior is enable by.
	 *                  If isDefaultBehavior == true,
	 *                  then this stack is equivalent to the container stack.
	 * @param world The world object the player is in
	 * @param player The EntityPlayer
	 * @param itemInUseCount How long the use has been
	 */
	public void onPlayerStoppedUsing(ItemStack container, ItemStack source, World world,
			EntityPlayer player, int itemInUseCount) {}

	@SideOnly(Side.CLIENT)
	abstract protected ResourceLocation getTexture();

	protected int getTextureSize() {
		return 16;
	}

	@SideOnly(Side.CLIENT)
	public void draw(Minecraft mc, double x, double y, double z, double w, double h,
			boolean renderText) {
		GL11.glPushMatrix();
		Rendering.bindResource(this.getTexture());

		double iconScale = w / this.getTextureSize();
		Rendering.drawTexture(
				new Tuple2<Object, Object>((int)(x), (int)(y)),
				new Tuple2<Object, Object>((float)0, (float)0),
				new Tuple2<Object, Object>(
						MathHelper.floor_double(this.getTextureSize() * iconScale),
						MathHelper.floor_double(this.getTextureSize() * iconScale)),
				new Tuple2<Object, Object>((float)w, (float)h)
		);

		GL11.glPopMatrix();

		if (renderText) {
			GL11.glPushMatrix();
			float scale = 0.5F;
			float aScale = 1F / scale;
			GL11.glScaled(scale, scale, scale);
			mc.fontRenderer.drawStringWithShadow(
					this.name,
					(int) ((x + w / 2) * aScale) -
							(int) (mc.fontRenderer.getStringWidth(this.name) / 2.0F),
					(int) ((y + h) * aScale) + 2,
					16777215
			);
			GL11.glPopMatrix();
		}

	}

	public boolean onSelection(EntityPlayer player) {
		return true;
	}

	public void addInformationOnHover(List<String> hoverInfo) {
		hoverInfo.add(this.getName());
	}

}
