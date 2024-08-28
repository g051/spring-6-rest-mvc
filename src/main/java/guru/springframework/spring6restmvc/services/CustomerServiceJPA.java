package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
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
public class CustomerServiceJPA implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  @Override
  public Optional<CustomerDTO> getCustomerById(UUID uuid) {
    return Optional.ofNullable(
        customerMapper.customerToCustomerDTO(
            customerRepository.findById(uuid).orElse(null)
        )
    );
  }

  @Override
  public List<CustomerDTO> listCustomers() {
    return customerRepository.findAll()
        .stream()
        .map(customerMapper::customerToCustomerDTO)
        .toList();
  }

  @Override
  public CustomerDTO saveNewCustomer(CustomerDTO customer) {
    return customerMapper.customerToCustomerDTO(
        customerRepository.save(
            customerMapper.customerDTOToCustomer(customer)
        )
    );
  }

  @Override
  public Optional<CustomerDTO> updateCustomerById(UUID customerId, CustomerDTO customer) {

    var atomicReference = new AtomicReference<Optional<CustomerDTO>>();

    customerRepository.findById(customerId).ifPresentOrElse(existing -> {
      existing.setName(customer.getName());
      atomicReference.set(Optional.of(
          customerMapper.customerToCustomerDTO(
              customerRepository.save(existing)
          )
      ));
    }, () -> {
      atomicReference.set(Optional.empty());
    });

    return atomicReference.get();
  }

  @Override
  public Boolean deleteCustomerById(UUID id) {
    if(customerRepository.existsById(id)) {
      customerRepository.deleteById(id);
      return true;
    }
    return false;
  }

  @Override
  public Optional<CustomerDTO> patchCustomerById(UUID customerId, CustomerDTO customer) {

    var atomicReference = new AtomicReference<Optional<CustomerDTO>>();

    customerRepository.findById(customerId).ifPresentOrElse(existing -> {
      if (StringUtils.hasText(customer.getName())) {
        existing.setName(customer.getName());
      }
      atomicReference.set(Optional.of(
          customerMapper.customerToCustomerDTO(
              customerRepository.save(existing)
          )
      ));
    }, () -> {
      atomicReference.set(Optional.empty());
    });

    return atomicReference.get();
  }
}
