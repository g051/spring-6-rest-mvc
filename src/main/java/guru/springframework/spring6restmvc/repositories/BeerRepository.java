package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerRepository extends JpaRepository<Beer, UUID> {

    List<Beer> findAllByBeerNameIsLikeIgnoreCase(String name);

    List<Beer> findAllByBeerStyle(BeerStyle style);

    List<Beer> findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle(String name, BeerStyle style);
}