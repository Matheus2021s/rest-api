package br.com.mariah.restapi.service.generator;

import br.com.mariah.restapi.model.ModelData;
import br.com.mariah.restapi.utils.ResourceUtils;
import lombok.RequiredArgsConstructor;
import org.burningwave.core.classes.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Modifier;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ControllerGeneratorService implements GeneratorService {

    private final ResourceUtils resourceUtils;

    @Override
    public List<UnitSourceGenerator> generate(ModelData modelData) {

        String controllerPackage = String.format("%s.controller", resourceUtils.getBasePackage());
        UnitSourceGenerator controllerUnitSource = UnitSourceGenerator.create(controllerPackage);

        ClassSourceGenerator controllerClass = ClassSourceGenerator
                .create(
                        TypeDeclarationSourceGenerator
                                .create(String.format("%sController", modelData.getCamelNameFirstLetterUpper()))
                ).addModifier(Modifier.PUBLIC)
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(RestController.class)
                ).addAnnotation(
                        AnnotationSourceGenerator
                                .create(RequestMapping.class)
                                .addParameter(
                                        VariableSourceGenerator
                                                .create("value")
                                                .setValue(String.format("\"/%s\"", modelData.getNameLowerSeparatedByDash()))
                                )
                ).addAnnotation(
                        AnnotationSourceGenerator
                                .create(RequiredArgsConstructor.class)
                );

        VariableSourceGenerator variableService = VariableSourceGenerator.create(
                        TypeDeclarationSourceGenerator
                                .create(
                                        String.format("%s.service.%sService", resourceUtils.getBasePackage(), modelData.getCamelNameFirstLetterUpper()),
                                        String.format("%sService", modelData.getCamelNameFirstLetterUpper())
                                ), String.format("%sService", modelData.getCamelNameFirstLetterLower())

                ).addModifier(Modifier.PRIVATE)
                .addModifier(Modifier.FINAL);

        controllerClass.addField(variableService);


        FunctionSourceGenerator listMethod = FunctionSourceGenerator
                .create("list")
                .addParameter(
                        VariableSourceGenerator
                                .create(
                                        TypeDeclarationSourceGenerator
                                                .create(Pageable.class),
                                        "pageable"
                                )
                )
                .setReturnType(ResponseEntity.class)
                .addModifier(Modifier.PUBLIC)
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(GetMapping.class)
                )
                .addBodyCodeLine(String.format("return ResponseEntity.ok(this.%sService.list(pageable));", modelData.getCamelNameFirstLetterLower()));

        controllerClass.addMethod(listMethod);

        controllerUnitSource.addClass(controllerClass);
        return List.of(controllerUnitSource);
    }
}
