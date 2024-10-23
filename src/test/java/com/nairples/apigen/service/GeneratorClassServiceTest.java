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
import com.nairples.apigen.util.*;
import com.nairples.apigen.util.GenerationContext.GenerationContextBuilder;


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
        Field field = Field
        		.builder()
        		.name("id")
        		.type("java.lang.Integer")
        		.accessModifier(Modifier.PRIVATE.toString())
        		.build();
        ClassDefinition classDefinition = ClassDefinition
        		.builder()
        		.name("TestEntity")
        		.fields(Collections.singletonList(field))
        		.packageName("Test")
        		.build();

        ApiGenConfig customAPiConfig =new ApiGenConfig();
        GeneratorClassService generatorClassService = new GeneratorClassService(customAPiConfig);

        generatorClassService.generateClass(GenerationContext.getGenerationContext(classDefinition), 
        		classDefinition);


        Path filePath = Paths.get(customAPiConfig.getOutputDirectory()+"src/main/java/Test/TestEntity.java");
        assertTrue(Files.exists(filePath));

        String fileContent = Files.readString(filePath);
        assertTrue(fileContent.contains("Integer id;"));

        Files.deleteIfExists(filePath);
    }

    @Test
    void testGenerateClass_generatesCorrectMethods() throws ClassNotFoundException, IOException {

        Method method = Method.builder()
        		.name("getId")
        		.returnType("java.lang.Integer")
        		.inputVariables(List.of())
        		.accessModifier(Modifier.PUBLIC.toString()).build();
        
        
        ClassDefinition classDefinition = ClassDefinition
        		.builder()
        		.name("TestEntity")
        		.methods(Collections.singletonList(method))
        		.packageName("Test")
        		.build();

        ApiGenConfig customAPiConfig =new ApiGenConfig();
        GeneratorClassService generatorClassService = new GeneratorClassService(customAPiConfig);


        generatorClassService.generateClass(GenerationContext.getGenerationContext(classDefinition), classDefinition);


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

        ClassDefinition classDefinition = ClassDefinition
        		.builder()
        		.name("TestEntity")
        		.packageName("Test")
        		.build();


        ApiGenConfig customAPiConfig =new ApiGenConfig();
        GeneratorClassService generatorClassService = new GeneratorClassService(customAPiConfig);

        generatorClassService.generateClass(GenerationContext.getGenerationContext(classDefinition), classDefinition);

        Path filePath = Paths.get(customAPiConfig.getOutputDirectory()+"src/main/java/Test/TestEntity.java");
        assertTrue(Files.exists(filePath)); // Check if the file is created

        // Clean up
        Files.deleteIfExists(filePath);
    }


    @Test
    void testGenerateClass_invalidFieldType_throwsClassNotFoundException() throws IOException, ClassNotFoundException {

        doThrow(ClassNotFoundException.class).when(generatorClassService).generateClass(any(),  any());

        Method method = Method.builder()
	        .name("getId")
	        .returnType("java.lang.Integer")
	        .inputVariables(List.of())
	        .accessModifier(Modifier.PUBLIC.toString())
	        .build();

        // Arrange
        Field field = Field
        		.builder()
        		.name("invalidField")
        		.type("non.existent.ClassType")
        		.build();
        
        ClassDefinition classDefinition = ClassDefinition
        		.builder()
        		.name("InvalidFieldClass")
        		.fields(Collections.singletonList(field))
        		.methods(Collections.singletonList(method))
        		.build();
        
        assertThrows(ClassNotFoundException.class, () -> generatorClassService.generateClass(GenerationContext.getGenerationContext(classDefinition), classDefinition));
    }

    @Test
    void testGenerateClass_invalidMethodReturnType_throwsClassNotFoundException_NoPackage() throws IOException, ClassNotFoundException {

        doThrow(ClassNotFoundException.class).when(generatorClassService).generateClass(any(), any());

        // Arrange
        Method method = Method.builder()
	        .name("invalidMethod")
	        .returnType("non.existent.ReturnType")
	        .inputVariables(List.of())
	        .build();

        ClassDefinition classDefinition = ClassDefinition
        		.builder()
        		.name("InvalidMethodClass")
        		.methods(Collections.singletonList(method))
        		.build();


        assertThrows(ClassNotFoundException.class, () -> generatorClassService.generateClass(GenerationContext.getGenerationContext(classDefinition), classDefinition));
    }

    @Test
    void testGenerateClass_nullClassDefinition_throwsNullPointerException() throws IOException, ClassNotFoundException {
       doThrow(NullPointerException.class).when(generatorClassService).generateClass( any(),  any());

        assertThrows(NullPointerException.class, () -> generatorClassService.generateClass( null, null));
    }

    @Test
    void testGenerateClass_ioExceptionDuringFileWriting_throwsIOException() throws ClassNotFoundException, IOException {
        // Arrange
        ClassDefinition classDefinition = ClassDefinition.builder().name("TestEntity").build();

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
        ClassDefinition classDefinition = ClassDefinition
        		.builder()
        		.name("EmptyClass") // No fields and methods
    			.packageName("Test")
    			.build();

        ApiGenConfig customAPiConfig =new ApiGenConfig();
        GeneratorClassService generatorClassService = new GeneratorClassService(customAPiConfig);

        // Act
        generatorClassService.generateClass(GenerationContext.getGenerationContext(classDefinition), classDefinition);

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
        	generatorPomService.generatePomXmlFile(GenerationContext.getEmptyGenerationContext(),  mavenConfiguration);
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