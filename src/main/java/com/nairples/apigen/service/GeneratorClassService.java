package com.nairples.apigen.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.FieldSpec;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.ParameterSpec;
import org.springframework.javapoet.TypeSpec;
import org.springframework.stereotype.Component;

import com.nairples.apigen.config.ApiGenConfig;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Field;
import com.nairples.apigen.model.InputVariable;
import com.nairples.apigen.model.Method;
import com.nairples.apigen.util.StringUtils;

@Component
public class GeneratorClassService {
	
	@Autowired
	private ApiGenConfig apiGenConfig;

	public void generateClass(ClassDefinition classDefinition) throws ClassNotFoundException, IOException {

		ArrayList<FieldSpec> fields = new ArrayList<>();
		if (classDefinition.getFields() != null) {
			for (Field field : classDefinition.getFields()) {
				FieldSpec fieldSpec = FieldSpec.builder(ClassName.get("", field.getType()), field.getName()).build();
				if(field.isGet()) {
					Method getMethod = new Method();
					getMethod.setName("get"+StringUtils.capitalizeFirstLetter(field.getName().toLowerCase()));
					getMethod.setCode("return "+field.getName()+";\n");
					InputVariable fieldInput = new InputVariable();
					fieldInput.setName(field.getName());
					fieldInput.setType(field.getType());
					getMethod.setInputVariables(List.of(fieldInput));
					getMethod.setReturnType(field.getType());
					if(classDefinition.getMethods() == null) {
						classDefinition.setMethods(new ArrayList<Method>());
					}
					classDefinition.getMethods().add(getMethod );
				}
				
				if(field.isSet()) {
					Method setMethod = new Method();
					if(classDefinition.getMethods() == null) {
						classDefinition.setMethods(new ArrayList<Method>());
					}
					setMethod.setName("set"+StringUtils.capitalizeFirstLetter(field.getName().toLowerCase()));
					setMethod.setCode("this."+field.getName()+" = " + field.getName()+"; \n");
					InputVariable fieldInput = new InputVariable();
					fieldInput.setName(field.getName());
					fieldInput.setType(field.getType());
					setMethod.setInputVariables(List.of(fieldInput));
					setMethod.setReturnType("void");
					classDefinition.getMethods().add(setMethod );
				}
				
				fields.add(fieldSpec);
			}
		}

		ArrayList<MethodSpec> methods = new ArrayList<>();
		if (classDefinition.getMethods() != null) {
			for (Method method : classDefinition.getMethods()) {
				
				MethodSpec mSpec = MethodSpec.methodBuilder(method.getName())
						.returns(ClassName.get("", method.getReturnType()))
						.addModifiers(Modifier.PUBLIC)
						
						.addCode(method.getCode() != null ? method.getCode() : "return null;\n")
						.build();
				
				for (InputVariable inputVariable : method.getInputVariables()) {
					ParameterSpec parameterSpec = ParameterSpec.builder(ClassName.get("", inputVariable.getType()), inputVariable.getName())
							.build();
					mSpec = mSpec.toBuilder()
							.addParameter(parameterSpec)
							.build();
				}
				
				methods.add(mSpec);
			}
		}

		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(classDefinition.getName())
				.addModifiers(Modifier.PUBLIC);


		for (FieldSpec fs : fields) {
			classBuilder.addField(fs);
		}

		for (MethodSpec ms : methods) {
			classBuilder.addMethod(ms);
		}


		TypeSpec definedClass = classBuilder.build();


		JavaFile javaFile = JavaFile.builder(classDefinition.getPackageName(), definedClass)
				.build();

		javaFile.writeTo(Paths.get(apiGenConfig.getOutputDirectory()+"src/main/java"));
	}

}
