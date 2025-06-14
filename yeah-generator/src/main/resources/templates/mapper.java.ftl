package ${package.Mapper};

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ${package.Entity}.${table.entityName}Entity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ${table.entityName}Mapper extends ${superMapperClass}<${table.entityName}Entity> {
}
