package com.yeshimin.yeahboot;

import cn.hutool.core.io.FileUtil;
import com.yeshimin.yeahboot.admin.YeahAdminApplication;
import com.yeshimin.yeahboot.upms.domain.entity.*;
import com.yeshimin.yeahboot.upms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest(classes = YeahAdminApplication.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class AreaTests {

    private final AreaProvinceRepo areaProvinceRepo;
    private final AreaCityRepo areaCityRepo;
    private final AreaDistrictRepo areaDistrictRepo;
    private final AreaStreetRepo areaStreetRepo;
    private final AreaVillageRepo areaVillageRepo;

    /**
     * test import province
     */
    @Test
    public void testImportProvince() {
        List<String> data = FileUtil.readLines("/Users/yeshimin/IdeaProjects/yeah-boot/area.d/provinces.csv", "UTF-8");

        // clear
        areaProvinceRepo.remove(null);

        // skip header
        List<AreaProvinceEntity> listEntity = data.stream().skip(1).map(s -> {
            String[] split = s.split(",");
            AreaProvinceEntity e = new AreaProvinceEntity();
            e.setCode(split[0]);
            e.setName(split[1].replaceAll("\"", ""));
            return e;
        }).collect(Collectors.toList());

        // save batch
        areaProvinceRepo.saveBatch(listEntity);
    }

    /**
     * test import city
     */
    @Test
    public void testImportCity() {
        List<String> data = FileUtil.readLines("/Users/yeshimin/IdeaProjects/yeah-boot/area.d/cities.csv", "UTF-8");

        // clear
        areaCityRepo.remove(null);

        // skip header
        List<AreaCityEntity> listEntity = data.stream().skip(1).map(s -> {
            String[] split = s.split(",");
            AreaCityEntity e = new AreaCityEntity();
            e.setCode(split[0]);
            e.setName(split[1].replaceAll("\"", ""));
            e.setParentCode(split[2]);
            return e;
        }).collect(Collectors.toList());

        // save batch
        areaCityRepo.saveBatch(listEntity);
    }

    /**
     * test import district
     */
    @Test
    public void testImportDistrict() {
        List<String> data = FileUtil.readLines("/Users/yeshimin/IdeaProjects/yeah-boot/area.d/areas.csv", "UTF-8");

        // clear
        areaDistrictRepo.remove(null);

        // skip header
        List<AreaDistrictEntity> listEntity = data.stream().skip(1).map(s -> {
            String[] split = s.split(",");
            AreaDistrictEntity e = new AreaDistrictEntity();
            e.setCode(split[0]);
            e.setName(split[1].replaceAll("\"", ""));
            e.setParentCode(split[2]);
            return e;
        }).collect(Collectors.toList());

        // save batch
        areaDistrictRepo.saveBatch(listEntity);
    }

    /**
     * test import street
     */
    @Test
    public void testImportStreet() {
        List<String> data = FileUtil.readLines("/Users/yeshimin/IdeaProjects/yeah-boot/area.d/streets.csv", "UTF-8");

        // clear
        areaStreetRepo.remove(null);

        // skip header
        List<AreaStreetEntity> listEntity = data.stream().skip(1).map(s -> {
            String[] split = s.split(",");
            AreaStreetEntity e = new AreaStreetEntity();
            e.setCode(split[0]);
            e.setName(split[1].replaceAll("\"", ""));
            e.setParentCode(split[2]);
            return e;
        }).collect(Collectors.toList());

        // save batch
        areaStreetRepo.saveBatch(listEntity);
    }

    /**
     * test import village
     */
    @Test
    public void testImportVillage() {
        List<String> data = FileUtil.readLines("/Users/yeshimin/IdeaProjects/yeah-boot/area.d/villages.csv", "UTF-8");

        // clear
        areaVillageRepo.remove(null);

        // skip header
        List<AreaVillageEntity> listEntity = data.stream().skip(1).map(s -> {
            String[] split = s.split(",");
            AreaVillageEntity e = new AreaVillageEntity();
            e.setCode(split[0]);
            e.setName(split[1].replaceAll("\"", ""));
            e.setParentCode(split[2]);

            return e;
        }).collect(Collectors.toList());

        // save batch
        areaVillageRepo.saveBatch(listEntity);
    }
}
