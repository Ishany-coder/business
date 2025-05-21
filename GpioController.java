import java.io.*;

public class GpioController {
    private final int pin;
    private final File directionFile;
    private final File valueFile;

    public GpioController(int pin) throws IOException {
        this.pin = pin;

        File gpioDir = new File("/sys/class/gpio/gpio" + pin);
        if (!gpioDir.exists()) {
            writeToFile(new File("/sys/class/gpio/export"), String.valueOf(pin));
        }

        this.directionFile = new File("/sys/class/gpio/gpio" + pin + "/direction");
        this.valueFile = new File("/sys/class/gpio/gpio" + pin + "/value");

        writeToFile(directionFile, "out");
    }

    public void turnOn() throws IOException {
        writeToFile(valueFile, "1");
    }

    public void turnOff() throws IOException {
        writeToFile(valueFile, "0");
    }

    public boolean isOn() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(valueFile))) {
            return "1".equals(reader.readLine().trim());
        }
    }

    private void writeToFile(File file, String value) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(value);
        }
    }
}