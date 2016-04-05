/**
 * Copyright 2014-2016 the original author or authors.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kaczmarzyk;

import net.kaczmarzyk.spring.data.jpa.Customer;
import net.kaczmarzyk.spring.data.jpa.CustomerRepository;
import net.kaczmarzyk.spring.data.jpa.Event;
import net.kaczmarzyk.spring.data.jpa.domain.Conjunction;
import net.kaczmarzyk.spring.data.jpa.domain.IsNotNull;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.utils.CollectionSpecificationGenerator;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LikeE2eTest extends E2eTestBase {

    @Controller
    public static class LikeSpecController {

        @Autowired
        CustomerRepository customerRepo;

        @RequestMapping(value = "/customers", params = "lastName")
        @ResponseBody
        public Object findCustomersByLastName(
                @Spec(path = "lastName", spec = Like.class) Specification<Customer> spec) {

            return customerRepo.findAll(spec);
        }

        @RequestMapping(value = "/customers", params = "eventName")
        @ResponseBody
        public Object findCustomersByEventNameLike(@RequestParam(value = "eventName") String eventName,
                                               @And({
                                                       @Spec(path = "id", spec = IsNotNull.class)
                                               }) Specification<Customer> specification) {
            CollectionSpecificationGenerator<Customer, Event> customerEventCollectionSpecificationGenerator = new CollectionSpecificationGenerator<>(Customer.class, "events", Event.class);
            ((Conjunction<Customer>) specification).addInnerSpecs(customerEventCollectionSpecificationGenerator.like("name", eventName));
            return customerRepo.findAll(specification);
        }
    }

    @Test
    public void filtersByFirstName() throws Exception {
        mockMvc.perform(get("/customers")
                .param("lastName", "im")
                .accept(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$[?(@.firstName=='Homer')]").exists())
               .andExpect(jsonPath("$[?(@.firstName=='Marge')]").exists())
               .andExpect(jsonPath("$[?(@.firstName=='Bart')]").exists())
               .andExpect(jsonPath("$[?(@.firstName=='Lisa')]").exists())
               .andExpect(jsonPath("$[?(@.firstName=='Maggie')]").exists())
               .andExpect(jsonPath("$[5]").doesNotExist());
    }

    @Test
    public void filtersByEventName() throws Exception {
        mockMvc.perform(get("/customers")
                .param("eventName", "ISPS%")
                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$[?(@.firstName=='Homer')]").exists())
               .andExpect(jsonPath("$[?(@.firstName=='Marge')]").exists())
               .andExpect(jsonPath("$[?(@.firstName=='Bart')]").exists())
               .andExpect(jsonPath("$[?(@.firstName=='Maggie')]").exists())
               .andExpect(jsonPath("$[?(@.firstName=='Ned')]").exists())
               .andExpect(jsonPath("$[5]").doesNotExist());
    }
}
