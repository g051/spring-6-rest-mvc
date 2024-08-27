package guru.springframework.spring6restmvc.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BootstrapDataTest {

  @Autowired
  private BeerRepository beerRepository;

  @Autowired
  private CustomerRepository customerRepository;

  BootstrapData bootstrapData;

  @BeforeEach
  void setUp() {
    bootstrapData = new BootstrapData(beerRepository, customerRepository);
  }

  @Test
  void testRun() throws Exception {
    bootstrapData.run(null);
    assertEquals(3, beerRepository.count());
    assertThat(customerRepository.count()).isEqualTo(3);
  }
}