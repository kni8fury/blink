package com.blink;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.blink.designer.model.EntityAttribute;
import com.sun.codemodel.JDefinedClass;

public interface ActionMethodGenerator {
	
	public void getCreateActionMethod(JDefinedClass jDefinedclass,JDefinedClass abstractClass) throws Exception;
	
	public void getReadActionMethod(JDefinedClass jDefinedclass,JDefinedClass abstractClass) throws Exception;
	
	public void getUpdateActionMethod(JDefinedClass jDefinedclass,JDefinedClass abstractClass,Class<?> bizClass) throws Exception;
	
	public void getDeleteActionMethod(JDefinedClass jDefinedclass,JDefinedClass abstractClass) throws Exception;
	
	public JDefinedClass generateAllActionMethods(JDefinedClass actionClass, JDefinedClass absractClass, Class<?> bizClass) throws Exception;
	
	public JDefinedClass generateAbstractAction(JDefinedClass abstractClass,String repo)throws FileNotFoundException,IOException;
	
	public void getUpdateAttrActionMethod(JDefinedClass actionClass,JDefinedClass abstractClass,EntityAttribute entityAttribute);

}
