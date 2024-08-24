package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class BeerServiceImpl implements BeerService {

  private Map<UUID, Beer> beerMap;

  public BeerServiceImpl() {
    beerMap = new HashMap<>();

    beerMap.put(getUUID(), createBeer("Galaxy Cat", BeerStyle.PALE_ALE, "12356", 12.99, 122));
    beerMap.put(getUUID(), createBeer("Crank", BeerStyle.PALE_ALE, "12356222", 11.99, 392));
    beerMap.put(getUUID(), createBeer("Sunshine City", BeerStyle.IPA, "12356", 13.99, 144));
  }

  private UUID getUUID() {
    return UUID.randomUUID();
  }

  private Beer createBeer(String name, BeerStyle style, String upc, double price, int quantity) {
    return createBeer(name, style, upc, BigDecimal.valueOf(price), quantity);
  }

  private Beer createBeer(String name, BeerStyle style, String upc, BigDecimal price, int quantity) {

    return Beer.builder()
        .id(getUUID())
        .version(1)
        .beerName(name)
        .beerStyle(style)
        .upc(upc)
        .price(price)
        .quantityOnHand(quantity)
        .createdDate(LocalDateTime.now())
        .updateDate(LocalDateTime.now())
        .build();
  }

  @Override
  public List<Beer> listBeers() {
    return new ArrayList<>(beerMap.values());
  }

  @Override
  public Beer getBeerById(UUID id) {

    log.debug("Get Beer by Id - in service. Id: " + id.toString());

    return beerMap.get(id);
  }

  @Override
  public Beer saveNewBeer(Beer beer) {

    Beer savedBeer = createBeer(beer.getBeerName(), beer.getBeerStyle(), beer.getUpc(), beer.getPrice(), beer.getQuantityOnHand());
    beerMap.put(savedBeer.getId(), savedBeer);

    return savedBeer;
  }

  @Override
  public void updateBeerById(UUID id, Beer beer) {

      Beer existing = beerMap.get(id);
      existing.setVersion(existing.getVersion() + 1);
      existing.setBeerName(beer.getBeerName());
      existing.setBeerStyle(beer.getBeerStyle());
      existing.setPrice(beer.getPrice());
      existing.setUpc(beer.getUpc());
      existing.setQuantityOnHand(beer.getQuantityOnHand());
      existing.setUpdateDate(LocalDateTime.now());

      beerMap.put(id, existing);
  }

  @Override
  public void deleteById(UUID id) {
    beerMap.remove(id);
  }

  @Override
  public void patchBeerById(UUID id, Beer beer) {

    Beer existing = beerMap.get(id);
    boolean patched = false;

    if(StringUtils.hasText(beer.getBeerName())) {
      existing.setBeerName(beer.getBeerName());
      patched = true;
    }

    if(beer.getBeerStyle() != null) {
      existing.setBeerStyle(beer.getBeerStyle());
      patched = true;
    }

    if(StringUtils.hasText(beer.getUpc())) {
      existing.setUpc(beer.getUpc());
      patched = true;
    }

    if(beer.getPrice() != null) {
      existing.setPrice(beer.getPrice());
      patched = true;
    }

    if(beer.getQuantityOnHand() != null) {
      existing.setQuantityOnHand(beer.getQuantityOnHand());
      patched = true;
    }

    if(patched) {
      existing.setVersion(existing.getVersion() + 1);
      existing.setUpdateDate(LocalDateTime.now());
      beerMap.put(id, existing);
    }
  }
}

















