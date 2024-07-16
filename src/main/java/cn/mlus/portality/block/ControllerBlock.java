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
package cn.mlus.portality.block;

import cn.mlus.portality.data.PortalDataManager;
import cn.mlus.portality.data.PortalInformation;
import cn.mlus.portality.tile.ControllerTile;
import cn.mlus.portality.Portality;
import cn.mlus.portality.proxy.CommonProxy;
import com.hrznstudio.titanium.block.RotatableBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class ControllerBlock extends RotatableBlock<ControllerTile> {

    public ControllerBlock() {
        super("controller", Block.Properties.copy(Blocks.IRON_BLOCK), ControllerTile.class);
        setItemGroup(Portality.TITANIUM_TAB);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        PortalInformation information = new PortalInformation(UUID.randomUUID(), placer.getUUID(), false, false, worldIn.dimension(), pos, "X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ(), new ItemStack(CommonProxy.BLOCK_FRAME.getLeft().get()), false);
        PortalDataManager.addInformation(worldIn, information);
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        super.destroy(worldIn, pos, state);
        PortalDataManager.removeInformation(worldIn, pos);
    }

    @Override
    public void wasExploded(Level worldIn, BlockPos pos, Explosion explosionIn) {
        super.wasExploded(worldIn, pos, explosionIn);
        PortalDataManager.removeInformation(worldIn, pos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING_HORIZONTAL, context.getHorizontalDirection().getOpposite());
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult ray) {
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (tile instanceof ControllerTile) {
            ControllerTile controller = (ControllerTile) tile;
            if (!worldIn.isClientSide()) {
                if (!controller.isFormed()) {
                    playerIn.displayClientMessage(MutableComponent.create(new LiteralContents("portality.controller.error.size")).withStyle(ChatFormatting.RED), true);
                    return InteractionResult.SUCCESS;
                }
                if (controller.isPrivate() && !controller.getOwner().equals(playerIn.getUUID())) {
                    playerIn.displayClientMessage(MutableComponent.create(new LiteralContents("portality.controller.error.privacy")).withStyle(ChatFormatting.RED), true);
                    return InteractionResult.SUCCESS;
                }
            } else if (controller.isFormed()) {
                if (controller.isPrivate() && !controller.getOwner().equals(playerIn.getUUID()))
                    return InteractionResult.SUCCESS;
                Minecraft.getInstance().submitAsync(() -> {
                    ControllerTile.OpenGui.open(0, (ControllerTile) tile);
                });
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, worldIn, pos, playerIn, hand, ray);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity entity = worldIn.getBlockEntity(pos);
        if (entity instanceof ControllerTile) {
            ((ControllerTile) entity).breakController();
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return ControllerTile::new;
    }


}
