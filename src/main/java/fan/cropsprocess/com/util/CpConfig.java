package fan.cropsprocess.com.util;

import fan.cropsprocess.com.Main;
import fan.cropsprocess.com.data.crud.CropDataCrud;
import fan.cropsprocess.com.data.crud.RankDataCrud;
import fan.cropsprocess.com.util.sql.MysqlImpl;
import fan.cropsprocess.com.util.sql.SqlUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class CpConfig {
    public File ymlFile;
    public File citizenFile;
    public SqlUtils redisImpl;
    public CropDataCrud cropDataCrud;
    public RankDataCrud rankDataCrud;

    public CpConfig() {
        ymlFile = new File(CpCore.THIS.getDataFolder(), "农作物配置");
        citizenFile = new File(CpCore.THIS.getDataFolder(), "村民配置");
        ymlFile.mkdir();
        citizenFile.mkdir();
        redisImpl = Main.getContext().getBean(MysqlImpl.class);
        cropDataCrud = new CropDataCrud();
        rankDataCrud = new RankDataCrud();
    }

    public void loadYmlFile() {

        if (ymlFile.listFiles() == null) {
            return;
        }
        for (File file : ymlFile.listFiles()) {
            CpCore.cpcore.configs.put(file.getName(), YamlConfiguration.loadConfiguration(file));
        }
    }

    public void loadCitizenFile() {

        if (citizenFile.listFiles() == null) {
            return;
        }
        for (File file : citizenFile.listFiles()) {
            CpCore.cpcore.configs.put(file.getName(), YamlConfiguration.loadConfiguration(file));
        }
    }


    public void loadCropData() {
//		Jedis jedis = null;
//		try {
//			jedis = CpCore.pool.getResource();
//			for (byte[] key : jedis.keys("cropData.*".getBytes())) {
//				CropData cropdata = (CropData) SerializeUtil.unserialize(jedis.get(key));
//				CpCore.cpcore.cropmap.put(cropdata.getLoc().toLocation(), cropdata);
//			}
//		} finally {
//			// Be sure to close it! It can and will cause memory leaks.
//			jedis.close();
//		}
    }

    public void saveCropData() {
//		Jedis jedis = null;
//		try {
//			jedis = CpCore.pool.getResource();
//			for (CropData cropdata : CpCore.cpcore.cropmap.values()) {
//				if (cropdata.isDelete()) {
//					continue;
//				}
//				byte[] key = ("cropData." + cropdata.getLoc().toString()).getBytes();
//				byte[] value = SerializeUtil.serialize(cropdata);
//				jedis.set(key, value);
//			}
//		} finally {
//			// Be sure to close it! It can and will cause memory leaks.
//			jedis.close();
//		}
    }

}