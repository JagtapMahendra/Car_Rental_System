package com.example.carrental.service;

import com.example.carrental.model.Car;
import com.example.carrental.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    public List<Car> getAvailableCars() {
        try {
            return carRepository.findByAvailabilityTrue();
        } catch (Exception e) {
            System.err.println("Error fetching available cars: " + e.getMessage());
            return Collections.emptyList();
        }
    }

	public List<Car> getAllCars() {
        return carRepository.findAll(); // Fetch all cars
	}
	
	public Car getCarById(Long id) {
	    return carRepository.findById(id).orElse(null);
	}


	public void deleteCarById(Long id) {
		
		carRepository.deleteById(id);
	}
	
	public List<Car> getFilteredCars(String brand, Integer minSeater, Integer maxPrice, Boolean ac, String fuelType, String transmission) {
	    return carRepository.findFilteredCars(brand, minSeater, maxPrice, ac, fuelType, transmission);
	}

	public List<String> getAllBrands() {
	    return carRepository.findDistinctBrands();
	}

	public List<String> getAllFuelTypes() {
	    return carRepository.findDistinctFuelTypes();
	}

	public List<String> getAllTransmissions() {
	    return carRepository.findDistinctTransmissions();
	}


	
	 
}