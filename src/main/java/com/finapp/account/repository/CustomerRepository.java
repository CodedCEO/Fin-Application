package com.finapp.account.repository;


import com.finapp.account.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
   // "select * from customer " +"where mobile_number = 08066439570"
       Customer findByMobileNumber(String phoneNumber);
       Customer findByCustomerId(Long customerId);
       Customer findByEmail(String email);
       Customer findByName(String name);
       Customer findByEmailAndName(String email,String name);
       List<Customer> findByEmailOrName(String email, String name);

}
