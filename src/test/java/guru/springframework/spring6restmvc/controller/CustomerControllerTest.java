package guru.springframework.spring6restmvc.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.Customer;
import guru.springframework.spring6restmvc.services.CustomerService;
import guru.springframework.spring6restmvc.services.CustomerServiceImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  CustomerService customerService;

  CustomerServiceImpl customerServiceImpl;
  List<Customer> customerList;
  Customer customer;

  @Captor
  ArgumentCaptor<UUID> uuidArgumentCaptor;

  @Captor
  ArgumentCaptor<Customer> customerArgumentCaptor;

  @BeforeEach
  void setUp() {

    customerServiceImpl = new CustomerServiceImpl();
    customerList = customerServiceImpl.listCustomers();
    customer = customerList.get(0);
  }

  @Test
  void listCustomers() throws Exception {

    given(customerService.listCustomers()).willReturn(customerList);

    mockMvc.perform(get(CustomerController.CUSTOMER_PATH)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()", is(customerList.size())));
  }

  @Test
  void getCustomerById() throws Exception {

    UUID id = customer.getId();

    given(customerService.getCustomerById(id)).willReturn(Optional.of(customer));

    mockMvc.perform(get(CustomerController.CUSTOMER_ID_PATH,  id)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(id.toString())))
        .andExpect(jsonPath("$.name", is(customer.getName())));
  }

  @Test
  void getCustomerByIdNotFound() throws Exception {

//    given(customerService.getCustomerById(any(UUID.class))).willReturn(Optional.empty());

    mockMvc.perform(get(CustomerController.CUSTOMER_ID_PATH, UUID.randomUUID()))
        .andExpect(status().isNotFound());
  }

  @Test
  void createCustomer() throws Exception {

    customer.setVersion(null);
    customer.setId(null);

    given(customerService.saveNewCustomer(any(Customer.class))).willReturn(customerList.get(1));

    mockMvc.perform(post(CustomerController.CUSTOMER_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(customer)))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"));
  }

  @Test
  void updateCustomerById() throws Exception {

    mockMvc.perform(put(CustomerController.CUSTOMER_ID_PATH,  customer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(customer)))
        .andExpect(status().isNoContent());

    verify(customerService).updateCustomerById(customer.getId(), customer);
  }

  @Test
  void patchCustomerById() throws Exception {

    Map<String, Object> custMap = new HashMap<>();
    custMap.put("name", "New Name");

    mockMvc.perform(patch(CustomerController.CUSTOMER_ID_PATH,  customer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(custMap)))
        .andExpect(status().isNoContent());

    verify(customerService).patchCustomerById(uuidArgumentCaptor.capture(), customerArgumentCaptor.capture());

    assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
    assertThat(custMap.get("name")).isEqualTo(customerArgumentCaptor.getValue().getName());
  }

  @Test
  void deleteCustomerById() throws Exception {

    mockMvc.perform(delete(CustomerController.CUSTOMER_ID_PATH,  customer.getId()))
        .andExpect(status().isNoContent());

    // Option 1: compare UUID directly
    verify(customerService).deleteCustomerById(customer.getId());
    // Option 2: use mockito eq method for comparison
    verify(customerService).deleteCustomerById(eq(customer.getId()));

    // Option 3: user ArgumentCaptor which can do further analysis (e.g. assertion) after verify()
    verify(customerService).deleteCustomerById(uuidArgumentCaptor.capture());

    assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
  }
}