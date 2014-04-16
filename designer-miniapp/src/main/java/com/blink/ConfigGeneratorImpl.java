package com.blink;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration.Dynamic;

import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.apache.cxf.transport.servlet.CXFServlet;
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

import com.blink.designer.model.App;
import com.blink.designer.model.AppConfig;
import com.blink.designer.model.DBConfig;
import com.blink.designer.model.Entity;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;


public class ConfigGeneratorImpl implements ConfigGenerator {
	
       @PersistenceContext(unitName="jpa.blink")
       EntityManager entityManager;
       
        public static App app;
       
	    public JDefinedClass generateConfig(JDefinedClass configClass,String repo,App app){
		JDefinedClass webConfig = null;
		try {
			
			webConfig = generateWebConfig(configClass);
			generateJPAConfig(configClass,app);
			try {
				generateCXFBeanFile(repo);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (JClassAlreadyExistsException e) {
			e.printStackTrace();
		}
		
        configClass.annotate(Configuration.class);
        return webConfig;
	}
	public void generateCXFBeanFile(String repo)throws FileNotFoundException,IOException
	{
		String s1="/Users/rpoosar/Desktop/blink/designer-miniapp/src/main/resources/beansCXFConfig.xml";
		String s2=repo+"/../resources/beans.xml";
		File source=new File(s1);
		File dest=new File(s2);
		System.out.println("beans path :"+s2);
		if(!dest.exists()) {
		    dest.createNewFile();
		}
		//InputStream input = this.getClass().getResourceAsStream("beansCXFConfig.xml");
		InputStream input=new FileInputStream(source);
		OutputStream output = null;

		try {
			        output = new FileOutputStream(dest);
			        byte[] buf = new byte[1024];
			        int bytesRead;
			        while ((bytesRead = input.read(buf)) > 0) {
			            output.write(buf, 0, bytesRead);
			        }
			    } finally {
			        input.close();
			        output.close();
			    }

		}
	
	private void generateJPAConfig(JDefinedClass configClass,App app) throws JClassAlreadyExistsException {
		
		JCodeModel codeModel = configClass.owner();
		configClass.annotate(EnableTransactionManagement.class);
		configClass.annotate(ImportResource.class).param("value","classpath:beans.xml");
		this.app=app;
		/*JFieldVar dataSourceField = configClass.field(JMod.PRIVATE, javax.sql.DataSource.class, "dataSource");
		dataSourceField.annotate(Autowired.class); */
        JMethod dataSourceMethod = configClass.method(JMod.PUBLIC, ComboPooledDataSource.class, "dataSource");
		dataSourceMethod.annotate(Bean.class);
		//dataSourceMethod._throws(PropertyVetoException.class);
		JTryBlock tryBlock = dataSourceMethod.body()._try();
		JBlock tryBlockBody = tryBlock.body();
		JVar dataSourceField = tryBlockBody.decl(codeModel.ref(ComboPooledDataSource.class), "dataSource",JExpr._new(codeModel.ref(ComboPooledDataSource.class)));
		long appId= app.getId();
		System.out.println("id of the app:"+appId);
		Query q = entityManager.createQuery("Select c from com.blink.designer.model.AppConfig c where c.app = (Select d from com.blink.designer.model.App d where d.id = :appId) ");
	    q.setParameter("appId" ,appId);
	    AppConfig appConfig=(AppConfig) q.getSingleResult();
		DBConfig dbConfig=appConfig.getDbConfig();
		tryBlockBody.add(dataSourceField.invoke("setDriverClass").arg(dbConfig.getJdbcDriver()));
		tryBlockBody.add(dataSourceField.invoke("setJdbcUrl").arg(dbConfig.getJdbcURL()));
		tryBlockBody.add(dataSourceField.invoke("setUser").arg(dbConfig.getUsername()));
		tryBlockBody.add(dataSourceField.invoke("setPassword").arg(dbConfig.getPassword()));
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
		entityManagerFactoryMethod.body().add(factoryFieldVar.invoke("setPackagesToScan").arg(app.getBasePackage()+"."+(PackageType.DO.toString()).toLowerCase()));
		entityManagerFactoryMethod.body().add(factoryFieldVar.invoke("setDataSource").arg(JExpr.invoke(dataSourceMethod)));
		entityManagerFactoryMethod.body().add(factoryFieldVar.invoke("afterPropertiesSet"));
		entityManagerFactoryMethod.body()._return(factoryFieldVar.invoke("getObject"));
		
		JMethod transactionManagerMethod = configClass.method(JMod.PUBLIC,PlatformTransactionManager.class , "transactionManager");
		JVar field = transactionManagerMethod.body().decl(codeModel.ref(JpaTransactionManager.class), "txManager", JExpr._new(codeModel.ref(JpaTransactionManager.class)));
		transactionManagerMethod.annotate(Bean.class);
		transactionManagerMethod.body().add(field.invoke("setEntityManagerFactory").arg(JExpr.invoke(entityManagerFactoryMethod)));
		transactionManagerMethod.body()._return(field);
		
		JMethod dozerBeanMethod=configClass.method(JMod.PUBLIC,org.dozer.DozerBeanMapper.class , "mapper");
		JAnnotationUse annotation=dozerBeanMethod.annotate(Bean.class);
		annotation.param("name", "mapper");
		dozerBeanMethod.body()._return(JExpr._new(codeModel.ref(org.dozer.DozerBeanMapper.class)));
		
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
	    invocation.arg(codeModel.ref(CXFServlet.class).dotclass());
		
	    JVar dynamic = method.body().decl(codeModel.ref(Dynamic.class), "dynamic",invocation);
		
	    //method.body().add(dynamic.invoke("setInitParameter").arg("jaxrs.serviceClasses").arg(GeneratorContext.getFacade(PackageType.DTO).fullName()));
	    method.body().add(dynamic.invoke("addMapping").arg("/services/*"));
		
		
    }
    
}
