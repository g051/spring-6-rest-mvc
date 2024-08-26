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
import guru.springframework.spring6restmvc.model.Beer;
import guru.springframework.spring6restmvc.services.BeerService;
import guru.springframework.spring6restmvc.services.BeerServiceImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

@WebMvcTest(BeerController.class)
class BeerControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  BeerService beerService;

  BeerServiceImpl beerServiceImpl;
  List<Beer> beerList;
  Beer beer;

  @Captor
  ArgumentCaptor<UUID> uuidArgumentCaptor;

  @Captor
  ArgumentCaptor<Beer> beerArgumentCaptor;

  @BeforeEach
  void setUp() {

    beerServiceImpl = new BeerServiceImpl();
    beerList = beerServiceImpl.listBeers();
    beer = beerList.get(0);
  }

  @Test
  void listBeers() throws Exception {

    given(beerService.listBeers()).willReturn(beerList);

    mockMvc.perform(get("/api/v1/beer")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()", is(beerList.size())));
  }

  @Test
  void getBeerById() throws Exception {

    UUID id = beer.getId();

    given(beerService.getBeerById(id)).willReturn(beer);

    mockMvc.perform(get("/api/v1/beer/" + id)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(id.toString())))
        .andExpect(jsonPath("$.beerName", is(beer.getBeerName())));
  }

  @Test
  void createBeer() throws Exception {

    beer.setVersion(null);
    beer.setId(null);

    given(beerService.saveNewBeer(any(Beer.class))).willReturn(beerList.get(1));

    mockMvc.perform(post("/api/v1/beer")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(beer)))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"));
  }

  @Test
  void updateBeerById() throws Exception {

    mockMvc.perform(put("/api/v1/beer/" + beer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(beer)))
        .andExpect(status().isNoContent());

    verify(beerService).updateBeerById(beer.getId(), beer);
  }

  @Test
  void patchBeerById() throws Exception {

    Map<String, Object> beerMap = new HashMap<>();
    beerMap.put("beerName", "New Beer Name");

    mockMvc.perform(patch("/api/v1/beer/" + beer.getId())
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

    mockMvc.perform(delete("/api/v1/beer/" + beer.getId()))
        .andExpect(status().isNoContent());

    verify(beerService).deleteBeerById(uuidArgumentCaptor.capture());

    assertThat(beer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
  }
}