package com.creativemd.creativecore.client.rendering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.creativemd.creativecore.client.rendering.model.CreativeBakedQuad;
import com.creativemd.creativecore.common.utils.ColorUtils;
import com.creativemd.creativecore.common.utils.CubeObject;
import com.creativemd.creativecore.common.utils.RotationUtils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.EnumFaceDirection.VertexInformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCubeObject extends CubeObject {
	
	public static enum EnumSideRender {
		
		INSIDE_RENDERED(true, false),
		INSIDE_NOT_RENDERED(false, false),
		OUTSIDE_RENDERED(true, true),
		OUTSIDE_NOT_RENDERD(false, true);
		
		public final boolean shouldBeRendered;
		public final boolean outside;
		
		EnumSideRender(boolean shouldBeRendered, boolean outside)
		{
			this.shouldBeRendered = shouldBeRendered;
			this.outside = outside;
		}
		
	}
	
	public Block block;
	public int meta = 0;
	public int color = -1;
	
	public Object customData = null;
	
	public boolean keepVU = false;
	
	private EnumSideRender renderEast = EnumSideRender.INSIDE_RENDERED;
	private EnumSideRender renderWest = EnumSideRender.INSIDE_RENDERED;
	private EnumSideRender renderUp = EnumSideRender.INSIDE_RENDERED;
	private EnumSideRender renderDown = EnumSideRender.INSIDE_RENDERED;
	private EnumSideRender renderSouth = EnumSideRender.INSIDE_RENDERED;
	private EnumSideRender renderNorth = EnumSideRender.INSIDE_RENDERED;
	
	private List<BakedQuad> quadEast = null;
	private List<BakedQuad> quadWest = null;
	private List<BakedQuad> quadUp = null;
	private List<BakedQuad> quadDown = null;
	private List<BakedQuad> quadSouth = null;
	private List<BakedQuad> quadNorth = null;
	
	public boolean doesNeedQuadUpdate = true;
	
	public void setQuad(EnumFacing facing, List<BakedQuad> quad)
	{
		switch(facing)
		{
		case DOWN:
			quadDown = quad;
			break;
		case EAST:
			quadEast = quad;
			break;
		case NORTH:
			quadNorth = quad;
			break;
		case SOUTH:
			quadSouth = quad;
			break;
		case UP:
			quadUp = quad;
			break;
		case WEST:
			quadWest = quad;
			break;
		}
	}
	
	public List<BakedQuad> getQuad(EnumFacing facing)
	{
		switch(facing)
		{
		case DOWN:
			return quadDown;
		case EAST:
			return quadEast;
		case NORTH:
			return quadNorth;
		case SOUTH:
			return quadSouth;
		case UP:
			return quadUp;
		case WEST:
			return quadWest;
		}
		return null;
	}
	
	public RenderCubeObject(CubeObject cube, RenderCubeObject cube2)
	{
		super(cube);
		applyExtraCubeData(cube2);
	}
	
	public RenderCubeObject(CubeObject cube, Block block, int meta)
	{
		super(cube);
		this.block = block;
		this.meta = meta;
	}
	
	public RenderCubeObject(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Block block)
	{
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.block = block;
	}
	
	public RenderCubeObject(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Block block, int meta)
	{
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.block = block;
		this.meta = meta;
	}
	
	protected void applyExtraCubeData(CubeObject cube)
	{
		if(cube instanceof RenderCubeObject)
		{
			this.block = ((RenderCubeObject) cube).block;
			this.meta = ((RenderCubeObject) cube).meta;
			this.color = ((RenderCubeObject) cube).color;
			this.renderEast = ((RenderCubeObject) cube).renderEast;
			this.renderWest = ((RenderCubeObject) cube).renderWest;
			this.renderUp = ((RenderCubeObject) cube).renderUp;
			this.renderDown = ((RenderCubeObject) cube).renderDown;
			this.renderSouth = ((RenderCubeObject) cube).renderSouth;
			this.renderNorth = ((RenderCubeObject) cube).renderNorth;
		}
	}
	
	public RenderCubeObject setColor(Vec3i color)
	{
		this.setColor(ColorUtils.RGBToInt(color));
		return this;
	}
	
	public RenderCubeObject setColor(int color)
	{
		this.color = color;
		return this;
	}
	
	public IBlockState getModelState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return block.getExtendedState(state, world, pos);
	}
	
	public IBlockState getBlockState()
	{
		if(meta != -1)
			return block.getStateFromMeta(meta);
		else
			return block.getDefaultState();
	}
	
	public IBlockState getBlockState(Block block)
	{
		if(meta != -1)
			return block.getStateFromMeta(meta);
		else
			return block.getDefaultState();
	}
	
	public void setSideRender(EnumFacing facing, EnumSideRender renderer)
	{
		switch(facing)
		{
		case DOWN:
			renderDown = renderer;
			break;
		case EAST:
			renderEast = renderer;
			break;
		case NORTH:
			renderNorth = renderer;
			break;
		case SOUTH:
			renderSouth = renderer;
			break;
		case UP:
			renderUp = renderer;
			break;
		case WEST:
			renderWest = renderer;
			break;
		}
	}
	
	public EnumSideRender getSidedRendererType(EnumFacing facing)
	{
		switch(facing)
		{
		case DOWN:
			return renderDown;
		case EAST:
			return renderEast;
		case NORTH:
			return renderNorth;
		case SOUTH:
			return renderSouth;
		case UP:
			return renderUp;
		case WEST:
			return renderWest;
		}
		return EnumSideRender.INSIDE_RENDERED;
	}
	
	public boolean shouldSideBeRendered(EnumFacing facing)
	{
		switch(facing)
		{
		case DOWN:
			return renderDown.shouldBeRendered;
		case EAST:
			return renderEast.shouldBeRendered;
		case NORTH:
			return renderNorth.shouldBeRendered;
		case SOUTH:
			return renderSouth.shouldBeRendered;
		case UP:
			return renderUp.shouldBeRendered;
		case WEST:
			return renderWest.shouldBeRendered;
		}
		return true;
	}
	
	public List<BakedQuad> getBakedQuad(BlockPos offset, IBlockState state, IBakedModel blockModel, EnumFacing facing, long rand, boolean overrideTint, int defaultColor)
	{
		List<BakedQuad> blockQuads = blockModel.getQuads(state, facing, rand);
		
		if(blockQuads.isEmpty())
			return Collections.emptyList();
		
		int color = this.color != -1 ? this.color : defaultColor;
		
		List<BakedQuad> quads = new ArrayList<>();
		for(int i = 0; i < blockQuads.size(); i++)
		{
			BakedQuad oldQuad = blockQuads.get(i);
			
			int index = 0;
			float tempMinX = Float.intBitsToFloat(oldQuad.getVertexData()[index]);
			float tempMinY = Float.intBitsToFloat(oldQuad.getVertexData()[index + 1]);
			float tempMinZ = Float.intBitsToFloat(oldQuad.getVertexData()[index + 2]);
			
			index = 2 * oldQuad.getFormat().getIntegerSize();
			float tempMaxX = Float.intBitsToFloat(oldQuad.getVertexData()[index]);
			float tempMaxY = Float.intBitsToFloat(oldQuad.getVertexData()[index + 1]);
			float tempMaxZ = Float.intBitsToFloat(oldQuad.getVertexData()[index + 2]);
			
			float minX = Math.min(tempMinX, tempMaxX);
			float minY = Math.min(tempMinY, tempMaxY);
			float minZ = Math.min(tempMinZ, tempMaxZ);
			float maxX = Math.max(tempMinX, tempMaxX);
			float maxY = Math.max(tempMinY, tempMaxY);
			float maxZ = Math.max(tempMinZ, tempMaxZ);
			
			
			//Check if it is intersecting, otherwise there is no need to render it
			switch(facing.getAxis())
			{
			case X:
				if(!(maxY > this.minY && minY < this.maxY && maxZ > this.minZ && minZ < this.maxZ))
					continue;
				break;
			case Y:
				if(!(maxX > this.minX && minX < this.maxX && maxZ > this.minZ && minZ < this.maxZ))
					continue;
				break;
			case Z:
				if(!(maxX > this.minX && minX < this.maxX && maxY > this.minY && minY < this.maxY))
					continue;
				break;
			}
			
			float sizeX = maxX - minX;
			float sizeY = maxY - minY;
			float sizeZ = maxZ - minZ;
			
			BakedQuad quad = new CreativeBakedQuad(blockQuads.get(i), this, color, overrideTint && (defaultColor == -1 || blockQuads.get(i).hasTintIndex()) && color != -1, facing);
			
			int uvIndex = quad.getFormat().getUvOffsetById(0) / 4;
			float u1 = Float.intBitsToFloat(quad.getVertexData()[uvIndex]);
			float v1 = Float.intBitsToFloat(quad.getVertexData()[uvIndex+1]);
			uvIndex = 2 * quad.getFormat().getIntegerSize() + quad.getFormat().getUvOffsetById(0) / 4;
			float u2 = Float.intBitsToFloat(quad.getVertexData()[uvIndex]);
			float v2 = Float.intBitsToFloat(quad.getVertexData()[uvIndex+1]);
			
			float sizeU = Math.abs(u2 - u1);
			float sizeV = Math.abs(v2 - v1);
			
			EnumFaceDirection direction = EnumFaceDirection.getFacing(facing);
			
			for (int k = 0; k < 4; k++) {
				VertexInformation vertex = direction.getVertexInformation(k);
				
				index = k * quad.getFormat().getIntegerSize();
				
				float x = facing.getAxis() == Axis.X ? getVertexInformationPosition(vertex.xIndex) : MathHelper.clamp(getVertexInformationPosition(vertex.xIndex), minX, maxX);
				float y = facing.getAxis() == Axis.Y ? getVertexInformationPosition(vertex.yIndex) : MathHelper.clamp(getVertexInformationPosition(vertex.yIndex), minY, maxY);
				float z = facing.getAxis() == Axis.Z ? getVertexInformationPosition(vertex.zIndex) : MathHelper.clamp(getVertexInformationPosition(vertex.zIndex), minZ, maxZ);
				
				float oldX = Float.intBitsToFloat(quad.getVertexData()[index]);
				float oldY = Float.intBitsToFloat(quad.getVertexData()[index+1]);
				float oldZ = Float.intBitsToFloat(quad.getVertexData()[index+2]);
				
				quad.getVertexData()[index] = Float.floatToIntBits(x);
				quad.getVertexData()[index+1] = Float.floatToIntBits(y);
				quad.getVertexData()[index+2] = Float.floatToIntBits(z);
				
				if(keepVU)
					continue;
				
				float uOffset = ((RotationUtils.getUFromFacing(facing, oldX, oldY, oldZ) - RotationUtils.getUFromFacing(facing, x, y, z)) / RotationUtils.getUFromFacing(facing, sizeX, sizeY, sizeZ)) * sizeU;
				float vOffset = ((RotationUtils.getVFromFacing(facing, oldX, oldY, oldZ) - RotationUtils.getVFromFacing(facing, x, y, z)) / RotationUtils.getVFromFacing(facing, sizeX, sizeY, sizeZ)) * sizeV;
				
				uvIndex = index + quad.getFormat().getUvOffsetById(0) / 4;
				
				if(facing == EnumFacing.NORTH || facing == EnumFacing.EAST)
					uOffset *= -1;
				
				if(facing == EnumFacing.DOWN || facing.getAxis() != Axis.Y)
					vOffset *= -1;
				
				quad.getVertexData()[uvIndex] = Float.floatToRawIntBits(Float.intBitsToFloat(quad.getVertexData()[uvIndex]) - uOffset);
				quad.getVertexData()[uvIndex + 1] = Float.floatToRawIntBits(Float.intBitsToFloat(quad.getVertexData()[uvIndex + 1]) - vOffset);
			}
			quads.add(quad);
		}
		return quads;
	}
	
}
