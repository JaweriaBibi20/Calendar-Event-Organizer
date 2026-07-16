# 📅 Calendar & Event Organizer

A beautiful and interactive Desktop Calendar and Event Management application built in Java Swing. This project showcases core Object-Oriented Programming (OOP) principles, custom GUI styling, background multithreading, and local file handling.

## 🚀 Key Features

* **Custom Aesthetic UI:** Features a customized `GradientPanel` with soft pastel colors and an interactive `JCalendar` integration that dynamically highlights "Today" (Light Blue) and "Selected Day" (Light Pink).
* **Full CRUD Operations:** Seamlessly Add, Update, and Delete events with built-in validation checks (such as preventing duplicate events at the same date and time).
* **Local Data Persistence:** Uses Java Object Serialization to automatically save and load your events from a local `events.dat` file, ensuring no data is lost when the app closes.
* **Smart Search & Filters:** Built-in search functionality to instantly filter events by Title or Category, with a quick option to reset the view.
* **Background Reminder Thread:** Utilizes Java's `ScheduledExecutorService` to run a background timer that checks active events every minute and triggers a graphical popup alert (`JOptionPane`) when an event is due.

## 🛠️ Tech Stack & OOP Concepts

* **Language:** Java (JDK 8+)
* **GUI Toolkit:** Java Swing & AWT, JCalendar Library
* **OOP Concepts Demonstrated:**
  * **Inheritance:** Extends `JFrame` for the main application window and `JPanel` for the custom gradient styling (`GradientPanel`).
  * **Encapsulation:** Clean structure of the data inside the serializable `Event` class.
  * **Abstraction & Polymorphism:** Customizing component drawing by overriding `paintComponent` and custom UI Plaf setups.
* **Core Java Libraries:** Java Concurrency (`ScheduledExecutorService`), Java Time API (`LocalDate`, `LocalTime`, `LocalDateTime`), and File I/O Streams.
