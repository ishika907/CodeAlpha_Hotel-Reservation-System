package com.hotel;

import com.hotel.service.HotelService;
import com.hotel.service.PaymentService;
import com.hotel.storage.FileStorage;

/**
 * Main entry point for the Hotel Reservation System.
 * Plain Java console application - no Spring Boot, no Hibernate.
 */
public class Main {
    public static void main(String[] args) {
        String dataFolder = "data";
        FileStorage storage = new FileStorage(dataFolder);

        HotelService hotelService = new HotelService(storage);
        PaymentService paymentService = new PaymentService(storage);

        HotelApp app = new HotelApp(hotelService, paymentService);
        app.start();
    }
}
