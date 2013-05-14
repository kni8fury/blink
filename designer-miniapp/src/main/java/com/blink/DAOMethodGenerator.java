package com.blink;

import com.sun.codemodel.JDefinedClass;

public interface DAOMethodGenerator {

	public abstract void generateAllDAOMethods(JDefinedClass serviceClass,
			JDefinedClass daoDataClass);

	public abstract void getCreateDAOMethod(JDefinedClass serviceClass,
			JDefinedClass daoDataClass);

	public abstract void getReadDAOMethod(JDefinedClass serviceClass,
			JDefinedClass daoDataClass);

	public abstract void getUpdateDAOMethod(JDefinedClass serviceClass,
			JDefinedClass daoDataClass);

	public abstract void getDeleteDAOMethod(JDefinedClass serviceClass,
			JDefinedClass daoDataClass);

}