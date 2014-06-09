package com.blink;

import com.sun.codemodel.JDefinedClass;

public interface BizMethodGenerator {

	public abstract void generateAllBizMethods(JDefinedClass serviceClass,
			Class<?> bizClass, JDefinedClass jDefinedClass);

	public abstract void getCreateBizMethod(JDefinedClass serviceClass,
			Class<?> bizClass);

	public abstract void getReadBizMethod(JDefinedClass serviceClass,
			Class<?> bizClass);

	public abstract void getUpdateBizMethod(JDefinedClass serviceClass,
			Class<?> bizClass);

	public abstract void getDeleteBizMethod(JDefinedClass serviceClass,
			Class<?> bizClass);

}