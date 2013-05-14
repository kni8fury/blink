package com.blink;

import com.sun.codemodel.JDefinedClass;

public interface ServiceMethodGenerator {

	public abstract void generateAllServiceMethods(JDefinedClass serviceClass,
			JDefinedClass serviceDataClass);

	public abstract void getCreateServiceMethod(JDefinedClass serviceClass,
			JDefinedClass serviceDataClass);

	public abstract void getReadServiceMethod(JDefinedClass serviceClass,
			JDefinedClass serviceDataClass);

	public abstract void getUpdateServiceMethod(JDefinedClass serviceClass,
			JDefinedClass serviceDataClass);

	public abstract void getDeleteServiceMethod(JDefinedClass serviceClass,
			JDefinedClass serviceDataClass);

}