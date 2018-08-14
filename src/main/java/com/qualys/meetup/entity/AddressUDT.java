package com.qualys.meetup.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import info.archinnov.achilles.annotations.Column;
import info.archinnov.achilles.annotations.UDT;
import lombok.Data;

@UDT(keyspace = "meetup", name="address")
@Data
public class AddressUDT {

    @Column("street")
    private String street;

    @Column("city")
    private String city;

    @Column("country")
    private String country;

    @Column("pincode")
    private long pincode;

    @JsonIgnore
    public static AddressUDT of(String street, String city, String country, long pincode){
        AddressUDT addressUDT = new AddressUDT();
        addressUDT.setStreet(street);
        addressUDT.setCity(city);
        addressUDT.setCountry(country);
        addressUDT.setPincode(pincode);
        return addressUDT;
    }

}
