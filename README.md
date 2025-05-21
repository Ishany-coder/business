# 🌱 Self-Watering Flower Pot (Raspberry Pi Edition)

A smart flower pot powered by a Raspberry Pi that waters your plant automatically at scheduled times — ideal for plant lovers with busy lives.

## 🧠 What It Does

This project turns on a GPIO port at a scheduled **start time** (e.g., 6 AM) and keeps it on until a specified **end time** (e.g., 1 PM). It can be connected to a water pump or relay to automatically water plants.

- ✅ Custom start & end time
- ✅ Runs as a Java service
- ✅ Uses Pi4J to control Raspberry Pi GPIO
- ✅ Reliable hardware automation

## ⚙️ Hardware Requirements

- Raspberry Pi (any model with GPIO)
- Relay module or MOSFET to control a water pump
- Jumper wires
- Water pump (5V or 12V, depending on your relay)
- Power source (for the pump)

## 💻 Software Requirements

- Java (OpenJDK recommended)
- Pi4J (v1.4)
- Raspberry Pi OS

---

## 🔧 Installation

### 🥣 On your Raspberry Pi

Run the following commands to install Pi4J:

```bash
sudo apt-get update
sudo apt-get install libpi4j-java
