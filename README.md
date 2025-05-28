# 🌱 Smart Plant Care System

This Java project uses OpenAI's API to create intelligent schedules for watering and lighting plants based on the plant's name. It is designed to run on a Raspberry Pi and control real-world devices using GPIO pins.

---

## 🚀 Features

- 🌿 Dynamically fetches watering and light schedules using OpenAI GPT
- ⚡ Controls GPIO pins to activate water pumps and grow lights
- 🧠 Modular structure with clean separation between scheduling, AI logic, and GPIO control
- ✅ Validates time ranges and logs schedules at runtime
- 🔌 Graceful shutdown to turn off all devices

---

## 🛠️ Project Structure

```
backendCode/
├── Main.java                  # Entry point
├── api/
│   └── OpenAICall.java        # Handles OpenAI API requests
├── logic/
│   └── PlantAdvisor.java      # Combines AI and logic
├── scheduler/
│   └── DeviceScheduler.java   # Schedules device activation
└── GpioController.java        # Handles turning devices on/off
```

---

## 🧪 Requirements

- Java 17+
- Raspberry Pi with Pi4J v2
- `.env` file with your OpenAI API key:
  ```
  OPENAI_API_KEY=your-api-key-here
  ```
- Add these dependencies in `build.gradle`:
  ```groovy
  implementation 'org.json:json:20231013'
  implementation 'io.github.cdimascio:dotenv-java:2.3.2'
  implementation 'com.pi4j:pi4j-core:2.3.0'
  implementation 'com.pi4j:pi4j-plugin-raspberrypi:2.3.0'
  ```

---

## 🧠 How It Works

1. You run the app with a plant name:
   ```bash
   java Main basil
   ```

2. The app queries OpenAI for optimal watering and lighting times for that plant.

3. `DeviceScheduler` schedules activation windows based on those times.

4. `GpioController` handles the actual GPIO pin toggling.


---

## 🙌 Contributors

- [@Ishany-coder](https://github.com/Ishany-coder)
- [@Rohitdroid99](https://github.com/Rohitdroid99)
