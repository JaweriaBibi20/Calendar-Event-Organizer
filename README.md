# 📖 Smart Hybrid Dictionary (GUI)

A high-performance, intelligent Desktop Dictionary application built in Java. This application showcases a unique **hybrid search architecture**: utilizing an ultra-fast local **Radix Tree (Trie)** data structure for instant autocomplete suggestions, combined with an **Online Dictionary API** to dynamically fetch real-time meanings, phonetics, and synonyms.

## 🚀 Key Features

* **Hybrid Search Mechanism:** * **Local Search:** Loads a local vocabulary dataset into a compressed Radix Tree for sub-millisecond, instant prefix-matching/autocomplete as you type.
  * **Online Search:** Dynamically connects to a REST API to fetch live, detailed dictionary data when a word is queried.
* **Interactive Swing GUI:** A responsive and clean Graphical User Interface built with NetBeans GUI Builder for a seamless user experience.
* **Dynamic Autocomplete:** Shows instant suggestions in real-time as the user types, optimized by the Radix Tree structure.
* **API Integration & JSON Parsing:** Performs live HTTP requests to retrieve meanings, part of speech, audio pronunciations, and synonyms, parsing JSON data on the fly.

## 🛠️ Tech Stack & Concepts Demonstrated

* **Language:** Java (JDK 8+)
* **Data Structures:** Radix Tree (Patricia Trie) for space-optimized prefix routing.
* **GUI Framework:** Java Swing & AWT
* **Networking & API:** HTTP Client (`HttpURLConnection`), REST API integration, and JSON Data Parsing.
* **OOP Principles:**
  * **Separation of Concerns:** Clear architecture separating the GUI View, Radix Tree local logic, and Network/API parsing layers.
