package cn.mlus.portality.tile;

import cn.mlus.portality.proxy.CommonProxy;
import com.hrznstudio.titanium.block.BasicTileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class BasicFrameTile extends FrameTile<BasicFrameTile> {

    public BasicFrameTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<BasicFrameTile>) CommonProxy.BLOCK_FRAME.getLeft().get(), CommonProxy.BLOCK_FRAME.getRight().get(), pos, state);
    }

    @Nonnull
    @Override
    public BasicFrameTile getSelf() {
        return this;
    }
}
