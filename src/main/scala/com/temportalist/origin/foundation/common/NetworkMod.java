package com.temportalist.origin.foundation.common;

import com.temportalist.origin.api.common.resource.IModDetails;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

/**
 * Created by TheTemportalist on 12/22/2015.
 */
public abstract class NetworkMod {

	abstract public IModDetails getDetails();

	private SimpleNetworkWrapper network;
	private int packetDescriminator = 0;

	protected void registerNetwork() {
		this.network = NetworkRegistry.INSTANCE.newSimpleChannel(this.getDetails().getModid());
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
		else this.network.registerMessage(handler, packetClass, this.packetDescriminator++, side);
	}
}
