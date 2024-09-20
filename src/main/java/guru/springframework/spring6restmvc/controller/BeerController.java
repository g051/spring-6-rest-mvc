package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BeerController {

  public static final String BEER_PATH = "/api/v1/beer";
  public static final String BEER_ID_PATH = BEER_PATH + "/{beerId}";

  private final BeerService beerService;

  @GetMapping(BEER_PATH)
  public Page<BeerDTO> listBeers(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) BeerStyle style,
      @RequestParam(required = false) Boolean showInventory,
      @RequestParam(required = false) Integer pageNumber,
      @RequestParam(required = false) Integer pageSize) {
    return beerService.listBeers(name, style, showInventory, pageNumber, pageSize);
  }

  @GetMapping(BEER_ID_PATH)
  public BeerDTO getBeerById(@PathVariable("beerId") UUID id) {
    log.debug("Get Beer by Id - in controller");
    return beerService.getBeerById(id).orElseThrow(NotFoundException::new);
  }

  @PostMapping(BEER_PATH)
  //@RequestMapping(method = RequestMethod.POST)
  public ResponseEntity createBeer(@Validated @RequestBody BeerDTO beer) {

    BeerDTO savedBeer = beerService.saveNewBeer(beer);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Location", "/api/v1/beer/" + savedBeer.getId().toString());

    return new ResponseEntity(headers, HttpStatus.CREATED);
  }

  @PutMapping(BEER_ID_PATH)
  public ResponseEntity updateBeerById(
      @PathVariable("beerId") UUID id,
      @Validated @RequestBody BeerDTO beer) {
    beerService.updateBeerById(id, beer).orElseThrow(NotFoundException::new);
    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }

  @PatchMapping(BEER_ID_PATH)
  public ResponseEntity patchBeerById(@PathVariable("beerId") UUID id, @RequestBody BeerDTO beer) {
    beerService.patchBeerById(id, beer).orElseThrow(NotFoundException::new);
    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping(BEER_ID_PATH)
  public ResponseEntity deleteBeerById(@PathVariable("beerId") UUID id) {
    if (!beerService.deleteBeerById(id)) {
      throw new NotFoundException();
    }
    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }
}
