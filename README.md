#  Online Car Rental System

A ** Online Car Rental System** application built in Java with MySQL as the database. This program allows users to rent cars, return them, view available cars, and manage their accounts. It also provides administrative functionality like car listings and cancellation of bookings.

## Features

### User Functionalities:
1. **Login**  
   - Users can log in using their username and password.
   - Displays a message if the account exists or not.

2. **Create an Account**  
   - Users can create a new account by providing a username and password.
   - Automatic redirection to login after account creation.

3. **View All Cars**  
   - Displays all cars, including rented and available ones.

4. **View Available Cars**  
   - Displays only the cars that are currently available for rental.

5. **Book a Car**  
   - Users can book an available car by providing the car ID, rental start date, end date, and rental duration.
   - Calculates the total rental cost based on daily rent and duration.
   - Generates a receipt in a text file with rental details.

6. **Return a Car**  
   - Users can return a rented car by entering its ID.
   - Updates the availability status of the car in the database.

7. **Cancel Booking**  
   - Users can cancel their bookings by entering the car ID, marking the car as available again.

8. **Delete Account**  
   - Users can delete their accounts if no active bookings exist.

9. **Logout**  
   - Allows users to log out and return to the main menu.

10. **Exit**  
    - Safely exits the program.

### Administrative Functionalities:
- Display and manage all cars in the system.
- Track car availability.

---

## Requirements

### Software Requirements:
- Java Development Kit (JDK) 8 or later
- MySQL Database Server
- MySQL Connector/J

### Database Schema:
#### Users Table
| Column      | Type         | Description                  |
|-------------|--------------|------------------------------|
| `user_id`   | INT (Primary Key) | Unique user identifier      |
| `username`  | VARCHAR(50)  | Username of the user         |
| `password`  | VARCHAR(50)  | User's password              |

#### Cars Table
| Column          | Type         | Description                  |
|------------------|--------------|------------------------------|
| `car_id`         | INT (Primary Key) | Unique car identifier        |
| `company_name`   | VARCHAR(50)  | Name of the car's company    |
| `model_name`     | VARCHAR(50)  | Model of the car             |
| `colour`         | VARCHAR(30)  | Car color                   |
| `number_plate`   | VARCHAR(15)  | Car's number plate          |
| `available`      | ENUM('yes', 'no') | Availability status          |
| `rent_price`     | DOUBLE       | Daily rental cost            |

#### Rental Cars Details Table
| Column        | Type         | Description                  |
|---------------|--------------|------------------------------|
| `rental_id`   | INT (Primary Key) | Unique rental identifier     |
| `user_id`     | INT          | User who rented the car       |
| `car_id`      | INT          | Car that was rented           |
| `start_date`  | DATE         | Start date of the rental      |
| `end_date`    | DATE         | End date of the rental        |
| `total_cost`  | DOUBLE       | Total rental cost             |

---

## Installation Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/car-rental-system.git
## How to Use Program
How to Use<br>
1.Start the program.<br>
2.Create a new account or log in with an existing account.<br>
3.Use the menu to:<br>
View cars,<br>
Book, return, or cancel  Car bookings.<br>
4.Manage your account.<br>
5.Log out or exit the application when done.<br>

## Output of the example
Receipt for Car Rental
Car ID: 101
Number of days: 3
Rental Rate per day: 1000 rupees
Rental Start Date: 2024-12-01
Rental End Date: 2024-12-04
Total Cost: 3000 rupees


