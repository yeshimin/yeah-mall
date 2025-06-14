package ${package.Controller};

import ${package.Entity}.${table.entityName}Entity;
import ${basePackage}.${moduleName}.mapper.${table.entityName}Mapper;
import ${basePackage}.${moduleName}.repository.${table.entityName}Repo;
import ${basePackage}.${moduleName}.service.${table.entityName}Service;
import ${basePackage}.common.controller.base.CrudController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ${table.comment!}
 */
@RestController
@RequestMapping("/${table.entityName?uncap_first}")
public class ${table.entityName}Controller extends CrudController<${table.entityName}Mapper, ${table.entityName}Entity, ${table.entityName}Repo> {

    @Autowired
    private ${table.entityName}Service service;

    public ${table.entityName}Controller(${table.entityName}Repo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
