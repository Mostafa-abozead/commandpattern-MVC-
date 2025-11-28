# Smart Home Automation - Strict Command Design Pattern with MVC

## 1. Conceptual Mapping

### Architecture Overview

This implementation demonstrates a **Strict Command Design Pattern** integrated with **Model-View-Controller (MVC)** architecture.

### Key Constraints (Strict Command Pattern)

1. **Invoker-Command Aggregation**: The Controller (Invoker) holds a `private Command command;` reference.
2. **Strict Decoupling**: The Controller has **NO direct reference** to the Receiver (LightService). It communicates **ONLY via the Command interface**.
3. **Clean Diagram**: No direct relationship between View and Model class - Controller handles all data flow.

### MVC Components

| MVC Layer | Implementation | Description |
|-----------|----------------|-------------|
| **View** | `dashboard.html` | Thymeleaf template that displays the Smart Home Dashboard. |
| **Controller** | `DashboardController` | Invoker - holds Command reference, executes commands, returns View name. |
| **Model** | `LightState` | POJO that carries data from Controller to View. |

### Command Pattern Participants

| Pattern Role | Implementation | Description |
|--------------|----------------|-------------|
| **Invoker** | `DashboardController` | Holds `private Command command;` and calls `execute()`. Has NO reference to Receiver. |
| **Command Interface** | `Command` | Declares `execute()` method returning `LightState`. |
| **Concrete Commands** | `LightOnCommand`, `LightOffCommand`, `GetStatusCommand` | Hold reference to Receiver, encapsulate actions. |
| **Receiver** | `LightService` | Contains actual hardware logic. Only Commands access it. |

### Strict Decoupling Flow

```
Controller (Invoker) → Command Interface → Concrete Command → LightService (Receiver)
                              ↓
                         LightState (Model)
                              ↓
                        View (dashboard.html)
```

**Note**: Controller NEVER accesses LightService directly - only through Commands.

---

## 2. PlantUML Class Diagram (Strict Compliance)

```plantuml
@startuml
skinparam shadowing false
skinparam monochrome true

title Smart Home Dashboard - Strict Command Pattern Class Diagram

' ========================================
' View Layer
' ========================================
package "View Layer" #LightBlue {
    class "dashboard.html" <<View>> <<Thymeleaf>> {
        Displays light status
        Shows ON/OFF buttons
    }
}

' ========================================
' Controller Layer (Invoker)
' ========================================
package "Controller Layer" #LightGray {
    class DashboardController <<Invoker>> <<@Controller>> {
        -command: Command
        -lightOnCommand: Command
        -lightOffCommand: Command
        -getStatusCommand: Command
        +showDashboard(model: Model): String
        +turnLightOn(model: Model): String
        +turnLightOff(model: Model): String
    }
}

' ========================================
' Model Layer
' ========================================
package "Model Layer" #LightGreen {
    class LightState <<Model>> <<POJO>> {
        -on: boolean
        -statusMessage: String
        +isOn(): boolean
        +getStatusMessage(): String
    }
}

' ========================================
' Command Pattern
' ========================================
package "Command Pattern" #White {
    interface Command <<Command Interface>> {
        +execute(): LightState
    }
    
    class LightOnCommand <<Concrete Command>> {
        -lightService: LightService
        +execute(): LightState
    }
    
    class LightOffCommand <<Concrete Command>> {
        -lightService: LightService
        +execute(): LightState
    }
    
    class GetStatusCommand <<Concrete Command>> {
        -lightService: LightService
        +execute(): LightState
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
' Relationships (Strict Compliance)
' ========================================

' Invoker-Command Aggregation (Controller holds Command reference)
DashboardController o-- Command : aggregation\n(private command)

' Controller-Model (Controller creates Model)
DashboardController --> LightState : creates

' Controller-View (Controller returns view name)
DashboardController ..> "dashboard.html" : returns "dashboard"

' View requests to Controller
"dashboard.html" ..> DashboardController : HTTP Request

' Command hierarchy
LightOnCommand ..|> Command : implements
LightOffCommand ..|> Command : implements
GetStatusCommand ..|> Command : implements

' Commands return Model
Command ..> LightState : returns

' Commands use Receiver (ONLY Commands access LightService)
LightOnCommand --> LightService : uses
LightOffCommand --> LightService : uses
GetStatusCommand --> LightService : uses

' ========================================
' Notes for Strict Compliance
' ========================================
note right of DashboardController
  **STRICT DECOUPLING**
  - Holds Command reference (Aggregation)
  - NO direct reference to LightService
  - Communicates ONLY via Command interface
end note

note bottom of Command
  **Command returns LightState**
  Controller gets Model from Command,
  never touches the Receiver
end note

' ========================================
' Legend
' ========================================
legend right
  |= Stereotype |= Description |
  | <<Invoker>> | Holds Command, calls execute() |
  | <<Command Interface>> | Declares execute(): LightState |
  | <<Concrete Command>> | Holds Receiver reference |
  | <<Receiver>> | Actual hardware logic |
  | <<Model>> | Data carrier POJO |
  | <<View>> | HTML template |
  
  **KEY: No arrow between View and Model**
  **KEY: No arrow from Controller to LightService**
endlegend

@enduml
```

---

## 3. PlantUML Sequence Diagram (Strict MVC Cycle)

```plantuml
@startuml
skinparam shadowing false
skinparam monochrome true

title Turn On Light - Strict Command Pattern + MVC Flow

' ========================================
' Participants
' ========================================
actor "User" as user

box "View Layer" #LightBlue
    participant "dashboard.html\n<<View>>" as view
end box

box "Controller Layer" #LightGray
    participant "DashboardController\n<<Invoker>>\n<<@Controller>>" as controller
end box

box "Command Pattern" #White
    participant "Command\n<<Interface>>" as commandInterface
    participant "LightOnCommand\n<<Concrete Command>>" as command
end box

box "Service Layer" #LightYellow
    participant "LightService\n<<Receiver>>" as service
end box

box "Model Layer" #LightGreen
    participant "LightState\n<<Model>>" as model
end box

' ========================================
' Sequence Flow
' ========================================

== User clicks button on View ==
user -> view : Click "Turn ON" button
activate view

view -> controller : GET /light/on
activate controller
deactivate view

== Controller sets Command (Aggregation) ==
note right of controller
  **STRICT: Controller holds Command**
  this.command = lightOnCommand
  (NO reference to LightService)
end note

controller -> controller : this.command = lightOnCommand

== Controller executes Command ==
controller -> commandInterface : command.execute()
activate commandInterface

commandInterface -> command : execute()
activate command

== Command delegates to Receiver ==
note right of command
  **ONLY Command accesses Receiver**
  Controller never touches LightService
end note

command -> service : turnOn()
activate service

service --> command : "Light is ON"
deactivate service

== Command creates and returns Model ==
command -> model ** : new LightState(true, "Light is ON")

command --> commandInterface : LightState
deactivate command

commandInterface --> controller : LightState
deactivate commandInterface

== Controller adds Model and returns View ==
note right of controller
  **Controller receives Model from Command**
  Never accesses Receiver directly
end note

controller -> controller : model.addAttribute("lightState", lightState)

controller --> view : return "dashboard"
activate view
deactivate controller

== View renders with Model data ==
view --> user : Display updated dashboard
deactivate view

@enduml
```

---

## 4. Java Spring Boot Implementation

### File Structure
```
src/main/java/com/smarthome/
├── SmartHomeApplication.java
├── command/
│   ├── Command.java               # Interface: execute() returns LightState
│   ├── LightOnCommand.java        # Concrete: holds LightService
│   ├── LightOffCommand.java       # Concrete: holds LightService
│   └── GetStatusCommand.java      # Concrete: holds LightService
├── controller/
│   └── DashboardController.java   # Invoker: holds Command, NO LightService reference
├── model/
│   └── LightState.java            # POJO: carries data to View
└── service/
    └── LightService.java          # Receiver: actual hardware logic

src/main/resources/templates/
└── dashboard.html                 # View: displays data
```

### Key Implementation Details

**DashboardController (Invoker):**
```java
@Controller
public class DashboardController {
    // STRICT: Holds Command reference (Aggregation)
    private Command command;
    
    // Pre-configured commands
    private final Command lightOnCommand;
    private final Command lightOffCommand;
    private final Command getStatusCommand;
    
    // NO direct reference to LightService in methods!
    
    @GetMapping("/light/on")
    public String turnLightOn(Model model) {
        this.command = lightOnCommand;        // Set command
        LightState lightState = command.execute();  // Execute
        model.addAttribute("lightState", lightState);
        return "dashboard";
    }
}
```

**Command Interface:**
```java
public interface Command {
    LightState execute();  // Returns Model, not String
}
```

**Concrete Command:**
```java
public class LightOnCommand implements Command {
    private final LightService lightService;  // Only Commands hold Receiver
    
    @Override
    public LightState execute() {
        String result = lightService.turnOn();
        return new LightState(lightService.isLightOn(), result);
    }
}
```

---

## Summary - Strict Compliance

| Requirement | Implementation |
|-------------|----------------|
| **Invoker-Command Aggregation** | `DashboardController` has `private Command command;` field |
| **Strict Decoupling** | Controller has NO reference to `LightService` - only Commands do |
| **No View-Model direct arrow** | Controller handles all data flow between View and Model |
| **Command returns Model** | `execute()` returns `LightState`, not `String` |

This architecture ensures the Controller (Invoker) is completely decoupled from the Receiver (LightService) and communicates **ONLY through the Command interface**.
