import java.util.*;
import javax.swing.*;

class SeatAlreadyBookedException extends Exception {
    SeatAlreadyBookedException(String message) {
        super(message);
    }
}

class Movie {
    int id;
    String name;
    String genre;
    double rating;
    double price;

    Movie(int id, String name, String genre, double rating, double price) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.rating = rating;
        this.price = price;
    }

    public String toString() {
        return id + " | " + name + " | " + genre +
                " | Rating: " + rating + " | ₹" + price;
    }
}

class Screen {
    int screenNo;
    int totalSeats;

    Screen(int screenNo, int totalSeats) {
        this.screenNo = screenNo;
        this.totalSeats = totalSeats;
    }

    public String toString() {
        return "Screen " + screenNo + " | Seats: " + totalSeats;
    }
}

class Customer {
    int id;
    String name;
    String phone;

    Customer(int id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public String toString() {
        return id + " | " + name + " | " + phone;
    }
}

class Show {
    int id;
    Movie movie;
    Screen screen;
    String time;

    Show(int id, Movie movie, Screen screen, String time) {
        this.id = id;
        this.movie = movie;
        this.screen = screen;
        this.time = time;
    }

    public String toString() {
        return id + " | " + movie.name +
                " | " + time +
                " | Screen " + screen.screenNo;
    }
}

class Ticket {
    int ticketId;
    Customer customer;
    Show show;
    String seat;
    double price;

    Ticket(int ticketId, Customer customer, Show show,
           String seat, double price) {
        this.ticketId = ticketId;
        this.customer = customer;
        this.show = show;
        this.seat = seat;
        this.price = price;
    }

    public String toString() {
        return "========== CINEMA TICKET ==========\n" +
                "Ticket ID : " + ticketId + "\n" +
                "Customer  : " + customer.name + "\n" +
                "Phone     : " + customer.phone + "\n" +
                "Movie     : " + show.movie.name + "\n" +
                "Show Time : " + show.time + "\n" +
                "Screen    : " + show.screen.screenNo + "\n" +
                "Seat      : " + seat + "\n" +
                "Amount    : ₹" + price + "\n" +
                "===================================";
    }
}

public class CinemaManagementSystem {

    static ArrayList<Movie> movies = new ArrayList<>();
    static ArrayList<Screen> screens = new ArrayList<>();
    static ArrayList<Show> shows = new ArrayList<>();
    static ArrayList<Customer> customers = new ArrayList<>();

    static LinkedList<String> bookingHistory = new LinkedList<>();
    static LinkedList<String> cancellationHistory = new LinkedList<>();

    static HashMap<Integer, Ticket> tickets = new HashMap<>();
    static HashMap<Integer, boolean[][]> showSeats = new HashMap<>();

    static TreeMap<String, Show> showsByTime = new TreeMap<>();

    static int nextCustomerId = 1;
    static int nextTicketId = 1001;
    static int nextMovieId = 6;
    static int nextShowId = 11;

    static void addMovie(Movie movie) {
        movies.add(movie);
    }

    static void addScreen(Screen screen) {
        screens.add(screen);
    }

    static void addShow(Show show) {
        shows.add(show);

        String key = show.time + " - " + show.movie.name;
        showsByTime.put(key, show);

        showSeats.put(show.id, new boolean[5][10]);
    }

    static String getMovies() {
        StringBuilder result = new StringBuilder();

        for (Movie movie : movies) {
            result.append(movie).append("\n");
        }

        return result.length() == 0
                ? "No movies available."
                : result.toString();
    }

    static String getShows() {
        StringBuilder result = new StringBuilder();

        for (Show show : shows) {
            result.append(show).append("\n");
        }

        return result.length() == 0
                ? "No shows available."
                : result.toString();
    }

    static String getCustomers() {
        StringBuilder result = new StringBuilder();

        for (Customer customer : customers) {
            result.append(customer).append("\n");
        }

        return result.length() == 0
                ? "No customers registered."
                : result.toString();
    }

    static String searchMovie(String name) {

        if (name == null || name.trim().isEmpty()) {
            return "Please enter a movie name.";
        }

        String search = name.trim().toLowerCase();
        StringBuilder result = new StringBuilder();

        for (Movie movie : movies) {

            if (movie.name.toLowerCase().contains(search)) {
                result.append(movie).append("\n");
            }
        }

        if (result.length() == 0) {
            return "Movie not found.";
        }

        return result.toString();
    }

    static String searchTicket(int ticketId) {

        Ticket ticket = tickets.get(ticketId);

        if (ticket == null) {
            return "Ticket not found.";
        }

        return ticket.toString();
    }

    static String sortMoviesByName() {

        movies.sort(
                Comparator.comparing(movie ->
                        movie.name.toLowerCase())
        );

        return getMovies();
    }

    static String sortMoviesByRating() {

        movies.sort(
                (m1, m2) ->
                        Double.compare(m2.rating, m1.rating)
        );

        return getMovies();
    }

    static Movie findMovieById(int id) {

        for (Movie movie : movies) {

            if (movie.id == id) {
                return movie;
            }
        }

        return null;
    }

    static Show findShowById(int id) {

        for (Show show : shows) {

            if (show.id == id) {
                return show;
            }
        }

        return null;
    }

    static Customer findCustomerByPhone(String phone) {

        for (Customer customer : customers) {

            if (customer.phone.equals(phone)) {
                return customer;
            }
        }

        return null;
    }

    static Customer createCustomer(String name, String phone) {

        Customer customer =
                new Customer(
                        nextCustomerId++,
                        name,
                        phone
                );

        customers.add(customer);

        return customer;
    }

    static String validateCustomer(
            String name,
            String phone) {

        if (name == null || name.trim().isEmpty()) {
            return "Customer name cannot be empty.";
        }

        if (phone == null ||
                !phone.trim().matches("\\d{10}")) {

            return "Phone number must contain exactly 10 digits.";
        }

        return "VALID";
    }

    static int[] getSeatPosition(String seatName) {

        if (seatName == null) {
            return null;
        }

        seatName = seatName.trim().toUpperCase();

        if (seatName.length() < 2) {
            return null;
        }

        char rowChar = seatName.charAt(0);

        int row = rowChar - 'A';
        int column;

        try {

            column =
                    Integer.parseInt(
                            seatName.substring(1)
                    ) - 1;

        } catch (NumberFormatException e) {

            return null;
        }

        if (row < 0 || row >= 5 ||
                column < 0 || column >= 10) {

            return null;
        }

        return new int[]{row, column};
    }

    static String getSeatName(
            int row,
            int column) {

        char rowName =
                (char) ('A' + row);

        return rowName +
                String.valueOf(column + 1);
    }

    static String getAvailableSeats(Show show) {

        boolean[][] seats =
                showSeats.get(show.id);

        if (seats == null) {
            return "Seat information not available.";
        }

        StringBuilder result =
                new StringBuilder(
                        "Available Seats:\n\n"
                );

        for (int row = 0;
             row < seats.length;
             row++) {

            for (int col = 0;
                 col < seats[row].length;
                 col++) {

                if (!seats[row][col]) {

                    result.append(
                            getSeatName(row, col)
                    ).append("  ");
                }
            }

            result.append("\n");
        }

        return result.toString();
    }

    static ArrayList<Show> getMovieShows(Movie movie) {

        ArrayList<Show> result =
                new ArrayList<>();

        for (Show show : shows) {

            if (show.movie.id == movie.id) {
                result.add(show);
            }
        }

        return result;
    }

    static String bookTicket(
            String customerName,
            String phone,
            Show show,
            String seatName) {

        String validation =
                validateCustomer(
                        customerName,
                        phone
                );

        if (!validation.equals("VALID")) {
            return validation;
        }

        if (show == null) {
            return "Please select a show.";
        }

        int[] position =
                getSeatPosition(seatName);

        if (position == null) {
            return "Invalid seat. Use A1 to E10.";
        }

        boolean[][] seats =
                showSeats.get(show.id);

        if (seats == null) {
            return "Seat information not available.";
        }

        try {

            if (seats[position[0]][position[1]]) {

                throw new SeatAlreadyBookedException(
                        "Seat " +
                                seatName.toUpperCase() +
                                " is already booked."
                );
            }

            Customer customer =
                    findCustomerByPhone(
                            phone.trim()
                    );

            if (customer == null) {

                customer =
                        createCustomer(
                                customerName.trim(),
                                phone.trim()
                        );

            } else {

                customer.name =
                        customerName.trim();
            }

            seats[position[0]][position[1]] = true;

            Ticket ticket =
                    new Ticket(
                            nextTicketId,
                            customer,
                            show,
                            seatName.trim().toUpperCase(),
                            show.movie.price
                    );

            tickets.put(
                    nextTicketId,
                    ticket
            );

            bookingHistory.add(
                    "Ticket " +
                            nextTicketId +
                            " | " +
                            customer.name +
                            " | " +
                            show.movie.name +
                            " | Seat " +
                            seatName.toUpperCase()
            );

            String result =
                    ticket.toString();

            nextTicketId++;

            return result;

        } catch (SeatAlreadyBookedException e) {

            return e.getMessage();
        }
    }

    static String cancelTicket(int ticketId) {

        Ticket ticket =
                tickets.get(ticketId);

        if (ticket == null) {
            return "Ticket not found.";
        }

        int[] position =
                getSeatPosition(ticket.seat);

        boolean[][] seats =
                showSeats.get(ticket.show.id);

        if (seats != null && position != null) {

            seats[position[0]][position[1]] =
                    false;
        }

        cancellationHistory.add(
                "Ticket " +
                        ticketId +
                        " cancelled | " +
                        ticket.customer.name
        );

        tickets.remove(ticketId);

        return "Ticket " +
                ticketId +
                " cancelled successfully.\nSeat " +
                ticket.seat +
                " is now available.";
    }

    static String addNewMovie(
            String name,
            String genre,
            double rating,
            double price) {

        if (name == null ||
                name.trim().isEmpty()) {

            return "Movie name cannot be empty.";
        }

        if (genre == null ||
                genre.trim().isEmpty()) {

            return "Genre cannot be empty.";
        }

        if (rating < 0 || rating > 10) {
            return "Rating must be between 0 and 10.";
        }

        if (price <= 0) {
            return "Price must be greater than 0.";
        }

        addMovie(
                new Movie(
                        nextMovieId++,
                        name.trim(),
                        genre.trim(),
                        rating,
                        price
                )
        );

        return "Movie added successfully.";
    }

    static String updateMovie(
            int id,
            String name,
            String genre,
            double rating,
            double price) {

        Movie movie =
                findMovieById(id);

        if (movie == null) {
            return "Movie not found.";
        }

        if (name == null ||
                name.trim().isEmpty()) {

            return "Movie name cannot be empty.";
        }

        if (rating < 0 || rating > 10) {
            return "Rating must be between 0 and 10.";
        }

        if (price <= 0) {
            return "Price must be greater than 0.";
        }

        movie.name = name.trim();
        movie.genre = genre.trim();
        movie.rating = rating;
        movie.price = price;

        rebuildShowTimeMap();

        return "Movie updated successfully.";
    }

    static String deleteMovie(int id) {

        Movie movie =
                findMovieById(id);

        if (movie == null) {
            return "Movie not found.";
        }

        for (Show show : shows) {

            if (show.movie.id == id) {

                return "Cannot delete movie because shows exist for it.";
            }
        }

        movies.remove(movie);

        return "Movie deleted successfully.";
    }

    static void rebuildShowTimeMap() {

        showsByTime.clear();

        for (Show show : shows) {

            String key =
                    show.time +
                            " - " +
                            show.movie.name;

            showsByTime.put(
                    key,
                    show
            );
        }
    }

    static String getBookingReport() {

        StringBuilder result =
                new StringBuilder();

        result.append(
                "========== BOOKING REPORT ==========\n\n"
        );

        result.append("BOOKINGS:\n");

        if (bookingHistory.isEmpty()) {

            result.append(
                    "No bookings yet.\n"
            );

        } else {

            for (String record :
                    bookingHistory) {

                result.append(
                        record
                ).append("\n");
            }
        }

        result.append(
                "\nCANCELLATIONS:\n"
        );

        if (cancellationHistory.isEmpty()) {

            result.append(
                    "No cancellations yet.\n"
            );

        } else {

            for (String record :
                    cancellationHistory) {

                result.append(
                        record
                ).append("\n");
            }
        }

        return result.toString();
    }

    static void createMainGUI() {

        JFrame frame =
                new JFrame(
                        "Cinema Hall Management System"
                );

        frame.setSize(600, 400);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        JLabel title =
                new JLabel(
                        "CINEMA HALL MANAGEMENT SYSTEM"
                );

        title.setBounds(
                160, 60, 300, 30
        );

        frame.add(title);

        JButton customerButton =
                new JButton("Customer Panel");

        customerButton.setBounds(
                120, 150, 150, 50
        );

        frame.add(customerButton);

        customerButton.addActionListener(
                e -> createCustomerPanel()
        );

        JButton adminButton =
                new JButton("Admin Panel");

        adminButton.setBounds(
                320, 150, 150, 50
        );

        frame.add(adminButton);

        adminButton.addActionListener(
                e -> createAdminPanel()
        );

        frame.setVisible(true);
    }

    static void createCustomerPanel() {

        JFrame frame =
                new JFrame(
                        "Customer Panel"
                );

        frame.setSize(650, 500);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        JLabel title =
                new JLabel(
                        "CUSTOMER PANEL"
                );

        title.setBounds(
                270, 20, 150, 30
        );

        frame.add(title);

        JButton moviesButton =
                new JButton("View Movies");

        moviesButton.setBounds(
                40, 80, 150, 45
        );

        frame.add(moviesButton);

        moviesButton.addActionListener(
                e -> showMessage(
                        frame,
                        getMovies(),
                        "Movies"
                )
        );

        JButton searchButton =
                new JButton("Search Movie");

        searchButton.setBounds(
                220, 80, 150, 45
        );

        frame.add(searchButton);

        searchButton.addActionListener(
                e -> {

                    String name =
                            JOptionPane.showInputDialog(
                                    frame,
                                    "Enter movie name:"
                            );

                    if (name != null) {

                        showMessage(
                                frame,
                                searchMovie(name),
                                "Search Result"
                        );
                    }
                }
        );

        JButton showsButton =
                new JButton("View Shows");

        showsButton.setBounds(
                400, 80, 150, 45
        );

        frame.add(showsButton);

        showsButton.addActionListener(
                e -> showMessage(
                        frame,
                        getShows(),
                        "Shows"
                )
        );

        JButton sortButton =
                new JButton("Sort Movies");

        sortButton.setBounds(
                40, 150, 150, 45
        );

        frame.add(sortButton);

        sortButton.addActionListener(
                e -> {

                    String[] options = {
                            "By Name",
                            "By Rating"
                    };

                    int choice =
                            JOptionPane.showOptionDialog(
                                    frame,
                                    "Sort movies by:",
                                    "Sorting",
                                    JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    options,
                                    options[0]
                            );

                    if (choice == 0) {

                        showMessage(
                                frame,
                                sortMoviesByName(),
                                "Movies by Name"
                        );

                    } else if (choice == 1) {

                        showMessage(
                                frame,
                                sortMoviesByRating(),
                                "Movies by Rating"
                        );
                    }
                }
        );

        JButton bookButton =
                new JButton("Book Ticket");

        bookButton.setBounds(
                220, 150, 150, 45
        );

        frame.add(bookButton);

        bookButton.addActionListener(
                e -> bookingDialog(frame)
        );

        JButton cancelButton =
                new JButton("Cancel Ticket");

        cancelButton.setBounds(
                400, 150, 150, 45
        );

        frame.add(cancelButton);

        cancelButton.addActionListener(
                e -> {

                    String input =
                            JOptionPane.showInputDialog(
                                    frame,
                                    "Enter Ticket ID:"
                            );

                    if (input == null) {
                        return;
                    }

                    try {

                        int ticketId =
                                Integer.parseInt(
                                        input.trim()
                                );

                        showMessage(
                                frame,
                                cancelTicket(ticketId),
                                "Cancellation"
                        );

                    } catch (
                            NumberFormatException ex) {

                        showMessage(
                                frame,
                                "Invalid Ticket ID.",
                                "Error"
                        );
                    }
                }
        );

        JButton ticketButton =
                new JButton("Search Ticket");

        ticketButton.setBounds(
                40, 220, 150, 45
        );

        frame.add(ticketButton);

        ticketButton.addActionListener(
                e -> {

                    String input =
                            JOptionPane.showInputDialog(
                                    frame,
                                    "Enter Ticket ID:"
                            );

                    if (input == null) {
                        return;
                    }

                    try {

                        int ticketId =
                                Integer.parseInt(
                                        input.trim()
                                );

                        showMessage(
                                frame,
                                searchTicket(ticketId),
                                "Ticket"
                        );

                    } catch (
                            NumberFormatException ex) {

                        showMessage(
                                frame,
                                "Invalid Ticket ID.",
                                "Error"
                        );
                    }
                }
        );

        JButton seatsButton =
                new JButton("Seat Availability");

        seatsButton.setBounds(
                220, 220, 150, 45
        );

        frame.add(seatsButton);

        seatsButton.addActionListener(
                e -> selectShowForSeats(frame)
        );

        JButton backButton =
                new JButton("Back");

        backButton.setBounds(
                400, 220, 150, 45
        );

        frame.add(backButton);

        backButton.addActionListener(
                e -> frame.dispose()
        );

        frame.setVisible(true);
    }

    static void createAdminPanel() {

        JFrame frame =
                new JFrame("Admin Panel");

        frame.setSize(650, 500);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        JLabel title =
                new JLabel("ADMIN PANEL");

        title.setBounds(
                280, 20, 150, 30
        );

        frame.add(title);

        JButton viewMovies =
                new JButton("View Movies");

        viewMovies.setBounds(
                40, 80, 150, 45
        );

        frame.add(viewMovies);

        viewMovies.addActionListener(
                e -> showMessage(
                        frame,
                        getMovies(),
                        "Movies"
                )
        );

        JButton addMovie =
                new JButton("Add Movie");

        addMovie.setBounds(
                220, 80, 150, 45
        );

        frame.add(addMovie);

        addMovie.addActionListener(
                e -> addMovieDialog(frame)
        );

        JButton updateMovie =
                new JButton("Update Movie");

        updateMovie.setBounds(
                400, 80, 150, 45
        );

        frame.add(updateMovie);

        updateMovie.addActionListener(
                e -> updateMovieDialog(frame)
        );

        JButton deleteMovie =
                new JButton("Delete Movie");

        deleteMovie.setBounds(
                40, 150, 150, 45
        );

        frame.add(deleteMovie);

        deleteMovie.addActionListener(
                e -> deleteMovieDialog(frame)
        );

        JButton viewShows =
                new JButton("View Shows");

        viewShows.setBounds(
                220, 150, 150, 45
        );

        frame.add(viewShows);

        viewShows.addActionListener(
                e -> showMessage(
                        frame,
                        getShows(),
                        "Shows"
                )
        );

        JButton customers =
                new JButton("View Customers");

        customers.setBounds(
                400, 150, 150, 45
        );

        frame.add(customers);

        customers.addActionListener(
                e -> showMessage(
                        frame,
                        getCustomers(),
                        "Customers"
                )
        );

        JButton report =
                new JButton("Booking Report");

        report.setBounds(
                40, 220, 150, 45
        );

        frame.add(report);

        report.addActionListener(
                e -> showMessage(
                        frame,
                        getBookingReport(),
                        "Booking Report"
                )
        );

        JButton back =
                new JButton("Back");

        back.setBounds(
                220, 220, 150, 45
        );

        frame.add(back);

        back.addActionListener(
                e -> frame.dispose()
        );

        frame.setVisible(true);
    }

    static void bookingDialog(JFrame frame) {

        JTextField nameField =
                new JTextField();

        JTextField phoneField =
                new JTextField();

        JComboBox<Movie> movieBox =
                new JComboBox<>();

        for (Movie movie : movies) {
            movieBox.addItem(movie);
        }

        JComboBox<Show> showBox =
                new JComboBox<>();

        movieBox.addActionListener(
                e -> {

                    showBox.removeAllItems();

                    Movie movie =
                            (Movie)
                                    movieBox.getSelectedItem();

                    if (movie != null) {

                        for (Show show :
                                getMovieShows(movie)) {

                            showBox.addItem(show);
                        }
                    }
                }
        );

        if (!movies.isEmpty()) {

            Movie movie =
                    movies.get(0);

            for (Show show :
                    getMovieShows(movie)) {

                showBox.addItem(show);
            }
        }

        JTextField seatField =
                new JTextField();

        JPanel panel =
                new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.add(
                new JLabel("Customer Name:")
        );

        panel.add(nameField);

        panel.add(
                new JLabel("Phone Number:")
        );

        panel.add(phoneField);

        panel.add(
                new JLabel("Select Movie:")
        );

        panel.add(movieBox);

        panel.add(
                new JLabel("Select Show:")
        );

        panel.add(showBox);

        panel.add(
                new JLabel("Seat (A1 to E10):")
        );

        panel.add(seatField);

        int result =
                JOptionPane.showConfirmDialog(
                        frame,
                        panel,
                        "Book Ticket",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result ==
                JOptionPane.OK_OPTION) {

            Show show =
                    (Show)
                            showBox.getSelectedItem();

            if (show == null) {

                showMessage(
                        frame,
                        "Please select a show.",
                        "Booking"
                );

                return;
            }

            String message =
                    bookTicket(
                            nameField.getText(),
                            phoneField.getText(),
                            show,
                            seatField.getText()
                    );

            showMessage(
                    frame,
                    message,
                    "Booking Result"
            );
        }
    }

    static void selectShowForSeats(
            JFrame frame) {

        if (shows.isEmpty()) {

            showMessage(
                    frame,
                    "No shows available.",
                    "Seats"
            );

            return;
        }

        Show selectedShow =
                (Show)
                        JOptionPane.showInputDialog(
                                frame,
                                "Select Show:",
                                "Seat Availability",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                shows.toArray(),
                                shows.get(0)
                        );

        if (selectedShow != null) {

            showMessage(
                    frame,
                    getAvailableSeats(
                            selectedShow
                    ),
                    "Available Seats"
            );
        }
    }

    static void addMovieDialog(
            JFrame frame) {

        JTextField name =
                new JTextField();

        JTextField genre =
                new JTextField();

        JTextField rating =
                new JTextField();

        JTextField price =
                new JTextField();

        JPanel panel =
                new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.add(
                new JLabel("Movie Name:")
        );

        panel.add(name);

        panel.add(
                new JLabel("Genre:")
        );

        panel.add(genre);

        panel.add(
                new JLabel("Rating:")
        );

        panel.add(rating);

        panel.add(
                new JLabel("Price:")
        );

        panel.add(price);

        int result =
                JOptionPane.showConfirmDialog(
                        frame,
                        panel,
                        "Add Movie",
                        JOptionPane.OK_CANCEL_OPTION
                );

        if (result ==
                JOptionPane.OK_OPTION) {

            try {

                double r =
                        Double.parseDouble(
                                rating.getText().trim()
                        );

                double p =
                        Double.parseDouble(
                                price.getText().trim()
                        );

                showMessage(
                        frame,
                        addNewMovie(
                                name.getText(),
                                genre.getText(),
                                r,
                                p
                        ),
                        "Add Movie"
                );

            } catch (
                    NumberFormatException e) {

                showMessage(
                        frame,
                        "Rating and price must be numbers.",
                        "Error"
                );
            }
        }
    }

    static void updateMovieDialog(
            JFrame frame) {

        String idInput =
                JOptionPane.showInputDialog(
                        frame,
                        "Enter Movie ID:"
                );

        if (idInput == null) {
            return;
        }

        try {

            int id =
                    Integer.parseInt(
                            idInput.trim()
                    );

            Movie movie =
                    findMovieById(id);

            if (movie == null) {

                showMessage(
                        frame,
                        "Movie not found.",
                        "Update Movie"
                );

                return;
            }

            String name =
                    JOptionPane.showInputDialog(
                            frame,
                            "Movie Name:",
                            movie.name
                    );

            String genre =
                    JOptionPane.showInputDialog(
                            frame,
                            "Genre:",
                            movie.genre
                    );

            String ratingInput =
                    JOptionPane.showInputDialog(
                            frame,
                            "Rating:",
                            movie.rating
                    );

            String priceInput =
                    JOptionPane.showInputDialog(
                            frame,
                            "Price:",
                            movie.price
                    );

            double rating =
                    Double.parseDouble(
                            ratingInput.trim()
                    );

            double price =
                    Double.parseDouble(
                            priceInput.trim()
                    );

            showMessage(
                    frame,
                    updateMovie(
                            id,
                            name,
                            genre,
                            rating,
                            price
                    ),
                    "Update Movie"
            );

        } catch (
                NumberFormatException e) {

            showMessage(
                    frame,
                    "Invalid input.",
                    "Error"
            );
        }
    }

    static void deleteMovieDialog(
            JFrame frame) {

        String input =
                JOptionPane.showInputDialog(
                        frame,
                        "Enter Movie ID:"
                );

        if (input == null) {
            return;
        }

        try {

            int id =
                    Integer.parseInt(
                            input.trim()
                    );

            showMessage(
                    frame,
                    deleteMovie(id),
                    "Delete Movie"
            );

        } catch (
                NumberFormatException e) {

            showMessage(
                    frame,
                    "Invalid Movie ID.",
                    "Error"
            );
        }
    }

    static void showMessage(
            JFrame frame,
            String message,
            String title) {

        JOptionPane.showMessageDialog(
                frame,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {

        addMovie(
                new Movie(
                        1,
                        "Avengers: Endgame",
                        "Action",
                        8.4,
                        250
                )
        );

        addMovie(
                new Movie(
                        2,
                        "Avatar: The Way of Water",
                        "Sci-Fi",
                        7.6,
                        280
                )
        );

        addMovie(
                new Movie(
                        3,
                        "Interstellar",
                        "Sci-Fi",
                        8.7,
                        300
                )
        );

        addMovie(
                new Movie(
                        4,
                        "Inception",
                        "Thriller",
                        8.8,
                        270
                )
        );

        addMovie(
                new Movie(
                        5,
                        "The Dark Knight",
                        "Action",
                        9.0,
                        300
                )
        );

        Screen screen1 =
                new Screen(1, 50);

        Screen screen2 =
                new Screen(2, 50);

        Screen screen3 =
                new Screen(3, 50);

        addScreen(screen1);
        addScreen(screen2);
        addScreen(screen3);

        addShow(
                new Show(
                        1,
                        movies.get(0),
                        screen1,
                        "10:00 AM"
                )
        );

        addShow(
                new Show(
                        2,
                        movies.get(0),
                        screen1,
                        "06:00 PM"
                )
        );

        addShow(
                new Show(
                        3,
                        movies.get(0),
                        screen1,
                        "09:30 PM"
                )
        );

        addShow(
                new Show(
                        4,
                        movies.get(1),
                        screen2,
                        "11:00 AM"
                )
        );

        addShow(
                new Show(
                        5,
                        movies.get(1),
                        screen2,
                        "07:00 PM"
                )
        );

        addShow(
                new Show(
                        6,
                        movies.get(2),
                        screen3,
                        "10:30 AM"
                )
        );

        addShow(
                new Show(
                        7,
                        movies.get(2),
                        screen3,
                        "06:30 PM"
                )
        );

        addShow(
                new Show(
                        8,
                        movies.get(3),
                        screen2,
                        "01:00 PM"
                )
        );

        addShow(
                new Show(
                        9,
                        movies.get(3),
                        screen2,
                        "09:00 PM"
                )
        );

        addShow(
                new Show(
                        10,
                        movies.get(4),
                        screen1,
                        "03:00 PM"
                )
        );

        createMainGUI();
    }
}