package guru.springframework.spring6restmvc.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class BaseModel {

  private UUID id;
  private LocalDateTime createdDate;
  private LocalDateTime updateDate;
}
