package cn.mlus.portality.item;

import cn.mlus.portality.Portality;
import cn.mlus.portality.tile.ControllerTile;
import com.hrznstudio.titanium.item.BasicItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelReader;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class TeleportationTokenItem extends BasicItem {

    public TeleportationTokenItem() {
        super(new Properties().stacksTo(1));
        setItemGroup(Portality.TITANIUM_TAB);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof ControllerTile){
            return InteractionResult.PASS;
        }
        CompoundTag compoundNBT = context.getItemInHand().getOrCreateTag();
        compoundNBT.putString("Dimension", context.getLevel().dimension().location().toString());
        compoundNBT.putInt("X", context.getClickedPos().getX());
        compoundNBT.putInt("Y", context.getClickedPos().getY());
        compoundNBT.putInt("Z", context.getClickedPos().getZ());
        compoundNBT.putString("Direction", context.getHorizontalDirection().name());
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == null;
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        if (key == null && stack.hasTag()){
            String dimension =  stack.getOrCreateTag().getString("Dimension");
            if (dimension.contains(":")){
                dimension = dimension.split(":")[1];
            }
            tooltip.add(MutableComponent.create(new LiteralContents("portality.display.dimension")).append(WordUtils.capitalize(dimension)).withStyle(ChatFormatting.GRAY));
            tooltip.add(MutableComponent.create(new LiteralContents("portality.display.position")).append(stack.getOrCreateTag().getInt("X") + ", " + stack.getOrCreateTag().getInt("Y") + ", " +stack.getOrCreateTag().getInt("Z")).withStyle(ChatFormatting.GRAY));
            tooltip.add(MutableComponent.create(new LiteralContents("portality.display.direction")).append(WordUtils.capitalize(stack.getOrCreateTag().getString("Direction").toLowerCase(Locale.ROOT))).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.hasTag();
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return world.getBlockEntity(pos) instanceof ControllerTile;
    }
}
