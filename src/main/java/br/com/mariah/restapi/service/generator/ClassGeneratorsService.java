package br.com.mariah.restapi.service.generator;

import br.com.mariah.restapi.model.ModelData;
import br.com.mariah.restapi.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
@Service
public class ClassGeneratorsService {
    private final List<GeneratorService> generators;
    private final StorageService storageService;


    public void generate(ModelData modelData) {
        this.generators.forEach(generatorService ->
                Optional.ofNullable(generatorService.generate(modelData))
                        .ifPresent(unitSourceGenerators -> storageService.store(unitSourceGenerators))
        );
    }
}
