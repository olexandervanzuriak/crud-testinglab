package edu.vanzuriak.crud_operations;

/*
    @author  olexander
    @project crud_operations
    @class   ProjectArchitectureTests
    @version 1.0.0
    @since 4/19/25 - 10 - 14
*/

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@SpringBootTest
public class ProjectArchitectureTests {

    private JavaClasses classes;

    @BeforeEach
    void setup() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("edu.vanzuriak.crud_operations");
    }

    @Test
    void shouldFollowLayerArchitecture() {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")

                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")

                .check(classes);
    }

    @Test
    void controllerClassesShouldBeAnnotatedWithRestController() {
        classes()
                .that().resideInAPackage("..controller..")
                .should().beAnnotatedWith(RestController.class)
                .check(classes);
    }

    @Test
    void controllerClassesShouldBeNamedXController() {
        classes()
                .that().resideInAPackage("..controller..")
                .should().haveSimpleNameEndingWith("Controller")
                .check(classes);
    }

    @Test
    void repositoriesShouldBeInterfaces() {
        classes()
                .that().resideInAPackage("..repository..")
                .should().beInterfaces()
                .check(classes);
    }

    @Test
    void repositoriesShouldNotDependOnServiceLayer() {
        noClasses()
                .that().resideInAPackage("..repository..")
                .should().dependOnClassesThat().resideInAPackage("..service..")
                .check(classes);
    }

    @Test
    void controllersShouldNotDependOnOtherControllers() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .check(classes);
    }

    @Test
    void controllerFieldsShouldNotBeAutowiredDirectly() {
        noFields()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().beAnnotatedWith(Autowired.class)
                .check(classes);
    }

    @Test
    void modelFieldsShouldBePrivate() {
        fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..model..")
                .should().notBePublic()
                .check(classes);
    }

    @Test
    void servicesShouldBeAnnotatedWithService() {
        classes()
                .that().resideInAPackage("..service..")
                .should().beAnnotatedWith(org.springframework.stereotype.Service.class)
                .check(classes);
    }

    @Test
    void serviceClassesShouldHaveServiceSuffix() {
        classes()
                .that().resideInAPackage("..service..")
                .should().haveSimpleNameEndingWith("Service")
                .check(classes);
    }

    @Test
    void repositoryClassesShouldHaveRepositorySuffix() {
        classes()
                .that().resideInAPackage("..repository..")
                .should().haveSimpleNameEndingWith("Repository")
                .check(classes);
    }

    @Test
    void servicesShouldNotAccessControllers() {
        noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .check(classes);
    }

    @Test
    void modelsShouldNotDependOnRepositories() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should().dependOnClassesThat().resideInAPackage("..repository..")
                .check(classes);
    }

    @Test
    void servicesShouldOnlyBeAccessedByControllersOrServices() {
        classes()
                .that().resideInAPackage("..service..")
                .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..")
                .check(classes);
    }

    @Test
    void repositoryClassesShouldBeAnnotatedWithRepository() {
        classes()
                .that().resideInAPackage("..repository..")
                .should().beAnnotatedWith(org.springframework.stereotype.Repository.class)
                .check(classes);
    }

    @Test
    void controllerMethodsShouldBePublic() {
        methods()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().bePublic()
                .check(classes);
    }

    @Test
    void serviceMethodsShouldBePublic() {
        methods()
                .that().areDeclaredInClassesThat().resideInAPackage("..service..")
                .should().bePublic()
                .check(classes);
    }

    @Test
    void controllersShouldNotBeAccessedByOtherLayers() {
        noClasses()
                .that().resideOutsideOfPackage("..controller..")
                .should().dependOnClassesThat()
                .resideInAPackage("..controller..")
                .because("Controllers should only be entry points and not used internally")
                .check(classes);
    }

    @Test
    void modelClassesShouldNotBeAnnotatedWithAutowired() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should().beAnnotatedWith(Autowired.class)
                .check(classes);
    }

    @Test
    void modelsShouldNotDependOnServiceOrController() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should().dependOnClassesThat().resideInAnyPackage("..service..", "..controller..")
                .check(classes);
    }
}

