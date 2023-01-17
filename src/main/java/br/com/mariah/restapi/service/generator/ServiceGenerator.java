package br.com.mariah.restapi.service.generator;

import br.com.mariah.restapi.model.ModelData;
import br.com.mariah.restapi.utils.ResourceUtils;
import lombok.RequiredArgsConstructor;
import org.burningwave.core.classes.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Modifier;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ServiceGenerator implements GeneratorService {

    private final ResourceUtils resourceUtils;

    @Override
    public List<UnitSourceGenerator> generate(ModelData modelData) {

        UnitSourceGenerator unitSourceGenerator = UnitSourceGenerator.create(
                String.format("%s.service", resourceUtils.getBasePackage())
        );


        ClassSourceGenerator classSourceGenerator = ClassSourceGenerator
                .create(
                        TypeDeclarationSourceGenerator
                                .create(
                                        String.format("%sService", modelData.getName())
                                )
                )
                .addModifier(Modifier.PUBLIC)
                .addAnnotation(
                        AnnotationSourceGenerator.create(
                                Service.class
                        )
                )
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(RequiredArgsConstructor.class)
                );


        String repositoryName = String.format("%sRepository", modelData.getName());
        VariableSourceGenerator repository = VariableSourceGenerator
                .create(
                        TypeDeclarationSourceGenerator
                                .create(
                                        String.format("%s.repository.%s", this.resourceUtils.getBasePackage(), repositoryName),
                                        repositoryName
                                ),
                        "repository"

                )
                .addModifier(Modifier.PRIVATE)
                .addModifier(Modifier.FINAL);

        classSourceGenerator.addField(repository);


        String entityName = String.format("%sEntity", modelData.getName());

        FunctionSourceGenerator listMethod = FunctionSourceGenerator.create("list")
                .addParameter(
                        VariableSourceGenerator
                                .create(
                                        TypeDeclarationSourceGenerator
                                                .create(
                                                        Pageable.class
                                                ), "pageable"
                                )
                ).addModifier(Modifier.PUBLIC)
                .setReturnType(
                        TypeDeclarationSourceGenerator
                                .create(
                                        Page.class
                                ).addGeneric(
                                        GenericSourceGenerator
                                                .create(
                                                        entityName
                                                )
                                )
                )
                .addBodyCodeLine("return this.repository.findAll(pageable);");

        unitSourceGenerator.addImport(String.format("%s.domain.%s", resourceUtils.getBasePackage(), entityName));


        Boolean isComposePrimaryKey = modelData.getIsComposePrimaryKey();

        FunctionSourceGenerator findByIdMethod = FunctionSourceGenerator
                .create("findById")
                .addModifier(Modifier.PUBLIC)
                .setReturnType(entityName)
                .addBodyCodeLine("return this.repository.findById(id)")
                .addBodyCodeLine(String.format(".orElseThrow(()-> new ResourceNotFoundException(\" %s não encontrado!\"));", modelData.getName()));

        unitSourceGenerator.addImport(String.format("%s.exception.ResourceNotFoundException", resourceUtils.getBasePackage()));


        FunctionSourceGenerator deleteMethod = FunctionSourceGenerator
                .create("delete")
                .addModifier(Modifier.PUBLIC)
                .setReturnType("void")
                .addBodyCodeLine("this.repository.delete(findById(id));");


        FunctionSourceGenerator updateMethod = FunctionSourceGenerator.create("Update")
                .addModifier(Modifier.PUBLIC)
                .addParameter(
                        VariableSourceGenerator
                                .create(
                                        TypeDeclarationSourceGenerator.create(entityName),
                                        "entity"
                                )
                )
                .setReturnType(
                        TypeDeclarationSourceGenerator.create(entityName)
                )
                .addBodyCodeLine(String.format("%s entityFound = findById(id);", entityName));


        List<FunctionSourceGenerator> commonMethod = List.of(findByIdMethod, deleteMethod, updateMethod);

        if (isComposePrimaryKey) {

            String embeddedName = String.format("%sEmbeddedId", modelData.getName());
            String embeddedImport = String.format("%s.domain.%s", resourceUtils.getBasePackage(), embeddedName);

            commonMethod.forEach(functionSourceGenerator -> functionSourceGenerator.addParameter(
                    VariableSourceGenerator
                            .create(
                                    TypeDeclarationSourceGenerator
                                            .create(
                                                    embeddedImport, embeddedName
                                            ), "id"
                            )
            ));

            modelData.getPrimaryKeys().forEach(parameterData -> {
                updateMethod.addBodyCodeLine(String.format("entityFound.getId().set%s(entity.getId().get%s());", parameterData.getCamelNameFirstLetterUpper(), parameterData.getCamelNameFirstLetterUpper()));
            });

            modelData.getNonPrimaryKeys().forEach(parameterData -> {
                updateMethod.addBodyCodeLine(String.format("entityFound.set%s(entity.get%s());", parameterData.getCamelNameFirstLetterUpper(), parameterData.getCamelNameFirstLetterUpper()));
            });

        } else {
            modelData.getPrimaryKeys()
                    .forEach(parameterData -> {
                        commonMethod.forEach(functionSourceGenerator -> functionSourceGenerator.addParameter(
                                VariableSourceGenerator
                                        .create(
                                                TypeDeclarationSourceGenerator
                                                        .create(
                                                                parameterData.getDataType()
                                                        ), "id"
                                        )
                        ));
                    });
            modelData.getParameters().forEach(parameterData -> {
                updateMethod.addBodyCodeLine(String.format("entityFound.set%s(entity.get%s());", parameterData.getCamelNameFirstLetterUpper(), parameterData.getCamelNameFirstLetterUpper()));
            });
        }


        updateMethod.addBodyCodeLine("return entityFound;");


        FunctionSourceGenerator createMethod = FunctionSourceGenerator.create("create")
                .addModifier(Modifier.PUBLIC)
                .addParameter(
                        VariableSourceGenerator
                                .create(
                                        TypeDeclarationSourceGenerator.create(entityName),
                                        "entity"
                                )
                )
                .setReturnType(entityName)
                .addBodyCodeLine("return this.repository.save(entity);");


        classSourceGenerator.addMethod(listMethod);
        classSourceGenerator.addMethod(findByIdMethod);
        classSourceGenerator.addMethod(createMethod);
        classSourceGenerator.addMethod(updateMethod);
        classSourceGenerator.addMethod(deleteMethod);
        unitSourceGenerator.addClass(
                classSourceGenerator
        );

        return List.of(unitSourceGenerator);
    }
}
