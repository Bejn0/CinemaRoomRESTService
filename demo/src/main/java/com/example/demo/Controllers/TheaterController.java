package com.example.demo.Controllers;

import com.example.demo.Klase.*;
import com.example.demo.exceptions.TicketAlreadyPurchasedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
public class TheaterController {

    Stats stats = new Stats(0,81,0);

    int rows = 9;
    int columns = 9;

    int numberOfBookedSeats = 0;
    public Seat[] getAvailableSeats(int rows,int columns){
        Seat[] niz = new Seat[rows*columns];
        int br=0;
        for(int i = 1; i <= rows; i++){
            for(int j = 1; j <= columns; j++){
                if(i<=4) {
                    niz[br++] = new Seat(i, j, 10);
                }else {
                    niz[br++] = new Seat(i, j, 8);
                }
            }
        }
        return niz;
    }



    Seat[] availableSeats = getAvailableSeats(rows,columns);

    List<Ticket> bookedSeats = new ArrayList<>();

    public void addBookedSeat(Ticket seat){

        bookedSeats.add(seat);
    }

    public void removeBookedSeat(Ticket seat){

        bookedSeats.remove(seat);
    }

    public boolean isBooked(Seat seat){

        for (Ticket bookedSeat : bookedSeats) {
            if(bookedSeat.getTicket().getRow() == seat.getRow() && bookedSeat.getTicket().getColumn() == seat.getColumn()){
                return true;
            }
        }
        return false;
    }

    public boolean isBooked(UUID token){

        for (Ticket bookedSeat : bookedSeats) {
            if(bookedSeat.getToken().compareTo(token) == 0){
                return true;
            }
        }
        return false;
    }

    @GetMapping("/seats")
    public MovieTheater getMt(){
        MovieTheater mt = new MovieTheater(rows,columns,availableSeats);
        return mt;
    }

    @PostMapping(path = "/purchase", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSeat(@RequestBody Seat seat){
        if(seat.getRow() == 0){
            seat.setRow(seat.getRow()+1);
        }

        if(seat.getRow() < 0 || seat.getRow() > 9 || seat.getColumn() < 0 || seat.getColumn() > 9){
            return ResponseEntity.badRequest().body(Map.of(
                    "error",
                    "The number of a row or a column is out of bounds!"));

        }

        Ticket ticket = new Ticket(UUID.randomUUID(), seat);

        if(isBooked(ticket.getTicket())){
            return ResponseEntity.badRequest().body(Map.of(
                    "error",
                    "The ticket has been already purchased!"));

        }

        if(ticket.getTicket().getRow() <= 4) {
            ticket.getTicket().setPrice(10);
            stats.setCurrentIncome(stats.getCurrentIncome()+10);

        }else {
            ticket.getTicket().setPrice(8);
            stats.setCurrentIncome(stats.getCurrentIncome()+8);
        }

        stats.setNumberOfAvailableSeats(stats.getNumberOfAvailableSeats()-1);
        stats.setNumberOfPurchasedTickets(stats.getNumberOfPurchasedTickets()+1);
        addBookedSeat(ticket);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping(path = "/return", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> returnSeat(@RequestBody Token token){

        Ticket currentTicket = new Ticket();

        if(!isBooked(token.getToken())){
            return ResponseEntity.badRequest().body(Map.of(
                    "error",
                    "Wrong token!"));
        }

        for (Ticket bookedSeat : bookedSeats) {
            if(bookedSeat.getToken().compareTo(token.getToken()) == 0){
                currentTicket = bookedSeat;
            }
        }

        if(currentTicket.getTicket().getRow() <= 4) {
            stats.setCurrentIncome(stats.getCurrentIncome()-10);
        }else {
            stats.setCurrentIncome(stats.getCurrentIncome()-8);
        }

        stats.setNumberOfAvailableSeats(stats.getNumberOfAvailableSeats()+1);
        stats.setNumberOfPurchasedTickets(stats.getNumberOfPurchasedTickets()-1);

        removeBookedSeat(currentTicket);

        return ResponseEntity.ok(Map.of("returned_ticket", currentTicket.getTicket()));

    }
    @PostMapping(path = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> showStats(@RequestParam( required = false, defaultValue = "password") String password){

        if(password.equals("super_secret")){
            return ResponseEntity.ok(stats);
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error",
                    "The password is wrong!"));
        }

    }


}
