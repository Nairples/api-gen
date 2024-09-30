package com.nairples.apigen.service;

import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Field;
import com.nairples.apigen.model.Method;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;


class GeneratorClassServiceTest {

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




}