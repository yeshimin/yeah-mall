package ${package.Repo};

import com.yeshimin.yeahboot.basic.domain.entity.AreaCityEntity;
import com.yeshimin.yeahboot.basic.mapper.AreaCityMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ${table.entityName}Repo extends BaseRepo<${table.entityName}Mapper, ${table.entityName}Entity> {
}
