package com.example.supermarketcheckout;

import com.example.supermarketcheckout.internal.Checkout;
import com.example.supermarketcheckout.internal.Pricing;
import com.example.supermarketcheckout.internal.Scan;
import com.example.supermarketcheckout.internal.SpecialOffer;
import com.example.supermarketcheckout.requestbody.ScanRequestBody;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CheckoutService {

    private final CheckoutRepository checkoutRepository;

    public CheckoutService(CheckoutRepository checkoutRepository) {
        this.checkoutRepository = checkoutRepository;
    }

    /**
     * Start a new checkout with no pricing and no scans
     * @return checkoutId
     */
    public String startNewCheckout() {
        Checkout checkout = checkoutRepository.save(new Checkout());
        return checkout.getCheckoutID();
    }

    /**
     * Loads the pricing rules to the checkout with given checkoutId.
     * @param checkoutId id of checkout
     * @throws IllegalStateException if there is no checkout with given checkoutId
     * @param pricingList pricingList to be added to the checkout
     */
    public void loadPricingRules(String checkoutId, List<Pricing> pricingList) {
        Optional<Checkout> checkoutOptional = checkoutRepository.findById(checkoutId);
        if (checkoutOptional.isEmpty()) {
            throw new IllegalStateException("Checkout with id " + checkoutId + " does not exist");
        }
        Checkout checkout = checkoutOptional.get();
        checkout.setPricingList(pricingList);
        checkoutRepository.save(checkout);
    }

    /**
     * Adds scan to the checkout. If that item is already scanned, increases the count by 1.
     * If not, creates a new scan with count 1 and adds that to the checkout's scanList.
     * @param checkoutId id of checkout
     * @throws IllegalStateException if there is no checkout with given checkoutId
     * @param scanRequestBody scanRequestBody that holds the item sku
     */
    public void scanItem(String checkoutId, ScanRequestBody scanRequestBody) {
        Optional<Checkout> checkoutOptional = checkoutRepository.findById(checkoutId);
        if (checkoutOptional.isEmpty()) {
            throw new IllegalStateException("Checkout with id " + checkoutId + " does not exist");
        }
        Checkout checkout = checkoutOptional.get();
        Optional<Scan> scanOptional = checkout.findScanBySku(scanRequestBody.getSku());
        if (scanOptional.isPresent()) {
            scanOptional.get().setCount(scanOptional.get().getCount() + 1);
        } else {
            checkout.getScanList().add(new Scan(scanRequestBody.getSku(), 1));
        }
        checkoutRepository.save(checkout);
    }

    /**
     * Calculates the total cost of scanned items according to the pricing rules.
     * @param checkoutId id of checkout
     * @throws IllegalStateException if there is no checkout with given checkoutId
     * @return total cost as int. If there are no items scanned, returns 0.
     */
    public int calculateTotal(String checkoutId) {
        Optional<Checkout> checkoutOptional = checkoutRepository.findById(checkoutId);
        if (checkoutOptional.isEmpty()) {
            throw new IllegalStateException("Checkout with id " + checkoutId + " does not exist");
        }
        int total = 0;
        Checkout checkout = checkoutOptional.get();
        for (Scan scan : checkout.getScanList()) {
            total += calculateCostWithSpecialOffers(scan, checkout);
        }
        return total;
    }

    /**
     * Calculates cost of a single scan.
     * @param scan scan containing sku and count
     * @param checkout checkout that contains pricing info
     * @throws IllegalStateException if there is no pricing in the checkout for that sku
     * @return total cost of a scan. Returns 0 if there are no items
     */
    private int calculateCostWithSpecialOffers(Scan scan, Checkout checkout){
        Optional<Pricing> pricingOptional = checkout.findPricingBySku(scan.getSku());
        if (pricingOptional.isEmpty()) {
            throw new IllegalStateException("Trying to checkout with SKU: '" +
                    scan.getSku() + "' without Pricing");
        }
        Pricing pricing = pricingOptional.get();
        int total = 0;
        total += scan.getCount() * pricing.getPrice();
        total -= calculateSpecialOfferDiscount(scan, pricing);

        return total;
    }

    /**
     * Calculates discount by checking if there is a specialOffer, and how many times the specialOffer is being used.
     * @param scan scan containing sku and count
     * @param pricing Pricing rules that contain specialOffer
     * @return discount amount. returns 0 if there are no discounts.
     */
    private int calculateSpecialOfferDiscount(Scan scan, Pricing pricing) {
        if (pricing.getSpecialOffer().isEmpty()) {
            return 0;
        }
        SpecialOffer specialOffer = pricing.getSpecialOffer().get();
        if (scan.getCount() < specialOffer.getCount()) {
            return 0;
        }

        int offerAmount = scan.getCount() / specialOffer.getCount();
        int discount = pricing.getPrice() * specialOffer.getCount() - specialOffer.getPrice();
        return offerAmount * discount;
    }
}
