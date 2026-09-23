# 🔬 LabSphere

> A centralized laboratory inventory and equipment usage management system designed to streamline checkout, monitor item conditions, and manage incident reporting and fines.

---

## 📌 Overview

**LabSphere** replaces manual paper logs in college or institutional laboratories. It provides a real-time tracking interface where equipment checkouts by students during lab sessions are recorded digitally. Lab assistants and staff can inspect returned equipment, update item statuses (e.g., Working, Damaged), and automatically assess fines for broken components or tools.

---

## 🚀 Key Features

* **Digital Check-in / Check-out:** Fast logging of tools and equipment issued to students during lab sessions.
* **Item Classification:** Distinct handling for durable equipment (multi-use instruments) and consumable tools.
* **Condition & Damage Assessment:** Staff can flag items as intact or damaged upon return.
* **Fine & Penalty Management:** Integrated calculation of repair or replacement fees directly attributed to student incidents.
* **Database Backing:** Persistent storage of items, logs, and fines via MySQL.

---

## 🏗️ Project Architecture

```text
LabSphere/
├── backend/
│   ├── Bookable.java               # Interface for reservable/issueable resources
│   ├── ConsumableTool.java         # Class handling consumable/disposable tools
│   ├── Equipment.java              # Model for durable instruments and devices
│   ├── LabItem.java                # Base entity class for laboratory items
│   ├── LabInventorySystem.java     # Core business logic and database interactions
│   ├── LabServer.java              # Backend server handling frontend client requests
│   └── mysql-connector-j-26.7.0.jar# JDBC driver for MySQL database integration
└── frontend/
    ├── index.html                  # Lab dashboard interface for students and staff
    └── style.css                   # Custom responsive styling
