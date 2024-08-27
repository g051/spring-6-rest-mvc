package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.CustomerDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.util.StringUtils;

/**
 * Created by jt, Spring Framework Guru.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

  private Map<UUID, CustomerDTO> customerMap;

  private final String[] custNames = {"Customer 1", "Customer 2", "Customer 3"};

  public CustomerServiceImpl() {

    customerMap = new HashMap<>();

    for (String name : custNames) {
      CustomerDTO customer = createCustomer(name);
      customerMap.put(customer.getId(), customer);
    }
  }

  private CustomerDTO createCustomer(String name) {

    return CustomerDTO.builder()
        .id(UUID.randomUUID())
        .name(name)
        .version(1)
        .createdDate(LocalDateTime.now())
        .updateDate(LocalDateTime.now())
        .build();
  }

  @Override
  public Optional<CustomerDTO> getCustomerById(UUID uuid) {
    return Optional.of(customerMap.get(uuid));
  }

  @Override
  public List<CustomerDTO> listCustomers() {
    return new ArrayList<>(customerMap.values());
  }

  @Override
  public CustomerDTO saveNewCustomer(CustomerDTO customer) {
    CustomerDTO savedCustomer = createCustomer(customer.getName());
    customerMap.put(savedCustomer.getId(), savedCustomer);

    return savedCustomer;
  }

  @Override
  public void updateCustomerById(UUID customerId, CustomerDTO customer) {
    CustomerDTO existing = customerMap.get(customerId);
    existing.setName(customer.getName());
    customerMap.put(customerId, existing);
  }

  @Override
  public void deleteCustomerById(UUID customerId) {
    customerMap.remove(customerId);
  }

  @Override
  public void patchCustomerById(UUID customerId, CustomerDTO customer) {
    CustomerDTO existing = customerMap.get(customerId);

    if (StringUtils.hasText(existing.getName())) {
      existing.setName(customer.getName());
      existing.setVersion(existing.getVersion() + 1);
      existing.setUpdateDate(LocalDateTime.now());
      customerMap.put(customerId, existing);
    }
  }
}











