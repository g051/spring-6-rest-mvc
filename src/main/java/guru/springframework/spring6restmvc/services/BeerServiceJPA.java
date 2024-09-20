package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {

  private final BeerRepository beerRepository;
  private final BeerMapper beerMapper;

  public final static int DEFAULT_PAGE_NUMBER = 0;
  public final static int DEFAULT_PAGE_SIZE = 25;
  public final static int MAX_PAGE_SIZE = 1000;

  @Override
  public Page<BeerDTO> listBeers(String name, BeerStyle style, Boolean showInventory,
      Integer pageNumber, Integer pageSize) {

    PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);

    Page<Beer> beerPage;
    if (StringUtils.hasText(name) && style != null) {
      beerPage = listBeersByNameAndStyle(name, style, pageRequest);
    } else if (StringUtils.hasText(name) && style == null) {
      beerPage = listBeersByName(name, pageRequest);
    } else if (!StringUtils.hasText(name) && style != null) {
      beerPage = listBeersByStyle(style, pageRequest);
    } else {
      beerPage = beerRepository.findAll(pageRequest);
    }

    if(showInventory != null && !showInventory) {
      beerPage.forEach(beer -> beer.setQuantityOnHand(null));
    }

    return beerPage.map(beerMapper::beerToBeerDTO);
  }

  public PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {

    final int queryPageNumber = (pageNumber != null && pageNumber > 0)
        ? pageNumber - 1
        : DEFAULT_PAGE_NUMBER;
    final int queryPageSize = pageSize != null
        ? (pageSize > MAX_PAGE_SIZE ? MAX_PAGE_SIZE: pageSize)
        : DEFAULT_PAGE_SIZE;

//    Sort sort = Sort.by(Sort.Direction.ASC, "beerName");
    Sort sort = Sort.by(Sort.Order.asc("beerName"));

    return PageRequest.of(queryPageNumber, queryPageSize, sort);
  }

  private Page<Beer> listBeersByNameAndStyle(String name, BeerStyle style, Pageable pageable) {
    return beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%" + name + "%", style, pageable);
  }

  private Page<Beer> listBeersByStyle(BeerStyle style, Pageable pageable) {
    return beerRepository.findAllByBeerStyle(style, pageable);
  }

  private Page<Beer> listBeersByName(String name, Pageable pageable) {
    return beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + name + "%", pageable);
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
