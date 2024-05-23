
# Welcome!

In this assignment, we need you to implement an API (`public-api`) that exposes our internal API (`addition-service`), which performs addition of two numbers. Unfortunately the implementation of the addition service is a mixture of a synchronous rest interface and asynchronous results on a kafka topic. The topic is named `addition-service.results` and the events are in json format. Our internal system is poorly documented, but the source code is provided the folder `./addition-service/`.

* The API specification you'll implement is documented in `./public-api.yml`, and the `./docker-compose.yml` describes the system.
* Place all your implementation files in the `./public-api/` folder. You're not allowed to change any of the files outside `./public-api/`.
* Use `./start.sh` to rebuild and start the system.

Feel free to provide improvement suggestions for all the parts of this assignment.

Some constraints to consider:
 - If you implement your solution using C#, please use .NET Core.
 - If you implement your solution using Java, please use Spring Boot.
 - If you implement your solution using Python, please use Python Flask.

When reviewing your submitted solution, we will consider security, technical correctness, code quality as well as performance/scalability.

Please submit your solution in a way you deem fit. Pull requests are preferred.

Good luck!
