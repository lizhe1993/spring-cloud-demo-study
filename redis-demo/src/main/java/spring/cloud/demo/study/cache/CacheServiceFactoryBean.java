package spring.cloud.demo.study.cache;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public class CacheServiceFactoryBean implements FactoryBean<Object>, EnvironmentAware {

	private Environment environment;

	public Object getObject() throws Exception {
		CacheServiceFactory factory = CacheServiceFactory.getInstance(this.environment);
		return factory.createCacheService();
	}

	public Class<?> getObjectType() {
		return CacheService.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
