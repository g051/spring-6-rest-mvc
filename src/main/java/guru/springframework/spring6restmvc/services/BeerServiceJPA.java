package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

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
    return null;
  }

  @Override
  public void updateBeerById(UUID id, BeerDTO beer) {

  }

  @Override
  public void deleteBeerById(UUID id) {

  }

  @Override
  public void patchBeerById(UUID id, BeerDTO beer) {

  }
}
