package br.com.mariah.restapi.service.generator;

import br.com.mariah.restapi.model.ModelData;
import br.com.mariah.restapi.model.ParameterData;
import br.com.mariah.restapi.utils.ResourceUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.burningwave.core.classes.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Modifier;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DtoGeneratorService implements GeneratorService {

    private final ResourceUtils resourceUtils;

    @Override
    public List<UnitSourceGenerator> generate(ModelData modelData) {
        String dtoCreatePackage = String.format("%s.dto.create", this.resourceUtils.getBasePackage());

        String dtoUpdatePackage = String.format("%s.dto.update", this.resourceUtils.getBasePackage());

        String dtoResponsePackage = String.format("%s.dto.response", this.resourceUtils.getBasePackage());

        UnitSourceGenerator dtoCreateUnitSource = UnitSourceGenerator.create(dtoCreatePackage);

        UnitSourceGenerator dtoUpdateUnitSource = UnitSourceGenerator.create(dtoUpdatePackage);

        UnitSourceGenerator dtoResponseUnitSource = UnitSourceGenerator.create(dtoResponsePackage);

        ClassSourceGenerator createDtoClass = ClassSourceGenerator
                .create(
                        TypeDeclarationSourceGenerator
                                .create(String.format("%sCreateDTO", modelData.getCamelNameFirstLetterUpper()))
                ).addModifier(Modifier.PUBLIC);

        addClassAnotations(createDtoClass, dtoCreateUnitSource);


        ClassSourceGenerator updateDtoClass = ClassSourceGenerator
                .create(
                        TypeDeclarationSourceGenerator
                                .create(String.format("%sUpdateDTO", modelData.getCamelNameFirstLetterUpper()))
                ).addModifier(Modifier.PUBLIC);

        addClassAnotations(updateDtoClass, dtoUpdateUnitSource);


        ClassSourceGenerator responseDtoClass = ClassSourceGenerator
                .create(
                        TypeDeclarationSourceGenerator
                                .create(String.format("%sResponseDTO", modelData.getCamelNameFirstLetterUpper()))
                ).addModifier(Modifier.PUBLIC);

        addClassAnotations(responseDtoClass, dtoResponseUnitSource);

        modelData.getParameters().forEach(parameterData -> {
            addParameterField(createDtoClass, parameterData);
            addParameterField(updateDtoClass, parameterData);
            addParameterField(responseDtoClass, parameterData);
        });

        FunctionSourceGenerator toEntityMethod = FunctionSourceGenerator
                .create("toEntity")
                .addModifier(Modifier.PUBLIC)
                .setReturnType(String.format("%sEntity", modelData.getCamelNameFirstLetterUpper()))
                .addBodyCodeLine(String.format("return %sEntity.builder()", modelData.getCamelNameFirstLetterUpper()));

        addToEntityBody(modelData, toEntityMethod);

        dtoCreateUnitSource.addImport(String.format("%s.domain.%sEntity", this.resourceUtils.getBasePackage(), modelData.getCamelNameFirstLetterUpper()));
        dtoUpdateUnitSource.addImport(String.format("%s.domain.%sEntity", this.resourceUtils.getBasePackage(), modelData.getCamelNameFirstLetterUpper()));

        if (modelData.getIsComposePrimaryKey()) {
            dtoCreateUnitSource.addImport((String.format("%s.domain.%sEmbeddedId", this.resourceUtils.getBasePackage(), modelData.getCamelNameFirstLetterUpper())));
            dtoUpdateUnitSource.addImport((String.format("%s.domain.%sEmbeddedId", this.resourceUtils.getBasePackage(), modelData.getCamelNameFirstLetterUpper())));
        }

        createDtoClass.addMethod(toEntityMethod);
        updateDtoClass.addMethod(toEntityMethod);


        FunctionSourceGenerator ofMethod = FunctionSourceGenerator
                .create("of")
                .addModifier(Modifier.PUBLIC)
                .addModifier(Modifier.STATIC)
                .setReturnType(String.format("%sResponseDTO", modelData.getCamelNameFirstLetterUpper()))
                .addParameter(
                        VariableSourceGenerator
                                .create(
                                        TypeDeclarationSourceGenerator
                                                .create(String.format("%s.domain.%sEntity", this.resourceUtils.getBasePackage(), modelData.getCamelNameFirstLetterUpper()),
                                                        String.format("%sEntity", modelData.getCamelNameFirstLetterUpper())),
                                        String.format("%sEntity", modelData.getCamelNameFirstLetterLower())
                                )
                )
                .addBodyCodeLine(String.format("return %sResponseDTO.builder()", modelData.getCamelNameFirstLetterUpper()));

        if (modelData.getIsComposePrimaryKey()) {
            modelData.getPrimaryKeys().forEach(parameterData -> {
                ofMethod.addBodyCodeLine(String.format("\t.%s(%sEntity.getId().get%s())",
                        parameterData.getCamelNameFirstLetterLower(),
                        modelData.getCamelNameFirstLetterLower(),
                        parameterData.getCamelNameFirstLetterUpper()
                ));
            });

            modelData.getNonPrimaryKeys().forEach(parameterData -> {
                ofMethod.addBodyCodeLine(String.format("\t.%s(%sEntity.get%s())",
                        parameterData.getCamelNameFirstLetterLower(),
                        modelData.getCamelNameFirstLetterLower(),
                        parameterData.getCamelNameFirstLetterUpper()
                ));
            });


        } else {
            modelData.getParameters().forEach(parameterData -> {
                ofMethod.addBodyCodeLine(String.format("\t.%s(%sEntity.get%s())",
                        parameterData.getCamelNameFirstLetterLower(),
                        modelData.getCamelNameFirstLetterLower(),
                        parameterData.getCamelNameFirstLetterUpper()
                ));
            });
        }

        ofMethod.addBodyCodeLine(".build();");

        responseDtoClass.addMethod(ofMethod);

        dtoCreateUnitSource.addClass(createDtoClass);
        dtoUpdateUnitSource.addClass(updateDtoClass);
        dtoResponseUnitSource.addClass(responseDtoClass);

        return List.of(dtoCreateUnitSource, dtoUpdateUnitSource, dtoResponseUnitSource);
    }

    private void addToEntityBody(ModelData modelData, FunctionSourceGenerator toEntityMethod) {
        if (modelData.getIsComposePrimaryKey()) {

            toEntityMethod.addBodyCodeLine("\t.id(");
            toEntityMethod.addBodyCodeLine(String.format("\t\t%sEmbeddedId.builder()", modelData.getCamelNameFirstLetterUpper()));

            modelData.getPrimaryKeys().forEach(parameterData -> {
                toEntityMethod.addBodyCodeLine(String.format("\t\t.%s(this.%s)", parameterData.getCamelNameFirstLetterLower(), parameterData.getCamelNameFirstLetterLower()));
            });

            toEntityMethod.addBodyCodeLine("\t.build()");
            toEntityMethod.addBodyCodeLine("\t)");

            modelData.getNonPrimaryKeys().forEach(parameterData -> {
                addBuildParameter(toEntityMethod, parameterData);
            });


        } else {
            modelData.getParameters().forEach(parameterData -> {
                addBuildParameter(toEntityMethod, parameterData);
            });
        }

        toEntityMethod.addBodyCodeLine("\t.build();");

    }

    private static void addClassAnotations(ClassSourceGenerator createDtoClass, UnitSourceGenerator dtoCreateUnitSource) {
        createDtoClass.addAnnotation(
                        AnnotationSourceGenerator
                                .create(Getter.class)
                )
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(Setter.class)
                )
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(AllArgsConstructor.class)
                )
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(NoArgsConstructor.class)
                )
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(Builder.class)
                )
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(JsonInclude.class)
                                .addParameter(
                                        VariableSourceGenerator
                                                .create("value")
                                                .setValue("JsonInclude.Include.NON_ABSENT")
                                )
                );

        dtoCreateUnitSource.addImport("com.fasterxml.jackson.annotation.JsonInclude");
    }

    private static void addParameterField(ClassSourceGenerator classSourceGenerator, ParameterData parameterData) {
        classSourceGenerator.addField(
                VariableSourceGenerator
                        .create(
                                TypeDeclarationSourceGenerator
                                        .create(parameterData.getDataType()),
                                parameterData.getCamelNameFirstLetterLower()
                        ).addModifier(Modifier.PRIVATE)
                        .addAnnotation(
                                AnnotationSourceGenerator
                                        .create(JsonProperty.class)
                                        .addParameter(
                                                VariableSourceGenerator
                                                        .create("value")
                                                        .setValue(String.format("\"%s\"", parameterData.getNameUpperSeparatedByUnderscore()))
                                        )
                        )
        );
    }

    private static void addBuildParameter(FunctionSourceGenerator method, ParameterData parameterData) {
        method.addBodyCodeLine(String.format("\t.%s(this.%s)", parameterData.getCamelNameFirstLetterLower(), parameterData.getCamelNameFirstLetterLower()));
    }
}
