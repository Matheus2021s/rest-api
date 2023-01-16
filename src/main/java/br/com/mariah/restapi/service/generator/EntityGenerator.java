package br.com.mariah.restapi.service.generator;

import br.com.mariah.restapi.model.ModelData;
import br.com.mariah.restapi.utils.ResourceUtils;
import jakarta.persistence.*;
import lombok.*;
import org.burningwave.core.classes.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Modifier;
import java.util.List;

@RequiredArgsConstructor
@Component
public class EntityGenerator implements GeneratorService {

    private final ResourceUtils resourceUtils;

    @Override
    public List<UnitSourceGenerator> generate(ModelData modelData) {

        UnitSourceGenerator unitSourceGenerator = UnitSourceGenerator
                .create(String.format("%s.domain", resourceUtils.getBasePackage()));

        ClassSourceGenerator classSource = ClassSourceGenerator
                .create(TypeDeclarationSourceGenerator
                        .create(String.format("%sEntity", modelData.getName())
                        )
                ).addModifier(Modifier.PUBLIC)
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(Entity.class)
                )
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(Getter.class)
                ).addAnnotation(
                        AnnotationSourceGenerator
                                .create(Setter.class)
                ).addAnnotation(
                        AnnotationSourceGenerator
                                .create(AllArgsConstructor.class)
                ).addAnnotation(
                        AnnotationSourceGenerator
                                .create(NoArgsConstructor.class)
                ).addAnnotation(
                        AnnotationSourceGenerator
                                .create(Builder.class)
                ).addAnnotation(
                        AnnotationSourceGenerator
                                .create(Table.class)
                                .addParameter(
                                        VariableSourceGenerator
                                                .create("name")
                                                .setValue(String.format("\"%s\"", modelData.getName()))
                                )
                );


        Boolean composePrimaryKey = modelData.getIsComposePrimaryKey();

        modelData.getParameters().forEach(parameterData -> {

                    VariableSourceGenerator parameter = VariableSourceGenerator
                            .create(TypeDeclarationSourceGenerator
                                    .create(parameterData.getDataType()), parameterData.getName())
                            .addModifier(Modifier.PRIVATE);

                    if (parameterData.getIsPrimaryKey() && !composePrimaryKey) {
                        parameter
                                .addAnnotation(
                                        AnnotationSourceGenerator
                                                .create(Id.class)
                                )
                                .addAnnotation(
                                        AnnotationSourceGenerator
                                                .create(GeneratedValue.class)
                                                .addParameter(VariableSourceGenerator.create("strategy")
                                                        .setValue("GenerationType.IDENTITY"))
                                );
                        unitSourceGenerator.addImport("jakarta.persistence.GenerationType");
                    }

                    parameter
                            .addAnnotation(
                                    AnnotationSourceGenerator
                                            .create(Column.class)
                                            .addParameter(
                                                    VariableSourceGenerator
                                                            .create("name")
                                                            .setValue(String.format("\"%s\"", parameterData.getName()))
                                            )
                            );
                    classSource.addField(parameter);
                }
        );


        unitSourceGenerator.addClass(classSource);

        return List.of(unitSourceGenerator);
    }
}
