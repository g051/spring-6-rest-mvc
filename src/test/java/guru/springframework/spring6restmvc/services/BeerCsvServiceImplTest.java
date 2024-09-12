package guru.springframework.spring6restmvc.services;

import static org.assertj.core.api.Assertions.assertThat;

import guru.springframework.spring6restmvc.model.BeerCSVRecord;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

@SpringBootTest
class BeerCsvServiceImplTest {

  @Autowired
  BeerCsvService beerCsvService;

  @Test
  void convertCSV() throws FileNotFoundException {
    File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
    List<BeerCSVRecord> recs = beerCsvService.convertCSV(file);

    System.out.println(recs.size());
    assertThat(recs.size()).isGreaterThan(0);
  }
}