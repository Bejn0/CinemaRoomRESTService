package com.example.demo.Klase;

public class MovieTheater {

    private int total_rows;
    private int total_columns;

    private Seat[] available_seats;

    public MovieTheater(int total_rows, int total_columns, Seat[] available_seats) {
        this.total_rows = total_rows;
        this.total_columns = total_columns;
        this.available_seats = available_seats;
    }

    public MovieTheater() {
    }

    public int getTotal_rows() {
        return total_rows;
    }

    public void setTotal_rows(int total_rows) {
        this.total_rows = total_rows;
    }

    public int getTotal_columns() {
        return total_columns;
    }

    public void setTotal_columns(int total_columns) {
        this.total_columns = total_columns;
    }

    public Seat[] getAvailable_seats() {
        return available_seats;
    }

    public void setAvailable_seats(Seat[] available_seats) {
        this.available_seats = available_seats;
    }
}
