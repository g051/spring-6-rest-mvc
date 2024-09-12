package guru.springframework.spring6restmvc.services;

import com.opencsv.bean.CsvToBeanBuilder;
import guru.springframework.spring6restmvc.model.BeerCSVRecord;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BeerCsvServiceImpl implements BeerCsvService {

  @Override
  public List<BeerCSVRecord> convertCSV(File file) {
    try {
      return new CsvToBeanBuilder<BeerCSVRecord>(new FileReader(file))
          .withType(BeerCSVRecord.class)
          .build().parse();
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
