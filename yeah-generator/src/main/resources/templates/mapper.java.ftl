package ${package.Mapper};

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.basic.domain.entity.AreaCityEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ${table.mapperName} extends ${superMapperClass}<${entity}Entity> {
}
