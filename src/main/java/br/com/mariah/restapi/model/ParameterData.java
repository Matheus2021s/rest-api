package br.com.mariah.restapi.model;

import br.com.mariah.restapi.utils.StringUtils;
import lombok.*;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ParameterData {

    private String name;
    public String getCamelNameFirstLetterUpper(){
       return StringUtils.getCamelCaseFirstLetterUpper(this.name);
    }

    @Builder.Default
    private Boolean isPrimaryKey = false;

    private Class<?> dataType;

}
