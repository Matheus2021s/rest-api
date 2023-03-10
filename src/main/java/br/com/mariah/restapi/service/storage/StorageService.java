package br.com.mariah.restapi.service.storage;

import br.com.mariah.restapi.utils.ResourceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.burningwave.core.classes.UnitSourceGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Log4j2
@Service
public class StorageService {

    private final ResourceUtils resourceUtils;

    public void store(List<UnitSourceGenerator> units) {

        String path = String.format("%s/%s", this.resourceUtils.getApplicationBasePath(), resourceUtils.getJavaResourcesPath());
        units.forEach(unitSourceGenerator -> {
            log.info("Saving on {}: , fie: {}", path, unitSourceGenerator.toString());
            unitSourceGenerator.storeToClassPath(path);

        });
    }
}
