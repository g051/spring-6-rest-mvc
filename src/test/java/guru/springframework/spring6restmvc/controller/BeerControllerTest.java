package guru.springframework.spring6restmvc.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.services.BeerService;
import guru.springframework.spring6restmvc.services.BeerServiceImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  BeerService beerService;

  BeerServiceImpl beerServiceImpl;
  List<BeerDTO> beerList;
  BeerDTO beer;

  @Captor
  ArgumentCaptor<UUID> uuidArgumentCaptor;

  @Captor
  ArgumentCaptor<BeerDTO> beerArgumentCaptor;

  @BeforeEach
  void setUp() {

    beerServiceImpl = new BeerServiceImpl();
    beerList = beerServiceImpl.listBeers(null, null, false);
    beer = beerList.get(0);
  }

  @Test
  void listBeers() throws Exception {

    given(beerService.listBeers(null, null, null)).willReturn(beerList);

    mockMvc.perform(get(BeerController.BEER_PATH)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()", is(beerList.size())));
  }

  @Test
  void getBeerById() throws Exception {

    UUID id = beer.getId();

    given(beerService.getBeerById(id)).willReturn(Optional.of(beer));

    mockMvc.perform(get(BeerController.BEER_ID_PATH, id)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(id.toString())))
        .andExpect(jsonPath("$.beerName", is(beer.getBeerName())));
  }

  @Test
  void getBeerByIdNotFound() throws Exception {

    given(beerService.getBeerById(any(UUID.class))).willReturn(Optional.empty());

    mockMvc.perform(get(BeerController.BEER_ID_PATH, UUID.randomUUID()))
        .andExpect(status().isNotFound());
  }

  @Test
  void createBeer() throws Exception {

    beer.setVersion(null);
    beer.setId(null);

    given(beerService.saveNewBeer(any(BeerDTO.class))).willReturn(beerList.get(1));

    mockMvc.perform(post(BeerController.BEER_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(beer)))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"));
  }

  @Test
  void createBeerWithNullName() throws Exception {

    beer.setBeerName(null);
    beer.setPrice(null);
    beer.setBeerStyle(null);
    beer.setUpc(" ");

    given(beerService.saveNewBeer(any(BeerDTO.class))).willReturn(beerList.get(1));

    MvcResult mvcResult = mockMvc.perform(post(BeerController.BEER_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(beer)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.length()", is(4)))
        .andReturn();

    System.out.println(mvcResult.getResponse().getContentAsString());
  }

  @Test
  void updateBeerById() throws Exception {

    given(beerService.updateBeerById(beer.getId(), beer)).willReturn(Optional.of(beer));

    mockMvc.perform(put(BeerController.BEER_ID_PATH, beer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(beer)))
        .andExpect(status().isNoContent());

    verify(beerService).updateBeerById(beer.getId(), beer);
  }

  @Test
  void updateBeerByIdWithNullName() throws Exception {

    given(beerService.updateBeerById(beer.getId(), beer)).willReturn(Optional.of(beer));

    beer.setBeerName(null);

    mockMvc.perform(put(BeerController.BEER_ID_PATH, beer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(beer)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.length()", is(1)));
  }

  @Test
  void patchBeerById() throws Exception {

    Map<String, Object> beerMap = new HashMap<>();
    beerMap.put("beerName", "New Beer Name");

    given(beerService.patchBeerById(any(), any())).willReturn(Optional.of(beer));

    mockMvc.perform(patch(BeerController.BEER_ID_PATH, beer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(beerMap)))
        .andExpect(status().isNoContent());

    verify(beerService).patchBeerById(uuidArgumentCaptor.capture(), beerArgumentCaptor.capture());

    assertThat(beer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
    assertThat(beerMap.get("beerName")).isEqualTo(beerArgumentCaptor.getValue().getBeerName());
  }

  @Test
  void deleteBeerById() throws Exception {

    given(beerService.deleteBeerById(beer.getId())).willReturn(true);

    mockMvc.perform(delete(BeerController.BEER_ID_PATH, beer.getId()))
        .andExpect(status().isNoContent());

    verify(beerService).deleteBeerById(uuidArgumentCaptor.capture());

    assertThat(beer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
  }
}