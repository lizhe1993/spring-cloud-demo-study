package spring.cloud.demo.study.register;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import spring.cloud.demo.study.filter.LogFilter;

/**
 * ClassName: LogFilterRegistrationBean
 * Description: 将自定义过滤器注册到spring filter中
 * Author: LiZhe lizhej@enn.cn
 * Date: 2019/3/25 18:43
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public class LogFilterRegistrationBean extends FilterRegistrationBean<LogFilter> {

    public LogFilterRegistrationBean() {
        super();
        this.setFilter(new LogFilter());//添加LogFilter过滤器
        this.addUrlPatterns("/*");//匹配所有路径
        this.setName("LogFilter");//定义过滤器名称
        this.setOrder(1);//设置优先级
    }
}
