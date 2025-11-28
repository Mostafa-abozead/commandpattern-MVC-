# Smart Home Automation - Command Design Pattern with Strict MVC

## 1. Conceptual Mapping

### Architecture Overview

This implementation demonstrates a **Strict Model-View-Controller (MVC)** architecture integrated with the **Command Design Pattern**.

### MVC Components

| MVC Layer | Implementation | Description |
|-----------|----------------|-------------|
| **View** | `dashboard.html` | Thymeleaf template that displays the Smart Home Dashboard. Shows light status and provides control buttons. |
| **Controller** | `DashboardController` | Receives requests, creates/executes Commands, updates Model, and returns View name. Uses `@Controller` annotation. |
| **Model** | `LightState` | Simple POJO that carries data (light status) from Controller to View. |

### Command Pattern Participants

| Pattern Role | Implementation | Description |
|--------------|----------------|-------------|
| **Invoker** | `DashboardController` | Creates Command objects and calls `execute()` without knowing implementation details. |
| **Receiver** | `LightService` | Contains the actual business/hardware logic for controlling the smart light bulb. |
| **Command Interface** | `Command` | Interface that declares the `execute()` method contract. |
| **Concrete Commands** | `LightOnCommand`, `LightOffCommand` | Encapsulate specific actions and delegate to the Receiver. |

### Complete MVC + Command Pattern Flow

```
User → View (dashboard.html) → Controller → Command → Service (Receiver) → Update Model → Return View → User
```

### Key Benefits

1. **MVC Separation**: View only knows about Model data, not about Services or Commands.
2. **Command Decoupling**: Controller doesn't know HOW to control the light, only Commands know.
3. **Single Responsibility**: Each layer has one job.
4. **Testability**: Each component can be tested independently.

---

## 2. PlantUML Class Diagram

```plantuml
@startuml
skinparam shadowing false
skinparam monochrome true

title Smart Home Dashboard - Class Diagram\n(Strict MVC Architecture + Command Design Pattern)

' ========================================
' View Layer (MVC)
' ========================================
package "View Layer" #LightBlue {
    class "dashboard.html" <<View>> <<Thymeleaf>> {
        Displays light status
        Shows ON/OFF buttons
        Renders LightState data
    }
}

' ========================================
' Controller Layer (MVC + Invoker)
' ========================================
package "Controller Layer" #LightGray {
    class DashboardController <<Invoker>> <<@Controller>> {
        -lightService: LightService
        +showDashboard(model: Model): String
        +turnLightOn(model: Model): String
        +turnLightOff(model: Model): String
    }
}

' ========================================
' Model Layer (MVC)
' ========================================
package "Model Layer" #LightGreen {
    class LightState <<Model>> <<POJO>> {
        -on: boolean
        -statusMessage: String
        +isOn(): boolean
        +setOn(on: boolean): void
        +getStatusMessage(): String
        +setStatusMessage(msg: String): void
    }
}

' ========================================
' Command Pattern Layer
' ========================================
package "Command Pattern" #White {
    interface Command <<Command Interface>> {
        +execute(): String
    }
    
    class LightOnCommand <<Concrete Command>> {
        -lightService: LightService
        +LightOnCommand(lightService: LightService)
        +execute(): String
    }
    
    class LightOffCommand <<Concrete Command>> {
        -lightService: LightService
        +LightOffCommand(lightService: LightService)
        +execute(): String
    }
}

' ========================================
' Service Layer (Receiver)
' ========================================
package "Service Layer" #LightYellow {
    class LightService <<Receiver>> <<@Service>> {
        -isOn: boolean
        +turnOn(): String
        +turnOff(): String
        +isLightOn(): boolean
        +getStatus(): String
    }
}

' ========================================
' Relationships
' ========================================

' View-Controller
"dashboard.html" ..> DashboardController : HTTP Request
DashboardController ..> "dashboard.html" : returns view name

' Controller-Model
DashboardController --> LightState : creates & populates

' View-Model
"dashboard.html" ..> LightState : displays data

' Controller-Command
DashboardController ..> Command : creates & executes

' Command hierarchy
LightOnCommand ..|> Command : implements
LightOffCommand ..|> Command : implements

' Command-Service
LightOnCommand --> LightService : delegates to
LightOffCommand --> LightService : delegates to

' Controller-Service (for dependency injection)
DashboardController --> LightService : @Autowired

' ========================================
' Legend
' ========================================
legend right
  |= Stereotype |= Description |
  | <<View>> | HTML template (Thymeleaf) |
  | <<@Controller>> | Spring MVC Controller |
  | <<Model>> | Data carrier POJO |
  | <<Invoker>> | Creates & executes Commands |
  | <<Command Interface>> | Declares execute() |
  | <<Concrete Command>> | Encapsulates action |
  | <<Receiver>> | Performs actual work |
  | <<@Service>> | Spring Service bean |
endlegend

@enduml
```

---

## 3. PlantUML Sequence Diagram

```plantuml
@startuml
skinparam shadowing false
skinparam monochrome true

title Turn On Light - Complete MVC + Command Pattern Flow

' ========================================
' Participants grouped by Layer
' ========================================
actor "User" as user

box "View Layer" #LightBlue
    participant "dashboard.html\n<<View>>" as view
end box

box "Controller Layer" #LightGray
    participant "DashboardController\n<<Invoker>>\n<<@Controller>>" as controller
end box

box "Command Pattern" #White
    participant "LightOnCommand\n<<Concrete Command>>" as command
end box

box "Service Layer" #LightYellow
    participant "LightService\n<<Receiver>>\n<<@Service>>" as service
end box

box "Model Layer" #LightGreen
    participant "LightState\n<<Model>>" as model
end box

' ========================================
' Sequence Flow - Complete MVC Cycle
' ========================================

== User Interaction ==
user -> view : Click "Turn ON" button
activate view

view -> controller : GET /light/on
activate controller
deactivate view

== Command Pattern Execution ==
note right of controller
  **Step 1: Invoker receives request**
  Controller does NOT know HOW
  to turn on the light
end note

controller -> command ** : new LightOnCommand(lightService)

note right of command
  **Step 2: Create Command**
  Command holds reference to Receiver
end note

controller -> command : execute()
activate command

command -> service : turnOn()
activate service

note right of service
  **Step 3: Receiver performs action**
  Actual hardware logic here
end note

service --> command : "Light is ON"
deactivate service

command --> controller : "Light is ON"
deactivate command

== Model Update ==
controller -> model ** : new LightState(true, "Light is ON")

note right of model
  **Step 4: Update Model**
  Model carries data to View
end note

controller -> controller : model.addAttribute("lightState", lightState)

== Return View ==
note right of controller
  **Step 5: Return View name**
  Controller returns "dashboard"
end note

controller --> view : return "dashboard"
activate view
deactivate controller

== View Rendering ==
view -> model : Read lightState.on
view -> model : Read lightState.statusMessage

note right of view
  **Step 6: Render View**
  Thymeleaf displays Model data
end note

view --> user : Display updated dashboard
deactivate view

@enduml
```

---

## 4. Java Spring Boot Implementation

### File Structure
```
src/main/java/com/smarthome/
├── SmartHomeApplication.java      # Main application entry point
├── command/
│   ├── Command.java               # Command interface
│   ├── LightOnCommand.java        # Concrete command to turn ON
│   └── LightOffCommand.java       # Concrete command to turn OFF
├── controller/
│   ├── DashboardController.java   # MVC Controller (Invoker) - uses @Controller
│   └── SmartHomeController.java   # REST Controller (for API access)
├── model/
│   └── LightState.java            # Model POJO for View data
└── service/
    └── LightService.java          # Light service (Receiver)

src/main/resources/
└── templates/
    └── dashboard.html             # Thymeleaf View template
```

### How to Run

1. **Build the application:**
   ```bash
   mvn clean install
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Access the Dashboard:**
   - Open browser: `http://localhost:8080/dashboard`
   - Click "Turn ON" or "Turn OFF" buttons

4. **Alternative REST API:**
   - Turn ON: `curl http://localhost:8080/light/on`
   - Turn OFF: `curl http://localhost:8080/light/off`
   - Status: `curl http://localhost:8080/light/status`

---

## Summary

This implementation demonstrates a **Strict MVC Architecture** integrated with the **Command Design Pattern**:

| Layer | Component | Role |
|-------|-----------|------|
| **View** | `dashboard.html` | Displays UI, shows Model data |
| **Controller** | `DashboardController` | Receives requests, executes Commands, updates Model, returns View |
| **Model** | `LightState` | Data carrier POJO between Controller and View |
| **Command** | `Command`, `LightOnCommand`, `LightOffCommand` | Encapsulates actions, provides decoupling |
| **Service** | `LightService` | Receiver - performs actual hardware logic |

The complete flow is: **View → Controller → Command → Service → Update Model → Return View**

This architecture provides:
- **Clear separation** between UI (View), logic (Controller/Command), and data (Model)
- **Decoupling** via Command Pattern - Controller doesn't know HOW to control the light
- **Testability** - each component can be tested independently
- **Maintainability** - changes in one layer don't affect others
