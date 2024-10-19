package com.nairples.apigen.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import com.nairples.apigen.config.ApiGenConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.util.GenerationContext;

@ExtendWith(MockitoExtension.class)
class GeneratorRepositoryServiceTest {

   private ApiGenConfig apiGenConfig = new ApiGenConfig();
    GeneratorRepositoryService generatorRepositoryService = new GeneratorRepositoryService(apiGenConfig);

    @AfterAll
    static void tearDown() throws IOException {
        String userHome = System.getProperty("user.home");
        deleteDirectory(Paths.get( userHome + File.separator + "apiGen"));
    }

    @Test
    void testGenerateRepositoryInterface_validSimpleClassName() throws IOException {

        ClassDefinition classDefinition = ClassDefinition
        		.builder()
        		.name("User")
        		.packageName("com.example.model")
        		.build();


        generatorRepositoryService.generateRepositoryInterface(GenerationContext.getGenerationContext(classDefinition), classDefinition);


        assertTrue(Files.exists(Paths.get(apiGenConfig.getOutputDirectory()+"src/main/java/com/example/model/repository/UserRepository.java")));
    }

    @Test
    void testGenerateRepositoryInterface_validComplexClassName() throws IOException {

        ClassDefinition classDefinition = ClassDefinition
        		.builder()
        		.name("ComplexUser")
        		.packageName("com.example.complex")
        		.build();


        generatorRepositoryService.generateRepositoryInterface(GenerationContext.getGenerationContext(classDefinition), classDefinition);

        assertTrue(Files.exists(Paths.get(apiGenConfig.getOutputDirectory()+"src/main/java/com/example/complex/repository/ComplexUserRepository.java")));
    }

    @Test
    void testGenerateRepositoryInterface_nestedPackageName() throws IOException {

        ClassDefinition classDefinition = ClassDefinition
        		.builder()
        		.name("Order")
        		.packageName("com.example.business.domain")
        		.build();


        generatorRepositoryService.generateRepositoryInterface(GenerationContext.getGenerationContext(classDefinition), classDefinition);


        assertTrue(Files.exists(Paths.get(apiGenConfig.getOutputDirectory()+"src/main/java/com/example/business/domain/repository/OrderRepository.java")));
    }

    @Test
    void testGenerateRepositoryInterface_noMethods() throws IOException {
        // Arrange
        ClassDefinition classDefinition = ClassDefinition
        		.builder()
        		.name("Product")
        		.packageName("com.example.store")
        		.build();

        // Act
        generatorRepositoryService.generateRepositoryInterface(GenerationContext.getGenerationContext(classDefinition), classDefinition);

        // Assert
        assertTrue(Files.exists(Paths.get(apiGenConfig.getOutputDirectory()+"src/main/java/com/example/store/repository/ProductRepository.java")));
    }
    
    @Test
    void testGenerateRepositoryInterface_classWithConstructor() throws IOException {
        // Arrange
        ClassDefinition classDefinition = ClassDefinition
        		.builder()
        		.name("Product")
        		.packageName("com.example.store")
        		.build();

        // Act
        generatorRepositoryService.generateRepositoryInterface(GenerationContext.getGenerationContext(classDefinition), classDefinition);

        // Assert
        assertTrue(Files.exists(Paths.get(apiGenConfig.getOutputDirectory()+"src/main/java/com/example/store/repository/ProductRepository.java")));
    }

    static void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}