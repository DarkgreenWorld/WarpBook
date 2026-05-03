package com.arcanc.warp_book.content.gui.inventory.container;

import com.arcanc.warp_book.Database;
import com.arcanc.warp_book.content.gui.inventory.SlotWarpBook;
import com.arcanc.warp_book.content.item.WarpBookItem;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class ContainerWarpBook implements Container, Nameable
{
	private final ItemStack heldStack;
	private List<ItemStack> content;
	public final int slotsCount = 54;

	public ContainerWarpBook(ItemStack heldItem)
	{
		ItemContainerContents contents = WarpBookItem.getContent(heldItem);
		content = NonNullList.withSize(54, ItemStack.EMPTY);
		
		for(int i = 0; i < contents.getSlots();i++)
		{
			content.set(i, contents.getStackInSlot(i));
		}

        this.heldStack = heldItem;
    }
	
	@Override
	public int getContainerSize()
	{
		return slotsCount;
	}
	
	@Override
	public @NotNull ItemStack getItem(int index)
	{
		return index >= 0 && index < content.size() ? content.get(index) : ItemStack.EMPTY;
	}
	
	
	@Override
	public @NotNull ItemStack removeItem(int index, int count)
	{
		ItemStack itemstack = ContainerHelper.removeItem(content, index, count);
		
		if (!itemstack.isEmpty())
			this.setChanged();

		return itemstack;
	}

	@Override
	public @NotNull ItemStack removeItemNoUpdate(int index)
	{
		return ContainerHelper.takeItem(content, index);
	}
	@Override
	public void setItem(int index, @NotNull ItemStack stack)
	{
		content.set(index, stack);
		stack.limitSize(getMaxStackSize(stack));
		this.setChanged();
	}
	
	@Override
	public int getMaxStackSize() {
		return 64;
	}
	
	@Override
	public boolean stillValid(@NotNull Player player) {
		return true;
	}
	
	@Override
	public boolean canPlaceItem(int i, @NotNull ItemStack itemstack) {
		return SlotWarpBook.itemValid(itemstack);
	}
	
	@Override
	public void setChanged()
	{
		WarpBookItem.setWarpBookContent(heldStack, ItemContainerContents.fromItems(content));
	}
	
	@Override
	public @NotNull Component getName()
	{
		return Component.translatable(Database.GUI_TEXT_WARP_BOOK_INVENTORY_TITLE);
	}
	
	@Override
	public boolean hasCustomName() {
		return true;
	}
	
	@Override
	public boolean isEmpty()
	{
		for (ItemStack st : content)
			if (!st.isEmpty())
				return false;
		return true;
	}

	@Override
	public void clearContent()
	{
		content.clear();
	}

	/*public record WarpBookContent(List<ItemStack> list)
	{
		public static final WarpBookContent EMPTY = new WarpBookContent(54);
		public static final Codec<WarpBookContent> CODEC = ItemStack.OPTIONAL_CODEC.listOf().xmap(WarpBookContent :: new, WarpBookContent :: list);
		public static final StreamCodec<RegistryFriendlyByteBuf, WarpBookContent> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC.
				apply(ByteBufCodecs.list()).
				map(WarpBookContent :: new, WarpBookContent :: list);

		public WarpBookContent(int size, ItemStack defaultValue)
		{
			this(NonNullList.withSize(size, defaultValue));
		}

		public WarpBookContent(int size)
		{
			this(size, ItemStack.EMPTY);
		}

		public boolean isEmpty()
		{
			for (ItemStack stack : list())
			{
				if (!stack.isEmpty())
					return false;
			}
			return true;
		}
	}

	 */
}
