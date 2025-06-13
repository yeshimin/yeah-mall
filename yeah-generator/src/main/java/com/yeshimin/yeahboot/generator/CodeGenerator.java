package com.yeshimin.yeahboot.generator;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.yeshimin.yeahboot.common.common.properties.CodeGeneratorProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.*;

@Component
@RequiredArgsConstructor
public class CodeGenerator {

    private final CodeGeneratorProperties properties;

    private static final Set<String> TABLE_PREFIX = new HashSet<>(Arrays.asList("t_"));
    private static final List<String> BASE_ENTITY_FIELDS = new ArrayList<>(Arrays.asList("id", "deleteTime", "deleted", "createTime",
            "updateTime", "createBy", "updateTime", "updateBy"));

    public void generate() {
        FastAutoGenerator.create(properties.getUrl(), properties.getUsername(), properties.getPassword())
                .globalConfig(builder -> builder
                        .author(properties.getAuthor())
                        .outputDir(Paths.get(System.getProperty("user.dir")) + "/target/autogen")
                        .commentDate("yyyy-MM-dd")
                        // 禁止自动打开输出目录
                        .disableOpenDir()
                )
                .packageConfig(builder -> builder
                        .parent(properties.getPack())
                        .moduleName("default")
                )
                .strategyConfig(builder -> builder
                        .entityBuilder().disable()
                        .mapperBuilder().disable()
                        .serviceBuilder().disable()
                        .controllerBuilder().disable()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .injectionConfig(builder -> {
                    Map<String, Object> customMap = new HashMap<>();
                    customMap.put("baseEntityFields", BASE_ENTITY_FIELDS);
                    builder.customMap(customMap);

                    List<CustomFile> customFiles = new ArrayList<>();
                    // entity
                    customFiles.add(new CustomFile.Builder()
                            .fileName("Entity.java")
                            .templatePath("/templates/entity.java.ftl")
                            .packageName("com.yeshimin.yeahboot.domain.entity")
                            .filePath(Paths.get(System.getProperty("user.dir")) + "/target/autogen")
                            .formatNameFunction(tableInfo -> {
                                for (String prefix : TABLE_PREFIX) {
                                    if (tableInfo.getName().startsWith(prefix)) {
                                        String camelCase = StrUtil.toCamelCase(prefix);
                                        return tableInfo.getEntityName().replaceAll("^" + camelCase, "");
                                    }
                                }
                                return tableInfo.getEntityName();
                            })
                            .enableFileOverride()
                            .build());
                    // mapper
                    customFiles.add(new CustomFile.Builder()
                            .fileName("Mapper.java")
                            .templatePath("/templates/mapper.java.ftl")
                            .packageName("com.yeshimin.yeahboot.mapper")
                            .filePath(Paths.get(System.getProperty("user.dir")) + "/target/autogen")
                            .formatNameFunction(tableInfo -> {
                                for (String prefix : TABLE_PREFIX) {
                                    if (tableInfo.getName().startsWith(prefix)) {
                                        String camelCase = StrUtil.toCamelCase(prefix);
                                        return tableInfo.getMapperName().replaceAll("^" + camelCase, "");
                                    }
                                }
                                return tableInfo.getMapperName();
                            })
                            .enableFileOverride()
                            .build());
                    // repository
                    customFiles.add(new CustomFile.Builder()
                            .fileName("Repo.java")
                            .templatePath("/templates/repository.java.ftl")
                            .packageName("com.yeshimin.yeahboot.repository")
                            .filePath(Paths.get(System.getProperty("user.dir")) + "/target/autogen")
                            .formatNameFunction(tableInfo -> {
                                for (String prefix : TABLE_PREFIX) {
                                    if (tableInfo.getName().startsWith(prefix)) {
                                        String camelCase = StrUtil.toCamelCase(prefix);
                                        return tableInfo.getEntityName().replaceAll("^" + camelCase, "") + "Repo";
                                    }
                                }
                                return tableInfo.getEntityName() + "Repo";
                            })
                            .enableFileOverride()
                            .build());
                    // service
                    customFiles.add(new CustomFile.Builder()
                            .fileName("Service.java")
                            .templatePath("/templates/service.java.ftl")
                            .packageName("com.yeshimin.yeahboot.service")
                            .filePath(Paths.get(System.getProperty("user.dir")) + "/target/autogen")
                            .formatNameFunction(tableInfo -> {
                                for (String prefix : TABLE_PREFIX) {
                                    if (tableInfo.getName().startsWith(prefix)) {
                                        String camelCase = StrUtil.toCamelCase(prefix);
                                        return tableInfo.getServiceName().replaceAll("^" + camelCase, "");
                                    }
                                }
                                return tableInfo.getServiceName();
                            })
                            .enableFileOverride()
                            .build());
                    // service
                    customFiles.add(new CustomFile.Builder()
                            .fileName("Controller.java")
                            .templatePath("/templates/controller.java.ftl")
                            .packageName("com.yeshimin.yeahboot.controller")
                            .filePath(Paths.get(System.getProperty("user.dir")) + "/target/autogen")
                            .formatNameFunction(tableInfo -> {
                                for (String prefix : TABLE_PREFIX) {
                                    if (tableInfo.getName().startsWith(prefix)) {
                                        String camelCase = StrUtil.toCamelCase(prefix);
                                        return tableInfo.getControllerName().replaceAll("^" + camelCase, "");
                                    }
                                }
                                return tableInfo.getControllerName();
                            })
                            .enableFileOverride()
                            .build());
                    builder.customFile(customFiles);
                })
                .execute();
    }
}
