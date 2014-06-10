package com.blink;

import java.io.File;
import javax.jms.Queue;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;

import com.blink.designer.model.Entity;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class ActionMethodGeneratorImpl implements ActionMethodGenerator{
	@PersistenceContext
	EntityManager entityManager;
	
	Entity entity;
	
	public JDefinedClass generateAllActionMethods(JDefinedClass actionClass,JDefinedClass abstractClass,String repo) throws Exception {
		
		String className=actionClass.name().substring(0, actionClass.name().lastIndexOf("action"));
		System.out.println("name used in query "+className);
		Query q=entityManager.createQuery("Select d from com.blink.designer.model.Entity d where d.name = :name");
		q.setParameter("name" ,className);
		entity=(Entity)q.getSingleResult();
		
		actionClass._extends(abstractClass);
		
		getCreateActionMethod(actionClass,abstractClass);
    	getReadActionMethod(actionClass,abstractClass);
    	getUpdateActionMethod(actionClass,abstractClass);
    	getDeleteActionMethod(actionClass,abstractClass);
    	
    	return actionClass;
	}
	
	
	
//	public void generateAbstractAction(JDefinedClass abstractClass,String repo)throws FileNotFoundException,IOException
//	{
//		String s1="/Users/rpoosar/Desktop/blink/designer-miniapp/src/main/resources/AbstractAction.java";
//		String s2=repo+"."+abstractClass.fullName();
//		s2=s2.replace('.', '/');
//		s2=s2.substring(0, s2.lastIndexOf(abstractClass.name()));
//		System.out.println("location final"+s2);
//		File source=new File(s1);
//		File dest=new File(s2);
//		dest.mkdirs();
//		File dest1=new File(s2+"AbstractAction.java");
//		if(!dest1.exists()) {
//		    dest1.createNewFile();
//		}
//		//InputStream input = this.getClass().getResourceAsStream("beansCXFConfig.xml");
//		InputStream input=new FileInputStream(source);
//		OutputStream output = null;
//
//		try {
//			        output = new FileOutputStream(dest1);
//			        byte[] buf = new byte[1024];
//			        int bytesRead;
//			        while ((bytesRead = input.read(buf)) > 0) {
//			            output.write(buf, 0, bytesRead);
//			        }
//			    } finally {
//			        input.close();
//			        output.close();
//			    }
//
//		}
	
	public JDefinedClass generateAbstractAction(JDefinedClass abstractClass,String repo)throws FileNotFoundException,IOException
	{
		JFieldVar entityManager;
		entityManager = abstractClass.field(JMod.PRIVATE,javax.persistence.EntityManager.class, "em");
		entityManager.annotate(javax.persistence.PersistenceContext.class);
		
		JFieldVar queue;
		//queue=abstractClass.field(JMod.PRIVATE,javax.jms.Queue.class,"queue");
		//queue.annotate(Autowired.class);
		
		JMethod createMethod=abstractClass.method(JMod.ABSTRACT, void.class,"createAction");
		createMethod.param(Object.class, "obj");
		
		JMethod readMethod=abstractClass.method(JMod.ABSTRACT, void.class,"readAction");
		readMethod.param(Object.class, "obj");
		
		JMethod updateMethod=abstractClass.method(JMod.ABSTRACT, void.class,"updateAction");
		updateMethod.param(Object.class, "obj");
		
		JMethod deleteMethod=abstractClass.method(JMod.ABSTRACT, void.class,"deleteAction");
		deleteMethod.param(Object.class, "obj");
		
		return abstractClass;
		
	}
	
	
	public void getCreateActionMethod(JDefinedClass actionClass,JDefinedClass abstractClass) throws Exception{
		JMethod method  = actionClass.method(JMod.PUBLIC, void.class, "createAction");
		method.param(Object.class, "obj");
		method.annotate(Override.class);
		method.body().directStatement(entity.getCreateAction());
		
	}
	
	public void getReadActionMethod(JDefinedClass actionClass,JDefinedClass abstractClass) throws Exception{
		JMethod method  = actionClass.method(JMod.PUBLIC, void.class, "readAction");
		method.param(Object.class, "obj");
		method.annotate(Override.class);
		method.body().directStatement(entity.getReadAction());		
	}
	
	public void getUpdateActionMethod(JDefinedClass actionClass,JDefinedClass abstractClass) throws Exception{
		JMethod method  = actionClass.method(JMod.PUBLIC, void.class, "updateAction");
		method.param(Object.class, "obj");
		method.annotate(Override.class);
		method.body().directStatement(entity.getUpdateAction());		
	}
	
	public void getDeleteActionMethod(JDefinedClass actionClass,JDefinedClass abstractClass) throws Exception{
		JMethod method  = actionClass.method(JMod.PUBLIC, void.class, "deleteAction");
		method.param(Object.class, "obj");
		method.annotate(Override.class);
		method.body().directStatement(entity.getDeleteAction());		
	}
	

}