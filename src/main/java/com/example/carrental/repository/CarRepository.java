package com.example.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.carrental.model.Car;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByAvailabilityTrue(); // Directly fetches available cars
    
    @Query("SELECT c FROM Car c WHERE (:brand IS NULL OR c.brand = :brand) " +
            "AND (:minSeater IS NULL OR c.seater >= :minSeater) " +
            "AND (:maxPrice IS NULL OR c.pricePerDay <= :maxPrice) " +
            "AND (:acType IS NULL OR c.acType = :acType) " +
            "AND (:fuelType IS NULL OR c.fuelType = :fuelType) " +
            "AND (:transmission IS NULL OR c.transmissionType = :transmission)")
     List<Car> findFilteredCars(
             @Param("brand") String brand,
             @Param("minSeater") Integer minSeater,
             @Param("maxPrice") Integer maxPrice,
             @Param("acType") Boolean acType,
             @Param("fuelType") String fuelType,
             @Param("transmission") String transmission);

     @Query("SELECT DISTINCT c.brand FROM Car c")
     List<String> findDistinctBrands();

     @Query("SELECT DISTINCT c.fuelType FROM Car c")
     List<String> findDistinctFuelTypes();

     @Query("SELECT DISTINCT c.transmissionType FROM Car c")
     List<String> findDistinctTransmissions();




}
