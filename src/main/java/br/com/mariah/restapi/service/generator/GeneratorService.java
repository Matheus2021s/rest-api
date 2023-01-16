package br.com.mariah.restapi.service.generator;

import br.com.mariah.restapi.model.ModelData;
import org.burningwave.core.classes.UnitSourceGenerator;

import java.util.List;

public interface GeneratorService {
    List<UnitSourceGenerator> generate(ModelData modelData);
}
