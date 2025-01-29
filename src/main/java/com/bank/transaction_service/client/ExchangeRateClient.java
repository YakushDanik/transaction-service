package com.bank.transaction_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "exchangeRateClient", url = "https://v6.exchangerate-api.com/v6/c16b390176fb7c060c20e5d8")
public interface ExchangeRateClient {
    @GetMapping("/latest/{base}")
    Map<String, Object> getExchangeRates(@PathVariable("base") String baseCurrency);
}


