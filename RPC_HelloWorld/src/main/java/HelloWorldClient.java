package main.java;

import jakarta.xml.ws.Service;
import javax.xml.namespace.QName;
import java.net.URL;

public class HelloWorldClient {
    public static void main(String[] args) throws Exception {
        URL wsdl = new URL(
                "http://localhost:8080/HelloWorld?wsdl" // Must match the server address.
        );
        QName serviceName = new QName(
                "http://server/", // Must match the service namespace.
                "HelloWorldServerService");
        QName portName = new QName(
                "http://server/",
                "HelloWorldServerPort" // Must match the server's portName.
        );
        Service service = Service.create(wsdl, serviceName); // Read the WSDL service description.
        HelloWorldServerInterface client = service.getPort(portName, HelloWorldServerInterface.class); // Create the
                                                                                                       // remote client
        System.out.println(client.sayHello()); // Call the remote operation and print its reply.
    }
}