package com.creativemd.creativecore.common.utils.sorting;

import net.minecraft.block.Block;

public abstract class BlockSelector {
	
	public abstract boolean is(Block block, int meta);
	
	public static class BlockSelectorAnd extends BlockSelector {
		
		public BlockSelector[] selectors;
		
		public BlockSelectorAnd(BlockSelector... selectors) {
			this.selectors = selectors;
		}
		
		@Override
		public boolean is(Block block, int meta) {
			for (BlockSelector selector : selectors) {
				if (!selector.is(block, meta))
					return false;
			}
			return true;
		}
		
	}
	
	public static class BlockSelectorOr extends BlockSelector {
		
		public BlockSelector[] selectors;
		
		public BlockSelectorOr(BlockSelector... selectors) {
			this.selectors = selectors;
		}
		
		@Override
		public boolean is(Block block, int meta) {
			for (BlockSelector selector : selectors) {
				if (selector.is(block, meta))
					return true;
			}
			return false;
		}
	}
	
	public static class BlockSelectorBlock extends BlockSelector {
		
		public Block block;
		
		public BlockSelectorBlock(Block block) {
			this.block = block;
		}
		
		@Override
		public boolean is(Block block, int meta) {
			return this.block == block;
		}
		
	}
	
	public static class BlockSelectorBlocks extends BlockSelector {
		
		public Block[] blocks;
		
		public BlockSelectorBlocks(Block... blocks) {
			this.blocks = blocks;
		}
		
		@Override
		public boolean is(Block block, int meta) {
			for (Block searchBlock : blocks) {
				if (searchBlock == block)
					return true;
			}
			return false;
		}
		
	}
	
	public static class BlockSelectorState extends BlockSelector {
		
		public Block block;
		public int meta;
		
		public BlockSelectorState(Block block, int meta) {
			this.block = block;
			this.meta = meta;
		}
		
		@Override
		public boolean is(Block block, int meta) {
			return this.block == block && this.meta == meta;
		}
	}
	
}
