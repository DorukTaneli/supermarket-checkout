package com.example.supermarketcheckout;

import com.example.supermarketcheckout.internal.Checkout;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckoutRepository extends MongoRepository<Checkout, String> {

}
