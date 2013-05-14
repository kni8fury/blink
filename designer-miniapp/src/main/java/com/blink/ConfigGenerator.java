package com.blink;

import com.sun.codemodel.JDefinedClass;

public interface ConfigGenerator {

	public JDefinedClass generateConfig(JDefinedClass configClass);
	public void postConfig(JDefinedClass configClass);
}
