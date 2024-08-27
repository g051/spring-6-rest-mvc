package guru.springframework.spring6restmvc.controller;

import static org.assertj.core.api.Assertions.assertThat;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class BeerControllerIT {

  @Autowired
  BeerController beerController;

  @Autowired
  BeerRepository beerRepository;

  @BeforeEach
  void setUp() {
  }

  @Test
  void listBeers() {
    List<BeerDTO> dtos = beerController.listBeers();
    assertThat(dtos.size()).isEqualTo(3);
  }

  @Rollback
  @Transactional
  @Test
  void listBeersEmpty() {
    beerRepository.deleteAll();

    List<BeerDTO> dtos = beerController.listBeers();
    assertThat(dtos.size()).isEqualTo(0);
  }

  @Test
  void getBeerById() {
  }

  @Test
  void createBeer() {
  }

  @Test
  void updateBeerById() {
  }

  @Test
  void patchBeerById() {
  }

  @Test
  void deleteBeerById() {
  }
}