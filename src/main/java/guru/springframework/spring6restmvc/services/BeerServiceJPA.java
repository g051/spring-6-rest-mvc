package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {

  private final BeerRepository beerRepository;
  private final BeerMapper beerMapper;

  @Override
  public List<BeerDTO> listBeers() {
    return beerRepository.findAll()
        .stream()
        .map(beerMapper::beerToBeerDTO)
//        .collect(Collectors.toList());
        .toList();
  }

  @Override
  public Optional<BeerDTO> getBeerById(UUID id) {
    return Optional.ofNullable(
        beerMapper.beerToBeerDTO(
            beerRepository.findById(id).orElse(null)
        )
    );
  }

  @Override
  public BeerDTO saveNewBeer(BeerDTO beer) {
    return beerMapper.beerToBeerDTO(
        beerRepository.save(beerMapper.BeerDTOToBeer(beer))
    );
  }

  @Override
  public Optional<BeerDTO> updateBeerById(UUID id, BeerDTO beer) {

    var atomicReference = new AtomicReference<Optional<BeerDTO>>();

    beerRepository.findById(id).ifPresentOrElse(existing -> {
      existing.setBeerName(beer.getBeerName());
      existing.setBeerStyle(beer.getBeerStyle());
      existing.setPrice(beer.getPrice());
      existing.setUpc(beer.getUpc());
      existing.setQuantityOnHand(beer.getQuantityOnHand());

      atomicReference.set(Optional.of(
          beerMapper.beerToBeerDTO(
              beerRepository.save(existing))));
    }, () -> {
      atomicReference.set(Optional.empty());
    });

    return atomicReference.get();
  }

  @Override
  public Boolean deleteBeerById(UUID id) {
    if (beerRepository.existsById(id)) {
      beerRepository.deleteById(id);
      return true;
    }
    return false;
  }

  @Override
  public Optional<BeerDTO> patchBeerById(UUID id, BeerDTO beer) {

    var atomicReference = new AtomicReference<Optional<BeerDTO>>();

    beerRepository.findById(id).ifPresentOrElse(existing -> {

      if (StringUtils.hasText(beer.getBeerName())) {
        existing.setBeerName(beer.getBeerName());
      }

      if (beer.getBeerStyle() != null) {
        existing.setBeerStyle(beer.getBeerStyle());
      }

      if (StringUtils.hasText(beer.getUpc())) {
        existing.setUpc(beer.getUpc());
      }

      if (beer.getPrice() != null) {
        existing.setPrice(beer.getPrice());
      }

      if (beer.getQuantityOnHand() != null) {
        existing.setQuantityOnHand(beer.getQuantityOnHand());
      }

      atomicReference.set(Optional.of(
          beerMapper.beerToBeerDTO(
              beerRepository.save(existing))));
    }, () -> {
      atomicReference.set(Optional.empty());
    });

    return atomicReference.get();
  }
}
