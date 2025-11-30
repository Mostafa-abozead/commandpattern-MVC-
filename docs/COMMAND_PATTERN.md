# Smart Home Automation - Strict Command Design Pattern with MVC

## 1. Conceptual Mapping

### Architecture Overview

This implementation demonstrates a **Strict Command Design Pattern** integrated with **Model-View-Controller (MVC)** architecture. The architecture has been refactored to achieve strict separation of concerns by extracting the Invoker logic into a dedicated `CommandInvoker` class.

### Key Constraints (Strict Command Pattern with Queued Invoker)

1. **Dedicated Invoker Class**: The `CommandInvoker` class acts as the sole Invoker, maintaining a FIFO queue of commands.
2. **Controller as Client**: The `DashboardController` acts as a Client, not an Invoker. It identifies commands and pushes them to the `CommandInvoker`.
3. **Strict Decoupling**: The Controller has **NO direct reference** to the Receiver (LightService). It communicates **ONLY via the CommandInvoker**.
4. **FIFO Queue**: Commands are executed sequentially in the order they are pushed.
5. **Clean Diagram**: No direct relationship between View and Model class - Controller handles all data flow.

### MVC Components

| MVC Layer | Implementation | Description |
|-----------|----------------|-------------|
| **View** | `dashboard.html` | Thymeleaf template that displays the Smart Home Dashboard. |
| **Controller** | `DashboardController` | Client - identifies Command beans, pushes to Invoker, triggers execution, returns View name. |
| **Model** | `LightState` | POJO that carries data from Controller to View. |

### Command Pattern Participants

| Pattern Role | Implementation | Description |
|--------------|----------------|-------------|
| **Invoker** | `CommandInvoker` | Dedicated class that maintains FIFO queue, executes commands sequentially. |
| **Client** | `DashboardController` | Identifies commands, pushes to Invoker, triggers execution. Has NO reference to Receiver. |
| **Command Interface** | `Command` | Declares `execute()` method returning `LightState`. |
| **Concrete Commands** | `LightOnCommand`, `LightOffCommand`, `GetStatusCommand` | Hold reference to Receiver, encapsulate actions. |
| **Receiver** | `LightService` | Contains actual hardware logic. Only Commands access it. |

### Refactored Execution Flow

```
Controller (Client) → Creates Command → Pushes Command to Invoker → Invoker processes Queue → Command.execute() → LightService (Receiver)
                                                                        ↓
                                                                   LightState (Model)
                                                                        ↓
                                                                  View (dashboard.html)
```

**Note**: Controller NEVER accesses LightService directly and NEVER calls execute() directly - only through the CommandInvoker.

---

## 2. PlantUML Class Diagram (Refactored with CommandInvoker)

```plantuml
@startuml
skinparam shadowing false
skinparam monochrome true

title Smart Home Dashboard - Refactored Command Pattern Class Diagram

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
' Controller Layer (Client)
' ========================================
package "Controller Layer" #LightGray {
    class DashboardController <<Client>> <<@Controller>> {
        -commandInvoker: CommandInvoker
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
' Invoker
' ========================================
package "Invoker Layer" #LightCoral {
    class CommandInvoker <<Invoker>> <<@Component>> {
        -commandQueue: Queue<Command>
        -command: Command
        +createCommand(command: Command): void
        +getCommand(): Command
        +pushCurrentCommand(): void
        +push(command: Command): void
        +executeCommands(): LightState
        +pushAndExecute(command: Command): LightState
        +getQueueSize(): int
        +clearQueue(): void
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
' Relationships (Refactored)
' ========================================

' Controller-Invoker (Controller depends on Invoker)
DashboardController --> CommandInvoker : uses

' Controller holds Command references (for identification)
DashboardController o-- Command : holds reference

' Controller-Model (Controller creates Model)
DashboardController --> LightState : creates

' Controller-View (Controller returns view name)
DashboardController ..> "dashboard.html" : returns "dashboard"

' View requests to Controller
"dashboard.html" ..> DashboardController : HTTP Request

' Invoker-Command (Invoker queues and executes Commands)
CommandInvoker o-- Command : queues & executes

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
' Notes for Refactored Architecture
' ========================================
note right of DashboardController
  **REFACTORED: Controller as Client**
  - NO direct execute() calls
  - Pushes Commands to Invoker
  - Triggers execution via Invoker
  - NO reference to LightService
end note

note right of CommandInvoker
  **NEW: Dedicated Invoker**
  - Maintains FIFO Queue
  - Executes Commands sequentially
  - push() adds to queue
  - executeCommands() processes queue
end note

note bottom of Command
  **Command returns LightState**
  Invoker gets Model from Command,
  Controller never touches the Receiver
end note

' ========================================
' Legend
' ========================================
legend right
  |= Stereotype |= Description |
  | <<Client>> | Identifies Command, pushes to Invoker |
  | <<Invoker>> | Maintains queue, executes commands |
  | <<Command Interface>> | Declares execute(): LightState |
  | <<Concrete Command>> | Holds Receiver reference |
  | <<Receiver>> | Actual hardware logic |
  | <<Model>> | Data carrier POJO |
  | <<View>> | HTML template |
  
  **KEY: No arrow between View and Model**
  **KEY: No arrow from Controller to LightService**
  **KEY: Controller depends on CommandInvoker**
endlegend

@enduml
```

---

## 3. PlantUML Sequence Diagram (Refactored with Queued Invoker)

```plantuml
@startuml
skinparam shadowing false
skinparam monochrome true

title Turn On Light - Refactored Command Pattern + MVC Flow

' ========================================
' Participants
' ========================================
actor "User" as user

box "View Layer" #LightBlue
    participant "dashboard.html\n<<View>>" as view
end box

box "Controller Layer" #LightGray
    participant "DashboardController\n<<Client>>\n<<@Controller>>" as controller
end box

box "Invoker Layer" #LightCoral
    participant "CommandInvoker\n<<Invoker>>\n<<@Component>>" as invoker
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

== Controller creates Command and pushes to Invoker ==
note right of controller
  **REFACTORED: Controller as Client**
  - Sets Command via createCommand()
  - Pushes Command via pushCurrentCommand()
  - Does NOT call execute() directly
end note

controller -> invoker : createCommand(lightOnCommand)
activate invoker
invoker -> invoker : this.command = lightOnCommand
invoker --> controller : void
deactivate invoker

controller -> invoker : pushCurrentCommand()
activate invoker
invoker -> invoker : commandQueue.add(this.command)
invoker --> controller : void
deactivate invoker

== Controller triggers execution via Invoker ==
controller -> invoker : executeCommands()
activate invoker

== Invoker processes Queue ==
note right of invoker
  **Invoker processes FIFO Queue**
  - Polls command from queue
  - Calls execute() on command
  - Returns result of last command
end note

invoker -> commandInterface : command.execute()
activate commandInterface

commandInterface -> command : execute()
activate command

== Command delegates to Receiver ==
note right of command
  **ONLY Command accesses Receiver**
  Controller and Invoker never touch LightService
end note

command -> service : turnOn()
activate service

service --> command : "Light is ON"
deactivate service

== Command creates and returns Model ==
command -> model ** : new LightState(true, "Light is ON")

command --> commandInterface : LightState
deactivate command

commandInterface --> invoker : LightState
deactivate commandInterface

invoker --> controller : LightState
deactivate invoker

== Controller adds Model and returns View ==
note right of controller
  **Controller receives Model from Invoker**
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
│   └── DashboardController.java   # Client: pushes to Invoker, NO LightService reference
├── invoker/
│   └── CommandInvoker.java        # NEW: Dedicated Invoker with FIFO queue
├── model/
│   └── LightState.java            # POJO: carries data to View
└── service/
    └── LightService.java          # Receiver: actual hardware logic

src/main/resources/templates/
└── dashboard.html                 # View: displays data
```

### Key Implementation Details

**CommandInvoker (New Dedicated Invoker):**
```java
@Component
public class CommandInvoker {
    // FIFO queue to hold pending commands
    private final Queue<Command> commandQueue;
    
    // Currently set command
    private Command command;

    public CommandInvoker() {
        this.commandQueue = new LinkedList<>();
    }

    // Create command before pushing
    public void createCommand(Command command) {
        this.command = command;
    }
    
    // Get currently set command
    public Command getCommand() {
        return this.command;
    }
    
    // Push the currently set command to queue
    public void pushCurrentCommand() {
        if (this.command == null) {
            throw new IllegalStateException("No command has been set. Call createCommand() first.");
        }
        commandQueue.add(this.command);
    }

    // Push command to queue (direct push)
    public void push(Command command) {
        if (command != null) {
            commandQueue.add(command);
        }
    }

    // Execute all pending commands sequentially
    public LightState executeCommands() {
        LightState lastState = null;
        while (!commandQueue.isEmpty()) {
            Command command = commandQueue.poll();
            lastState = command.execute();
        }
        return lastState;
    }
}
```

**DashboardController (Refactored as Client):**
```java
@Controller
public class DashboardController {
    // Controller now depends on CommandInvoker, not direct execution
    private final CommandInvoker commandInvoker;
    
    // Pre-configured commands
    private final Command lightOnCommand;
    private final Command lightOffCommand;
    private final Command getStatusCommand;
    
    // NO direct reference to LightService in methods!
    
    @GetMapping("/light/on")
    public String turnLightOn(Model model) {
        // Create the command first
        commandInvoker.createCommand(lightOnCommand);
        // Push to Invoker
        commandInvoker.pushCurrentCommand();
        // Trigger execution via Invoker
        LightState lightState = commandInvoker.executeCommands();
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

## Summary - Refactored Architecture

| Requirement | Implementation |
|-------------|----------------|
| **Dedicated Invoker Class** | `CommandInvoker` class with FIFO queue, createCommand/push/execute methods |
| **Create Command Before Push** | `createCommand()` allows setting which command before pushing via `pushCurrentCommand()` |
| **Controller as Client** | `DashboardController` creates command, pushes to Invoker, triggers execution, no direct execute() |
| **FIFO Queue** | `CommandInvoker` maintains `Queue<Command>` for sequential execution |
| **Strict Decoupling** | Controller has NO reference to `LightService` - only Commands do |
| **Execution Flow** | Controller → Invoker.createCommand() → Invoker.pushCurrentCommand() → Invoker.executeCommands() → Command.execute() → Service |
| **No View-Model direct arrow** | Controller handles all data flow between View and Model |
| **Command returns Model** | `execute()` returns `LightState`, not `String` |

This refactored architecture ensures:
1. The Controller (Client) is completely decoupled from the Receiver (LightService)
2. The Controller can create which command before pushing it to the queue
3. The Controller does NOT call execute() directly on commands
4. All command execution goes through the dedicated CommandInvoker
5. Commands can be queued and executed sequentially (FIFO)
6. Strict separation of concerns between Client, Invoker, Command, and Receiver
