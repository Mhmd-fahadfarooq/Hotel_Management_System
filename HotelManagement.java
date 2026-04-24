import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import javax.swing.*;
import java.awt.*;
import java.io.*;

class Hotel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int TOTAL_ROOMS = 10;
    private boolean[] rooms = new boolean[10];
    private Customer head = null;
    private Queue<Customer> waitingQueue = new LinkedList<>();
    private Stack<Customer> bookingHistory = new Stack<>();
    private int totalRevenue = 0;

    // CHECK IF ID ALREADY EXISTS
    public boolean isIdExists(int id) {
        // check ID in linked list for active customers who booked a room
        for (Customer temp = head; temp != null; temp = temp.next) {
            if (temp.id == id) {
                return true;
            }
        }

        // check ID in waiting queue for ppl waiting
        for (Customer c : waitingQueue) {
            if (c.id == id) {
                return true;
            }
        }

        // also check booking history so you can't enter a ID of customer who has even checked out!
        for (Customer c : bookingHistory) {
            if (c.id == id) {
                return true;
            }
        }
        return false;
    }

    private int getAvailableRoom() {
        for(int i = 0; i < 10; ++i) {
            if (!this.rooms[i]) {
                return i + 1;
            }
        }
        return -1;
    }

    private int calculateBill(String type) {
        switch (type.toLowerCase()) {
            case "double":
                return 3500;
            case "single":
                return 2000;
        }
        return 5000;
    }

    public String showRooms() {
        StringBuilder sb = new StringBuilder("\nRoom Status:\n");
        for(int i = 0; i < 10; ++i) {
            sb.append("Room ").append(i + 1).append(" : ").append(this.rooms[i] ? "Booked\n" : "Available\n");
        }
        return sb.toString();
    }

    public String showWaitingCustomers() {
        if (waitingQueue.isEmpty()) {
            return "No waiting customers.\n";
        } else {
            StringBuilder sb = new StringBuilder("\nWaiting Queue:\n");
            for (Customer c : waitingQueue) {
                sb.append("ID: ").append(c.id).append(", Name: ").append(c.name).append("\n");
            }
            return sb.toString();
        }
    }

    public String addCustomer(int id, String name, String type) {
        int room = this.getAvailableRoom();
        int bill = this.calculateBill(type);
        if (room == -1) {
            this.waitingQueue.add(new Customer(id, name, 0, type, bill));
            String result = "Hotel full! Added to waiting queue.\n";
            result += showWaitingCustomers();
            return result;
        } else {
            this.rooms[room - 1] = true;
            Customer c = new Customer(id, name, room, type, bill);
            c.next = this.head;
            this.head = c;
            this.bookingHistory.push(c);
            return "Room " + room + " booked successfully.\n";
        }
    }

    public String showCustomers() {
        if (this.head == null) {
            return "No active customers.\n";
        } else {
            StringBuilder sb = new StringBuilder("\nActive Customers:\n");
            for(Customer temp = this.head; temp != null; temp = temp.next) {
                sb.append("ID: ").append(temp.id).append(", Name: ").append(temp.name)
                  .append(", Room: ").append(temp.roomNo).append(", Type: ").append(temp.roomType)
                  .append(", Bill: Rs.").append(temp.bill).append("\n");
            }
            return sb.toString();
        }
    }

    public String searchCustomer(int id) {
        // check ID for customers who booked a room
        for (Customer temp = head; temp != null; temp = temp.next) {
            if (temp.id == id) {
                return "Customer Found (Active): " + temp.name + " | Room " + temp.roomNo + "\n";
            }
        }

        // check Customer ID in waiting queue
        for (Customer c : waitingQueue) {
            if (c.id == id) {
                return "Customer Found (Waiting): " + c.name + " | No room assigned yet\n";
            }
        }

        return "Customer not found.\n";
    }

    public String checkoutCustomer(int id) {
        Customer temp = this.head;
        Customer prev = null;
        for(; temp != null && temp.id != id; temp = temp.next) {
            prev = temp;
        }

        if (temp == null) {
            // check if in waiting queue
            for (Customer c : waitingQueue) {
                if (c.id == id) {
                    return "Customer is in waiting queue, cannot checkout.\n";
                }
            }
            return "Customer not found.\n";
        } else {
            this.rooms[temp.roomNo - 1] = false;
            this.totalRevenue += temp.bill;
            if (prev != null) {
                prev.next = temp.next;
            } else {
                this.head = temp.next;
            }

            StringBuilder result = new StringBuilder("Checkout successful.\n");
            if (!this.waitingQueue.isEmpty()) {
                Customer w = this.waitingQueue.poll();
                int room = temp.roomNo;
                this.rooms[room - 1] = true;
                w.roomNo = room;
                w.next = this.head;
                this.head = w;
                this.bookingHistory.push(w);
                result.append("Waiting customer ").append(w.name).append(" assigned Room ").append(room).append("\n");

                if (!waitingQueue.isEmpty()) {
                    result.append(showWaitingCustomers());
                }
            }
            return result.toString();
        }
    }

    public String showHistory() {
        if (this.bookingHistory.isEmpty()) {
            return "No booking history.\n";
        } else {
            StringBuilder sb = new StringBuilder("\nBooking History:\n");
            Stack<Customer> temp = (Stack<Customer>)this.bookingHistory.clone();
            while(!temp.isEmpty()) {
                Customer c = temp.pop();
                sb.append("ID: ").append(c.id).append(", Name: ").append(c.name).append(", Room: ").append(c.roomNo).append("\n");
            }
            return sb.toString();
        }
    }

    public String clearHistory() {
        if (this.bookingHistory.isEmpty()) {
            return "Booking history is already empty.\n";
        } else {
            this.bookingHistory.clear();
            return "Booking history has been cleared successfully.\n";
        }
    }

    public String showRevenue() {
        return "Total Revenue: Rs." + this.totalRevenue + "\n";
    }

    public String saveToFile() {
        try (FileWriter fw = new FileWriter("Hotel_Report.txt");
             PrintWriter out = new PrintWriter(fw)) {
            
            out.println("=== HOTEL MANAGEMENT SYSTEM DATA ===");
            out.println(showRooms());
            out.println(showCustomers());
            out.println(showWaitingCustomers());
            out.println(showHistory());
            out.println(showRevenue());
            
            return "Report successfully exported to Hotel_Report.txt\n";
        } catch (IOException e) {
            return "An error occurred while saving report: " + e.getMessage() + "\n";
        }
    }
}

class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    int id;
    String name;
    int roomNo;
    String roomType;
    int bill;
    Customer next;

    public Customer(int id, String name, int roomNo, String roomType, int bill) {
        this.id = id;
        this.name = name;
        this.roomNo = roomNo;
        this.roomType = roomType;
        this.bill = bill;
        this.next = null;
    }
}

public class HotelManagement extends JFrame {
    private Hotel hotel;
    private JTextArea displayArea;

    public HotelManagement() {
        hotel = loadData();
        boolean loaded = true;
        if (hotel == null) {
            hotel = new Hotel();
            loaded = false;
        }

        setTitle("Hotel Management System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveData();
                System.exit(0);
            }
        });
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Display Area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);

        if (loaded) {
            displayArea.setText("Previous hotel data loaded successfully.\n");
        } else {
            displayArea.setText("Welcome to Hotel Management System.\n");
        }

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(10, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnShowRooms = new JButton("Show Rooms");
        JButton btnAddCustomer = new JButton("Add Customer");
        JButton btnShowCustomers = new JButton("Show Customers");
        JButton btnSearchCustomer = new JButton("Search Customer");
        JButton btnCheckout = new JButton("Checkout Customer");
        JButton btnHistory = new JButton("Booking History");
        JButton btnClearHistory = new JButton("Clear History");
        JButton btnRevenue = new JButton("Total Revenue");
        JButton btnExportReport = new JButton("Export Report (.txt)");
        JButton btnExit = new JButton("Exit");

        buttonPanel.add(btnShowRooms);
        buttonPanel.add(btnAddCustomer);
        buttonPanel.add(btnShowCustomers);
        buttonPanel.add(btnSearchCustomer);
        buttonPanel.add(btnCheckout);
        buttonPanel.add(btnHistory);
        buttonPanel.add(btnClearHistory);
        buttonPanel.add(btnRevenue);
        buttonPanel.add(btnExportReport);
        buttonPanel.add(btnExit);

        add(buttonPanel, BorderLayout.WEST);

        // Action Listeners
        btnShowRooms.addActionListener(e -> displayArea.setText(hotel.showRooms()));

        btnAddCustomer.addActionListener(e -> addCustomerGUI());

        btnShowCustomers.addActionListener(e -> displayArea.setText(hotel.showCustomers()));

        btnSearchCustomer.addActionListener(e -> searchCustomerGUI());

        btnCheckout.addActionListener(e -> checkoutCustomerGUI());

        btnHistory.addActionListener(e -> displayArea.setText(hotel.showHistory()));

        btnClearHistory.addActionListener(e -> displayArea.setText(hotel.clearHistory()));

        btnRevenue.addActionListener(e -> displayArea.setText(hotel.showRevenue()));

        btnExportReport.addActionListener(e -> displayArea.setText(hotel.saveToFile()));

        btnExit.addActionListener(e -> {
            saveData();
            System.exit(0);
        });
    }

    private Hotel loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("hotel.dat"))) {
            return (Hotel) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("hotel.dat"))) {
            oos.writeObject(hotel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addCustomerGUI() {
        try {
            String idStr = JOptionPane.showInputDialog(this, "Enter Customer ID:");
            if (idStr == null || idStr.trim().isEmpty()) return;
            int id = Integer.parseInt(idStr);

            if (hotel.isIdExists(id)) {
                JOptionPane.showMessageDialog(this, "ID already exists! Enter a unique ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String name = JOptionPane.showInputDialog(this, "Enter Name:");
            if (name == null || name.trim().isEmpty() || !name.matches("[a-zA-Z ]+")) {
                JOptionPane.showMessageDialog(this, "Invalid name! Only alphabets allowed.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] options = {"Single", "Double", "Deluxe"};
            String type = (String) JOptionPane.showInputDialog(this, "Select Room Type:",
                    "Room Type", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (type == null) return;

            String result = hotel.addCustomer(id, name, type);
            displayArea.setText(result);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input! Please enter a valid number for ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchCustomerGUI() {
        try {
            String idStr = JOptionPane.showInputDialog(this, "Enter Customer ID to search:");
            if (idStr == null || idStr.trim().isEmpty()) return;
            int id = Integer.parseInt(idStr);
            displayArea.setText(hotel.searchCustomer(id));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input! Please enter a valid number for ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkoutCustomerGUI() {
        try {
            String idStr = JOptionPane.showInputDialog(this, "Enter Customer ID to checkout:");
            if (idStr == null || idStr.trim().isEmpty()) return;
            int id = Integer.parseInt(idStr);
            displayArea.setText(hotel.checkoutCustomer(id));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input! Please enter a valid number for ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HotelManagement().setVisible(true);
        });
    }
}