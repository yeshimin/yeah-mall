package ${package.Repo};

import ${package.Entity}.${table.entityName}Entity;
import ${basePackage}.${moduleName}.mapper.${table.entityName}Mapper;
import ${basePackage}.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ${table.entityName}Repo extends BaseRepo<${table.entityName}Mapper, ${table.entityName}Entity> {
}
