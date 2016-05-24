package temportalist.origin.foundation.common;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import temportalist.origin.api.common.resource.IModDetails;

/**
 * Created by TheTemportalist on 12/22/2015.
 */
public abstract class NetworkMod {

	abstract public IModDetails getDetails();

	private SimpleNetworkWrapper network;
	private int packetDiscriminator = 0;

	protected void registerNetwork() {
		this.network = NetworkRegistry.INSTANCE.newSimpleChannel(this.getDetails().getModID());
	}

	public SimpleNetworkWrapper getNetwork() {
		return this.network;
	}

	protected <REQ extends IMessage, REPLY extends IMessage> void registerPacket(
			Class<? extends IMessageHandler<REQ, REPLY>> handler,
			Class<REQ> packetClass, Side side
	) {
		if (side == null) {
			this.registerPacket(handler, packetClass, Side.SERVER);
			this.registerPacket(handler, packetClass, Side.CLIENT);
		}
		else this.network.registerMessage(handler, packetClass, this.packetDiscriminator++, side);
	}
}
