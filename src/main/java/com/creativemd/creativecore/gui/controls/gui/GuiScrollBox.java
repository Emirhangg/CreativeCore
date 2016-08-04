package com.creativemd.creativecore.gui.controls.gui;

import javax.vecmath.Vector4d;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.gui.ContainerControl;
import com.creativemd.creativecore.gui.GuiControl;
import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.container.GuiParent;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;

public class GuiScrollBox extends GuiParent{
	
	public int maxScroll = 0;
	public int scrolled = 0;
	public float scaleFactor;
	public boolean dragged;
	public int scrollbarWidth = 14;
	
	public GuiScrollBox(String name, int x, int y, int width, int height) {
		this(name, x, y, width, height, 1F);
	}

	public GuiScrollBox(String name, int x, int y, int width, int height, float scaleFactor) {
		super(name, x, y, width, height);
		this.scaleFactor = scaleFactor;
		this.marginWidth = 0;
	}
	
	@Override
	public float getScaleFactor()
	{
		return scaleFactor;
	}
	
	@Override
	protected int getOffsetY()
	{
		return -scrolled;
	}
	
	public void onScrolled()
	{
		if(this.scrolled < 0)
			this.scrolled = 0;
		if(this.scrolled > maxScroll)
			this.scrolled = maxScroll;
	}
	
	@Override
	public boolean mouseScrolled(int x, int y, int scrolled){
		if(super.mouseScrolled(x, y, scrolled))
			return true;
		this.scrolled -= scrolled*10;
		onScrolled();
		return true;
	}
	
	@Override
	public boolean mousePressed(int x, int y, int button)
	{
		if(button == 0 && width-x <= scrollbarWidth && needsScrollbar())
		{
			playSound(SoundEvents.UI_BUTTON_CLICK);
			dragged = true;
			return true;
		}
		return super.mousePressed(x, y, button);
	}
	
	@Override
	public void mouseMove(int x, int y, int button){
		if(dragged)
		{
			double percent = (double)(y-this.posY)/(double)(height);
			scrolled = (int) (percent*maxScroll);
			//System.out.println("scrolling to " + scrolled + "; percent=" + percent);
			onScrolled();
		}
		super.mouseMove(x, y, button);
	}
	
	@Override
	public void mouseReleased(int x, int y, int button){
		super.mouseReleased(x, y, button);
		dragged = false;
	}
	
	public boolean needsScrollbar()
	{
		return lastRenderedHeight > this.height-getContentOffset()*2;
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		super.renderContent(helper, style, width-scrollbarWidth, height);
		style.getBorder(this).renderStyle(width-scrollbarWidth, 0, helper, scrollbarWidth, height);
		style.getMouseOverBackground(this).renderStyle(width-scrollbarWidth+1, 0, helper, scrollbarWidth-1, height);	
		
		int scrollThingHeight = Math.max(10, Math.min(height, lastRenderedHeight/height/height));
		if(lastRenderedHeight < height)
			scrollThingHeight = height;
		double percent = (double)scrolled/(double)maxScroll;
		style.getBorder(this).renderStyle(width-scrollbarWidth+1, (int) (percent*(height-scrollThingHeight)), helper, scrollbarWidth-1, scrollThingHeight);
		style.getFace(this).renderStyle(width-scrollbarWidth+2, (int) (percent*(height-scrollThingHeight))+1, helper, scrollbarWidth-3, scrollThingHeight-2);
		
		maxScroll = Math.max(0, (lastRenderedHeight-height)+10);
	}
	
	@Override
	public boolean hasMouseOverEffect()
	{
		return false;
	}
}