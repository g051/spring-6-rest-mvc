package guru.springframework.spring6restmvc.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class BeerControllerIT {

  @Autowired
  BeerController beerController;

  @Autowired
  BeerRepository beerRepository;

  @Autowired
  BeerMapper beerMapper;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  WebApplicationContext wac;

  MockMvc mockMvc;

  Beer beer;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
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
  void getBeer() {
    BeerDTO dto = beerController.getBeerById(beer.getId());
    assertThat(dto).isNotNull();
  }

  @Test
  void getBeerNotFound() {
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
  void updateBeer() {
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
  void updateBeerNotFound() {
    assertThrows(NotFoundException.class, () -> {
      beerController.updateBeerById(UUID.randomUUID(), BeerDTO.builder().build());
    });
  }

  @Rollback
  @Transactional
  @Test
  void patchBeer() {
    final String beerName = "New Beer Name";
    BeerDTO beerDTO = BeerDTO.builder().beerName(beerName).build();

    ResponseEntity responseEntity = beerController.patchBeerById(beer.getId(), beerDTO);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    beerRepository.findById(beer.getId()).ifPresent(updatedBeer -> {
      assertThat(updatedBeer.getBeerName()).isEqualTo(beerName);
    });
  }

  @Test
  void patchBeerNotFound() {
    assertThrows(NotFoundException.class, () -> {
      beerController.patchBeerById(UUID.randomUUID(), BeerDTO.builder().build());
    });
  }

  @Test
  void patchBeerWithBadName() throws Exception {

    Map<String, Object> beerMap = new HashMap<>();
    beerMap.put("beerName", "New Beer Name".repeat(10));

    MvcResult result = mockMvc.perform(patch(BeerController.BEER_ID_PATH, beer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(beerMap)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.length()", is(1)))
        .andReturn();

    System.out.println(result.getResponse().getContentAsString());
  }

  @Rollback
  @Transactional
  @Test
  void deleteBeer() {
    ResponseEntity responseEntity = beerController.deleteBeerById(beer.getId());
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(beerRepository.findById(beer.getId())).isEmpty();
  }

  @Test
  void deleteBeerNotFound() {
    assertThrows(NotFoundException.class, () -> {
      beerController.deleteBeerById(UUID.randomUUID());
    });
  }
}