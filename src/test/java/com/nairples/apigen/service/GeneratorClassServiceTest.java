package com.nairples.apigen.service;

import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Field;
import com.nairples.apigen.model.Method;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;


class GeneratorClassServiceTest {

    private  JavaFile mockJavaFile = mock(JavaFile.class);

    @Test
    void testGenerateClass_generatesCorrectFields() throws ClassNotFoundException, IOException {
        Field field = new Field();
        field.setName("id");
        field.setType("java.lang.Integer");
        field.setAccessModifier(Modifier.PRIVATE.toString());

        ClassDefinition classDefinition = new ClassDefinition();
        classDefinition.setName("TestEntity");
        classDefinition.setFields(Collections.singletonList(field));

        GeneratorClassService generatorClassService = new GeneratorClassService();

        generatorClassService.generateClass(classDefinition);


        Path filePath = Paths.get("src/main/java/com/example/TestEntity.java");
        assertTrue(Files.exists(filePath));

        String fileContent = Files.readString(filePath);
        assertTrue(fileContent.contains("Integer id;"));

        Files.deleteIfExists(filePath);
    }

    @Test
    void testGenerateClass_generatesCorrectMethods() throws ClassNotFoundException, IOException {

        Method method = new Method();
        method.setName("getId");
        method.setReturnType("java.lang.Integer");
        method.setAccessModifier(Modifier.PUBLIC.toString());

        ClassDefinition classDefinition = new ClassDefinition();
        classDefinition.setName("TestEntity");
        classDefinition.setMethods(Collections.singletonList(method));

        GeneratorClassService generatorClassService = new GeneratorClassService();


        generatorClassService.generateClass(classDefinition);


        Path filePath = Paths.get("src/main/java/com/example/TestEntity.java");
        assertTrue(Files.exists(filePath));

        String fileContent = Files.readString(filePath);
        assertTrue(fileContent.contains("public Integer getId() {")); // Check if the method is correctly generated
        assertTrue(fileContent.contains("return null;")); // Check if the method body is correct

        // Clean up
        Files.deleteIfExists(filePath);
    }

    @Test
    void testGenerateClass_writesToFileSystem() throws ClassNotFoundException, IOException {

        ClassDefinition classDefinition = new ClassDefinition();
        classDefinition.setName("TestEntity");

        GeneratorClassService generatorClassService = new GeneratorClassService();


        generatorClassService.generateClass(classDefinition);

        Path filePath = Paths.get("src/main/java/com/example/TestEntity.java");
        assertTrue(Files.exists(filePath)); // Check if the file is created

        // Clean up
        Files.deleteIfExists(filePath);
    }


    @Test
    void testGenerateClass_invalidFieldType_throwsClassNotFoundException() {
        // Arrange
        Field field = new Field();
        field.setName("invalidField");
        field.setType("non.existent.ClassType"); // Invalid class type

        ClassDefinition classDefinition = new ClassDefinition();
        classDefinition.setName("InvalidFieldClass");
        classDefinition.setFields(Collections.singletonList(field));

        GeneratorClassService generatorClassService = new GeneratorClassService();

        assertThrows(ClassNotFoundException.class, () -> generatorClassService.generateClass(classDefinition));
    }

    @Test
    void testGenerateClass_invalidMethodReturnType_throwsClassNotFoundException() {
        // Arrange
        Method method = new Method();
        method.setName("invalidMethod");
        method.setReturnType("non.existent.ReturnType");

        ClassDefinition classDefinition = new ClassDefinition();
        classDefinition.setName("InvalidMethodClass");
        classDefinition.setMethods(Collections.singletonList(method));

        GeneratorClassService generatorClassService = new GeneratorClassService();

        assertThrows(ClassNotFoundException.class, () -> generatorClassService.generateClass(classDefinition));
    }

    @Test
    void testGenerateClass_nullClassDefinition_throwsNullPointerException() {
        GeneratorClassService generatorClassService = new GeneratorClassService();

        assertThrows(NullPointerException.class, () -> generatorClassService.generateClass(null));
    }

    @Test
    void testGenerateClass_ioExceptionDuringFileWriting_throwsIOException() throws ClassNotFoundException, IOException {
        // Arrange
        ClassDefinition classDefinition = new ClassDefinition();
        classDefinition.setName("TestEntity");

        doThrow(new IOException("Simulated IOException")).when(mockJavaFile).writeTo(Mockito.any(Path.class));



        Path invalidPath = Paths.get("/invalid/directory/"); // Invalid path


        // Act & Assert
        assertThrows(IOException.class, () -> {
            mockJavaFile.builder("com.example", TypeSpec.classBuilder("TestEntity").build())
                    .build();
            mockJavaFile.writeTo(invalidPath); // Simulate an IOException
        });
    }
    @Test
    void testGenerateClass_emptyFieldsAndMethods_generatesEmptyClass() throws ClassNotFoundException, IOException {
        // Arrange
        ClassDefinition classDefinition = new ClassDefinition();
        classDefinition.setName("EmptyClass"); // No fields and methods

        GeneratorClassService generatorClassService = new GeneratorClassService();

        // Act
        generatorClassService.generateClass(classDefinition);

        // Assert
        Path filePath = Paths.get("src/main/java/com/example/EmptyClass.java");
        assertTrue(Files.exists(filePath)); // File should still be generated

        String fileContent = Files.readString(filePath);
        assertFalse(fileContent.contains("private")); // No fields


        // Clean up
        Files.deleteIfExists(filePath);
    }





}