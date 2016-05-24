package com.temportalist.origin.screwdriver.api;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by TheTemportalist on 12/27/2015.
 */
public class ApiOriginScrewdriver {

	/**
	 * Maps a Behavior's name to its global ID. NEVER MODIFY DIRECTLY.
	 * Additions are made by registering at ApiOriginScrewdriver.registerBehavior(Behavior)
	 */
	private static HashMap<String, Integer> behavior_NameToGlobalID =
			new HashMap<String, Integer>();
	/**
	 * Private variable to refelct into AddonScrewdriver's registerBehavior method.
	 * Set during ApiOriginScrewdriver.preInit during
	 * AddonScrewdriver.preInit(FMLPreInitializationEvent)
	 */
	private static Method registerBehavior = null;
	private static Object addonScrewdriver = null;

	/**
	 * Setups the API. SHOULD ONLY BE CALLED BY AddonScrewdriver.
	 */
	public static void preInit(Object addonScrewdriver) {
		if (ApiOriginScrewdriver.registerBehavior == null) {
			try {
				ApiOriginScrewdriver.addonScrewdriver = addonScrewdriver;
				ApiOriginScrewdriver.registerBehavior = addonScrewdriver.getClass().
						getDeclaredMethod("registerBehavior", Behavior.class);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get the global ID of a certain behavior by name
	 * @param name The name of the behavior
	 * @return The global ID number, -1 if no such behavior name
	 */
	public static int getBehaviorGlobalID(String name) {
		if (ApiOriginScrewdriver.behavior_NameToGlobalID.containsKey(name))
			return ApiOriginScrewdriver.behavior_NameToGlobalID.get(name);
		else return -1;
	}

	/**
	 * Registers a behavior
	 */
	public static int registerBehavior(Behavior behavior) {
		if (ApiOriginScrewdriver.registerBehavior != null) {
			try {
				int id = (Integer)ApiOriginScrewdriver.registerBehavior.invoke(
						ApiOriginScrewdriver.addonScrewdriver, behavior);
				ApiOriginScrewdriver.behavior_NameToGlobalID.put(behavior.getName(), id);
				return id;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

}
