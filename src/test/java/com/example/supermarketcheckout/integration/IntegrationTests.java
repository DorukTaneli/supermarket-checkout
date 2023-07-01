package com.example.supermarketcheckout.integration;

import com.example.supermarketcheckout.CheckoutRepository;
import com.example.supermarketcheckout.internal.Pricing;
import com.example.supermarketcheckout.internal.Scan;
import com.example.supermarketcheckout.internal.SpecialOffer;
import com.example.supermarketcheckout.requestbody.ScanRequestBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-it.properties"
)
@AutoConfigureMockMvc
public class IntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CheckoutRepository checkoutRepository;


    @BeforeEach
    void setUp() {
        checkoutRepository.deleteAll();
    }

    @Test
    void performFullCheckout() throws Exception {
        // given
        Pricing pricingA = new Pricing('A', 40, new SpecialOffer(3, 100));
        Pricing pricingB = new Pricing('B', 50, new SpecialOffer(2, 80));
        Pricing pricingC = new Pricing('C', 25);
        Pricing pricingD = new Pricing('D', 20);

        List<Pricing> pricingList = List.of(pricingA, pricingB, pricingC, pricingD);

        ScanRequestBody scanA = new ScanRequestBody('A');
        ScanRequestBody scanB = new ScanRequestBody('B');
        ScanRequestBody scanC = new ScanRequestBody('C');
        ScanRequestBody scanD = new ScanRequestBody('D');


        // when
        ResultActions startResult = mockMvc.perform(get("/checkout/start"));
        String checkoutId = startResult.andReturn().getResponse().getContentAsString();

        mockMvc.perform(post(String.format("/checkout/%s/pricing", checkoutId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pricingList)));

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post(String.format("/checkout/%s/scan", checkoutId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(scanA)));
        }
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post(String.format("/checkout/%s/scan", checkoutId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(scanB)));
        }
        mockMvc.perform(post(String.format("/checkout/%s/scan", checkoutId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scanC)));
        mockMvc.perform(post(String.format("/checkout/%s/scan", checkoutId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scanB)));
        mockMvc.perform(post(String.format("/checkout/%s/scan", checkoutId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scanD)));

        ResultActions resultActions = mockMvc.perform(get(String.format("/checkout/%s/total", checkoutId)));

        // then
        resultActions.andExpect(status().isOk());
        assertEquals("305", resultActions.andReturn().getResponse().getContentAsString());
    }

}
