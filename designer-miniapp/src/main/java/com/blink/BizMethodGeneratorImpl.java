package com.blink;

import static com.blink.CodeUtil.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;

import com.blink.designer.model.Entity;
import com.blink.designer.model.EntityAttribute;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.sun.codemodel.JBlock;
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

public class BizMethodGeneratorImpl implements BizMethodGenerator{
	JFieldVar mapperField; 
	@PersistenceContext
	private EntityManager entityManager;
    private String primaryType;
    private JCodeModel codeModel;
    JFieldVar action;

	public void generateAllBizMethods(JDefinedClass serviceClass, Class<?> bizClass,JDefinedClass actionClass) {
		codeModel=serviceClass.owner();
		
		if( mapperField == null)
		{
			mapperField = serviceClass.field(JMod.PRIVATE,org.dozer.Mapper.class, "mapper");
			mapperField.annotate(Autowired.class);
		}
		String className=CodeUtil.upperCamelCase(bizClass.getSimpleName());
		action=serviceClass.field(JMod.PRIVATE,actionClass, className+"action");
		action.annotate(Autowired.class);
		Query q = entityManager.createQuery("Select c from com.blink.designer.model.Entity c where c.name = :name ");
	    q.setParameter("name" ,className);
	    Entity entity=(Entity) q.getSingleResult();
	    for(EntityAttribute e : entity.getEntityAttributes()){
	    	if(e.isPrimarykey())
	    		primaryType=e.getPrimitiveType().getClassName();
	    else
	    	System.out.println("Select PRIMARY KEY for the entity "+className);
	    }
		getCreateBizMethod(serviceClass,bizClass);
		getReadBizMethod(serviceClass,bizClass);
		getUpdateBizMethod(serviceClass,bizClass);
		getDeleteBizMethod(serviceClass,bizClass);
	}

	public void getCreateBizMethod(JDefinedClass serviceClass, Class<?> bizClass) {

		JClass definedBizClass = serviceClass.owner().ref(bizClass);
		JMethod method  = serviceClass.method(JMod.PUBLIC, definedBizClass, "create"+ CodeUtil.upperCamelCase(bizClass.getSimpleName()));
		JVar input= method.param(definedBizClass, CodeUtil.camelCase(bizClass.getSimpleName()));

		JVar doField = getMapperStatements(method,definedBizClass,input,PackageType.DO);
		JDefinedClass doFacade = GeneratorContext.getFacade(PackageType.DO);
		String doMethodName = "create" + upperCamelCase(doField.name());
		method.body().assign(doField, serviceClass.fields().get(camelCase(doFacade.name())).invoke(doMethodName).arg(doField));
		//method.body().add();
        
		getReturnStatement(method,doField, definedBizClass);

	}

	public void getReadBizMethod(JDefinedClass serviceClass, Class<?> bizClass) {
		JClass definedBizClass = serviceClass.owner().ref(bizClass);
		JMethod method  = serviceClass.method(JMod.PUBLIC, bizClass, "get"+ CodeUtil.upperCamelCase(bizClass.getSimpleName()));
	    JVar param=null;
		try {
			param = method.param(codeModel.parseType(primaryType),"id");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JClass doClass = definedBizClass.owner().ref(getDOFromBiz(definedBizClass,PackageType.DO));
		JDefinedClass doFacade = GeneratorContext.getFacade(PackageType.DO);
		String doMethodName = "get" + upperCamelCase(doClass.name());
		
		JVar doClassRef= method.body().decl(doClass, CodeUtil.camelCase(doClass.name()),serviceClass.fields().get(camelCase(doFacade.name())).invoke(doMethodName).arg(param));
		//method.body().assign(doClassRef, );
		method.body()._return(getMapperMethodInvocation(mapperField,doClassRef,definedBizClass));

	}

	public void getUpdateBizMethod(JDefinedClass serviceClass, Class<?> bizClass) {
		JClass definedBizClass = serviceClass.owner().ref(bizClass);
		JMethod method  = serviceClass.method(JMod.PUBLIC, bizClass, "update"+ CodeUtil.upperCamelCase(bizClass.getSimpleName()));
		JVar input = method.param(bizClass,CodeUtil.camelCase(bizClass.getSimpleName()));

		JVar doField = getMapperStatements(method,definedBizClass,input,PackageType.DO);
		
		JDefinedClass doFacade = GeneratorContext.getFacade(PackageType.DO);
		String doMethodName = "update" + upperCamelCase(doField.name());
		method.body().assign(doField, serviceClass.fields().get(camelCase(doFacade.name())).invoke(doMethodName).arg(doField));
		
		getReturnStatement(method,doField, definedBizClass);
	}

	public void getDeleteBizMethod(JDefinedClass serviceClass, Class<?> bizClass) {
		JMethod method  = serviceClass.method(JMod.PUBLIC, void.class, "delete"+ CodeUtil.upperCamelCase(bizClass.getSimpleName()));
		JVar param=null;
		try {
			param = method.param(codeModel.parseType(primaryType), "id");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JDefinedClass doFacade = GeneratorContext.getFacade(PackageType.DO);
		String doMethodName = "delete" + upperCamelCase(getSimpleNameFromFQN(bizClass.getName())+PackageType.DO.toString() );
		
		method.body().add (serviceClass.fields().get(camelCase(doFacade.name())).invoke(doMethodName).arg(param));
		
	}

	private JVar getMapperStatements(JMethod method, JClass bizClass, JVar input, PackageType packageType) {
		String doClassName =  getDOFromBiz(bizClass,packageType);

		JClass doClass = bizClass.owner().ref(doClassName);

		JInvocation i = getMapperMethodInvocation(mapperField,input,doClass);
		JVar doClassRef = method.body().decl(doClass, CodeUtil.getSimpleCamelCaseNameFromFQN(doClassName),i);
		return doClassRef;

	}

	private void getReturnStatement(JMethod method,JVar doClassRef, JClass bizClass){
		JInvocation j= getMapperMethodInvocation(mapperField,doClassRef,bizClass);

		method.body()._return(j);
	}

	private String getDOFromBiz(JClass dataClass,
			PackageType packageType) {
		String packageName = dataClass._package().name()+"." + packageType.toString().toLowerCase()+".";


		return packageName+ dataClass.name()+packageType.toString();
	}



}
