package guru.springframework.spring6restmvc.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class BeerControllerIT {

  @Autowired
  BeerController beerController;

  @Autowired
  BeerRepository beerRepository;

  @Autowired
  BeerMapper beerMapper;

  Beer beer;

  @BeforeEach
  void setUp() {
    beer = beerRepository.findAll().get(0);
  }

  @Test
  void listBeers() {
    List<BeerDTO> dtos = beerController.listBeers();
    assertThat(dtos.size()).isEqualTo(beerRepository.count());
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
    BeerDTO dto = beerController.getBeerById(beer.getId());
    assertThat(dto).isNotNull();
  }

  @Test
  void getBeerByIdNotFound() {
    assertThrows(NotFoundException.class, () -> {
      beerController.getBeerById(UUID.randomUUID());
    });
  }

  @Rollback
  @Transactional
  @Test
  void createBeer() {
    BeerDTO beerDto = BeerDTO.builder().beerName("New Beer").build();
    ResponseEntity responseEntity = beerController.createBeer(beerDto);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    URI location = responseEntity.getHeaders().getLocation();
    assertThat(location).isNotNull();

    UUID savedBeerId = UUID.fromString(StringUtils.substringAfterLast(location.getPath(), "/"));
    Optional<Beer> beer = beerRepository.findById(savedBeerId);
    assertThat(beer).isNotNull();
  }

  @Rollback
  @Transactional
  @Test
  void updateBeerById() {
    final String beerName = "New Beer Name";
    BeerDTO beerDTO = beerMapper.beerToBeerDTO(beer);
    beerDTO.setId(null);
    beerDTO.setVersion(null);
    beerDTO.setBeerName(beerName);

    ResponseEntity responseEntity = beerController.updateBeerById(beer.getId(), beerDTO);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    beerRepository.findById(beer.getId()).ifPresent(updatedBear -> {
      assertThat(updatedBear.getBeerName()).isEqualTo(beerName);
    });
  }

  @Test
  void updateBeerByIdNotFound() {
    assertThrows(NotFoundException.class, () -> {
      beerController.updateBeerById(UUID.randomUUID(), BeerDTO.builder().build());
    });
  }

  @Rollback
  @Transactional
  @Test
  void patchBeerById() {
    final String beerName = "New Beer Name";
    BeerDTO beerDTO = BeerDTO.builder().beerName(beerName).build();

    ResponseEntity responseEntity = beerController.patchBeerById(beer.getId(), beerDTO);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    beerRepository.findById(beer.getId()).ifPresent(updatedBeer -> {
      assertThat(updatedBeer.getBeerName()).isEqualTo(beerName);
    });
  }

  @Test
  void patchBeerByIdNotFound() {
    assertThrows(NotFoundException.class, () -> {
      beerController.patchBeerById(UUID.randomUUID(), BeerDTO.builder().build());
    });
  }

  @Rollback
  @Transactional
  @Test
  void deleteBeerById() {
    ResponseEntity responseEntity = beerController.deleteBeerById(beer.getId());
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(beerRepository.findById(beer.getId())).isEmpty();
  }

  @Test
  void deleteBeerByIdNotFound() {
    assertThrows(NotFoundException.class, () -> {
      beerController.deleteBeerById(UUID.randomUUID());
    });
  }
}