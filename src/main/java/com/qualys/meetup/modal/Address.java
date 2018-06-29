package com.qualys.meetup.modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qualys.meetup.entity.AddressUDT;
import com.qualys.meetup.entity.StudentEntity;
import lombok.Data;

/**
 * Created by aagarwal on 6/12/2018.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {

    @JsonProperty
    String street;

    @JsonProperty
    String city;

    @JsonProperty
    String country;

    @JsonProperty
    long pincode;

    @JsonIgnore
    public static Address fromEntityToModal(AddressUDT addressUDT) {
        Address address = new Address();
        address.setStreet(addressUDT.getStreet());
        address.setCity(addressUDT.getCity());
        address.setCountry(addressUDT.getCountry());
        address.setPincode(addressUDT.getPincode());
        return address;
    }
}
