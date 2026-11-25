package com.somevanguy.gemsind.content.acid_purifier;

import com.somevanguy.gemsind.content.AllMenus;
import com.somevanguy.gemsind.content.base.BaseTransmutatorMenu;
import com.somevanguy.gemsind.content.items.AcidPackItem;
import com.somevanguy.gemsind.helpers.RenderHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class AcidPurifierMenu extends BaseTransmutatorMenu {
    public AcidPurifierMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(AcidPurifierEntity.DATA_SLOT_COUNT)); // TODO:FIX EXTRA DATA
    }

    public AcidPurifierMenu(int containerId, Inventory inv, BlockEntity be, ContainerData data) {
        super(AllMenus.ACID_PURIFIER_MENU.get(), containerId, inv, be, data);
        var itemHandler = this.blockEntity.getItemHandler();
        if (itemHandler != null) {
            addSlot(new SlotItemHandler(itemHandler, 0, 26, 52));
            addSlot(new SlotItemHandler(itemHandler, 1, 90, 16));
            addSlot(new SlotItemHandler(itemHandler, 2, 108, 16));
            addSlot(new SlotItemHandler(itemHandler, 3, 126, 16));
            addSlot(new SlotItemHandler(itemHandler, 4, 90, 34));
            addSlot(new SlotItemHandler(itemHandler, 5, 108, 34));
            addSlot(new SlotItemHandler(itemHandler, 6, 126, 34));
            addSlot(new SlotItemHandler(itemHandler, 7, 90, 52));
            addSlot(new SlotItemHandler(itemHandler, 8, 108, 52));
            addSlot(new SlotItemHandler(itemHandler, 9, 126, 52));
        }
    }

    public float getDullingProgress() {
        return (float) this.data.get(5) / 100;
    }

    public float getRed() {
        return RenderHelper.inQuintEase(getDullingProgress(), (float) AcidPackItem.DULL_R / 255, (float) this.data.get(2) / 255);
    }

    public float getGreen() {
        return RenderHelper.inQuintEase(getDullingProgress(), (float) AcidPackItem.DULL_G / 255, (float) this.data.get(3) / 255);
    }

    public float getBlue() {
        return RenderHelper.inQuintEase(getDullingProgress(), (float) AcidPackItem.DULL_B / 255, (float) this.data.get(4) / 255);
    }

//    public boolean isProgressing() {
//        return this.data.get(0) > 0;
//    }

//    public int baseAcidHeight() {
//        return 63;
//    }
//
//    public int getAcidHeight() {
//        int progress = this.data.get(0);
//        int maxProgress = this.data.get(1);
//        int acidHeight = baseAcidHeight();
//
//        return maxProgress > 0 && progress > 0 ? (int) Math.max(3f, (float) (progress * acidHeight / maxProgress)) : 0;
//    }
}
