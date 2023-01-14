package cinema;

import java.util.Scanner;

public class Cinema {

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);

        System.out.println("Enter the number of rows:");
        var rows = scanner.nextInt();

        System.out.println("Enter the number of seats in each rows:");
        var seats = scanner.nextInt();

        var room = new Room(rows, seats);

        while (true) {
            System.out.println("""
                               1. Show the seats
                               2. Buy a ticket
                               3. Statistics
                               0. Exit
                               """);

            switch (scanner.nextInt()) {
                case 1 -> room.showSeats();
                case 2 -> room.butTicket();
                case 3 -> room.showStatistics();
                case 0 -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Wrong input!");
            }
        }
    }


    static class Room {
        private final int rows;
        private final int seats;
        private final char[][] seatMap;
        private final RoomStatistics statistics;

        Room(int rows, int seats) {
            this.rows = rows;
            this.seats = seats;
            this.statistics = new RoomStatistics(seats * rows, calculateTotalIncome());

            this.seatMap = new char[rows][seats];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < seats; j++) {
                    seatMap[i][j] = 'S';
                }
            }
        }

        public void showSeats() {
            System.out.println("Cinema:");
            System.out.print(" ");
            for (int i = 1; i <= seats; i++) {
                System.out.print(" " + i);
            }
            System.out.println();
            for (int i = 0; i < rows; i++) {
                System.out.print(i + 1);
                for (int j = 0; j < seats; j++) {
                    System.out.print(" " + seatMap[i][j]);
                }
                System.out.println();
            }
        }

        public void butTicket() {
            var scanner = new Scanner(System.in);

            while (true) {
                try {
                    System.out.println("Enter a row number:");
                    var row = scanner.nextInt();

                    System.out.println("Enter a seat number in that row:");
                    var seat = scanner.nextInt();

                    var price = buyTicket(row, seat);
                    System.out.println("Ticket price: $" + price);

                    return;
                } catch (SeatAlreadyBookedException e) {
                    System.out.println(e.getMessage());
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Wrong input!");
                }
            }
        }

        private int buyTicket(int row, int seat) {
            if (seatMap[row - 1][seat - 1] == 'B') {
                throw new SeatAlreadyBookedException("That ticket has already been purchased!");
            } else {
                seatMap[row - 1][seat - 1] = 'B';
                var price = calculateTicketPrice(row);
                statistics.currentIncome += price;
                statistics.ticketsBought++;

                return price;
            }
        }

        private int calculateTicketPrice(int row) {
            if (seats * rows <= 60) {
                return 10;
            } else {
                if (row <= rows / 2) {
                    return 10;
                } else {
                    return 8;
                }
            }
        }

        private int calculateTotalIncome() {
            if (seats * rows <= 60) {
                return 10 * seats * rows;
            } else {
                var firstHalf = rows / 2;
                return 10 * seats * firstHalf + 8 * seats * (rows - firstHalf);
            }
        }

        public void showStatistics() {
            System.out.println("Number of purchased tickets: " + statistics.ticketsBought);
            System.out.printf("Percentage: %.2f%%%n", statistics.calculatePercentage());
            System.out.println("Current income: $" + statistics.currentIncome);
            System.out.println("Total income: $" + statistics.totalIncome);
        }

        static class RoomStatistics {
            public int currentIncome;
            public int totalIncome;
            public int ticketsBought;

            private final int roomSize;

            RoomStatistics(int roomSize, int totalIncome) {
                this.roomSize = roomSize;
                this.totalIncome = totalIncome;
                this.ticketsBought = 0;
                this.currentIncome = 0;
            }

            public float calculatePercentage() {
                return (float) ticketsBought / roomSize * 100;
            }
        }

        static class SeatAlreadyBookedException extends RuntimeException {
            public SeatAlreadyBookedException(String message) {
                super(message);
            }
        }
    }
}