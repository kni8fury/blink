package com.blink;


import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class DAOMethodGeneratorImpl implements DAOMethodGenerator{

	JFieldVar entityManagerField ;

	public void generateAllDAOMethods(JDefinedClass serviceClass,JDefinedClass daoDataClass) {
		
				if( entityManagerField == null)
				{
			    entityManagerField = serviceClass.field(JMod.PRIVATE, javax.persistence.EntityManager.class, "entityManager");
				entityManagerField.annotate(javax.persistence.PersistenceContext.class);
				}

		
        getCreateDAOMethod(serviceClass,daoDataClass);
        getReadDAOMethod(serviceClass,daoDataClass);
		getUpdateDAOMethod(serviceClass,daoDataClass);
		getDeleteDAOMethod(serviceClass,daoDataClass);
	}

	public void getCreateDAOMethod(JDefinedClass serviceClass,JDefinedClass daoDataClass) {
		//serviceClass.
		JMethod method = serviceClass.method(JMod.PUBLIC, daoDataClass, "create"+CodeUtil.upperCamelCase(daoDataClass.name()));
		method.param(daoDataClass, CodeUtil.camelCase(daoDataClass.name()));

		method.body().directStatement("entityManager.persist("+ CodeUtil.camelCase(daoDataClass.name())+");");

		method.body()._return(JExpr.ref(CodeUtil.camelCase(daoDataClass.name())));

	}

	public void getReadDAOMethod(JDefinedClass serviceClass,JDefinedClass daoDataClass) {
		
		JMethod method = serviceClass.method(JMod.PUBLIC, daoDataClass, "get"+CodeUtil.upperCamelCase(daoDataClass.name()));
		method.param(Long.class, "id");
		method.body().directStatement("return entityManager.find("+ daoDataClass.fullName() +".class,id);");

	}

	public void getUpdateDAOMethod(JDefinedClass serviceClass,JDefinedClass daoDataClass) {
		
		JMethod method = serviceClass.method(JMod.PUBLIC, daoDataClass, "update"+CodeUtil.upperCamelCase(daoDataClass.name()));
		method.param(daoDataClass, CodeUtil.camelCase(daoDataClass.name()));

		method.body().directStatement("return entityManager.merge(" + CodeUtil.camelCase(daoDataClass.name())+");");

	}

	public void getDeleteDAOMethod(JDefinedClass serviceClass,JDefinedClass daoDataClass) {
		JMethod method = serviceClass.method(JMod.PUBLIC, void.class, "delete"+CodeUtil.upperCamelCase(daoDataClass.name()));
		method.param(Long.class, "id");
        method.body().directStatement(daoDataClass.fullName()+" "+daoDataClass.name()+"=entityManager.find("+ daoDataClass.fullName() +".class,id);");
		method.body().directStatement("entityManager.remove("+daoDataClass.name()+");");
	}

}
