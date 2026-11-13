# RPC HelloWorld — SOAP Web Service Demo

A simple Java project demonstrating **Remote Procedure Call (RPC)** over **SOAP** using **JAX-WS** (Jakarta XML Web Services). The client invokes a remote `sayHello()` method on a server, and the server returns a greeting — all transported as XML over HTTP via the SOAP protocol.

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

## Project Structure

```
lab4/
├── readme.md                     # This file
└── RPC_HelloWorld/
    ├── pom.xml                   # Maven build file (Java 21, JAX-WS, exec plugin)
    └── src/main/java/main/java/
        ├── HelloWorldServer.java # @WebService interface with @WebMethod sayHello()
        ├── HelloWorldServerInterface.java # placeholder class
        └── HelloWorldClient.java # placeholder class
```

### `HelloWorldServer.java`

Despite the file name, this is the **service endpoint interface (SEI)** — the contract between client and server:

```java
@WebService(
    name = "HelloWorldServer",
    targetNamespace = "http://server/"
)
public interface HelloWorldServer {
    @WebMethod
    String sayHello();
}
```

- `@WebService` marks the interface as a SOAP web service.
- `@WebMethod` exposes `sayHello()` as a remotely callable operation.
- The client calls `sayHello()` and receives a `String` response.

### `HelloWorldServerInterface.java` & `HelloWorldClient.java`

Placeholder classes intended for the server implementation (endpoint publisher) and the client stub that invokes the remote method. These are to be filled in as part of the lab exercise.

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

1. The client obtains a proxy (stub) for `HelloWorldServer`.
2. Calling `proxy.sayHello()` triggers JAX-WS to build a SOAP XML request.
3. The request is sent to the server's HTTP endpoint.
4. The server's JAX-WS runtime unmarshals the XML, invokes the method, and returns a SOAP response.
5. The client receives a plain Java `String`.

---

## Prerequisites

- **Java 21** (or later) — `maven.compiler.release` is set to `21` in `pom.xml`
- **Apache Maven** 3.6+

---

## Build & Run

From the project root:

```bash
# Compile the project
cd RPC_HelloWorld
mvn compile

# Run (once HelloWorldServer and HelloWorldClient are implemented)
mvn exec:java
```

The `exec-maven-plugin` is configured in `pom.xml` to launch the server and client classes.

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
