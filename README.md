# Smart Home Automation - Command Design Pattern (MVC)

A Spring Boot MVC web application demonstrating the **Command Design Pattern** for Smart Home IoT device control.

## 📋 Overview

This project implements a web dashboard that controls a smart light bulb using the Command Design Pattern. The architecture cleanly separates concerns:

- **Invoker (SmartHomeController)**: Receives HTTP requests and creates/executes commands
- **Command Interface**: Defines the `execute()` contract for all commands
- **Concrete Commands**: `LightOnCommand` and `LightOffCommand` encapsulate specific actions
- **Receiver (LightService)**: Contains the actual hardware control logic

## 🏗️ Architecture

```
┌─────────────────┐    ┌───────────────────┐    ┌─────────────────┐
│      User       │───▶│ SmartHomeController│───▶│    Command      │
│  (Web Dashboard)│    │    (Invoker)       │    │   (Interface)   │
└─────────────────┘    └───────────────────┘    └────────┬────────┘
                                                          │
                              ┌───────────────────────────┴───────────────────────────┐
                              │                           │                           │
                       ┌──────▼──────┐              ┌─────▼───────┐                    │
                       │LightOnCommand│              │LightOffCommand│                    │
                       │  (Concrete)  │              │  (Concrete)   │                    │
                       └──────┬───────┘              └──────┬────────┘                    │
                              │                             │                              │
                              └─────────────┬───────────────┘                              │
                                            │                                              │
                                     ┌──────▼──────┐                                       │
                                     │ LightService │◀──────────────────────────────────────┘
                                     │  (Receiver)  │
                                     └──────────────┘
```

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Build the Application

```bash
mvn clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

### Test the Endpoints

```bash
# Turn ON the light
curl http://localhost:8080/light/on

# Turn OFF the light
curl http://localhost:8080/light/off

# Check light status
curl http://localhost:8080/light/status
```

## 📁 Project Structure

```
src/main/java/com/smarthome/
├── SmartHomeApplication.java      # Main application entry point
├── command/
│   ├── Command.java               # Command interface
│   ├── LightOnCommand.java        # Concrete command to turn ON
│   └── LightOffCommand.java       # Concrete command to turn OFF
├── controller/
│   └── SmartHomeController.java   # REST controller (Invoker)
└── service/
    └── LightService.java          # Light service (Receiver)

docs/
└── COMMAND_PATTERN.md             # Detailed documentation with UML diagrams
```

## 📚 Documentation

For detailed documentation including:
- Conceptual Mapping
- PlantUML Class Diagram
- PlantUML Sequence Diagram
- Implementation Details

See [docs/COMMAND_PATTERN.md](docs/COMMAND_PATTERN.md)

## 🧪 Running Tests

```bash
mvn test
```

## ✨ Key Benefits of Command Pattern

1. **Decoupling**: The Controller doesn't know HOW to control the light—only the Command and Service know that
2. **Single Responsibility**: Each class has one job
3. **Open/Closed Principle**: New commands can be added without modifying existing code
4. **Testability**: Each component can be tested independently
5. **Extensibility**: Easy to add features like undo/redo, command history, or command queuing

## 📝 License

This project is for educational purposes as part of a university assignment.