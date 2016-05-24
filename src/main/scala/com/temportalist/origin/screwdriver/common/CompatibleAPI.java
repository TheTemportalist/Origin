package com.temportalist.origin.screwdriver.common;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModAPIManager;

/**
 * Created by TheTemportalist on 12/21/2015.
 */
public enum CompatibleAPI {

	// Has API
	AE(CompatibleAPI.AE_MOD_ID, "appliedenergistics2|API"),
	BUILDCRAFT(CompatibleAPI.BC_MOD_ID, "BuildCraftAPI|tools"),
	COFH(CompatibleAPI.COFH_MOD_ID, "CoFHAPI|item"),
	ENDERIO(CompatibleAPI.ENDERIO_MOD_ID, "EnderIOAPI|Tools"),
	RAILCRAFT(CompatibleAPI.RAILCRAFT_MOD_ID, "RailcraftAPI|items"),
	// No API used
	MFR(CompatibleAPI.MRF_MOD_ID),
	IE(CompatibleAPI.IE_MOD_ID);

	public static final String
			AE_MOD_ID = "appliedenergistics2",
			BC_MOD_ID = "BuildCraft|Core",
			COFH_MOD_ID = "CoFHLib",
			ENDERIO_MOD_ID = "EnderIO",
			RAILCRAFT_MOD_ID = "Railcraft",
			MRF_MOD_ID = "MineFactoryReloaded",
			IE_MOD_ID = "ImmersiveEngineering";

	private final String modid, apiID;

	CompatibleAPI(String id) {
		this(id, id);
	}

	CompatibleAPI(String modid, String apiID) {
		this.modid = modid;
		this.apiID = apiID;
	}

	public String getModid() {
		return this.modid;
	}

	public boolean isModLoaded() {
		return Loader.isModLoaded(this.modid);
	}

	public boolean isAPILoaded() {
		return ModAPIManager.INSTANCE.hasAPI(this.apiID);
	}

}
