/**
 * MIT License
 *
 * Copyright (c) 2018
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cn.mlus.portality.block.module;

import cn.mlus.portality.tile.ItemModuleTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public class CapabilityItemModuleBlock extends CapabilityModuleBlock<IItemHandler, ItemModuleTile> {

    public CapabilityItemModuleBlock() {
        super("module_items", ItemModuleTile.class);
    }

    @Override
    public Capability<IItemHandler> getCapability() {
        return ForgeCapabilities.ITEM_HANDLER;
    }

    @Override
    void internalWork(Level current, BlockPos myself, Level otherWorld, List<BlockPos> compatibleBlockPos) {
        current.getBlockEntity(myself).getCapability(this.getCapability(), Direction.UP).ifPresent(handlerSelf -> {
            for (BlockPos otherPos : compatibleBlockPos) {
                BlockEntity otherTile = otherWorld.getBlockEntity(otherPos);
                if (otherTile != null) {
                    otherTile.getCapability(this.getCapability(), Direction.UP).ifPresent(handlerOther -> {
                        for (int i = 0; i < handlerSelf.getSlots(); i++) {
                            ItemStack stack = handlerSelf.getStackInSlot(i);
                            if (stack.isEmpty()) continue;
                            if (ItemHandlerHelper.insertItem(handlerOther, stack, true).isEmpty()) {
                                ItemHandlerHelper.insertItem(handlerOther, stack.copy(), false);
                                handlerSelf.getStackInSlot(i).setCount(0);
                                return;
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return ItemModuleTile::new;
    }

}
