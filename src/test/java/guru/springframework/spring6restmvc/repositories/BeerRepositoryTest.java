package guru.springframework.spring6restmvc.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import guru.springframework.spring6restmvc.bootstrap.BootstrapData;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerCsvServiceImpl;
import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class BeerRepositoryTest {

  @Autowired
  BeerRepository beerRepository;

  Beer beer;

  @BeforeEach
  void setUp() {
    beer = Beer.builder()
        .beerName("My Beer")
        .beerStyle(BeerStyle.PALE_ALE)
        .upc("123123123123")
        .price(new BigDecimal("12.99"))
        .build();
  }

  @Test
  void saveBeer() {
    Beer savedBeer = beerRepository.save(beer);
    beerRepository.flush();

    assertThat(savedBeer).isNotNull();
    assertThat(savedBeer.getId()).isNotNull();
  }

  @Test
  void saveBeerWithLongName() {
    assertThrows(ConstraintViolationException.class, () -> {
      beer.setBeerName("This is a very long beer name that will cause an exception to be thrown");
      beerRepository.save(beer);
      beerRepository.flush();
    });
  }

  @Test
  void findAllByBeerNameIsLikeIgnoreCase() {
    List<Beer> list = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%IPA%");
    assertThat(list.size()).isEqualTo(336);
  }
}