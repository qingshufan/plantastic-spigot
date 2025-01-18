package fan.cropsprocess.com.listener.skill.tree;

import fan.cropsprocess.com.data.crud.RankDataCrud;
import fan.cropsprocess.com.data.entity.RankData;
import fan.cropsprocess.com.inventory.GuiLoader;
import fan.cropsprocess.com.inventory.MyInventory;
import fan.cropsprocess.com.util.CpCore;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

//种子影响
public class SkillTreeSeedListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        MyInventory myInventory = GuiLoader.loader("skillTree.seed");
        ConfigurationSection skillTreeConfig = CpCore.cpcore.configs.get("skillTree.yml").getConfigurationSection("skillTree.seed");
        YamlConfiguration langConfig = CpCore.cpcore.configs.get("language.yml");
        if (event.getView().getTitle().equals(myInventory.getTitle())) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            RankDataCrud rankDataCrud = CpCore.cpconfig.rankDataCrud;
            RankData rankData = rankDataCrud.existRankData(player.getUniqueId()) ? rankDataCrud.getRankData(player.getUniqueId()) :
                    new RankData();
            //保证第一次数据有UUID做主键
            rankData.setUuid(player.getUniqueId());
            String typeEnglish[] = {"prevalence_level", "resistance_level", "yield_level", "go_back"};
            String typeChinese[] = {"患病率", "抗逆性", "产量率"};

            //返回科技树主门户按钮
            if (event.getCurrentItem() != null && event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[3] + ".index")) {
                Inventory inv = GuiLoader.loader("skillTree.hub").getInventory();
                player.openInventory(inv);
                return;
            }

            //左键处理升级
            if (event.isLeftClick()) {
                //升级患病率等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[0] + ".index")) {
                    int prevalence = rankData.getPrevalenceLevel();
                    int result = player.getLevel() - skillTreeConfig.getInt(typeEnglish[0] + ".cost");
                    if (result < 0) {
                        CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.noCost"));
                        return;
                    }
                    player.setLevel(result);
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    prevalence++;
                    rankData.setPrevalenceLevel(prevalence);
                    player.closeInventory();
                    CpCore.cpcore.sendTtile(player, langConfig.getString("skillTree.seed.suc")
                            .replace("%type%", typeChinese[0])
                            .replace("%old%", "" + (prevalence - 1))
                            .replace("%new%", "" + (prevalence))
                    );
                }
                //升级抗逆性等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[1] + ".index")) {
                    int value = rankData.getResistanceLevel();
                    int result = player.getLevel() - skillTreeConfig.getInt(typeEnglish[1] + ".cost");
                    if (result < 0) {
                        CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.noCost"));
                        return;
                    }
                    player.setLevel(result);
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    value++;
                    rankData.setResistanceLevel(value);
                    player.closeInventory();
                    CpCore.cpcore.sendTtile(player, langConfig.getString("skillTree.seed.suc")
                            .replace("%type%", typeChinese[1])
                            .replace("%old%", "" + (value - 1))
                            .replace("%new%", "" + (value))
                    );
                }
                //升级产量率等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[2] + ".index")) {
                    int value = rankData.getYieldLevel();
                    int result = player.getLevel() - skillTreeConfig.getInt(typeEnglish[2] + ".cost");
                    if (result < 0) {
                        CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.noCost"));
                        return;
                    }
                    player.setLevel(result);
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    value++;
                    rankData.setYieldLevel(value);
                    player.closeInventory();
                    CpCore.cpcore.sendTtile(player, langConfig.getString("skillTree.seed.suc")
                            .replace("%type%", typeChinese[2])
                            .replace("%old%", "" + (value - 1))
                            .replace("%new%", "" + (value))
                    );
                }
                rankDataCrud.setRankData(rankData);
            }
            //右键获取等级
            else if (event.isRightClick()) {
                //获取患病率等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[0] + ".index")) {
                    CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.seed.get")
                            .replace("%type%", typeChinese[0])
                            .replace("%old%", "" + rankData.getPrevalenceLevel())
                    );
                }
                //获取抗逆性等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[1] + ".index")) {
                    CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.seed.get")
                            .replace("%type%", typeChinese[1])
                            .replace("%old%", "" + rankData.getResistanceLevel())
                    );
                }
                //获取产量率等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[2] + ".index")) {
                    CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.seed.get")
                            .replace("%type%", typeChinese[2])
                            .replace("%old%", "" + (rankData.getYieldLevel()))
                    );
                }
            }
        }
    }
}
