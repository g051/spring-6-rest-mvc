package guru.springframework.spring6restmvc.bootstrap;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import guru.springframework.spring6restmvc.services.BeerCsvService;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

  private final BeerRepository beerRepository;
  private final CustomerRepository customerRepository;
  private final BeerCsvService beerCsvService;

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    loadBeerData();
    loadCsvData();
    loadCustomerData();
  }

  private void loadBeerData() {
    if (beerRepository.count() == 0) {
      createBeer("Galaxy Cat", BeerStyle.PALE_ALE, "12356", 12.99, 122);
      createBeer("Crank", BeerStyle.PALE_ALE, "12356222", 11.99, 392);
      createBeer("Sunshine City", BeerStyle.IPA, "12356", 13.99, 144);
    }
  }

  private void loadCustomerData() {
    if (customerRepository.count() == 0) {
      createCustomer("John Wick");
      createCustomer("Harry Potter");
      createCustomer("Evelyne Brochu");
    }
  }

  private void loadCsvData() throws FileNotFoundException {
    if (beerRepository.count() < 10) {
      File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
      var recs = beerCsvService.convertCSV(file);

      recs.forEach(rec -> {
        BeerStyle beerStyle = switch (rec.getStyle()) {
          case "American Pale Lager" -> BeerStyle.LAGER;
          case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale",
               "American Blonde Ale" -> BeerStyle.ALE;
          case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> BeerStyle.IPA;
          case "American Porter" -> BeerStyle.PORTER;
          case "Oatmeal Stout", "American Stout" -> BeerStyle.STOUT;
          case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
          case "Fruit / Vegetable Beer", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
          case "English Pale Ale" -> BeerStyle.PALE_ALE;
          default -> BeerStyle.PILSNER;
        };

        createBeer(
            StringUtils.abbreviate(rec.getBeer(), 50),
            beerStyle,
            rec.getId().toString(),
            rec.getOunces(),
            rec.getCount());
      });
    }
  }

  private void createBeer(String name, BeerStyle style, String upc, double price, int quantity) {
    createBeer(name, style, upc, BigDecimal.valueOf(price), quantity);
  }

  private void createBeer(String name, BeerStyle style, String upc, BigDecimal price,
      int quantity) {

    Beer beer = Beer.builder()
        .beerName(name)
        .beerStyle(style)
        .upc(upc)
        .price(price)
        .quantityOnHand(quantity)
        .build();

    beerRepository.save(beer);
  }

  private void createCustomer(String name) {

    Customer customer = Customer.builder()
        .name(name)
        .build();

    customerRepository.save(customer);
  }
}
