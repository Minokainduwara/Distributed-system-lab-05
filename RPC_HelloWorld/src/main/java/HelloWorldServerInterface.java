package main.java;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService(
    name = "HelloWorldServer",
    targetNamespace = "http://server/"
)

public interface HelloWorldServerInterface {
    @WebMethod
    String sayHello(); //The remote operation that the client can invoke on the server
}
