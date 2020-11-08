### Spring Cloud Messaging
This sample project shows how easy it is to test messaging (e.g. Kafka, RabbitMQ etc.)
with [Spring Cloud Stream](https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/).

This project contains three examples (consumer, producer and function)

The Consumer Bean receives a message containing a Person object and just logs it.
The corresponding tests send such a message in multiple ways, as a String, as a Person object or as a Map<String,Object> 

The Producer example uses a [Stream Bridge](https://docs.spring.io/spring-cloud-stream/docs/3.0.10.RELEASE/reference/html/spring-cloud-stream.html#_sending_arbitrary_data_to_an_output_e_g_foreign_event_driven_sources)
do send data to an output destination.

The Function example reads from an input source and sends the converted data to an output destination.

Each example has its own configuration (based on the corresponding spring profile) so it is easier to see what needs to be added.

As you can see in the pom.xml, you don't even need a messaging dependency for writing and testing all the code.


### Additional Links (provided by the friendly folks from [Spring Initializr](https://start.spring.io))
These additional references should also help you:

* [Various sample apps using Spring Cloud Function](https://github.com/spring-cloud/spring-cloud-function/tree/master/spring-cloud-function-samples)

