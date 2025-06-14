package com.yeshimin.yeahboot.generator;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.yeshimin.yeahboot.common.common.properties.CodeGeneratorProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class CodeGenerator {

    private final CodeGeneratorProperties properties;

    public void generate() {
        // format name function
        Function<TableInfo, String> formatNameFunction = tableInfo -> {
            for (String prefix : properties.getTablePrefix()) {
                if (tableInfo.getName().startsWith(prefix)) {
                    String fixed = tableInfo.getName().replaceFirst(prefix, "");
                    tableInfo.setEntityName(StrUtil.upperFirst(StrUtil.toCamelCase(fixed)));
                    return tableInfo.getEntityName();
                }
            }
            return tableInfo.getEntityName();
        };

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
                        .moduleName(properties.getModule())
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
                    customMap.put("baseEntityFields", properties.getBaseEntityFields());
                    customMap.put("basePackage", properties.getPack());
                    customMap.put("moduleName", properties.getModule());
                    builder.customMap(customMap);

                    List<CustomFile> customFiles = new ArrayList<>();
                    // entity
                    customFiles.add(new CustomFile.Builder()
                            .fileName("Entity.java")
                            .templatePath("/templates/entity.java.ftl")
                            .packageName(properties.getPack() + "." + properties.getModule() + ".entity")
                            .filePath(Paths.get(System.getProperty("user.dir")) + "/target/autogen")
                            .formatNameFunction(formatNameFunction)
                            .enableFileOverride()
                            .build());
                    // mapper
                    customFiles.add(new CustomFile.Builder()
                            .fileName("Mapper.java")
                            .templatePath("/templates/mapper.java.ftl")
                            .packageName(properties.getPack() + "." + properties.getModule() + ".mapper")
                            .filePath(Paths.get(System.getProperty("user.dir")) + "/target/autogen")
                            .formatNameFunction(formatNameFunction)
                            .enableFileOverride()
                            .build());
                    // mapper xml
                    customFiles.add(new CustomFile.Builder()
                            .fileName("Mapper.xml")
                            .templatePath("/templates/mapper.xml.ftl")
                            .packageName(properties.getPack() + "." + properties.getModule() + ".mapper.xml")
                            .filePath(Paths.get(System.getProperty("user.dir")) + "/target/autogen")
                            .formatNameFunction(formatNameFunction)
                            .enableFileOverride()
                            .build());
                    // repository
                    customFiles.add(new CustomFile.Builder()
                            .fileName("Repo.java")
                            .templatePath("/templates/repository.java.ftl")
                            .packageName(properties.getPack() + "." + properties.getModule() + ".repository")
                            .filePath(Paths.get(System.getProperty("user.dir")) + "/target/autogen")
                            .formatNameFunction(formatNameFunction)
                            .enableFileOverride()
                            .build());
                    // service
                    customFiles.add(new CustomFile.Builder()
                            .fileName("Service.java")
                            .templatePath("/templates/service.java.ftl")
                            .packageName(properties.getPack() + "." + properties.getModule() + ".service")
                            .filePath(Paths.get(System.getProperty("user.dir")) + "/target/autogen")
                            .formatNameFunction(formatNameFunction)
                            .enableFileOverride()
                            .build());
                    // controller
                    customFiles.add(new CustomFile.Builder()
                            .fileName("Controller.java")
                            .templatePath("/templates/controller.java.ftl")
                            .packageName(properties.getPack() + "." + properties.getModule() + ".controller")
                            .filePath(Paths.get(System.getProperty("user.dir")) + "/target/autogen")
                            .formatNameFunction(formatNameFunction)
                            .enableFileOverride()
                            .build());
                    builder.customFile(customFiles);
                })
                .execute();
    }
}
