package temportalist.origin.foundation.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import temportalist.origin.foundation.common.modTraits.IHasDetails;

/**
 * Created by TheTemportalist on 4/9/2016.
 *
 * @author TheTemportalist
 */
public abstract class NetworkMod implements IHasDetails {

	private SimpleNetworkWrapper network;
	private int packetDescriminator = 0;

	public String getNetworkName() {
		if (this.getDetails().getModId().length() > 20)
			return this.getDetails().getModId().substring(0, 20);
		else return this.getDetails().getModId();
	}

	protected void registerNetwork() {
		if (this.network == null)
			this.network = NetworkRegistry.INSTANCE.newSimpleChannel(this.getDetails().getModId());
	}

	public SimpleNetworkWrapper getNetwork() {
		return this.network;
	}

	public <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
			Class<? extends IMessageHandler<REQ, REPLY>> handler,
			Class<REQ> packetClass
	) {
		this.registerMessage(handler, packetClass, null);
	}

	public <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
			Class<? extends IMessageHandler<REQ, REPLY>> handler,
			Class<REQ> packetClass, Side side
	) {
		if (side == null) {
			this.registerMessage(handler, packetClass, Side.SERVER);
			this.registerMessage(handler, packetClass, Side.CLIENT);
		}
		else
			this.network.registerMessage(handler, packetClass, this.packetDescriminator++, side);
	}

}
