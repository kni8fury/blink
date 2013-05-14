package com.blink;

import java.beans.PropertyVetoException;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration.Dynamic;

import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.util.Log4jConfigListener;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class ConfigGeneratorImpl implements ConfigGenerator {

	public JDefinedClass generateConfig(JDefinedClass configClass) {
		JDefinedClass webConfig = null;
		try {
			
			webConfig = generateWebConfig(configClass);
			generateJPAConfig(configClass);
			
		} catch (JClassAlreadyExistsException e) {
			e.printStackTrace();
		}
		
        configClass.annotate(Configuration.class);
        return webConfig;
	}

	
	private void generateJPAConfig(JDefinedClass configClass) throws JClassAlreadyExistsException {
		JCodeModel codeModel = configClass.owner();
		configClass.annotate(EnableTransactionManagement.class);
		configClass.annotate(ImportResource.class).param("value", "classpath*:META-INF/beans*.xml");
		
		/*JFieldVar dataSourceField = configClass.field(JMod.PRIVATE, javax.sql.DataSource.class, "dataSource");
		dataSourceField.annotate(Autowired.class); */
		
		JMethod dataSourceMethod = configClass.method(JMod.PUBLIC, ComboPooledDataSource.class, "dataSource");
		dataSourceMethod.annotate(Bean.class);
		//dataSourceMethod._throws(PropertyVetoException.class);
		JTryBlock tryBlock = dataSourceMethod.body()._try();
		JBlock tryBlockBody = tryBlock.body();
		JVar dataSourceField = tryBlockBody.decl(codeModel.ref(ComboPooledDataSource.class), "dataSource",JExpr._new(codeModel.ref(ComboPooledDataSource.class)));
		tryBlockBody.add(dataSourceField.invoke("setDriverClass").arg("com.mysql.jdbc.Driver"));
		tryBlockBody.add(dataSourceField.invoke("setJdbcUrl").arg("jdbc:mysql://localhost:3306/campusanytime"));
		tryBlockBody.add(dataSourceField.invoke("setUser").arg("campusanytime"));
		tryBlockBody.add(dataSourceField.invoke("setPassword").arg("password"));
		tryBlockBody._return(dataSourceField);
		tryBlock._catch(codeModel.ref(PropertyVetoException.class)).body()._return(JExpr._null());
		
		JMethod method = configClass.method(JMod.PUBLIC, PersistenceExceptionTranslator.class, "persistenceExceptionTranslator");
		method.annotate(Bean.class);
		method.body()._return(JExpr._new(codeModel.ref(HibernateExceptionTranslator.class)));
		
		JMethod entityManagerFactoryMethod = configClass.method(JMod.PUBLIC, EntityManagerFactory.class, "entityManagerFactory");
		JVar vendorFieldVar = entityManagerFactoryMethod.body().decl(codeModel.ref(HibernateJpaVendorAdapter.class), "vendorAdapter",JExpr._new(codeModel.ref(HibernateJpaVendorAdapter.class)));
		entityManagerFactoryMethod.body().add(vendorFieldVar.invoke("setGenerateDdl").arg(JExpr.TRUE));
		entityManagerFactoryMethod.annotate(Bean.class);
		JVar factoryFieldVar = entityManagerFactoryMethod.body().decl(codeModel.ref(LocalContainerEntityManagerFactoryBean.class), "factory",JExpr._new(codeModel.ref(LocalContainerEntityManagerFactoryBean.class)));
		entityManagerFactoryMethod.body().add(factoryFieldVar.invoke("setJpaVendorAdapter").arg(vendorFieldVar));
		entityManagerFactoryMethod.body().add(factoryFieldVar.invoke("setPackagesToScan").arg("com.campusanytime.jpa.entity"));
		entityManagerFactoryMethod.body().add(factoryFieldVar.invoke("setDataSource").arg(JExpr.invoke(dataSourceMethod)));
		entityManagerFactoryMethod.body().add(factoryFieldVar.invoke("afterPropertiesSet"));
		entityManagerFactoryMethod.body()._return(factoryFieldVar.invoke("getObject"));
		
		JMethod transactionManagerMethod = configClass.method(JMod.PUBLIC,PlatformTransactionManager.class , "transactionManager");
		JVar field = transactionManagerMethod.body().decl(codeModel.ref(JpaTransactionManager.class), "txManager", JExpr._new(codeModel.ref(JpaTransactionManager.class)));
		transactionManagerMethod.annotate(Bean.class);
		transactionManagerMethod.body().add(field.invoke("setEntityManagerFactory").arg(JExpr.invoke(entityManagerFactoryMethod)));
		transactionManagerMethod.body()._return(field);
		
	}
    private JDefinedClass generateWebConfig(JDefinedClass configClass) throws JClassAlreadyExistsException {
    	JCodeModel codeModel = configClass.owner();
    	JDefinedClass webConfig = codeModel._class(configClass._package().name()+".WebAppInitializer");
		//definedClass.annotate(javax.servlet.annotation.WebListener.class);
		webConfig._implements(WebApplicationInitializer.class);
		JType type = codeModel.ref(AnnotationConfigWebApplicationContext.class);
		JMethod method = webConfig.method(JMod.PUBLIC, void.class, "onStartup");
		JVar param = method.param(ServletContext.class, "servletContext");
		JVar field = method.body().decl(type, "rootContext", JExpr._new(type));
		method.body().add(field.invoke("scan").arg(configClass._package().name()));
		
		method.body().add(param.invoke("addListener").arg(JExpr._new(codeModel.ref(Log4jConfigListener.class))));
		method.body().add(param.invoke("addListener").arg(JExpr._new(codeModel.ref(ContextLoaderListener.class)).arg(field)));
		
		return webConfig;
	    		
    }
    
    /*Dynamic dynamic = servletContext.addServlet("cxfServlet",CXFNonSpringJaxrsServlet.class);
    dynamic.setInitParameter("jaxrs.serviceClasses", CampusAnytimeService.class.getName());
    dynamic.addMapping("/services/");
    dynamic.setLoadOnStartup(1); */
    
    public  void postConfig(JDefinedClass definedClass) {
    	JCodeModel codeModel = definedClass.owner();
    	JMethod method = null;
    	for (JMethod methodTmp : definedClass.methods() ) {
    		System.err.println(methodTmp.name());
    		
    		if(methodTmp.name().equals("onStartup")) {
    			method = methodTmp;
    		}
    	}
    	JVar param = null;
    	for ( JVar varTmp : method.listParams() ) {
    		if(varTmp.name().equals("servletContext")) {
    			param = varTmp;
    		}
    	}
    	JInvocation invocation = param.invoke("addServlet");
	    invocation.arg("cxfServlet");
	    invocation.arg(codeModel.ref(CXFNonSpringJaxrsServlet.class).dotclass());
		
	    JVar dynamic = method.body().decl(codeModel.ref(Dynamic.class), "dynamic",invocation);
		
	    method.body().add(dynamic.invoke("setInitParameter").arg("jaxrs.serviceClasses").arg(GeneratorContext.getFacade(PackageType.DTO).fullName()));
	    method.body().add(dynamic.invoke("addMapping").arg("/services/"));
		
		
    }
    
}
