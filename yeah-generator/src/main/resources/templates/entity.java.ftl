package ${package.Entity};

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ${table.comment!}
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("${table.name}")
public class ${entity}Entity extends ConditionBaseEntity<${entity}Entity> {
<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
    <#if !(baseEntityFields?seq_contains(field.propertyName))>
        <#if field.comment!?length gt 0>
            <#if entityFieldUseJavaDoc>

    /**
     * ${field.comment}
     */
            </#if>
        </#if>
        <#list field.annotationAttributesList as an>
    ${an.displayName}
        </#list>
    private ${field.propertyType} ${field.propertyName};
    </#if>
</#list>
<#------------  END 字段循环遍历  ---------->
}
