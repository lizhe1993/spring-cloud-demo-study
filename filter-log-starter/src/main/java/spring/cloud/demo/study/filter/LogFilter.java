package spring.cloud.demo.study.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * ClassName: LogFilter
 * Description:
 * Author: LiZhe lizhej@enn.cn
 * Date: 2019/3/25 18:37
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */

public class LogFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //从request中获取访问有效地址，并在控制台上打印出来。
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        logger.info("uri {} is working.", request.getRequestURI());
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("logFilter init....");
    }

    @Override
    public void destroy() {
        logger.info("logFilter destroy....");
    }
}
