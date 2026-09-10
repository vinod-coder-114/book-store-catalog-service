Develop/design an application Book-Store.
# Use Cases:
## Customer Responsibilities:
  - Customer can browse books by categories.
  - Customer can search for books by title, author, or ISBN.
  - Customer can view detailed information about a book, including title, author, ISBN, price, and availability.
  - Customer can add books to a shopping cart and proceed to checkout.
  - Customer can create an account, log in, and manage their profile information.
  - Customer can buy books and view their order history.
  - Customer may leave reviews and ratings for books they have purchased.
  - Customer may have multiple addresses for shipping and billing purposes.
## Administrator Responsibilities:
  - Administrator can add, update, or remove books from the catalog.
  - Administrator can manage categories and subcategories of books.
  - Administrator can view sales reports and customer activity.
  - Administrator can manage user accounts and permissions.
  - Administrator can manage reviews and ratings for books.
  - Administrator can manage promotional offers and discounts for books.
  - Administrator can manage inventory and stock levels for books.

# Architecture:
    - User Interface (UI): A web-based interface for customers and administrators to interact with the application.
    - Microservices: The application will be built using a microservices architecture, with separate services for 
        user management (User-Service), book catalog , shopping cart , order processing (Order-Service), and reviews/ratings (Review-Service).
        book catalog (Catalog-Service),
        shopping cart (Cart-Service),
        order processing (Order-Service),
        reviews/ratings (Review-Service).
## User-Service:
    - Responsible for user registration, authentication, and profile management.
    - Provides JWT tokens for secure authentication and authorization.
    Database: MYSQL (Relational Database).
## Catalog-Service:
    - Responsible for managing the book catalog, including adding, updating, and removing books.
    - Provides APIs for browsing and searching books by categories, title, author, or ISBN.
    Database: MongoDB (No SQL Database).
    Framework: Spring Boot (Java-based framework for building microservices).
    APIs: RESTful APIs for communication between microservices and the UI.
    Main Endpoint should be /catalog
        - GET /books: Retrieve a list of books.
        - GET /books/{id}: Retrieve detailed information about a specific book.
        - POST /books: Add a new book to the catalog (admin only).
        - PUT /books/{id}: Update information for a specific book (admin only).
        - DELETE /books/{id}: Remove a specific book from the catalog (admin only).
        - GET /categories: Retrieve a list of book categories.
        - GET /categories/{id}/books: Retrieve a list of books in a specific category.
        - GET /search: Search for books by title, author, or ISBN.
    Database Design:
        - Book Collection:
            - Fields: id, title, author, ISBN, price, availability, category_id, description, image_url
        - Category Collection:
            - Fields: id, name, description
## Cart-Service:
    - Responsible for managing the shopping cart, including adding and removing books, and calculating the total price.
    - Provides APIs for customers to view and manage their shopping cart.
## Order-Service:
    - Responsible for processing orders, including payment processing and order history management.
    - Provides APIs for customers to place orders and view their order history.
## Review-Service:
    - Responsible for managing reviews and ratings for books.
    - Provides APIs for customers to leave reviews and ratings for books they have purchased.

  The Application should use following technologies:
- Database: MongoDB (No SQL Database).
- Authentication: JWT (JSON Web Tokens) for secure user authentication.
  - We have user-service to get the token and validate it based on the OAuth2.0 protocol.
# Book-Store Catalog Database Design
