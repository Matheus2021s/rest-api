package br.com.mariah.restapi.service.generator;

import br.com.mariah.restapi.model.ModelData;
import br.com.mariah.restapi.model.ParameterData;
import br.com.mariah.restapi.utils.ResourceUtils;
import lombok.RequiredArgsConstructor;
import org.burningwave.core.classes.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.lang.reflect.Modifier;
import java.util.List;

@RequiredArgsConstructor
@Component
public class RepositoryGenerator implements GeneratorService {

    private final ResourceUtils resourceUtils;

    @Override
    public List<UnitSourceGenerator> generate(ModelData modelData) {
        UnitSourceGenerator unitSourceGenerator = UnitSourceGenerator
                .create(String.format("%s.repository", resourceUtils.getBasePackage()));


        unitSourceGenerator.addClass(
                ClassSourceGenerator
                        .createInterface(
                                TypeDeclarationSourceGenerator
                                        .create(String.format("%sRepository", modelData.getName())
                                        )
                        ).expands(
                                TypeDeclarationSourceGenerator
                                        .create(JpaRepository.class)
                                        .addGeneric(
                                                GenericSourceGenerator
                                                        .create(String.format("%sEntity", modelData.getName()))
                                        ).addGeneric(
                                                GenericSourceGenerator
                                                        .create(getRepositoryDataType(modelData,unitSourceGenerator))
                                        )
                        )
                        .addModifier(Modifier.PUBLIC)

        );

        unitSourceGenerator.addImport(String.format("%s.domain.%sEntity", resourceUtils.getBasePackage(),modelData.getName()));

        return List.of(unitSourceGenerator);
    }

    private String getRepositoryDataType(ModelData modelData, UnitSourceGenerator unitSourceGenerator) {
        if (modelData.getIsComposePrimaryKey()){
            String embedded = String.format("%sEmbeddedId", modelData.getName());
            unitSourceGenerator.addImport(String.format("%s.domain.%s", resourceUtils.getBasePackage(),embedded));
            return embedded;
        }

        Class<?> result = null;
        for (ParameterData data : modelData.getParameters()) {
            if (data.getIsPrimaryKey()) {
                result = data.getDataType();
                break;
            }
        }
        unitSourceGenerator.addImport(result);
        return result.getSimpleName();
    }
}
