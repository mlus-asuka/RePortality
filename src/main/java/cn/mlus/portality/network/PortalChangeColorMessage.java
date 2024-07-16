/**
 * MIT License
 * <p>
 * Copyright (c) 2018
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cn.mlus.portality.network;

import cn.mlus.portality.tile.ControllerTile;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class PortalChangeColorMessage extends Message {
    private BlockPos pos;
    private int color;

    public PortalChangeColorMessage(BlockPos pos, int color) {
        this.pos = pos;
        this.color = color;
    }

    public PortalChangeColorMessage() {
    }

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        Level world = context.getSender().level();
        context.enqueueWork(() -> {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof ControllerTile controllerTile) {
                controllerTile.setColor(color);
            }
        });
    }

}
