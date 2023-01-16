package br.com.mariah.restapi;

import br.com.mariah.restapi.model.ModelData;
import br.com.mariah.restapi.model.ParameterData;
import br.com.mariah.restapi.service.generator.ClassGeneratorsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@RequiredArgsConstructor
@SpringBootApplication
public class RestApiApplication implements CommandLineRunner {

    private final ClassGeneratorsService classGeneratorsService;

    public static void main(String[] args) {
        SpringApplication.run(RestApiApplication.class, args);
    }

    @Override
    public void run(String... args) {

        ModelData modelData = ModelData.builder()
                .name("User")
                .parameters(List.of(
                                ParameterData.builder()
                                        .name("login")
                                        .dataType(String.class)
                                        .isPrimaryKey(true)
                                        .build()
                                ,
                                ParameterData.builder()
                                        .name("password")
                                        .dataType(String.class)
                                        .build(),

                                ParameterData.builder()
                                        .name("email")
                                        .dataType(String.class)
                                        .build()
                        )
                )
                .build();

        this.classGeneratorsService.generate(modelData);
    }
}
