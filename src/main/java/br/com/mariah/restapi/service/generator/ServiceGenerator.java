package br.com.mariah.restapi.service.generator;

import br.com.mariah.restapi.model.ModelData;
import br.com.mariah.restapi.utils.ResourceUtils;
import lombok.RequiredArgsConstructor;
import org.burningwave.core.classes.*;
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

        unitSourceGenerator.addClass(
                classSourceGenerator
        );

        return List.of(unitSourceGenerator);
    }
}
