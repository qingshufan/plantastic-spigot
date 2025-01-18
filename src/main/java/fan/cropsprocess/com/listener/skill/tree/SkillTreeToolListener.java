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
public class SkillTreeToolListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        MyInventory myInventory = GuiLoader.loader("skillTree.tool");
        ConfigurationSection skillTreeConfig = CpCore.cpcore.configs.get("skillTree.yml").getConfigurationSection("skillTree.tool");
        YamlConfiguration langConfig = CpCore.cpcore.configs.get("language.yml");
        if (event.getView().getTitle().equals(myInventory.getTitle())) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            RankDataCrud rankDataCrud = CpCore.cpconfig.rankDataCrud;
            RankData rankData = rankDataCrud.existRankData(player.getUniqueId()) ? rankDataCrud.getRankData(player.getUniqueId()) :
                    new RankData();
            //保证第一次数据有UUID做主键
            rankData.setUuid(player.getUniqueId());
            String typeEnglish[] = {"hoe_level", "shovel_level", "bottle_level", "nutrition_level", "grow_level", "harvest_level", "go_back"};
            String typeChinese[] = {"锄头", "铲子", "灌溉", "施肥", "种植", "收获"};

            //返回科技树主门户按钮
            if (event.getCurrentItem() != null && event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[6] + ".index")) {
                Inventory inv = GuiLoader.loader("skillTree.hub").getInventory();
                player.openInventory(inv);
                return;
            }

            //左键处理升级
            if (event.isLeftClick()) {
                //升级锄头等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[0] + ".index")) {
                    int value = rankData.getHoeLevel();
                    int result = player.getLevel() - skillTreeConfig.getInt(typeEnglish[0] + ".cost");
                    if (result < 0) {
                        CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.noCost"));
                        return;
                    }
                    player.setLevel(result);
                    value++;
                    rankData.setHoeLevel(value);
                    player.closeInventory();
                    CpCore.cpcore.sendTtile(player, langConfig.getString("skillTree.tool.suc")
                            .replace("%type%", typeChinese[0])
                            .replace("%old%", "" + (value - 1))
                            .replace("%new%", "" + (value))
                    );
                }

                //升级铲子等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[1] + ".index")) {
                    int value = rankData.getShovelLevel();
                    int result = player.getLevel() - skillTreeConfig.getInt(typeEnglish[1] + ".cost");
                    if (result < 0) {
                        CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.noCost"));
                        return;
                    }
                    player.setLevel(result);
                    value++;
                    rankData.setShovelLevel(value);
                    player.closeInventory();
                    CpCore.cpcore.sendTtile(player, langConfig.getString("skillTree.tool.suc")
                            .replace("%type%", typeChinese[1])
                            .replace("%old%", "" + (value - 1))
                            .replace("%new%", "" + (value))
                    );
                }

                //升级灌溉等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[2] + ".index")) {
                    int value = rankData.getBottleLevel();
                    int result = player.getLevel() - skillTreeConfig.getInt(typeEnglish[2] + ".cost");
                    if (result < 0) {
                        CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.noCost"));
                        return;
                    }
                    player.setLevel(result);
                    value++;
                    rankData.setBottleLevel(value);
                    player.closeInventory();
                    CpCore.cpcore.sendTtile(player, langConfig.getString("skillTree.tool.suc")
                            .replace("%type%", typeChinese[2])
                            .replace("%old%", "" + (value - 1))
                            .replace("%new%", "" + (value))
                    );
                }

                //升级施肥等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[3] + ".index")) {
                    int value = rankData.getNutritionLevel();
                    int result = player.getLevel() - skillTreeConfig.getInt(typeEnglish[3] + ".cost");
                    if (result < 0) {
                        CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.noCost"));
                        return;
                    }
                    player.setLevel(result);
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    value++;
                    rankData.setNutritionLevel(value);
                    player.closeInventory();
                    CpCore.cpcore.sendTtile(player, langConfig.getString("skillTree.tool.suc")
                            .replace("%type%", typeChinese[3])
                            .replace("%old%", "" + (value - 1))
                            .replace("%new%", "" + (value))
                    );
                }

                //升级种植等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[4] + ".index")) {
                    int value = rankData.getGrowLevel();
                    int result = player.getLevel() - skillTreeConfig.getInt(typeEnglish[4] + ".cost");
                    if (result < 0) {
                        CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.noCost"));
                        return;
                    }
                    player.setLevel(result);
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    value++;
                    rankData.setGrowLevel(value);
                    player.closeInventory();
                    CpCore.cpcore.sendTtile(player, langConfig.getString("skillTree.tool.suc")
                            .replace("%type%", typeChinese[4])
                            .replace("%old%", "" + (value - 1))
                            .replace("%new%", "" + (value))
                    );
                }

                //升级收获等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[5] + ".index")) {
                    int value = rankData.getHarvestLevel();
                    int result = player.getLevel() - skillTreeConfig.getInt(typeEnglish[5] + ".cost");
                    if (result < 0) {
                        CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.noCost"));
                        return;
                    }
                    player.setLevel(result);
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    value++;
                    rankData.setHarvestLevel(value);
                    player.closeInventory();
                    CpCore.cpcore.sendTtile(player, langConfig.getString("skillTree.tool.suc")
                            .replace("%type%", typeChinese[5])
                            .replace("%old%", "" + (value - 1))
                            .replace("%new%", "" + (value))
                    );
                }
                rankDataCrud.setRankData(rankData);
            }
            //右键获取等级
            else if (event.isRightClick()) {
                //获取锄头等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[0] + ".index")) {
                    CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.tool.get")
                            .replace("%type%", typeChinese[0])
                            .replace("%old%", "" + (rankData.getHoeLevel()))
                    );
                }

                //获取铲子等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[1] + ".index")) {
                    CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.tool.get")
                            .replace("%type%", typeChinese[1])
                            .replace("%old%", "" + (rankData.getShovelLevel()))
                    );
                }

                //获取灌溉等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[2] + ".index")) {
                    CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.tool.get")
                            .replace("%type%", typeChinese[2])
                            .replace("%old%", "" + (rankData.getBottleLevel()))
                    );
                }

                //获取施肥等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[3] + ".index")) {
                    CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.tool.get")
                            .replace("%type%", typeChinese[3])
                            .replace("%old%", "" + (rankData.getNutritionLevel()))
                    );
                }

                //获取种植等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[4] + ".index")) {
                    CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.tool.get")
                            .replace("%type%", typeChinese[4])
                            .replace("%old%", "" + (rankData.getGrowLevel()))
                    );
                }

                //获取收获等级
                if (event.getSlot() == myInventory.getContentsConfig().getInt(typeEnglish[5] + ".index")) {
                    CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.tool.get")
                            .replace("%type%", typeChinese[5])
                            .replace("%old%", "" + (rankData.getHarvestLevel()))
                    );
                }
            }
        }

    }
}
