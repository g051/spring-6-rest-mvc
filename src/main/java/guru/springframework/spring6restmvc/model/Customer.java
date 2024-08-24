package guru.springframework.spring6restmvc.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseModel {

    private String name;
    private Integer version;
}
