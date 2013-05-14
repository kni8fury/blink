package com.blink;

import java.util.HashMap;
import java.util.Map;

import com.sun.codemodel.JDefinedClass;

public class GeneratorContext {
	private static Map<PackageType,JDefinedClass> context = new HashMap<PackageType,JDefinedClass>();
	
	
	public static void registerFacade(PackageType packageType,JDefinedClass facadeClass) {
		context.put(packageType, facadeClass);
	}

	public static JDefinedClass getFacade(PackageType packageType) {
		return context.get(packageType);
	}
}
