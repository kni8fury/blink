package com.blink;

import static com.blink.CodeUtil.*;

import java.lang.annotation.Annotation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;

import com.blink.designer.model.Entity;
import com.blink.designer.model.EntityAttribute;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class ServiceMethodGeneratorImpl implements ServiceMethodGenerator{

	JFieldVar mapperField; 
    JFieldVar gsonField;
    @PersistenceContext
	private EntityManager entityManager;
    private String primaryType;
    private JCodeModel codeModel;
	public ServiceMethodGeneratorImpl() {

	}

	public void generateAllServiceMethods(JDefinedClass serviceClass, JDefinedClass serviceDataClass) {
	    codeModel=serviceClass.owner();
		if( mapperField == null){
			mapperField = serviceClass.field(JMod.PRIVATE,org.dozer.Mapper.class, "mapper");
			mapperField.annotate(Autowired.class);
		}
		String className=serviceDataClass.name().substring(0,serviceDataClass.name().indexOf(PackageType.DTO.toString()));
		Query q = entityManager.createQuery("Select c from com.blink.designer.model.Entity c where c.name = :name ");
	    q.setParameter("name" ,className);
	    Entity entity=(Entity) q.getSingleResult();
	    for(EntityAttribute e : entity.getEntityAttributes()){
	    	if(e.isPrimarykey())
	    		primaryType=e.getPrimitiveType().getClassName();
	    }
		getCreateServiceMethod(serviceClass,serviceDataClass);
		getReadServiceMethod(serviceClass,serviceDataClass);
		getUpdateServiceMethod(serviceClass,serviceDataClass);
		getDeleteServiceMethod(serviceClass,serviceDataClass);
	}

	public void getCreateServiceMethod(JDefinedClass serviceClass,JDefinedClass serviceDataClass) {
		JMethod method = serviceClass.method(JMod.PUBLIC, serviceDataClass,"create" +serviceDataClass.name());
		JVar input = method.param(serviceDataClass, CodeUtil.camelCase(serviceDataClass.name()));
		//input.annotate(javax.ws.rs.QueryParam.class).param("value", "");

		//annotateMethod(javax.ws.rs.POST.class,method,"/create"+serviceDataClass.name()+"/");
		annotateMethod(javax.ws.rs.POST.class,method,serviceDataClass.name()+"/");
		
		JVar bizField = getMapperStatements(method, serviceDataClass, input,PackageType.DTO);
		
		JDefinedClass bizFacade = GeneratorContext.getFacade(PackageType.BIZ);
		String bizMethodName = "create" + upperCamelCase(bizField.name());
		method.body().assign(bizField, serviceClass.fields().get(camelCase(bizFacade.name())).invoke(bizMethodName).arg(bizField));
		
		getReturnStatement(method, serviceDataClass, bizField);
	}

	public void getReadServiceMethod(JDefinedClass serviceClass,JDefinedClass serviceDataClass) {
		JMethod method  = serviceClass.method(JMod.PUBLIC, serviceDataClass,"get" +serviceDataClass.name() );
		JVar param=null;
		try {
			param = method.param(codeModel.parseType(primaryType), "id");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		param.annotate(javax.ws.rs.PathParam.class).param("value", "id");

		//annotateMethod(javax.ws.rs.GET.class,method,"/get" + serviceDataClass.name()+"/{id}");
		annotateMethod(javax.ws.rs.GET.class,method, serviceDataClass.name()+"/{id}");
		String bizClassName =  getBizClassFromDTO(serviceDataClass,PackageType.DTO);
		JClass type = serviceClass.owner().ref(bizClassName);
		
		JDefinedClass bizFacade = GeneratorContext.getFacade(PackageType.BIZ);
		String bizMethodName = "get" + upperCamelCase(getSimpleNameFromFQN(bizClassName));
		JInvocation invocation = serviceClass.fields().get(camelCase(bizFacade.name())).invoke(bizMethodName).arg(param);
		
		JVar bizClass= method.body().decl(type, CodeUtil.getSimpleCamelCaseNameFromFQN(bizClassName),invocation);

		method.body()._return(getMapperMethodInvocation(mapperField,bizClass,serviceDataClass));
	}

	public void getUpdateServiceMethod(JDefinedClass serviceClass,JDefinedClass serviceDataClass) {
		JMethod method = serviceClass.method(JMod.PUBLIC, serviceDataClass,"update" +serviceDataClass.name());
		JVar input = method.param(serviceDataClass, CodeUtil.camelCase(serviceDataClass.name()));
		//input.annotate(javax.ws.rs.QueryParam.class).param("value", "");

		//annotateMethod(javax.ws.rs.PUT.class,method, "/update"+serviceDataClass.name()+"/");
		annotateMethod(javax.ws.rs.PUT.class,method, serviceDataClass.name()+"/");

		JVar bizField  = getMapperStatements(method, serviceDataClass, input,PackageType.DTO);
		JDefinedClass bizFacade = GeneratorContext.getFacade(PackageType.BIZ);
		String bizMethodName = "update" + upperCamelCase(bizField.name());
		method.body().assign(bizField, serviceClass.fields().get(camelCase(bizFacade.name())).invoke(bizMethodName).arg(bizField));
		getReturnStatement(method, serviceDataClass, bizField);

	}		

	public void getDeleteServiceMethod(JDefinedClass serviceClass,JDefinedClass serviceDataClass) {
		JMethod method = serviceClass.method(JMod.PUBLIC, void.class,"delete" +serviceDataClass.name() );
		JVar param=null;
		try {
			param = method.param(codeModel.parseType(primaryType), "id");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		param.annotate(javax.ws.rs.PathParam.class).param("value","id");

		//annotateMethod(javax.ws.rs.DELETE.class, method,"/delete"+serviceDataClass.name()+"/{id}");
		annotateMethod(javax.ws.rs.DELETE.class, method,serviceDataClass.name()+"/{id}");
		
		JDefinedClass bizFacade = GeneratorContext.getFacade(PackageType.BIZ);
		String doMethodName = "delete" + upperCamelCase(serviceDataClass.name().substring(0,serviceDataClass.name().length() - PackageType.DTO.toString().length()));
		
		method.body().add (serviceClass.fields().get(camelCase(bizFacade.name())).invoke(doMethodName).arg(param));

	}

	private void annotateMethod(Class<? extends  Annotation> clazz, JMethod method, String value) {
		method.annotate(clazz);
		JAnnotationUse annotationPath = method.annotate(javax.ws.rs.Path.class);
		annotationPath.param("value", value);
		method.annotate(Consumes.class).param("value", MediaType.APPLICATION_JSON);
		method.annotate(Produces.class).param("value", MediaType.APPLICATION_JSON);

	}

	private String getBizClassFromDTO(JDefinedClass serviceDataClass, PackageType serviceType) {
		String servicePrefix = serviceType.toString().toLowerCase();
		String serviceClassFullName = serviceDataClass.fullName();
		String className = serviceClassFullName.substring(0,serviceClassFullName.length()- servicePrefix.length());
		return className.replace("."+servicePrefix+".", ".");

	}

	private JVar  getMapperStatements(JMethod method, JDefinedClass dataClass, JVar input, PackageType packageType) {
		String bizClassName =  getBizClassFromDTO(dataClass,packageType);

		JClass type = dataClass.owner().ref(bizClassName);

		JInvocation i = getMapperMethodInvocation(mapperField,input,type);
		JVar bizClass= method.body().decl(type, CodeUtil.getSimpleCamelCaseNameFromFQN(bizClassName),i);
		return bizClass;
	}
	
	private void getReturnStatement(JMethod method, JDefinedClass dataClass, JVar bizClass) {
		JInvocation j= getMapperMethodInvocation(mapperField,bizClass,dataClass);

		method.body()._return(j);
	}
}
