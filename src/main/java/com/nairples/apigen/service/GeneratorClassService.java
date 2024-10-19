package com.nairples.apigen.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.javapoet.AnnotationSpec;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.FieldSpec;
import org.springframework.javapoet.FieldSpec.Builder;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.ParameterSpec;
import org.springframework.javapoet.TypeSpec;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.nairples.apigen.config.ApiGenConfig;
import com.nairples.apigen.model.Annotation;
import com.nairples.apigen.model.AnnotationMember;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Field;
import com.nairples.apigen.model.InputVariable;
import com.nairples.apigen.model.Method;
import com.nairples.apigen.util.CustomStringUtils;
import com.nairples.apigen.util.GenerationContext;

@Component
public class GeneratorClassService extends Generator {


    public GeneratorClassService(ApiGenConfig apiGenConfig) {
        super(apiGenConfig);
    }

    public void generateClass(GenerationContext context, ClassDefinition classDefinition) throws ClassNotFoundException, IOException {

		ArrayList<FieldSpec> fields = new ArrayList<>();
		if (classDefinition.getFields() != null) {
			for (Field field : classDefinition.getFields()) {
				Builder fieldSpecBuilder = FieldSpec
						.builder(ClassName.get("", field.getType()), field.getName());
				
				if( StringUtils.hasLength(field.getAccessModifier())){
					fieldSpecBuilder.addModifiers(getAccessModifier(field.getAccessModifier()));
				}
				
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
					classDefinition = classDefinition.toBuilder().method(getMethod ).build();
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
					classDefinition = classDefinition.toBuilder().method(setMethod ).build();
				}
				
				if( field.getAnnotations() != null ) {
					for (Annotation annotation : field.getAnnotations()) {
						AnnotationSpec annotationSpec = AnnotationSpec.builder(ClassName.get(annotation.getPackageName(), annotation.getName())).build();
						if(annotation.getMembers() != null) {
							for (AnnotationMember annotationMember : annotation.getMembers()) {
								annotationSpec = annotationSpec
										.toBuilder()
										.addMember(annotationMember.getMemberName(),  "$S", annotationMember.getMemberValue())
										.build();
							}
						}
						fieldSpecBuilder.addAnnotation(annotationSpec);
					}	
				}
				
				fields.add(fieldSpecBuilder.build());
			}
		}

		ArrayList<MethodSpec> methods = new ArrayList<>();
		if (classDefinition.getMethods() != null) {
			for (Method method : classDefinition.getMethods()) {
				
				org.springframework.javapoet.MethodSpec.Builder mSpecBuilder = MethodSpec.methodBuilder(method.getName())
						.returns(ClassName.get("", method.getReturnType()))
						.addModifiers(Modifier.PUBLIC)
						.addCode(method.getCode() != null ? method.getCode() : "return null;\n");
				
				for (InputVariable inputVariable : method.getInputVariables()) {
					ParameterSpec parameterSpec = ParameterSpec.builder(ClassName.get("", inputVariable.getType()), inputVariable.getName())
							.build();
					mSpecBuilder.addParameter(parameterSpec);
				}
				
				methods.add(mSpecBuilder.build());
			}
		}

		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(classDefinition.getName())
				.addModifiers(getAccessModifier(classDefinition));
		
		if( classDefinition.getAnnotations() != null ) {
			for (Annotation annotation : classDefinition.getAnnotations()) {
				AnnotationSpec annotationSpec = AnnotationSpec.builder(ClassName.get(annotation.getPackageName(), annotation.getName())).build();
				if(annotation.getMembers() != null) {
					for (AnnotationMember annotationMember : annotation.getMembers()) {
						annotationSpec = annotationSpec
								.toBuilder()
								.addMember(annotationMember.getMemberName(),  "$S", annotationMember.getMemberValue())
								.build();
					}
				}
				classBuilder.addAnnotation(annotationSpec);
			}	
		}


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
		JavaFile javaFile = JavaFile.builder(context.getPackageName()+"."+classDefinition.getPackageName(), definedClass)
				.build();

		writeFile(context, javaFile);
	}
    
    
    private Modifier getAccessModifier(ClassDefinition classDefinition) {
    	String accessModifier = classDefinition.getAccessModifier();
    	return getAccessModifier(accessModifier);
    }

	private Modifier getAccessModifier(String accessModifier) {
		
		Modifier modifier = null;
		

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
