package main.java;

import jakarta.jws.WebService;
import jakarta.xml.ws.Endpoint;

@WebService(endpointInterface = "HelloWorldServerInterface", serviceName = "HelloWorldServerService", // Must match the
                                                                                                      // client's
                                                                                                      // service name.
        portName = "HelloWorldServerPort", // Must match the client's port name.
        targetNamespace = "http://server/" // Keep consistent with the interface and client.
)
public class HelloWorldServer implements HelloWorldServerInterface {
    @Override
    public String sayHello() {
        return "Hello, World!"; // Customize the response as needed. This is the message that will be sent back
                                // to the client when it invokes the sayHello method.
    }

    public static void main(String[] args) {
        address = "http://localhost:8080/HelloWorld"; // Change the port/path here if needed.
        Endpoint.publish(address, new HelloWorldServer()); // Publish the SOAP service.
        System.out.println("SOAP server started.");
        System.out.println("WSDL: " + address + "?wsdl"); // WSDL describes the service.
    }

}
