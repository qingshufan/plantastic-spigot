package fan.cropsprocess.com.test;

import fan.cropsprocess.com.data.entity.CropData;
import fan.cropsprocess.com.mapper.CropDataMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath:spring/spring.xml")
public class Test_SpringMyBatis {

    //    @Autowired
//    private RankDataMapper rankDataMapper;
//    @Autowired
    private CropDataMapper cropDataMapper;

    @Test
    public void testFindUserList() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/spring.xml");
//        RankData rankData = new RankData();
//        rankData.setUuid(UUID.randomUUID());
//        Map<String, Boolean> books = new HashMap<>();
//        books.put("book1", true);
//        books.put("book2", false);
//        rankData.setBooks(books);
//        rankDataMapper.set(rankData);

//        CropData cropData = new CropData();
//        cropData.setLoc(new LocData("world",1d,1d,1d));
//        cropData.setSoilType(SoilType.ACIDIC);
//        cropData.addDisease(CropDisease.FUSARIUM_HEAD_BLIGHT);
//        cropData.addDisease(CropDisease.LEAF_BLIGHT);
//        cropDataMapper.set(cropData);
        cropDataMapper = context.getBean(CropDataMapper.class);
        List<CropData> list = cropDataMapper.list();
        System.out.println(list);
//        CropData cropData = cropDataMapper.get(new LocData("world",1d,1d,1d));
//        System.out.println(cropDataMapper.exist(new LocData("world",1d,1d,1d)));
    }

}