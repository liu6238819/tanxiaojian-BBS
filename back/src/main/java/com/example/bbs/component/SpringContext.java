package com.example.bbs.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SpringContext implements ApplicationContextAware {



	private static final Logger logger = LoggerFactory.getLogger(SpringContext.class);

	private static ApplicationContext applicationContext;

	private static DefaultListableBeanFactory beanFactory;

	public static <T> T getBean(String name, Class<T> clazz) {

		return applicationContext.getBean(name, clazz);
	}

	public static Object getBean(String name) {

		return applicationContext.getBean(name);
	}


	public static DefaultListableBeanFactory getBeanFactory() {
		return beanFactory;
	}

	public static ApplicationContext getApplicationContext() {

		return applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		SpringContext.applicationContext = applicationContext;

		ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext ;
		beanFactory = (DefaultListableBeanFactory) configurableApplicationContext
				.getBeanFactory();
	}


	public static void printBeanDefinitionNames() {
		String[] beanDefinitionNames = applicationContext
				.getBeanDefinitionNames();
		for (int i = 0; i < beanDefinitionNames.length; i++) {
			logger.info(beanDefinitionNames[i]);
		}
	}


	public static XmlBeanDefinitionReader getXmlBeanDefinitionReader() {
		return new XmlBeanDefinitionReader((BeanDefinitionRegistry) beanFactory);
	}

	/**
	 * 动态加载bean
	 * @param fileName
	 * @throws BeanDefinitionStoreException
	 * @throws IOException
	 */
	public static void loadBean(String fileName)
			throws BeanDefinitionStoreException, IOException {

		XmlBeanDefinitionReader beanDefinitionReader = getXmlBeanDefinitionReader();
		beanDefinitionReader.setResourceLoader(applicationContext);
		beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(
				applicationContext));
		beanDefinitionReader.loadBeanDefinitions(applicationContext
				.getResources(fileName));

	}

	public static void loadBean(String fileName, String propertyHolderBeanName)
			throws BeanDefinitionStoreException, IOException {

		XmlBeanDefinitionReader beanDefinitionReader = getXmlBeanDefinitionReader();
		beanDefinitionReader.setResourceLoader(applicationContext);
		beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(
				applicationContext));
		beanDefinitionReader.loadBeanDefinitions(applicationContext
				.getResources(fileName));

		if (propertyHolderBeanName != null) {
			postProcessBeanFactory(propertyHolderBeanName);
		}
	}

	public static void postProcessBeanFactory(String propertyHolderBeanName)
			throws BeanDefinitionStoreException, IOException {
		BeanFactoryPostProcessor bfpp = (BeanFactoryPostProcessor) SpringContext
				.getBean(propertyHolderBeanName);
		bfpp.postProcessBeanFactory(SpringContext.getBeanFactory());
	}
}
