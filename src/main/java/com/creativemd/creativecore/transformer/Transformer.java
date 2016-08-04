package com.creativemd.creativecore.transformer;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import javax.annotation.Nullable;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.world.biome.Biome;

public abstract class Transformer {
	
	public static ArrayList<Transformer> transformers = new ArrayList<>();
	
	public final String className;
	
	public Transformer(String className) {
		transformers.add(this);
		this.className = TransformerNames.patchClassName(className);
	}
	
	public static boolean areNodesEqual(AbstractInsnNode node, AbstractInsnNode node2)
	{
		if(node.getClass() == node2.getClass() && node.getOpcode() == node2.getOpcode())
		{
			if(node instanceof MethodInsnNode){
				return ((MethodInsnNode) node).name.equals(((MethodInsnNode) node2).name) &&
						((MethodInsnNode) node).desc.equals(((MethodInsnNode) node2).desc) &&
						((MethodInsnNode) node).owner.equals(((MethodInsnNode) node2).owner);
			}else if(node instanceof VarInsnNode){
				return ((VarInsnNode) node).var == ((VarInsnNode) node2).var;
			}else if(node instanceof InsnNode){
				return true;
			}else if(node instanceof TypeInsnNode){
				return ((TypeInsnNode) node).desc.equals(((TypeInsnNode) node2).desc);
			}else if(node instanceof JumpInsnNode){
				return ((JumpInsnNode) node).label == ((JumpInsnNode) node2).label;
			}else if(node instanceof LabelNode){
				return node == node2;
			}else if(node instanceof LineNumberNode){
				return ((LineNumberNode) node).line == ((LineNumberNode) node2).line;
			}else if(node instanceof FieldInsnNode){
				return ((FieldInsnNode) node).name.equals(((FieldInsnNode) node2).name) &&
						((FieldInsnNode) node).desc.equals(((FieldInsnNode) node2).desc) &&
						((FieldInsnNode) node).owner.equals(((FieldInsnNode) node2).owner);
			}else if(node instanceof LdcInsnNode){
				return ((LdcInsnNode) node).cst.equals(((LdcInsnNode) node2).cst);
			}
		}
		return false;
	}
	
	public void removeLabel(InsnList instructions, AbstractInsnNode node,  int labels)
	{
		replaceLabel(instructions, node, null, labels, false);
	}
	
	public void replaceLabel(InsnList instructions, AbstractInsnNode node, @Nullable ArrayList<AbstractInsnNode> replaceInstructions, int labels, boolean keepFirstLabel)
	{
		replaceLabelBefore(instructions, node, replaceInstructions, labels, 0, keepFirstLabel, true);
	}
	
	public void replaceLabelBefore(InsnList instructions, AbstractInsnNode node, @Nullable ArrayList<AbstractInsnNode> replaceInstructions, int labels, int labelsBefore, boolean keepFirstLabel, boolean deleteFrame)
	{
		ListIterator<AbstractInsnNode> iterator = instructions.iterator();
		LabelNode searchedLabel = null;
		ArrayList<LabelNode> foundLabels = new ArrayList<>();
		while(iterator.hasNext())
		{
			AbstractInsnNode insn = iterator.next();
			if(insn instanceof LabelNode)
				foundLabels.add((LabelNode) insn);
			if(areNodesEqual(insn, node))
			{
				int index = foundLabels.size()-1;
				index -= labelsBefore;
				if(index >= 0)
					searchedLabel = foundLabels.get(index);
				labels += labelsBefore;
				break;
			}
		}
		
		if(searchedLabel != null)
		{
			boolean found = false;
			int labelCounter = 0;
			iterator = instructions.iterator();
			while(iterator.hasNext())
			{
				AbstractInsnNode insn = iterator.next();
				if(found && insn instanceof LabelNode)
				{
					labelCounter++;
					if(labelCounter >= labels)
					{
						if(replaceInstructions != null)
							for(int i = 0; i < replaceInstructions.size(); i++)
								instructions.insertBefore(insn, replaceInstructions.get(i));
						break;
					}
				}
				if(insn == searchedLabel)
				{
					found = true;
					if(keepFirstLabel)
						continue;
				}
				if(found)
					if(deleteFrame || !(insn instanceof FrameNode))
						instructions.remove(insn);	
			}
		}else
			if(node instanceof LineNumberNode)
				System.out.println("COULD NOT FIND NODE line=" + ((LineNumberNode) node).line);
	}
	
	public boolean is(String className)
	{
		return className.equals(this.className);
	}
	
	public String patchDESC(String desc)
	{
		return TransformerNames.patchDESC(desc);
	}
	
	public String patchClassName(String className)
	{
		return TransformerNames.patchClassName(className);
	}
	
	public String patchFieldName(String fieldName)
	{
		return TransformerNames.patchFieldName(fieldName, patchClassName(this.className));
	}
	
	public String patchMethodName(String methodName, String desc)
	{
		return TransformerNames.patchMethodName(methodName, desc, patchClassName(this.className));
	}
	
	public abstract void transform(ClassNode node);
	
	public MethodNode findMethod(ClassNode node, String name, String desc)
	{
		Iterator<MethodNode> methods = node.methods.iterator();
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if (m.name.equals(name) && m.desc.equals(desc))
				return m;
		}
		return null;
	}
	
	public FieldNode findField(ClassNode node, String name)
	{
		Iterator<FieldNode> fields = node.fields.iterator();
		while(fields.hasNext())
		{
			FieldNode f = fields.next();
			if (f.name.equals(name))
				return f;
		}
		return null;
	}
	
	public void done()
	{
		transformers.remove(this);
		if(transformers.size() == 0)
			TransformerNames.emptyLists();
	}
	
}