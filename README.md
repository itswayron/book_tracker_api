
  **<summary>Book Tracker API</summary>**

An API for managing books and readings, allowing users to register their books, track reading progress, and manage authentication via JWT.
* (Readme gerado por IA. Erros e imprecisões podem ocorrer.)



---

<details>
  <summary>Description</summary>

The **Book Tracker API** offers features for registering books, tracking reading progress, and user authentication with JWT tokens. With support for book management and reading tracking, it's a complete solution for those who want to organize their readings and monitor their progress.

</details>

---

<details>
  <summary>Features</summary>

- **User Authentication**: Registration, login, and JWT token generation.
- **Book Management**: Add, edit, and retrieve books.
- **Reading Tracking**: Record daily reading progress (pages, chapters).
- **Access and Refresh Tokens**: Generate JWT access tokens and encrypted refresh tokens for session renewal.

</details>

---

<details>
  <summary>Technologies Used</summary>

- **Spring Boot 3.4.x**
- **Kotlin 1.9.25**
- **Spring Security** with JWT authentication
- **JPA** for data persistence with MySQL
- **JWT (JSON Web Token)** for authentication and token generation
- **AES** for encrypting refresh tokens

</details>

---

<details>
  <summary>How to Run the Project</summary>

### Prerequisites

- Java 17 or higher
- Kotlin 1.9.25
- MySQL or compatible database

### Steps to Run

1. Clone this repository:

   ```bash
   git clone https://github.com/yourusername/book_tracker_api.git
   ```

2. Navigate to the project directory:

   ```bash
   cd book_tracker_api
   ```

3. Install dependencies and run the project:

   ```bash
   mvn spring-boot:run
   ```

4. Access the application in your browser at `http://localhost:8080`.

</details>

---

<details>
  <summary>Endpoints</summary>

Here are the main endpoints of the **Book Tracker API** with their explanations:

### **Authentication and Registration**

#### **POST /auth/register**
- **Description**: Registers a new user in the system.
- **Request**:
  ```json
  {
    "username": "john_doe",
    "email": "john@example.com",
    "password": "securePassword123"
  }
  ```
- **Response**: Returns the created user information.
  ```json
  {
    "id": "unique-user-id",
    "username": "john_doe",
    "email": "john@example.com",
    "createdAt": "2025-03-16T10:00:00"
  }
  ```

#### **POST /auth/login**
- **Description**: Logs in a user and returns the access and refresh tokens.
- **Request**:
  ```json
  {
    "username": "john_doe",
    "password": "securePassword123"
  }
  ```
- **Response**: Returns the access token and refresh token.
  ```json
  {
    "accessToken": "your-access-token",
    "refreshToken": "your-refresh-token"
  }
  ```

#### **POST /auth/refresh**
- **Description**: Generates a new access token using the refresh token.
- **Request**:
  ```json
  {
    "refreshToken": "your-refresh-token"
  }
  ```
- **Response**: Returns a new access token.
  ```json
  {
    "accessToken": "new-access-token"
  }
  ```

### **Book Management**

#### **POST /books**
- **Description**: Adds a new book to the system.
- **Request**:
  ```json
  {
    "title": "The Lord of the Rings",
    "author": "J.R.R. Tolkien",
    "isbn": "978-0-345-33968-3",
    "genre": "Fantasy",
    "language": "English",
    "pages": 1216
  }
  ```
- **Response**: Returns the added book.
  ```json
  {
    "id": "unique-book-id",
    "title": "The Lord of the Rings",
    "author": "J.R.R. Tolkien",
    "isbn": "978-0-345-33968-3",
    "genre": "Fantasy",
    "language": "English",
    "pages": 1216,
    "createdAt": "2025-03-16T10:00:00"
  }
  ```

#### **GET /books**
- **Description**: Retrieves all books registered in the system, with pagination and sorting.
- **Request**:
    - `page`: Page number (default: 0)
    - `size`: Number of books per page (default: 10)
    - `sort`: Sorting field (default: "updatedAt")
    - `direction`: Sorting direction (default: "DESC")
- **Response**: Returns a paginated list of books.
  ```json
  {
    "content": [
      {
        "id": "unique-book-id",
        "title": "The Lord of the Rings",
        "author": "J.R.R. Tolkien",
        "isbn": "978-0-345-33968-3",
        "genre": "Fantasy",
        "language": "English",
        "pages": 1216,
        "createdAt": "2025-03-16T10:00:00"
      }
    ],
    "pageable": {
      "pageSize": 10,
      "pageNumber": 0
    }
  }
  ```

#### **GET /books/{id}**
- **Description**: Retrieves details of a specific book by ID.
- **Response**: Returns the details of the requested book.
  ```json
  {
    "id": "unique-book-id",
    "title": "The Lord of the Rings",
    "author": "J.R.R. Tolkien",
    "isbn": "978-0-345-33968-3",
    "genre": "Fantasy",
    "language": "English",
    "pages": 1216,
    "createdAt": "2025-03-16T10:00:00"
  }
  ```

#### **PUT /books/{id}**
- **Description**: Updates an existing book's details.
- **Request**:
  ```json
  {
    "title": "The Hobbit",
    "author": "J.R.R. Tolkien",
    "isbn": "978-0-345-33968-4",
    "genre": "Fantasy",
    "language": "English",
    "pages": 310
  }
  ```
- **Response**: Returns the updated book information.
  ```json
  {
    "id": "unique-book-id",
    "title": "The Hobbit",
    "author": "J.R.R. Tolkien",
    "isbn": "978-0-345-33968-4",
    "genre": "Fantasy",
    "language": "English",
    "pages": 310,
    "createdAt": "2025-03-16T10:00:00"
  }
  ```

#### **DELETE /books/{id}**
- **Description**: Deletes a book from the system by ID.
- **Response**: Returns a success message.
  ```json
  {
    "message": "Book deleted successfully."
  }
  ```

### **Reading Tracking**

#### **POST /readings/{bookId}**
- **Description**: Registers a reading session for a book.
- **Request**:
  ```json
  {
    "bookId": "unique-book-id",
    "trackingMethod": "PAGES",
    "dailyGoal": 10,
    "startReadingDate": "2025-03-16T10:00:00"
  }
  ```
- **Response**: Returns the created reading session.
  ```json
  {
    "id": "unique-reading-id",
    "bookId": "unique-book-id",
    "bookTitle": "The Lord of the Rings",
    "progressInPercentage": 0.0,
    "totalProgress": 0,
    "pages": 1216,
    "chapters": 0,
    "readingState": "TO_READ",
    "dailyGoal": 10,
    "startReadingDate": "2025-03-16T10:00:00",
    "endReadingDate": null,
    "estimatedCompletionDate": null
  }
  ```

#### **GET /readings/{bookId}**
- **Description**: Retrieves all reading sessions for a specific book.
- **Response**: Returns a list of reading sessions for the requested book.
  ```json
  [
    {
      "id": "unique-reading-id",
      "bookId": "unique-book-id",
      "bookTitle": "The Lord of the Rings",
      "progressInPercentage": 20.0,
      "totalProgress": 200,
      "pages": 1216,
      "chapters": 0,
      "readingState": "READING",
      "dailyGoal": 10,
      "startReadingDate": "2025-03-16T10:00:00",
      "endReadingDate": "2025-03-17T10:00:00",
      "estimatedCompletionDate": "2025-03-22T10:00:00"
    }
  ]
  ```

#### **POST /readings/add/{sessionId}**
- **Description**: Adds progress to an existing reading session.
- **Request**:
  ```json
  {
    "quantityRead": 20
  }
  ```
- **Response**: Returns the updated reading session details with the added progress.
  ```json
  {
    "id": "unique-reading-id",
    "bookId": "unique-book-id",
    "bookTitle": "The Lord of the Rings",
    "progressInPercentage": 20.0,
    "totalProgress": 220,
    "pages": 1216,
    "chapters": 0,
    "readingState": "READING",
    "dailyGoal": 10,
    "startReadingDate": "2025-03-16T10:00:00",
    "endReadingDate": "2025-03-17T10:00:00",
    "estimatedCompletionDate": "2025-03-22T10:00:00"
  }
  ```

</details>
