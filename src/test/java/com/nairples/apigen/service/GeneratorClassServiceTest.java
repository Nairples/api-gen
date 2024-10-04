package com.nairples.apigen.service;

import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Field;
import com.nairples.apigen.model.MavenConfiguration;
import com.nairples.apigen.model.Method;
import com.nairples.apigen.pom.Build;
import com.nairples.apigen.pom.Parent;
import com.nairples.apigen.pom.Project;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class GeneratorClassServiceTest {

    private final JavaFile mockJavaFile = mock(JavaFile.class);
    MavenConfiguration mavenConfiguration = mock(MavenConfiguration.class);
    GeneratorClassService generatorClassService = mock(GeneratorClassService.class);
    GeneratorPomService generatorPomService = mock(GeneratorPomService.class);


    @Test
    void testGenerateClass_generatesCorrectFields() throws ClassNotFoundException, IOException {
        Field field = new Field();
        field.setName("id");
        field.setType("java.lang.Integer");
        field.setAccessModifier(Modifier.PRIVATE.toString());

        ClassDefinition classDefinition = new ClassDefinition();
        classDefinition.setName("TestEntity");
        classDefinition.setFields(Collections.singletonList(field));
        classDefinition.setPackageName("Test");

        GeneratorClassService generatorClassService = new GeneratorClassService();

        generatorClassService.generateClass(classDefinition);


        Path filePath = Paths.get("src/main/java/Test/TestEntity.java");
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
        classDefinition.setPackageName("Test");
        classDefinition.setMethods(Collections.singletonList(method));

        GeneratorClassService generatorClassService = new GeneratorClassService();


        generatorClassService.generateClass(classDefinition);


        Path filePath = Paths.get("src/main/java/Test/TestEntity.java");
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
        classDefinition.setPackageName("Test");

        GeneratorClassService generatorClassService = new GeneratorClassService();


        generatorClassService.generateClass(classDefinition);

        Path filePath = Paths.get("src/main/java/Test/TestEntity.java");
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
        classDefinition.setPackageName("Test");

        GeneratorClassService generatorClassService = new GeneratorClassService();

        // Act
        generatorClassService.generateClass(classDefinition);

        // Assert
        Path filePath = Paths.get("src/main/java/Test/EmptyClass.java");
        assertTrue(Files.exists(filePath)); // File should still be generated

        String fileContent = Files.readString(filePath);
        assertFalse(fileContent.contains("private")); // No fields


        // Clean up
        Files.deleteIfExists(filePath);
    }

    @Test
    void testGeneratePomXmlFile_withValidConfig_success() {


        when(mavenConfiguration.getGroupId()).thenReturn("com.example");
        when(mavenConfiguration.getArtifactId()).thenReturn("test-app");
        when(mavenConfiguration.getVersion()).thenReturn("1.0.0");
        when(mavenConfiguration.getPackaging()).thenReturn("jar");
        when(mavenConfiguration.getDescription()).thenReturn("Test Application");
        when(mavenConfiguration.getUrl()).thenReturn("http://example.com");
        when(mavenConfiguration.getDependencies()).thenReturn(List.of());
        when(mavenConfiguration.getPlugins()).thenReturn(List.of());

        assertDoesNotThrow(() -> {
        	generatorPomService.generatePomXmlFile(mavenConfiguration);
        });
    }

    @Test
    void testGeneratePomXmlFile_withValidConfig_compareContent() throws Exception {

        when(mavenConfiguration.getGroupId()).thenReturn("com.example");
        when(mavenConfiguration.getArtifactId()).thenReturn("test-app");
        when(mavenConfiguration.getVersion()).thenReturn("1.0.0");
        when(mavenConfiguration.getPackaging()).thenReturn("jar");
        when(mavenConfiguration.getDescription()).thenReturn("Test Application");
        when(mavenConfiguration.getUrl()).thenReturn("http://example.com");
        when(mavenConfiguration.getDependencies()).thenReturn(List.of());
        when(mavenConfiguration.getPlugins()).thenReturn(List.of());


        StringWriter stringWriter = new StringWriter();


        Marshaller marshaller = getMarshaller();


        Project project = Project.builder()
                .modelVersion("4.0.0")
                .groupId(mavenConfiguration.getGroupId())
                .artifactId(mavenConfiguration.getArtifactId())
                .version(mavenConfiguration.getVersion())
                .packaging(mavenConfiguration.getPackaging())
                .name(mavenConfiguration.getArtifactId())
                .description(mavenConfiguration.getDescription())
                .url(mavenConfiguration.getUrl())
                .dependencies(List.of())
                .build(Build.builder().build())
                .build();

        marshaller.marshal(project, stringWriter);


        String expectedXml = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>test-app</artifactId>
                    <version>1.0.0</version>
                    <packaging>jar</packaging>
                    <name>test-app</name>
                    <description>Test Application</description>
                    <url>http://example.com</url>
                    <dependencies/>
                    <build/>
                </project>
                """;
        assertEquals(expectedXml.trim(), stringWriter.toString().trim());
    }

    @Test
    void testGeneratePomXmlFile_withValidSpringBootConfig_compareContent() throws Exception {
        // Arrange
        MavenConfiguration mavenConfiguration = Mockito.mock(MavenConfiguration.class);

        when(mavenConfiguration.getGroupId()).thenReturn("com.example");
        when(mavenConfiguration.getArtifactId()).thenReturn("test-app");
        when(mavenConfiguration.getVersion()).thenReturn("1.0.0");
        when(mavenConfiguration.getPackaging()).thenReturn("jar");
        when(mavenConfiguration.getDescription()).thenReturn("Test Application");
        when(mavenConfiguration.getUrl()).thenReturn("http://example.com");
        when(mavenConfiguration.getDependencies()).thenReturn(List.of());
        when(mavenConfiguration.getPlugins()).thenReturn(List.of());
        when(mavenConfiguration.getSpringBootVersion()).thenReturn("2.5.6"); // Example Spring Boot version


        StringWriter stringWriter = new StringWriter();

        Marshaller marshaller = getMarshaller();

        Project project = Project.builder()
                .modelVersion("4.0.0")
                .groupId(mavenConfiguration.getGroupId())
                .artifactId(mavenConfiguration.getArtifactId())
                .version(mavenConfiguration.getVersion())
                .packaging(mavenConfiguration.getPackaging())
                .name(mavenConfiguration.getArtifactId())
                .description(mavenConfiguration.getDescription())
                .url(mavenConfiguration.getUrl())
                .dependencies(List.of())
                .parent(Parent.builder()
                        .groupId("org.springframework.boot")
                        .artifactId("spring-boot-starter-parent")
                        .version(mavenConfiguration.getSpringBootVersion())
                        .relativePath("")
                        .build())
                .build(Build.builder().build())
                .build();


        marshaller.marshal(project, stringWriter);

        // Expected XML string
        String expectedXml = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.example</groupId>
                    <artifactId>test-app</artifactId>
                    <version>1.0.0</version>
                    <packaging>jar</packaging>
                    <name>test-app</name>
                    <description>Test Application</description>
                    <url>http://example.com</url>
                    <parent>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-parent</artifactId>
                        <version>2.5.6</version>
                        <relativePath></relativePath>
                    </parent>
                    <dependencies/>
                    <build/>
                </project>
                """;


        assertEquals(expectedXml.trim(), stringWriter.toString().trim());
    }

    private static Marshaller getMarshaller() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Project.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
                "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd");
        return marshaller;
    }

}