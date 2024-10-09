package com.nairples.apigen.service;

import static com.nairples.apigen.service.GeneratorRepositoryServiceTest.deleteDirectory;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.nairples.apigen.config.ApiGenConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.TypeSpec;

import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Field;
import com.nairples.apigen.model.MavenConfiguration;
import com.nairples.apigen.model.Method;
import com.nairples.apigen.pom.Build;
import com.nairples.apigen.pom.Parent;
import com.nairples.apigen.pom.Project;


@ExtendWith(MockitoExtension.class)
class GeneratorClassServiceTest {

    private final JavaFile mockJavaFile = mock(JavaFile.class);
    @Mock
    MavenConfiguration mavenConfiguration;
    @Mock
    GeneratorClassService generatorClassService;
    @Mock
    GeneratorPomService generatorPomService;


    @AfterAll
    static void tearDown() throws IOException {
        String userHome = System.getProperty("user.home");
        deleteDirectory(Paths.get( userHome + File.separator + "apiGen"));
    }


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

        ApiGenConfig customAPiConfig =new ApiGenConfig();
        GeneratorClassService generatorClassService = new GeneratorClassService(customAPiConfig);

        generatorClassService.generateClass(classDefinition);


        Path filePath = Paths.get(customAPiConfig.getOutputDirectory()+"src/main/java/Test/TestEntity.java");
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
        method.setInputVariables(List.of());
        method.setAccessModifier(Modifier.PUBLIC.toString());

        ClassDefinition classDefinition = new ClassDefinition();
        classDefinition.setName("TestEntity");
        classDefinition.setPackageName("Test");
        classDefinition.setMethods(Collections.singletonList(method));


        ApiGenConfig customAPiConfig =new ApiGenConfig();
        GeneratorClassService generatorClassService = new GeneratorClassService(customAPiConfig);


        generatorClassService.generateClass(classDefinition);


        Path filePath = Paths.get(customAPiConfig.getOutputDirectory()+"src/main/java/Test/TestEntity.java");
        assertTrue(Files.exists(filePath));

        String fileContent = Files.readString(filePath);
        assertTrue(fileContent.contains("public java.lang.Integer getId() {")); // Check if the method is correctly generated
        assertTrue(fileContent.contains("return null;")); // Check if the method body is correct

        // Clean up
        Files.deleteIfExists(filePath);
    }

    @Test
    void testGenerateClass_writesToFileSystem() throws ClassNotFoundException, IOException {

        ClassDefinition classDefinition = new ClassDefinition();
        classDefinition.setName("TestEntity");
        classDefinition.setPackageName("Test");

        ApiGenConfig customAPiConfig =new ApiGenConfig();
        GeneratorClassService generatorClassService = new GeneratorClassService(customAPiConfig);

        generatorClassService.generateClass(classDefinition);

        Path filePath = Paths.get(customAPiConfig.getOutputDirectory()+"src/main/java/Test/TestEntity.java");
        assertTrue(Files.exists(filePath)); // Check if the file is created

        // Clean up
        Files.deleteIfExists(filePath);
    }


    @Test
    void testGenerateClass_invalidFieldType_throwsClassNotFoundException() throws IOException, ClassNotFoundException {

        doThrow(ClassNotFoundException.class).when(generatorClassService).generateClass(any());

        Method method = new Method();
        method.setName("getId");
        method.setReturnType("java.lang.Integer");
        method.setInputVariables(List.of());
        method.setAccessModifier(Modifier.PUBLIC.toString());

        // Arrange
        Field field = new Field();
        field.setName("invalidField");
        field.setType("non.existent.ClassType"); // Invalid class type

        ClassDefinition classDefinition = new ClassDefinition();
        classDefinition.setName("InvalidFieldClass");
        classDefinition.setFields(Collections.singletonList(field));
        classDefinition.setMethods(Collections.singletonList(method));



        assertThrows(ClassNotFoundException.class, () -> generatorClassService.generateClass(classDefinition));
    }

    @Test
    void testGenerateClass_invalidMethodReturnType_throwsClassNotFoundException_NoPackage() throws IOException, ClassNotFoundException {

        doThrow(ClassNotFoundException.class).when(generatorClassService).generateClass(any());

        // Arrange
        Method method = new Method();
        method.setName("invalidMethod");
        method.setReturnType("non.existent.ReturnType");
        method.setInputVariables(List.of());

        ClassDefinition classDefinition = new ClassDefinition();
        classDefinition.setName("InvalidMethodClass");
        classDefinition.setMethods(Collections.singletonList(method));


        assertThrows(ClassNotFoundException.class, () -> generatorClassService.generateClass(classDefinition));
    }

    @Test
    void testGenerateClass_nullClassDefinition_throwsNullPointerException() throws IOException, ClassNotFoundException {
       doThrow(NullPointerException.class).when(generatorClassService).generateClass(any());

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

        ApiGenConfig customAPiConfig =new ApiGenConfig();
        GeneratorClassService generatorClassService = new GeneratorClassService(customAPiConfig);

        // Act
        generatorClassService.generateClass(classDefinition);

        // Assert
        Path filePath = Paths.get(customAPiConfig.getOutputDirectory()+"src/main/java/Test/EmptyClass.java");
        assertTrue(Files.exists(filePath)); // File should still be generated

        String fileContent = Files.readString(filePath);
        assertFalse(fileContent.contains("private")); // No fields


        // Clean up
        Files.deleteIfExists(filePath);
    }

    @Test
    void testGeneratePomXmlFile_withValidConfig_success() {


        assertDoesNotThrow(() -> {
        	generatorPomService.generatePomXmlFile(mavenConfiguration);
        });
    }

    @Test
    void testGeneratePomXmlFile_withValidConfig_compareContent() throws Exception {




        StringWriter stringWriter = new StringWriter();


        Marshaller marshaller = getMarshaller();


        Project project = Project.builder()
                .modelVersion("4.0.0")
                .groupId("com.example")
                .artifactId("test-app")
                .version("1.0.0")
                .packaging("jar")
                .name("test-app")
                .description("Test Application")
                .url("http://example.com")
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


        StringWriter stringWriter = new StringWriter();

        Marshaller marshaller = getMarshaller();

        Project project = Project.builder()
                .modelVersion("4.0.0")
                .groupId("com.example")
                .artifactId("test-app")
                .version("1.0.0")
                .packaging("jar")
                .name("test-app")
                .description("Test Application")
                .url("http://example.com")
                .dependencies(List.of())
                .parent(Parent.builder()
                        .groupId("org.springframework.boot")
                        .artifactId("spring-boot-starter-parent")
                        .version("2.5.6")
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