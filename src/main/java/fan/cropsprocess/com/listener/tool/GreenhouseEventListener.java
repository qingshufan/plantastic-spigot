package fan.cropsprocess.com.listener.tool;

import fan.cropsprocess.com.data.crud.CropDataCrud;
import fan.cropsprocess.com.data.entity.CropData;
import fan.cropsprocess.com.util.CpCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GreenhouseEventListener implements Listener {

    // 用于记录玩家选择温棚范围的起点位置，初始值为 `null`，等待玩家第一次点击方块来确定具体位置
    private Location startLocation;
    // 记录当前正在操作（进行温棚建造选择范围操作）的玩家，初始值为 `null`，在玩家开始操作时赋值
    private Player currentPlayer;

    // 这个方法用于判断玩家主手拿着的物品是否为玻璃方块
    private boolean isGlassInHand(Player player) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        return itemInHand != null && itemInHand.getType() == Material.GLASS;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        // 首先判断玩家手中是否拿着玻璃方块，如果是则进一步判断具体的交互动作
        if (isGlassInHand(player)) {
            // 检查玩家的交互动作是否为左键点击方块（`Action.LEFT_CLICK_BLOCK`）
            if (action == Action.LEFT_CLICK_BLOCK) {
                // 取消默认的交互事件行为，避免出现原本左键点击方块可能产生的其他默认操作
                event.setCancelled(true);
                // 如果 `startLocation` 为 `null`，说明这是玩家第一次点击方块，此次点击的位置将作为温棚范围的起点
                if (startLocation == null) {
                    // 将当前点击的方块位置记录为温棚范围的起点位置
                    startLocation = event.getClickedBlock().getLocation();
                    currentPlayer = player;
                    player.sendMessage("已选择温棚范围起点，再左键点击一个方块确定范围终点。");
                } else {
                    // 如果 `startLocation` 不为 `null`，意味着已经有了起点，此次点击的方块位置将作为温棚范围的终点
                    Location endLocation = event.getClickedBlock().getLocation();
                    // 调用 `buildGreenhouse` 方法，根据起点和终点位置来实际建造温棚
                    buildGreenhouse(currentPlayer, startLocation, endLocation);
                    // 建造完成后
                    startLocation = null;
                    currentPlayer = null;
                    player.sendMessage("温棚已成功建造！");
                }
            }
        }
    }

    // 这个私有方法负责根据玩家指定的起点和终点位置来实际建造温棚
    private void buildGreenhouse(final Player player, Location start, Location end) {

        final YamlConfiguration langConfig = CpCore.cpcore.configs.get("language.yml");
        final List<Block> list = new ArrayList<>();
        final List<CropData> list2 = new ArrayList<>();
        // 获取起点和终点在 `x` 坐标上的最小值，用于确定温棚在 `x` 方向上的范围边界
        int minX = Math.min(start.getBlockX(), end.getBlockX());
        // 获取起点和终点在 `y` 坐标上的最小值，用于确定温棚在 `y` 方向上的范围边界
        int minY = Math.min(start.getBlockY(), end.getBlockY());
        // 获取起点和终点在 `z` 坐标上的最小值，用于确定温棚在 `z` 方向上的范围边界
        int minZ = Math.min(start.getBlockZ(), end.getBlockZ());
        // 获取起点和终点在 `x` 坐标上的最大值，用于确定温棚在 `x` 方向上的范围边界
        int maxX = Math.max(start.getBlockX(), end.getBlockX());
        // 获取起点和终点在 `y` 坐标上的最大值，用于确定温棚在 `y` 方向上的范围边界
        int maxY = Math.max(start.getBlockY(), end.getBlockY());
        // 获取起点和终点在 `z` 坐标上的最大值，用于确定温棚在 `z` 方向上的范围边界
        int maxZ = Math.max(start.getBlockZ(), end.getBlockZ());

        int height = maxY + 3;

        // 以下是建造温棚地面部分的代码逻辑，使用土方块（`Material.DIRT`）来构建地面

        final CropDataCrud cropDataCrud = CpCore.cpconfig.cropDataCrud;
        ;
        // 通过两层嵌套的循环遍历温棚在 `x` 和 `z` 方向上的坐标范围（从最小值到最大值）
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                // 获取每个位置对应的方块对象
                Block block = start.getWorld().getBlockAt(x, minY, z);
                Block aboveBlock = block.getRelative(0, 1, 0);
                Location location = aboveBlock.getLocation();
                if (cropDataCrud.existCropData(location)) {
                    CropData cropData = cropDataCrud.getCropData(location);
                    cropData.setOwnGreenHouse(true);
                    if (cropData.isTemperatureError()) cropData.setTemperatureError(false);
                    cropDataCrud.setCropData(cropData); //修复温棚
                    list2.add(cropData);
                }
            }
        }
        // 以下是建造温棚四周玻璃墙的代码逻辑，使用玻璃（`Material.GLASS`）来构建墙

        // 通过三层嵌套的循环遍历温棚在 `y`（高度）、`x`（长度）方向上的坐标范围（从最小值到最大值），用于构建四面墙
        for (int z = minZ; z <= maxZ; z++) {
            for (int y = minY + 1; y <= height; y++) {
                // 以下是构建前后两面墙（在 `z` 坐标的两端）的逻辑

                // 获取该位置对应的方块对象
                Block frontBackBlock1 = start.getWorld().getBlockAt(minX, y, z);
                // 将该方块的类型设置为玻璃（`Material.GLASS`），构建前墙的一部分
                frontBackBlock1.setType(Material.GLASS);

                Block frontBackBlock2 = start.getWorld().getBlockAt(maxX, y, z);
                // 将该方块的类型设置为玻璃（`Material.GLASS`），构建后墙的一部分
                frontBackBlock2.setType(Material.GLASS);
                list.add(frontBackBlock1);
                list.add(frontBackBlock2);
            }
        }

        // 以下是构建左右两面墙（在 `x` 坐标的两端）的逻辑
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY + 1; y <= height; y++) {
                // 计算左墙在当前 `y`、`z` 坐标以及 `x` 坐标为 `0`（左墙位置）时的方块位置
                Block leftRightBlock1 = start.getWorld().getBlockAt(x, y, minZ);
                // 将该方块的类型设置为玻璃（`Material.GLASS`），构建左墙的一部分
                leftRightBlock1.setType(Material.GLASS);

                // 计算右墙在当前 `y`、`z` 坐标以及 `x` 坐标为 `maxX - minX`（右墙位置，基于范围计算）时的方块位置
                Block leftRightBlock2 = start.getWorld().getBlockAt(x, y, maxZ);
                // 将该方块的类型设置为玻璃（`Material.GLASS`），构建右墙的一部分
                leftRightBlock2.setType(Material.GLASS);
                list.add(leftRightBlock1);
                list.add(leftRightBlock2);
            }
        }

        // 以下是建造温棚屋顶部分的代码逻辑，同样使用玻璃（`Material.GLASS`）来构建屋顶

        // 通过两层嵌套的循环遍历温棚在 `x` 和 `z` 方向上的坐标范围（从最小值到最大值）
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                // 根据起点位置和相对坐标计算出屋顶每个方块的具体位置
                Block roofBlock = start.getWorld().getBlockAt(x, height + 1, z);
                // 将该方块的类型设置为玻璃（`Material.GLASS`），完成屋顶的建造
                roofBlock.setType(Material.GLASS);
                list.add(roofBlock);
            }
        }
        CpCore.cpcore.sendTtile(player, langConfig.getString("tool.greenHouse.place"));
        //删除温棚
        new BukkitRunnable() {
            UUID uuid = player.getUniqueId();

            @Override
            public void run() {

                for (Block block : list) {
                    block.setType(Material.AIR);
                }
                for (CropData cropData : list2) {
                    cropData.setOwnGreenHouse(false);
                    cropDataCrud.setCropData(cropData);
                }
                if (Bukkit.getPlayer(uuid) != null) {
                    CpCore.cpcore.sendTtile(Bukkit.getPlayer(uuid), langConfig.getString("tool.greenHouse.cancel"));
                }
            }
        }.runTaskLater(CpCore.THIS, 10 * 20);

    }
}