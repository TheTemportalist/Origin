package temportalist.origin.foundation.common.registers;

/**
 * @author TheTemportalist  5/4/15
 */
public enum RegisterPhase {
	PRE_INIT, POST_BLOCK, POST_ITEM, INIT;
	public static RegisterPhase[] PREINIT_ORDER = new RegisterPhase[]{
			PRE_INIT, POST_BLOCK, POST_ITEM};
}
