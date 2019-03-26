package spring.cloud.demo.study.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spring.cloud.demo.study.filter.LogFilter;
import spring.cloud.demo.study.register.LogFilterRegistrationBean;

/**
 * ClassName: LogFilterAutoConfiguration
 * Description:自动注册类，将LogFilterRegistrationBean注册到spring上下文中
 * Author: LiZhe lizhej@enn.cn
 * Date: 2019/3/25 18:48
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Configuration
//当某些类class位于类路径上才会实例化一个bean
@ConditionalOnClass({LogFilterRegistrationBean.class, LogFilter.class})
public class LogFilterAutoConfiguration {


    @Bean
    //当上下文中不存在该对象时候才会进行实例化，并注入到spring上下文中
    @ConditionalOnMissingBean(LogFilterRegistrationBean.class)
    public LogFilterRegistrationBean logFilterRegistrationBean() {
        return new LogFilterRegistrationBean();
    }

}
