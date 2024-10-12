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
import org.springframework.util.StringUtils;

import com.nairples.apigen.config.ApiGenConfig;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Field;
import com.nairples.apigen.model.InputVariable;
import com.nairples.apigen.model.Method;
import com.nairples.apigen.util.CustomStringUtils;

@Component
public class GeneratorClassService extends Generator {


    public GeneratorClassService(ApiGenConfig apiGenConfig) {
        super(apiGenConfig);
    }

    public void generateClass(String projectName, String domainName, String packageName, ClassDefinition classDefinition) throws ClassNotFoundException, IOException {

		ArrayList<FieldSpec> fields = new ArrayList<>();
		if (classDefinition.getFields() != null) {
			for (Field field : classDefinition.getFields()) {
				FieldSpec fieldSpec = FieldSpec.builder(ClassName.get("", field.getType()), field.getName()).build();
				if(field.isGet()) {
					Method getMethod = new Method();
					getMethod.setName("get"+CustomStringUtils.capitalizeFirstLetter(field.getName().toLowerCase()));
					getMethod.setCode("return "+field.getName()+";\n");
					InputVariable fieldInput = new InputVariable();
					fieldInput.setName(field.getName());
					fieldInput.setType(field.getType());
					getMethod.setInputVariables(List.of(fieldInput));
					getMethod.setReturnType(field.getType());
					if(classDefinition.getMethods() == null) {
						classDefinition.setMethods(new ArrayList<>());
					}
					classDefinition.getMethods().add(getMethod );
				}
				
				if(field.isSet()) {
					Method setMethod = new Method();
					if(classDefinition.getMethods() == null) {
						classDefinition.setMethods(new ArrayList<>());
					}
					setMethod.setName("set"+CustomStringUtils.capitalizeFirstLetter(field.getName().toLowerCase()));
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
				.addModifiers(getAccessModifier(classDefinition));


		for (FieldSpec fs : fields) {
			classBuilder.addField(fs);
		}
		
		if(classDefinition.isAbstract()) {
			classBuilder.modifiers.add(Modifier.ABSTRACT);
		}
		
		if(classDefinition.isFinal()) {
			classBuilder.modifiers.add(Modifier.FINAL);
		}

		for (MethodSpec ms : methods) {
			classBuilder.addMethod(ms);
		}
		
		
		if(StringUtils.hasLength(classDefinition.getExtendsClass())) {
			classBuilder.superclass(ClassName.get("", classDefinition.getExtendsClass()));
		}
		
		if(classDefinition.getImplementsInterfaces() != null && !classDefinition.getImplementsInterfaces().isEmpty()) {
			for(String interfaceToImpl: classDefinition.getImplementsInterfaces()) {
				if(StringUtils.hasLength(interfaceToImpl)) {
					classBuilder.addSuperinterface(ClassName.get("", interfaceToImpl));
				}
			}
		}
		
		


		TypeSpec definedClass = classBuilder.build();
		
		


		JavaFile javaFile = JavaFile.builder(packageName, definedClass)
				.build();

		writeFile(projectName, domainName, javaFile);
	}

	private Modifier getAccessModifier(ClassDefinition classDefinition) {
		
		Modifier modifier = null;
		String accessModifier = classDefinition.getAccessModifier();

		if (StringUtils.hasLength(accessModifier)) {
			switch (accessModifier) {
			case "public":
				modifier = Modifier.PUBLIC;
				break;
			case "private":
				modifier = Modifier.PRIVATE;
				break;
			default:
				modifier = Modifier.DEFAULT;
				break;

			}
		} else {
			modifier = Modifier.DEFAULT;
		}
		return modifier;
	}

}
