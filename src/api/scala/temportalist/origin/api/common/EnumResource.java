package temportalist.origin.api.common;

/**
 * Created by TheTemportalist on 4/13/2016.
 *
 * @author TheTemportalist
 */
public enum EnumResource {

	BLOCKSTATES("blockstates"),
	LANG("lang"),
	MODEL("model"),
	MODEL_BLOCK(EnumResource.MODEL.getPath() + "/block"),
	MODEL_ITEM(EnumResource.MODEL.getPath() + "/item"),
	SOUNDS("sounds"),
	TEXTURE("textures"),
	GUI(EnumResource.TEXTURE.getPath() + "/gui"),
	TEXTURE_BLOCK(EnumResource.TEXTURE.getPath() + "/blocks"),
	TEXTURE_ITEM(EnumResource.TEXTURE.getPath() + "/items"),
	TEXTURE_MODEL(EnumResource.TEXTURE.getPath() + "/models");

	private final String path;

	EnumResource(String pathFromModid) {
		this.path = pathFromModid;
	}

	public String getPath() {
		return path;
	}

}
