package io.github.lumue.getdown.app.springboot.application;

import io.github.lumue.getdown.core.download.job.ContentLocationResolver;
import io.github.lumue.getdown.core.download.job.ContentLocationResolverRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


@Component
public class ContentLocationResolverRegistrar implements InitializingBean, ApplicationContextAware {

	private final ContentLocationResolverRegistry registry;
	private ApplicationContext applicationContext;

	private final static Logger LOGGER = LoggerFactory.getLogger(ContentLocationResolverRegistrar.class);

	@Autowired
	public ContentLocationResolverRegistrar(ContentLocationResolverRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		String[] resolverBeanNames = applicationContext.getBeanNamesForType(ContentLocationResolver.class);
		for (String resolverBeanName : resolverBeanNames) {
			ContentLocationResolver resolverBean = applicationContext.getBean(resolverBeanName, ContentLocationResolver.class);
			LOGGER.debug("registering ContentLocationResolver " + resolverBean.toString());
			registry.register(resolverBean);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
