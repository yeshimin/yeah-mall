package com.yeshimin.yeahboot.upms.common.config.mybatis;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yeshimin.yeahboot.upms.common.consts.Common;
import com.yeshimin.yeahboot.upms.domain.base.BaseQueryDto;
import com.yeshimin.yeahboot.upms.domain.base.ConditionBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class QueryHelper<T> {

    /**
     * 生成QueryWrapper
     * 只按查询类定义进行查询
     */
    public static <T> QueryWrapper<T> getQueryWrapper(Object query) {
        return getQueryWrapper(query, Wrappers.query());
    }

    /**
     * 生成QueryWrapper
     *
     * @param clazz 实体类Class，用于设置按实体类查询
     */
    public static <T> QueryWrapper<T> getQueryWrapper(Object query, Class<T> clazz) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (clazz.isInstance(query)) {
            wrapper.setEntity(clazz.cast(query));
        }
        return getQueryWrapper(query, wrapper);
    }

    /**
     * 暂时不开放外部调用，后续有需要的场景再开放
     */
    private static <T> QueryWrapper<T> getQueryWrapper(Object query, QueryWrapper<T> wrapper) {
        // 获取@Query注解
        Query queryAnno = query.getClass().getAnnotation(Query.class);
        // 是否启用查询
        boolean queryEnabled = queryAnno != null && queryAnno.enabled();
        // 是否启用自定义查询
        boolean queryCustom = queryAnno != null && queryAnno.custom();
        if (!queryEnabled) {
            log.debug("class: [{}], @Query is not enabled, skip", query.getClass().getName());
            return wrapper;
        }

        // 解析自定义查询条件
        List<Condition> listCondition = parseConditions(queryCustom, query);

        // 解析类字段定义的查询条件
        Field[] fields = query.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 跳过conditions字段
            if (field.getName().equals(Common.CONDITIONS_FIELD_NAME)) {
                log.debug("skip 'conditions' field");
                continue;
            }

            // 判断是否有 @QueryField 注解
            QueryField queryField = field.getAnnotation(QueryField.class);
            if (queryField == null) {
                log.debug("{} is not annotated with @QueryField, skip", field.getName());
                continue;
            }

            // 获取原访问标识，用于复原
            boolean isAccessible = field.isAccessible();
            // 允许访问私有字段
            field.setAccessible(true);
            try {
                Object value = field.get(query);

                // 获取条件生效策略
                QueryField.ConditionStrategy conditionStrategy = queryField.conditionStrategy();
                if (conditionStrategy == QueryField.ConditionStrategy.NOT_BLANK) {
                    if (value == null) {
                        log.debug("{} is null, skip", field.getName());
                        continue;
                    }
                    if (value instanceof String && StrUtil.isBlank((String) value)) {
                        log.debug("{} is blank, skip", field.getName());
                        continue;
                    }
                } else if (conditionStrategy == QueryField.ConditionStrategy.NOT_NULL) {
                    if (value == null) {
                        log.debug("{} is null, skip", field.getName());
                        continue;
                    }
                } else {
                    log.warn("conditionStrategy is not supported, skip");
                    continue;
                }

                // 获取查询类型
                QueryField.Type type = queryField.value() == QueryField.Type.DEFAULT ?
                        (queryField.type() == QueryField.Type.DEFAULT ? (QueryField.Type.EQ) : (queryField.type())) :
                        queryField.value();

                listCondition.add(new Condition(field.getName(), type, value));
            } catch (IllegalAccessException e) {
                log.error("{}", e.getMessage());
                e.printStackTrace();
            } finally {
                field.setAccessible(isAccessible);
            }
        }

        for (Condition condition : listCondition) {
            String fieldName = condition.getProperty();
            // 实体类字段命名转为表字段命名（小驼峰转下划线）
            String columnName = StrUtil.toUnderlineCase(fieldName);
            QueryField.Type operator = condition.getOperator();
            Object value = condition.getValue();

            switch (operator) {
                // equal
                case EQ:
                    wrapper.eq(columnName, value);
                    break;
                // not equal
                case NE:
                    wrapper.ne(columnName, value);
                    break;
                // greater than
                case GT:
                    wrapper.gt(columnName, value);
                    break;
                // greater and equal
                case GE:
                    wrapper.ge(columnName, value);
                    break;
                // less than
                case LT:
                    wrapper.lt(columnName, value);
                    break;
                // less and equal
                case LE:
                    wrapper.le(columnName, value);
                    break;
                // in
                case IN:
                    if (value instanceof Object[]) {
                        if (((Object[]) value).length > 0) {
                            wrapper.in(columnName, (Object[]) value);
                        } else {
                            log.warn("{} is empty, skip", fieldName);
                        }
                    } else if (value instanceof Collection) {
                        if (!((Collection<?>) value).isEmpty()) {
                            wrapper.in(columnName, (Collection<?>) value);
                        } else {
                            log.warn("{} is empty, skip", fieldName);
                        }
                    }
                    break;
                // not in
                case NOT_IN:
                    if (value instanceof Object[]) {
                        if (((Object[]) value).length > 0) {
                            wrapper.notIn(columnName, (Object[]) value);
                        } else {
                            log.warn("{} is empty, skip", fieldName);
                        }
                    } else if (value instanceof Collection) {
                        if (!((Collection<?>) value).isEmpty()) {
                            wrapper.notIn(columnName, (Collection<?>) value);
                        } else {
                            log.warn("{} is empty, skip", fieldName);
                        }
                    }
                    break;
                // is null
                case IS_NULL:
                    wrapper.isNull(columnName);
                    break;
                // is not null
                case IS_NOT_NULL:
                    wrapper.isNotNull(columnName);
                    break;
                // between
                case BETWEEN:
                    if (value instanceof Object[]) {
                        Object[] arr = (Object[]) value;
                        if (arr.length == 2) {
                            wrapper.between(columnName, arr[0], arr[1]);
                        } else {
                            log.warn("{} is invalid, skip", fieldName);
                        }
                    } else if (value instanceof Collection) {
                        Collection<?> col = (Collection<?>) value;
                        if (col.size() == 2) {
                            Iterator<?> iterator = col.iterator();
                            wrapper.between(columnName, iterator.next(), iterator.next());
                        } else {
                            log.warn("{} is invalid, skip", fieldName);
                        }
                    } else {
                        log.warn("{} is invalid, skip", fieldName);
                    }
                    break;
                // not between
                case NOT_BETWEEN:
                    if (value instanceof Object[]) {
                        Object[] arr = (Object[]) value;
                        if (arr.length == 2) {
                            wrapper.notBetween(columnName, arr[0], arr[1]);
                        } else {
                            log.warn("{} is invalid, skip", fieldName);
                        }
                    } else if (value instanceof Collection) {
                        Collection<?> col = (Collection<?>) value;
                        if (col.size() == 2) {
                            Iterator<?> iterator = col.iterator();
                            wrapper.notBetween(columnName, iterator.next(), iterator.next());
                        } else {
                            log.warn("{} is invalid, skip", fieldName);
                        }
                    } else {
                        log.warn("{} is invalid, skip", fieldName);
                    }
                    break;
                // like
                case LIKE:
                    wrapper.like(columnName, value);
                    break;
                // like left
                case LIKE_LEFT:
                    wrapper.likeLeft(columnName, value);
                    break;
                // like right
                case LIKE_RIGHT:
                    wrapper.likeRight(columnName, value);
                    break;
                // not like
                case NOT_LIKE:
                    wrapper.notLike(columnName, value);
                    break;
                // not like left
                case NOT_LIKE_LEFT:
                    wrapper.notLikeLeft(columnName, value);
                    break;
                // not like right
                case NOT_LIKE_RIGHT:
                    wrapper.notLikeRight(columnName, value);
                    break;
                default:
                    break;
            }
        }

        return wrapper;
    }

    private static List<Condition> parseConditions(boolean queryCustom, Object query) {
        String conditions = null;
        if (queryCustom) {
            if (query instanceof BaseQueryDto) {
                conditions = ((BaseQueryDto) query).getConditions_();
            } else if (query instanceof ConditionBaseEntity) {
                conditions = ((ConditionBaseEntity<?>) query).getConditions_();
            } else {
                log.warn("query is not extends BaseQueryDto or ConditionBaseEntity, ignore");
            }
        }
        return conditions == null ? new ArrayList<>() : parseConditions(conditions);
    }

    /**
     * 解析自定义查询条件
     *
     * @param conditions 自定义查询条件（可能多个） 示例：username:likeLeft:yeshi;password:eq:123456
     * @return List<Condition>
     */
    private static List<Condition> parseConditions(String conditions) {
        List<Condition> list = new ArrayList<>();
        if (StrUtil.isBlank(conditions)) {
            log.debug("conditions is blank, ignore");
            return list;
        }
        String[] arr = conditions.split(";");
        for (String s : arr) {
            String[] arr2 = s.split(":");
            if (arr2.length != 3) {
                log.warn("condition [{}] is invalid, ignore", s);
                continue;
            }
            if (StrUtil.isBlank(arr2[0])) {
                log.warn("condition [{}] -> fieldName is blank, ignore", s);
                continue;
            }
            QueryField.Type operator = QueryField.Type.of(arr2[1]);
            if (operator == null) {
                log.warn("condition [{}] -> operator is invalid, ignore", s);
                continue;
            }

            list.add(new Condition(arr2[0], operator, arr2[2]));
        }
        return list;
    }

    @Data
    @AllArgsConstructor
    private static class Condition {
        private String property;
        QueryField.Type operator;
        Object value;
    }
}
