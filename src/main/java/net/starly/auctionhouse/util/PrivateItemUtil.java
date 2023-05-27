package net.starly.auctionhouse.util;

import net.starly.core.jb.version.nms.tank.NmsItemStackUtil;
import net.starly.core.jb.version.nms.wrapper.ItemStackWrapper;
import net.starly.core.jb.version.nms.wrapper.NBTTagCompoundWrapper;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class PrivateItemUtil {

    private PrivateItemUtil() {}

    public static void checkNbtTag(ItemStack itemStack, Consumer<Boolean> callback) {
        if (itemStack == null) {
            callback.accept(false);
            return;
        }

        ItemStackWrapper itemStackWrapper = NmsItemStackUtil.getInstance().asNMSCopy(itemStack);
        NBTTagCompoundWrapper nbtTagCompoundWrapper = itemStackWrapper.getTag();
        if (nbtTagCompoundWrapper == null) {
            callback.accept(false);
            return;
        }

        String stringNumber = nbtTagCompoundWrapper.getString("stprivateitem");
        if (stringNumber != null && !stringNumber.isEmpty()) {
            callback.accept(true);
        } else {
            callback.accept(false);
        }
    }
}
