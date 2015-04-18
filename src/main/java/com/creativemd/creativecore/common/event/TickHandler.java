package com.creativemd.creativecore.common.event;

import com.creativemd.creativecore.common.multiblock.IMultiBlock;
import com.creativemd.creativecore.common.multiblock.MultiBlockStructure;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TickHandler {
	
	@SideOnly(Side.SERVER)
	@SubscribeEvent
	public void onTick(TickEvent tick) //Remove all Structures which doesn't have any connections
	{
		if(tick.side == Side.SERVER && tick.phase == Phase.END && tick.type == tick.type.SERVER)
		{
			int i = 0;
			while(i < MultiBlockStructure.allstructures.size()){
				if(MultiBlockStructure.allstructures.get(i).connections.size() == 0)
					MultiBlockStructure.allstructures.remove(i);
				else if(!MultiBlockStructure.allstructures.get(i).isValid())
				{
					for (int j = 0; j < MultiBlockStructure.allstructures.get(i).connections.size(); j++) {
						((IMultiBlock) MultiBlockStructure.allstructures.get(i).connections).setStructure(null);
					}
					MultiBlockStructure.allstructures.remove(i);
				}
				else
					i++;
			}
		}
	}
	
}