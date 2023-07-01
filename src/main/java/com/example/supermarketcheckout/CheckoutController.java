package com.example.supermarketcheckout;

import com.example.supermarketcheckout.internal.Pricing;
import com.example.supermarketcheckout.requestbody.ScanRequestBody;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @GetMapping(path = "start")
    public String startNewCheckout(){
        return checkoutService.startNewCheckout();
    }

    @PostMapping(path = "{checkoutId}/pricing")
    public void loadPricingRules(@PathVariable("checkoutId") String checkoutId,
                                 @RequestBody List<Pricing> pricingList){
        System.out.println(pricingList);
        checkoutService.loadPricingRules(checkoutId, pricingList);
    }

    @PostMapping(path = "{checkoutId}/scan")
    public void scanItem(@PathVariable("checkoutId") String checkoutId,
                         @RequestBody ScanRequestBody scanRequestBody){
        checkoutService.scanItem(checkoutId, scanRequestBody);
    }

    @GetMapping(path = "{checkoutId}/total")
    public int calculateTotal(@PathVariable("checkoutId") String checkoutId) {
        return checkoutService.calculateTotal(checkoutId);
    }

}
