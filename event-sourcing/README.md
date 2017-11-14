# Overview

This repository serves as sample code for my talk [An Abridged Guide to Event Sourcing](https://www.slideshare.net/holograph/an-abridged-guide-to-event-sourcing). This is a full-blown implementation of an event-sourced blob store microservice using Java 8 on top of [Dropwizard](http://www.dropwizard.io/).
 
The `event-sourcing` project contains the following modules:

* [core](core) defines the event model, the materializer (which transforms events to the full domain model), the interfaces for the event/snapshot stores, and the actual service orchestrating all of the moving pieces;
* [lean-and-mean](lean-and-mean) provides a full-blown implementation of the service on top of Dropwizard, including MySQL-based storage and a full REST API;
* [guice](guice) showcases how the same components can be wired using an IoC/DI container; and
* [jasmine](jasmine) contains a browser-runnable test suite that verifies the REST API behaves as intended.

# Quickstart

1. Clone the repo locally:
   ```
   git clone git@github.com:holograph/examples.git
   ```
2. Build the project:
   ```
   cd event-sourcing && mvn install
   ```
3. Start a test server (requires a local installation of [Docker](https://www.docker.com/) with `docker-compose`):
   ```
   cd lean-and-mean && docker-compose up
   ```
4. Run the [test suite](jasmine/SpecRunner.html)

The server is accessible at port 8080 on your local machine. This is obviously not intended for production use, but your mileage may vary :-)

# Questions?

Feel free to open an issue, send a pull request or simply e-mail me at [tomer@tomergabel.com](mailto:tomer@tomergabel.com).

