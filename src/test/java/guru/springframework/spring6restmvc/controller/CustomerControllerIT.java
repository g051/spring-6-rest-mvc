package guru.springframework.spring6restmvc.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class CustomerControllerIT {

  @Autowired
  CustomerController customerController;

  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  CustomerMapper customerMapper;

  Customer customer;

  @BeforeEach
  void setUp() {
    customer = customerRepository.findAll().get(0);
  }

  @Test
  void listCustomers() {
    List< CustomerDTO> dtos = customerController.listCustomers();
    assertThat(dtos.size()).isEqualTo(customerRepository.count());
  }

  @Test
  @Rollback
  @Transactional
  void listCustomersEmpty() {
    customerRepository.deleteAll();

    List< CustomerDTO> dtos = customerController.listCustomers();
    assertThat(dtos.size()).isEqualTo(0);
  }

  @Test
  void getCustomerById() {
    Customer customer = customerRepository.findAll().get(0);
    CustomerDTO dto = customerController.getCustomerById(customer.getId());
    assertThat(dto).isNotNull();
  }

  @Test
  void getCustomerByIdNotFound() {
    assertThrows(NotFoundException.class, () -> customerController.getCustomerById(UUID.randomUUID()));
  }

  @Rollback
  @Transactional
  @Test
  void createCustomer() {
    CustomerDTO customerDTO = CustomerDTO.builder().name("New Customer").build();
    var responseEntity = customerController.createCustomer(customerDTO);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    URI location = responseEntity.getHeaders().getLocation();
    assertThat(location).isNotNull();

    UUID savedCustomerId = UUID.fromString(StringUtils.substringAfterLast(location.getPath(), "/"));
    Customer customer = customerRepository.findById(savedCustomerId).get();
    assertThat(customer).isNotNull();
  }

  @Rollback
  @Transactional
  @Test
  void updateCustomerById() {
    final String newName = "New Name";
    CustomerDTO customerDTO = customerMapper.customerToCustomerDTO(customer);
    customerDTO.setId(null);
    customerDTO.setVersion(null);
    customerDTO.setName(newName);

    var responseEntity = customerController.updateCustomerById(customer.getId(), customerDTO);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    customerRepository.findById(customer.getId()).ifPresent(
        updatedCustomer -> assertThat(updatedCustomer.getName()).isEqualTo(newName));
  }

  @Test
  void updateCustomerByIdNotFound() {
    assertThrows(NotFoundException.class, () -> 
        customerController.updateCustomerById(UUID.randomUUID(), CustomerDTO.builder().build()));
  }

  @Rollback
  @Transactional
  @Test
  void patchCustomerById() {
    final String newName = "New Name";
    CustomerDTO customerDTO = CustomerDTO.builder().name(newName).build();

    var responseEntity = customerController.patchCustomerById(customer.getId(), customerDTO);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    customerRepository.findById(customer.getId()).ifPresent(updatedCustomer -> assertThat(updatedCustomer.getName()).isEqualTo(newName));
  }

  @Test
  void patchCustomerByIdNotFound() {
    assertThrows(NotFoundException.class, () -> customerController.patchCustomerById(UUID.randomUUID(), CustomerDTO.builder().build()));
  }

  @Rollback
  @Transactional
  @Test
  void deleteCustomerById() {
    var responseEntity = customerController.deleteCustomerById(customer.getId());
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(customerRepository.findById(customer.getId())).isEmpty();
  }

  @Test
  void deleteCustomerByIdNotFound() {
    assertThrows(NotFoundException.class, () -> customerController.deleteCustomerById(UUID.randomUUID()));
  }
}