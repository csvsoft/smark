package com.csvsoft.smark.builder;

import com.csvsoft.smark.service.sqlsuggestor.SQLVarSuggestor;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestSQLVarSuggestor {

    static class Address{
        private String city;
        private String street;

        public Address(String city, String street) {
            this.city = city;
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setStreet(String street) {
            this.street = street;
        }
    }
    static class User{


        private Address address;

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        private String firstName;
        private String lastName;

        public User(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }
    @Test
    public void testSqlVar(){
        Map<String,Object> ctxMap = new HashMap<>();
        ctxMap.put("user",new User("firstName","lastName"));
        ctxMap.put("clientName","Manual Life");

        SQLVarSuggestor suggestor = new SQLVarSuggestor(ctxMap);
        suggestor.test("$us");
        suggestor.test("$user");
        suggestor.test("$user.");
        suggestor.test("$user.address.");
      //  suggestor.test("$user.firstName");


        ;
       // suggestor.test("${cl");
       // suggestor.test("$");



    }
}
