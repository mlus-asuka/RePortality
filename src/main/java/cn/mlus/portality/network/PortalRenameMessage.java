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
package cn.mlus.portality.network;

import cn.mlus.portality.tile.ControllerTile;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class PortalRenameMessage extends Message {

    private BlockPos tileLocation;
    private String name;

    public PortalRenameMessage() {

    }

    public PortalRenameMessage(String name, BlockPos tile) {
        this.name = name;
        this.tileLocation = tile;
    }

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        ServerPlayer serverPlayer = context.getSender();
        context.enqueueWork(() -> {
            Level world = serverPlayer.level();
            if (world.getBlockEntity(tileLocation) != null && world.getBlockEntity(tileLocation) instanceof ControllerTile) {
                ControllerTile controller = (ControllerTile) world.getBlockEntity(tileLocation);
                if (controller.getOwner().equals(serverPlayer.getUUID())) controller.setDisplayName(name);
            }
        });
    }

}
