# Java Hotel Management System

A comprehensive, GUI-based Hotel Management System built with Java and Swing. This application uses Data Structures (Linked Lists, Queues, Stacks) to efficiently manage hotel operations like room booking, waiting lists, and checkout history.

## Features
- **Interactive GUI**: User-friendly interface built with Java Swing.
- **Room Booking**: Assigns available rooms to customers.
- **Waiting Queue System**: If the hotel is full, new customers are automatically added to a waiting queue (using a Queue data structure).
- **Checkout & Auto-Assign**: When a room frees up, it is automatically assigned to the first person in the waiting queue.
- **Booking History**: Keeps track of all past bookings (using a Stack data structure).
- **Clear History**: Clear out old records with the click of a button.
- **Data Persistence**: Automatically saves and loads the hotel's state (Serialization) so data is not lost when the app closes.
- **Export Reports**: Generate text-based summaries of current active bookings, queues, and total revenue.

## How to Run
1. Compile the Java file:
   ```bash
   javac HotelManagement.java
   ```
2. Run the application:
   ```bash
   java HotelManagement
   ```

