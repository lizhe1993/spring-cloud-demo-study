package spring.cloud.demo.study.annotation;

import org.springframework.context.annotation.Import;
import spring.cloud.demo.study.config.LogFilterAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName: EnableLogFilter
 * Description:定义自动配置类生效注解
 * Author: LiZhe lizhej@enn.cn
 * Date: 2019/3/25 18:54
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
//@Import(LogFilterAutoConfiguration.class)//引入配置类
@Import(EnableLogFilterImportSelector.class)
public @interface EnableLogFilter {


}
