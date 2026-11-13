# RPC HelloWorld — SOAP Web Service Demo

A simple Java project demonstrating **Remote Procedure Call (RPC)** over **SOAP** using **JAX-WS** (Jakarta XML Web Services). The client invokes a remote `sayHello()` method on a server, and the server returns `"Hello, World!"` — all transported as XML over HTTP via the SOAP protocol.

---

## What is RPC?

**Remote Procedure Call (RPC)** is a protocol that allows a program to call a procedure (function/method) on a remote machine as if it were a local call. The caller does not need to know the network details — it simply invokes a method, and the RPC framework handles:

1. **Marshalling** — converting method parameters into a transmittable format
2. **Transport** — sending the request over the network to the server
3. **Unmarshalling** — decoding the request on the server and invoking the actual method
4. **Response** — returning the result back to the caller

In this lab, `HelloWorldClient` calls `sayHello()` on `HelloWorldServer`, which lives on a different JVM (potentially a different machine). The client receives a `String` result just like a normal local method call.

---

## What is SOAP?

**SOAP (Simple Object Access Protocol)** is an XML-based messaging protocol for exchanging structured information in distributed systems. A SOAP message is an XML envelope containing:

- **Envelope** — the root element identifying the document as a SOAP message
- **Header** (optional) — metadata such as authentication or routing info
- **Body** — the actual call (method name and parameters) or response

SOAP typically rides on top of HTTP, which makes it firewall-friendly and widely interoperable across platforms and languages.

### SOAP in this project

This project uses **JAX-WS** (`jakarta.xml.ws`) to hide the raw XML. You write a plain Java interface annotated with `@WebService` and `@WebMethod`, and JAX-WS automatically:

- Generates the WSDL (Web Services Description Language) contract
- Handles SOAP/XML marshalling and unmarshalling
- Exposes the service over HTTP

The `pom.xml` includes `jaxws-rt 4.0.3` (the JAX-WS runtime) and `exec-maven-plugin 3.5.0` to run the server and client from Maven.

---

## What is WSDL?

**WSDL (Web Services Description Language)** is an XML file that describes what a web service does, where it lives, and how to call it. It acts as a machine-readable contract between the client and the server.

When the server starts, JAX-WS automatically generates the WSDL and makes it available at:

```
http://localhost:8080/HelloWorld?wsdl
```

The client reads this WSDL to discover:

- The service name (`HelloWorldServerService`)
- The port name (`HelloWorldServerPort`)
- The available operation (`sayHello()`)
- The network address (endpoint URL)

Using the WSDL, JAX-WS generates a client proxy so you can call `sayHello()` as if it were a local method.

---

## Project Structure

```
lab4/
├── readme.md                        # This file
├── .gitignore                       # Git ignore rules
└── RPC_HelloWorld/
    ├── pom.xml                      # Maven build file (Java 21, JAX-WS, exec plugin)
    └── src/main/java/main/java/
        ├── HelloWorldServerInterface.java  # Service Endpoint Interface (SEI)
        ├── HelloWorldServer.java           # Server implementation + publisher
        └── HelloWorldClient.java           # Client that invokes the remote method
```

### `HelloWorldServerInterface.java` — The Service Contract

This is the **Service Endpoint Interface (SEI)** — the contract between client and server. It declares *what* operations are available remotely:

```java
package main.java;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService(
    name = "HelloWorldServer",
    targetNamespace = "http://server/"
)
public interface HelloWorldServerInterface {
    @WebMethod
    String sayHello(); // The remote operation that the client can invoke on the server
}
```

- `@WebService` marks the interface as a SOAP web service.
- `@WebMethod` exposes `sayHello()` as a remotely callable operation.

### `HelloWorldServer.java` — The Implementation & Publisher

This class **implements** the SEI and **publishes** the SOAP endpoint:

```java
package main.java;

import jakarta.jws.WebService;
import jakarta.xml.ws.Endpoint;

@WebService(
    endpointInterface = "main.java.HelloWorldServerInterface",
    serviceName = "HelloWorldServerService",
    portName = "HelloWorldServerPort",
    targetNamespace = "http://server/"
)
public class HelloWorldServer implements HelloWorldServerInterface {

    @Override
    public String sayHello() {
        return "Hello, World!";
    }

    public static void main(String[] args) {
        String address = "http://localhost:8080/HelloWorld";
        Endpoint.publish(address, new HelloWorldServer());
        System.out.println("SOAP server started.");
        System.out.println("WSDL: " + address + "?wsdl");
    }
}
```

- `implements HelloWorldServerInterface` provides the actual business logic.
- `Endpoint.publish(...)` starts a lightweight SOAP server at `http://localhost:8080/HelloWorld`.
- The WSDL is automatically available at `http://localhost:8080/HelloWorld?wsdl`.

### `HelloWorldClient.java` — The Client

This class invokes the remote `sayHello()` method via a JAX-WS proxy:

```java
package main.java;

import jakarta.xml.ws.Service;
import javax.xml.namespace.QName;
import java.net.URL;

public class HelloWorldClient {
    public static void main(String[] args) throws Exception {
        URL wsdl = new URL("http://localhost:8080/HelloWorld?wsdl");
        QName serviceName = new QName("http://server/", "HelloWorldServerService");
        QName portName = new QName("http://server/", "HelloWorldServerPort");
        Service service = Service.create(wsdl, serviceName);
        HelloWorldServerInterface client = service.getPort(portName, HelloWorldServerInterface.class);
        System.out.println(client.sayHello());
    }
}
```

1. Downloads the WSDL from the server.
2. Creates a client proxy for `HelloWorldServerInterface`.
3. Calls `sayHello()` as if it were a local method.
4. Prints the server's response (`"Hello, World!"`).

---

## How It Works (RPC over SOAP flow)

```
Client                                          Server
  |                                                |
  |---- sayHello()  ------------------------------>|  (SOAP request XML over HTTP)
  |                            [JAX-WS unmarshals]|
  |                                                |-- invokes sayHello()
  |                                                |-- returns "Hello, World!"
  |<---- return String  ---------------------------|  (SOAP response XML over HTTP)
  |                                                |
```

1. The client obtains a proxy (stub) for `HelloWorldServerInterface`.
2. Calling `proxy.sayHello()` triggers JAX-WS to build a SOAP XML request (marshalling).
3. The request is sent over HTTP to the server's endpoint.
4. The server's JAX-WS runtime unmarshals the XML and invokes `sayHello()`.
5. The server returns a SOAP response; the client receives a plain Java `String`.

---

## Prerequisites

- **Java 21** (or later) — `maven.compiler.release` is set to `21` in `pom.xml`
- **Apache Maven** 3.6+

---

## Build & Run

### Step 1: Compile

From the project root:

```bash
cd RPC_HelloWorld
mvn compile
```

### Step 2: Start the server (Terminal 1)

The server must be running **before** the client:

```bash
mvn exec:java "-Dexec.mainClass=main.java.HelloWorldServer"
```

Expected output:

```
SOAP server started.
WSDL: http://localhost:8080/HelloWorld?wsdl
```

Keep this terminal running. The server blocks waiting for connections.

### Step 3: Run the client (Terminal 2)

Open a **new terminal window/tab**, then:

```bash
cd RPC_HelloWorld
mvn exec:java "-Dexec.mainClass=main.java.HelloWorldClient"
```

Expected output:

```
Hello, World!
```

---

## Troubleshooting

| Problem | Cause | Solution |
|---------|-------|----------|
| `ClassNotFoundException: HelloWorldClient` | Class is in package `main.java` | Always use the fully qualified name: `main.java.HelloWorldClient` |
| `Address already in use` | A previous server is still running on port 8080 | Kill the old process: `lsof -i :8080` → `kill -9 <PID>`, then restart |
| `Connection refused` | Client ran before the server | Start the server first, wait for "SOAP server started", then run the client |
| `-Dexec.mainClass` ignored | `mainClass` is set in `pom.xml` | Use `java -cp "target/classes:$(cat /tmp/cp.txt)" main.java.HelloWorldClient` directly |

### Freeing port 8080

If you get `Address already in use`:

```bash
# Find the process using port 8080
lsof -i :8080

# Kill it (replace <PID> with the actual number)
kill -9 <PID>
```

---

## Key JAX-WS Annotations

| Annotation | Purpose |
|------------|---------|
| `@WebService` | Marks a class/interface as a web service endpoint |
| `@WebMethod` | Exposes a method as a remotely callable SOAP operation |

---

## Technologies

- **Language:** Java 21
- **Build:** Apache Maven
- **Web Service:** JAX-WS (Jakarta XML Web Services) via `jaxws-rt 4.0.3`
- **Protocol:** SOAP over HTTP
- **Style:** RPC (Remote Procedure Call)
- **Contract:** WSDL (auto-generated by JAX-WS)
