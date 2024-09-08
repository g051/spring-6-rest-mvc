package guru.springframework.spring6restmvc.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
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
  void testSaveBeer() {
    Beer savedBeer = beerRepository.save(beer);
    beerRepository.flush();

    assertThat(savedBeer).isNotNull();
    assertThat(savedBeer.getId()).isNotNull();
  }

  @Test
  void testSaveBeerWithLongName() {
    assertThrows(ConstraintViolationException.class, () -> {
      beer.setBeerName("This is a very long beer name that will cause an exception to be thrown");
      beerRepository.save(beer);
      beerRepository.flush();
    });
  }
}