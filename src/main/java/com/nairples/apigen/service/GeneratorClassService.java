package com.nairples.apigen.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.springframework.javapoet.AnnotationSpec;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.FieldSpec;
import org.springframework.javapoet.FieldSpec.Builder;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.ParameterSpec;
import org.springframework.javapoet.ParameterizedTypeName;
import org.springframework.javapoet.TypeName;
import org.springframework.javapoet.TypeSpec;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.nairples.apigen.config.ApiGenConfig;
import com.nairples.apigen.model.Annotation;
import com.nairples.apigen.model.AnnotationMember;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.CodeBlock;
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
    	TypeSpec.Builder classBuilder = null;
    	
    	if ("interface".equals(classDefinition.getType())) {
    		classBuilder = TypeSpec.interfaceBuilder(classDefinition.getName()).addModifiers(getAccessModifier(classDefinition));
			
    	} else {
    		classBuilder = TypeSpec.classBuilder(classDefinition.getName())
					.addModifiers(getAccessModifier(classDefinition));
    	}
    	addFields(classDefinition, classBuilder);
		addMethods(classDefinition, classBuilder);
		addAnnotations(classDefinition, classBuilder);
		if(classDefinition.isAbstract()) {
			classBuilder.modifiers.add(Modifier.ABSTRACT);
		}		
		if(classDefinition.isFinal()) {
			classBuilder.modifiers.add(Modifier.FINAL);
		}		
		if(classDefinition.getExtendsClass() != null) {
			classBuilder.superclass(ClassName.get(classDefinition.getExtendsClass().getPackageName(), classDefinition.getExtendsClass().getName()));
		}
		
		if(classDefinition.getImplementsInterfaces() != null && !classDefinition.getImplementsInterfaces().isEmpty()) {
			for(ClassDefinition interfaceToImpl: classDefinition.getImplementsInterfaces()) {
				
				if(interfaceToImpl.getGenerics() != null && !interfaceToImpl.getGenerics().isEmpty()) {
					ClassName types[] = new ClassName[interfaceToImpl.getGenerics().size()];
					
					for (int i = 0; i < interfaceToImpl.getGenerics().size(); i++) {
						ClassDefinition generic = interfaceToImpl.getGenerics().get(i);
						types[i] = ClassName.get(generic.getPackageName(), generic.getName());
						
					}
					
					classBuilder.addSuperinterface(ParameterizedTypeName.get(
		                    ClassName.get(interfaceToImpl.getPackageName(), interfaceToImpl.getName()),types));
				} else {
					classBuilder.addSuperinterface(
		                    ClassName.get(interfaceToImpl.getPackageName(), interfaceToImpl.getName()));
				}
			}
		}
		
		TypeSpec definedClass = classBuilder.build();
		String packageName = context.getPackageName();
		if(StringUtils.hasLength(classDefinition.getPackageName())) {
			packageName = packageName + "."+classDefinition.getPackageName();
		}
		JavaFile javaFile = JavaFile.builder(packageName, definedClass)
				.build();

		writeFile(context, javaFile);
	}

	private void addFields(ClassDefinition classDefinition, TypeSpec.Builder classBuilder) {
		
		ArrayList<FieldSpec> fields = new ArrayList<>();
		
		if (classDefinition.getFields() != null) {
			for (Field field : classDefinition.getFields()) {
				Builder fieldSpecBuilder = FieldSpec
						.builder(ClassName.get(field.getPackageName() != null ? field.getPackageName() : "", field.getClassName()), field.getName());
				
				if( StringUtils.hasLength(field.getAccessModifier())){
					fieldSpecBuilder.addModifiers(getAccessModifier(field.getAccessModifier()));
				}
				
				generateGetter(classDefinition, field);
				generateSetter(classDefinition, field);
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
		
		for (FieldSpec fs : fields) {
			classBuilder.addField(fs);
		}
	}

	private void addAnnotations(ClassDefinition classDefinition, TypeSpec.Builder classBuilder) {
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
	}

	private void generateSetter(ClassDefinition classDefinition, Field field) {
		if(field.isSet()) {
			if(classDefinition.getMethods() == null) {
				classDefinition.setMethods(new ArrayList<>());
			}
			InputVariable fieldInput = new InputVariable();
			fieldInput.setName(field.getName());
			fieldInput.setType(field.getClassName());
			Method setMethod = Method
					.builder()
					.name("set"+CustomStringUtils.capitalizeFirstLetter(field.getName().toLowerCase()))
					.code(CodeBlock.builder().code("this."+field.getName()+" = " + field.getName()+"; \n").build())
					.inputVariables(List.of(fieldInput))
					.returnType("void").build();
			classDefinition.setMethods(new ArrayList<>(classDefinition.getMethods()));
			classDefinition.getMethods().add(setMethod );
		}
	}

	private void generateGetter(ClassDefinition classDefinition, Field field) {
		
		if(field.isGet()) {
			InputVariable fieldInput = new InputVariable();
			fieldInput.setName(field.getName());
			fieldInput.setType(field.getClassName());
			Method getMethod = Method
					.builder()
					.name("get"+CustomStringUtils.capitalizeFirstLetter(field.getName().toLowerCase()))
					.code(CodeBlock.builder().code("return "+field.getName()+";\n").build())
					.inputVariables(List.of(fieldInput))
					.returnType(field.getClassName())
					.build();
			if(classDefinition.getMethods() == null) {
				classDefinition.setMethods(new ArrayList<>());
			}
			classDefinition.setMethods(new ArrayList<>(classDefinition.getMethods()));
			classDefinition.getMethods().add(getMethod);
		}
		
	}

	private void addMethods(ClassDefinition classDefinition, TypeSpec.Builder classBuilder) {
		ArrayList<MethodSpec> methods = new ArrayList<>();
		if (classDefinition.getMethods() != null) {
			for (Method method : classDefinition.getMethods()) {
				addMethod(methods, method);
			}
		}
		
		for (MethodSpec ms : methods) {
			classBuilder.addMethod(ms);
		}
	}

	private void addMethod(ArrayList<MethodSpec> methods, Method method) {
		org.springframework.javapoet.MethodSpec.Builder mSpecBuilder = MethodSpec.methodBuilder(method.getName())
				.returns(ClassName.get("", method.getReturnType()))
				.addModifiers(Modifier.PUBLIC);
		
		if(method.getCode() != null && method.getCode().getArguments() != null) {
			mSpecBuilder.addCode(method.getCode() != null ? method.getCode().getCode() : "return null;\n", method.getCode().getArguments());
		} else {
			mSpecBuilder.addCode(method.getCode() != null ? method.getCode().getCode() : "return null;\n");
		}
		
		for (InputVariable inputVariable : method.getInputVariables()) {
			ParameterSpec parameterSpec = ParameterSpec.builder(ClassName.get("", inputVariable.getType()), inputVariable.getName())
					.build();
			mSpecBuilder.addParameter(parameterSpec);
		}
		
		methods.add(mSpecBuilder.build());
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
