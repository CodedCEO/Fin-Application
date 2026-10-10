package com.finapp.card.repository;


import com.finapp.card.entity.Cards;
import com.finapp.card.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
   // "select * from customer " +"where mobile_number = 08066439570"
       Customer findByMobileNumber(String phoneNumber);
       Customer findByCustomerId(Long customerId);
       Optional<Customer> findByEmail(String email);
       Customer findByFullName(String fullName);
       Customer findByEmailAndFullName(String email,String fullName);
       List<Customer> findByEmailOrFullName(String email, String fullName);


}
