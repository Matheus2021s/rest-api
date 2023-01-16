package br.com.mariah.restapi.model;

import lombok.*;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ParameterData {

    private String name;

    @Builder.Default
    private Boolean isPrimaryKey = false;

    private Class<?> dataType;

}
