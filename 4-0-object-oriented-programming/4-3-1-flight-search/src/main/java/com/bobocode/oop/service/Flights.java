package com.bobocode.oop.service;

import java.util.List;
import java.util.Set;

public interface Flights {
    boolean register(String flightNumber);
     Set<String> findAll();
}
