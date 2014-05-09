package com.blink;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.blink.designer.model.AppConfig;
import com.blink.designer.model.Entity;
import com.blink.designer.model.EntityAttribute;
import com.blink.designer.model.Type;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class DAOMethodGeneratorImpl implements DAOMethodGenerator{

	JFieldVar entityManagerField ;
	@PersistenceContext
	private EntityManager entityManager;
	private String primaryType;
	private JCodeModel codeModel;

	public void generateAllDAOMethods(JDefinedClass serviceClass,JDefinedClass daoDataClass) {
		
				if( entityManagerField == null)
				{
			    entityManagerField = serviceClass.field(JMod.PRIVATE, javax.persistence.EntityManager.class, "entityManager");
				entityManagerField.annotate(javax.persistence.PersistenceContext.class);
				}
		codeModel=serviceClass.owner();
        String className=daoDataClass.name().substring(0,daoDataClass.name().indexOf(PackageType.DO.toString()));
		Query q = entityManager.createQuery("Select c from com.blink.designer.model.Entity c where c.name = :name ");
	    q.setParameter("name" ,className);
	    Entity entity=(Entity) q.getSingleResult();
	    for(EntityAttribute e : entity.getEntityAttributes()){
	    	if(e.isPrimarykey())
	    		primaryType=e.getPrimitiveType().getClassName();
	    }
        getCreateDAOMethod(serviceClass,daoDataClass);
        try {
			getReadDAOMethod(serviceClass,daoDataClass);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		getUpdateDAOMethod(serviceClass,daoDataClass);
		try {
			getDeleteDAOMethod(serviceClass,daoDataClass);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void getCreateDAOMethod(JDefinedClass serviceClass,JDefinedClass daoDataClass) {
		//serviceClass.
		JMethod method = serviceClass.method(JMod.PUBLIC, daoDataClass, "create"+CodeUtil.upperCamelCase(daoDataClass.name()));
		method.param(daoDataClass, CodeUtil.camelCase(daoDataClass.name()));

		method.body().directStatement("entityManager.persist("+ CodeUtil.camelCase(daoDataClass.name())+");");

		method.body()._return(JExpr.ref(CodeUtil.camelCase(daoDataClass.name())));

	}

	public void getReadDAOMethod(JDefinedClass serviceClass,JDefinedClass daoDataClass) throws ClassNotFoundException {
		
		JMethod method = serviceClass.method(JMod.PUBLIC, daoDataClass, "get"+CodeUtil.upperCamelCase(daoDataClass.name()));
		method.param(codeModel.parseType(primaryType),"id");
		method.body().directStatement("return entityManager.find("+ daoDataClass.fullName() +".class,id);");

	}

	public void getUpdateDAOMethod(JDefinedClass serviceClass,JDefinedClass daoDataClass) {
		
		JMethod method = serviceClass.method(JMod.PUBLIC, daoDataClass, "update"+CodeUtil.upperCamelCase(daoDataClass.name()));
		method.param(daoDataClass, CodeUtil.camelCase(daoDataClass.name()));

		method.body().directStatement("return entityManager.merge(" + CodeUtil.camelCase(daoDataClass.name())+");");

	}

	public void getDeleteDAOMethod(JDefinedClass serviceClass,JDefinedClass daoDataClass) throws ClassNotFoundException {
		JMethod method = serviceClass.method(JMod.PUBLIC, void.class, "delete"+CodeUtil.upperCamelCase(daoDataClass.name()));
		method.param(codeModel.parseType(primaryType), "id");
        method.body().directStatement(daoDataClass.fullName()+" "+daoDataClass.name()+"=entityManager.find("+ daoDataClass.fullName() +".class,id);");
		method.body().directStatement("entityManager.remove("+daoDataClass.name()+");");
	}

}
