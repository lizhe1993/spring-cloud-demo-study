package spring.cloud.demo.study.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * ClassName: EnableLogFilterImportSelector
 * Description: 自动装配类
 * Author: LiZhe lizhej@enn.cn
 * Date: 2019/3/25 18:58
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public class EnableLogFilterImportSelector implements DeferredImportSelector, BeanClassLoaderAware, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(EnableLogFilterImportSelector.class);

    private Class annotationClass = EnableLogFilter.class;

    private ClassLoader beanClassLoader;

    private Environment environment;

    private boolean isEnabled = true;


    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        //是否默认生效
        if (!isEnabled()) {
            return new String[0];
        }
        //获取注解中的属性
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(this.annotationClass.getName(), true));
        Assert.notNull(attributes, "No " + getSimpleName() + " attributes found. Is " + annotationMetadata.getClassName() + " annotated with @" + getSimpleName() + "?");
        //获取注解注入到自动配置类中
        List<String> factories = new ArrayList<>(new LinkedHashSet<>(SpringFactoriesLoader.loadFactoryNames(this.annotationClass, this.beanClassLoader)));

        if (factories.isEmpty() && !hasDefaultFactory()) {
            throw new IllegalStateException("Annotation @" + getSimpleName() + " found, but there are no implementations. Did you forget to include a starter?");
        }
        if (factories.size() > 1) {
            logger.warn("More than one implementation " + "of @" + getSimpleName() + " (now relying on @Conditionals to pick one): " + factories);
        }
        return factories.toArray(new String[factories.size()]);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    protected boolean isEnabled() {
        return isEnabled;
    }

    protected Environment getEnvironment() {
        return environment;
    }

    protected boolean hasDefaultFactory() {
        return false;
    }

    protected String getSimpleName() {
        return this.annotationClass.getSimpleName();
    }

    protected Class<EnableLogFilter> getAnnotationClass() {
        return this.annotationClass;
    }

}
