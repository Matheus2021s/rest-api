package br.com.mariah.restapi.service.generator;

import br.com.mariah.restapi.model.ModelData;
import br.com.mariah.restapi.model.ParameterData;
import br.com.mariah.restapi.utils.ResourceUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.burningwave.core.classes.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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

        FunctionSourceGenerator getByIdMethod = FunctionSourceGenerator.create("getById")
                .setReturnType(TypeDeclarationSourceGenerator.create(ResponseEntity.class))
                .addModifier(Modifier.PUBLIC);

        getByIdMethodContent(modelData, controllerUnitSource, getByIdMethod);

        addIdMappingAnnotation(getByIdMethod, modelData, AnnotationSourceGenerator.create(GetMapping.class));

        controllerClass.addMethod(getByIdMethod);


        FunctionSourceGenerator createMethod = FunctionSourceGenerator.create("create")
                .setReturnType(TypeDeclarationSourceGenerator.create(ResponseEntity.class))
                .addAnnotation(AnnotationSourceGenerator.create(PostMapping.class))
                .addModifier(Modifier.PUBLIC)
                .addParameter(
                        VariableSourceGenerator
                                .create(
                                        TypeDeclarationSourceGenerator
                                                .create(String.format("%sCreateDTO", modelData.getCamelNameFirstLetterUpper())),
                                        String.format("%sCreateDTO", modelData.getCamelNameFirstLetterLower())
                                ).addAnnotation(
                                        AnnotationSourceGenerator
                                                .create(RequestBody.class)
                                ).addAnnotation(
                                        AnnotationSourceGenerator
                                                .create(Valid.class)
                                )
                )
                .addBodyCodeLine(String.format("%sEntity entity = this.%sService.create(%sCreateDTO.toEntity());", modelData.getCamelNameFirstLetterUpper(), modelData.getCamelNameFirstLetterLower(), modelData.getCamelNameFirstLetterLower()))
                .addBodyCodeLine(String.format("return ResponseEntity.created(URI.create(\"/%s\")).body(UserResponseDTO.of(entity));", modelData.getNameLowerSeparatedByDash()));

        controllerUnitSource.addImport(String.format("%s.domain.%sEntity", resourceUtils.getBasePackage(),modelData.getCamelNameFirstLetterUpper()));
        controllerUnitSource.addImport(String.format("%s.dto.create.%sCreateDTO", resourceUtils.getBasePackage(),modelData.getCamelNameFirstLetterUpper()));
        controllerUnitSource.addImport(URI.class);

        controllerClass.addMethod(createMethod);

        controllerUnitSource.addClass(controllerClass);

        return List.of(controllerUnitSource);
    }

    private void getByIdMethodContent(ModelData modelData, UnitSourceGenerator controllerUnitSource, FunctionSourceGenerator getByIdMethod) {
        if (modelData.getIsComposePrimaryKey()) {

            modelData.getPrimaryKeys().stream()
                    .map(parameterData -> VariableSourceGenerator
                            .create(
                                    TypeDeclarationSourceGenerator
                                            .create(parameterData.getDataType()),
                                    parameterData.getCamelNameFirstLetterLower()
                            ).addAnnotation(
                                    AnnotationSourceGenerator
                                            .create(RequestParam.class)
                                            .addParameter(
                                                    VariableSourceGenerator
                                                            .create("name")
                                                            .setValue(String.format("\"%s\"", parameterData.getCamelNameFirstLetterLower()))
                                            )
                            )
                    )
                    .forEach(getByIdMethod::addParameter);


            String sb = String.format("%sEmbeddedId id = %sEmbeddedId.builder()\n", modelData.getCamelNameFirstLetterUpper(), modelData.getCamelNameFirstLetterUpper()) +
                    modelData.getPrimaryKeys().stream()
                            .map(ParameterData::getCamelNameFirstLetterLower)
                            .map(name -> String.format(".%s(%s)\n", name, name))
                            .collect(Collectors.joining()) +
                    ".build();";

            getByIdMethod.addBodyCodeLine(sb);

            String finalString = String.format("return ResponseEntity.ok(%sResponseDTO.of(this.%sService.findById(id)));", modelData.getCamelNameFirstLetterUpper(), modelData.getCamelNameFirstLetterLower());

            getByIdMethod.addBodyCodeLine(finalString);

            controllerUnitSource.addImport(String.format("%s.domain.%sEmbeddedId", resourceUtils.getBasePackage(), modelData.getCamelNameFirstLetterUpper()));


        } else {

            modelData.getPrimaryKeys().stream().findFirst()
                    .ifPresent(parameterData -> getByIdMethod.addParameter(
                            VariableSourceGenerator
                                    .create(
                                            TypeDeclarationSourceGenerator
                                                    .create(parameterData.getDataType()),
                                            parameterData.getCamelNameFirstLetterLower()
                                    )
                                    .addAnnotation(
                                            AnnotationSourceGenerator.create(PathVariable.class)
                                    )
                    ));

            String parameterId = modelData.getPrimaryKeys().stream()
                    .map(ParameterData::getCamelNameFirstLetterLower)
                    .collect(Collectors.joining());

            String finalString = String.format("return ResponseEntity.ok(%sResponseDTO.of(this.%sService.findById(%s)));", modelData.getCamelNameFirstLetterUpper(), modelData.getCamelNameFirstLetterLower(), parameterId);

            getByIdMethod.addBodyCodeLine(finalString);

        }
        controllerUnitSource.addImport(String.format("%s.dto.response.%sResponseDTO", resourceUtils.getBasePackage(), modelData.getCamelNameFirstLetterUpper()));

    }


    private void addIdMappingAnnotation(FunctionSourceGenerator getByIdMethod, ModelData modelData, AnnotationSourceGenerator mapping) {

        if (modelData.getIsComposePrimaryKey()) {
            getByIdMethod.addAnnotation(mapping);
        } else {
            modelData.getPrimaryKeys().stream()
                    .findFirst()
                    .map(ParameterData::getCamelNameFirstLetterLower)
                    .map(string -> String.format("\"{%s}\"", string))
                    .ifPresent(s -> mapping.addParameter(
                                    VariableSourceGenerator
                                            .create(
                                                    TypeDeclarationSourceGenerator
                                                            .create("name")
                                            ).setValue(s)
                            )
                    );
            getByIdMethod.addAnnotation(mapping);
        }

    }
}
