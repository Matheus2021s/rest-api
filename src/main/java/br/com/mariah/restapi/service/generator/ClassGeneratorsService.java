package br.com.mariah.restapi.service.generator;

import br.com.mariah.restapi.model.ModelData;
import br.com.mariah.restapi.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.burningwave.core.classes.UnitSourceGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Log4j2
@Service
public class ClassGeneratorsService {

    private final EntityGenerator entityGenerator;

    private final RepositoryGenerator repositoryGenerator;

    private final StorageService storageService;


    public void generate(ModelData modelData) {

        List<UnitSourceGenerator> itens = new ArrayList<>();
        itens.addAll(this.entityGenerator.generate(modelData));
        itens.addAll(this.repositoryGenerator.generate(modelData));


        storageService.store(itens);

    }
}
