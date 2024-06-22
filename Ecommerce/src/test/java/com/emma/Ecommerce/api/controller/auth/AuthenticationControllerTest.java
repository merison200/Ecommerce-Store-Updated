package com.emma.Ecommerce.api.controller.auth;

import com.emma.Ecommerce.api.model.RegistrationBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    //No.13 More test are still required. 14, 15, 16 and 17.

    @Autowired
    private MockMvc mvc;

    @Test
    @Transactional
    public void testRegister() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        RegistrationBody body = new RegistrationBody();
        body.setUserName(null);
        body.setEmail("AuthenticationControllerTest$testRegister@junit.com");
        body.setFirstName("firstName");
        body.setLastName("lastName");
        body.setPassword("passwordA123");
        mvc.perform(post("/authentication/register")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setUserName("AuthenticationControllerTest$testRegister");
        body.setEmail(null);
        mvc.perform(post("/authentication/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setEmail("AuthenticationControllerTest$testRegister@junit.com");
        body.setPassword(null);
        mvc.perform(post("/authentication/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setPassword("passwordA123");
        body.setFirstName(null);
        mvc.perform(post("/authentication/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setFirstName("firstName");
        body.setLastName(null);
        mvc.perform(post("/authentication/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        //TODO: Test password characters, password length and email validity.
    }
}
