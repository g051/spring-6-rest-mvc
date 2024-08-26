package guru.springframework.spring6restmvc.controller;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.Customer;
import guru.springframework.spring6restmvc.services.CustomerService;
import guru.springframework.spring6restmvc.services.CustomerServiceImpl;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

  @BeforeEach
  void setUp() {

    customerServiceImpl = new CustomerServiceImpl();
    customerList = customerServiceImpl.listCustomers();
  }

  @Test
  void listCustomers() throws Exception {

    given(customerService.listCustomers()).willReturn(customerList);

    mockMvc.perform(get("/api/v1/customer")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()", is(customerList.size())));
  }

  @Test
  void getCustomerById() throws Exception {

    Customer testCust = customerList.get(0);
    UUID id = testCust.getId();

    given(customerService.getCustomerById(id)).willReturn(testCust);

    mockMvc.perform(get("/api/v1/customer/" + id)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(id.toString())))
        .andExpect(jsonPath("$.name", is(testCust.getName())));
  }

  @Test
  void createCustomer() throws Exception {

    Customer customer = customerList.get(0);
    customer.setVersion(null);
    customer.setId(null);

    given(customerService.saveNewCustomer(any(Customer.class))).willReturn(customerList.get(1));

    mockMvc.perform(post("/api/v1/customer")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(customer)))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"));
  }
}