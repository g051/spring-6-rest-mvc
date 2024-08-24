package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.Customer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by jt, Spring Framework Guru.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private Map<UUID, Customer> customerMap;

    private final String[] custNames = {"Customer 1", "Customer 2", "Customer 3"};

    public CustomerServiceImpl() {

        customerMap = new HashMap<>();

        for (String name : custNames) {
            Customer customer = createCustomer(name);
            customerMap.put(customer.getId(), customer);
        }
    }

    private Customer createCustomer(String name) {

        return Customer.builder()
            .id(UUID.randomUUID())
            .name(name)
            .version(1)
            .createdDate(LocalDateTime.now())
            .updateDate(LocalDateTime.now())
            .build();
    }

    @Override
    public Customer getCustomerById(UUID uuid) {
        return customerMap.get(uuid);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customerMap.values());
    }

    @Override
    public Customer saveNewCustomer(Customer customer) {
        Customer savedCustomer = createCustomer(customer.getName());
        customerMap.put(savedCustomer.getId(), savedCustomer);

        return savedCustomer;
    }
}











