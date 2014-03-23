/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kaczmarzyk.spring.data.jpa;

import javax.persistence.EntityManager;

/**
 * Helper class for building test data
 * 
 * @author Tomasz Kaczmarzyk
 */
public class CustomerBuilder {

    private Customer customer = new Customer();
    
    private CustomerBuilder(String firstName, String lastName) {
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
    }
    
    public CustomerBuilder street(String street) {
        customer.getAddress().setStreet(street);
        return this;
    }
    
    public Customer build(EntityManager em) {
        em.persist(customer);
        return customer;
    }

    public static CustomerBuilder customer(String firstName, String lastName) {
        return new CustomerBuilder(firstName, lastName);
    }
}