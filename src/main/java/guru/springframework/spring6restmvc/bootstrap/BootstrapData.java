package guru.springframework.spring6restmvc.bootstrap;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

  private final BeerRepository beerRepository;
  private final CustomerRepository customerRepository;

  @Override
  public void run(String... args) throws Exception {
    loadBeerData();
    loadCustomerData();
  }

  private void loadBeerData() {
    if(beerRepository.count() == 0) {
      createBeer("Galaxy Cat", BeerStyle.PALE_ALE, "12356", 12.99, 122);
      createBeer("Crank", BeerStyle.PALE_ALE, "12356222", 11.99, 392);
      createBeer("Sunshine City", BeerStyle.IPA, "12356", 13.99, 144);
    }
  }

  private void loadCustomerData() {
    if(customerRepository.count() == 0) {
      createCustomer("John Wick");
      createCustomer("Harry Potter");
      createCustomer("Evelyne Brochu");
    }
  }


  private void createBeer(String name, BeerStyle style, String upc, double price, int quantity) {
    createBeer(name, style, upc, BigDecimal.valueOf(price), quantity);
  }

  private void createBeer(String name, BeerStyle style, String upc, BigDecimal price, int quantity) {

    Beer beer = Beer.builder()
        .beerName(name)
        .beerStyle(style)
        .upc(upc)
        .price(price)
        .quantityOnHand(quantity)
        .createdDate(LocalDateTime.now())
        .updateDate(LocalDateTime.now())
        .build();

    beerRepository.save(beer);
  }

  private void createCustomer(String name) {

    Customer cusomter = Customer.builder()
        .name(name)
        .createdDate(LocalDateTime.now())
        .updateDate(LocalDateTime.now())
        .build();

    customerRepository.save(cusomter);
  }
}
