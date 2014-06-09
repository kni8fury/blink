package com.blink;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.sun.codemodel.JDefinedClass;

public interface ActionMethodGenerator {
	
	public void getCreateActionMethod(JDefinedClass jDefinedclass,JDefinedClass abstractClass) throws Exception;
	
	public void getReadActionMethod(JDefinedClass jDefinedclass,JDefinedClass abstractClass) throws Exception;
	
	public void getUpdateActionMethod(JDefinedClass jDefinedclass,JDefinedClass abstractClass) throws Exception;
	
	public void getDeleteActionMethod(JDefinedClass jDefinedclass,JDefinedClass abstractClass) throws Exception;
	
	public void generateAllActionMethods(JDefinedClass actionClass, JDefinedClass absractClass, String repo) throws Exception;
	
	public void generateAbstractAction(JDefinedClass abstractClass,String repo)throws FileNotFoundException,IOException;

}
