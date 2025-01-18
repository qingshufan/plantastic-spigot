package fan.cropsprocess.com.listener.crop.click;

import fan.cropsprocess.com.data.TimeData;
import fan.cropsprocess.com.data.entity.CropData;
import fan.cropsprocess.com.util.CpCore;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class CropDelayListener implements Listener {
    @EventHandler
    public void InteractBlock(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        //主手右键
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getHand().equals(EquipmentSlot.HAND)) {
            Block block = e.getClickedBlock();
            if (CpCore.cpconfig.cropDataCrud.existCropData(block.getLocation())) {
                CropData cropdata = CpCore.cpconfig.cropDataCrud.getCropData(block.getLocation());
                if (cropdata.isDelete()) {
                    return;
                }
                //设置时间延迟
                if (CpCore.cpcore.timemap.containsKey(p.getUniqueId())) {
                    TimeData td = CpCore.cpcore.timemap.get(p.getUniqueId());
                    CpCore.cpcore.timemap.remove(p.getUniqueId());
                    cropdata.setPeriod(td.getPeriod());
                    cropdata.setDelay(td.getDelay());
                    cropdata.setShow(td.getShow());

                    CpCore.cpconfig.cropDataCrud.setCropData(cropdata); //及时写入

                    CpCore.cpcore.sendMsg(p, "设置成功");
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }
}
