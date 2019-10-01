# Enterprise Systems Development Project
*We're not entirely sure what it is yet. Though we do know we need Glassfish,
Derby, and JDK11. This project currently provides tooling for the ESD tech stack
that is compatible with Docker, IntelliJ, and NetBeans.*

## Quickstart
Requirements:
* [jdk11](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
* [gradle](https://gradle.org/install/)
* [git](https://git-scm.com/)
* [docker](https://docs.docker.com/get-started/)
* [docker-compose](https://docs.docker.com/compose/)

Steps:
1. Clone the repo `git clone https://github.com/novucs/esd.git && cd esd`
2. Run the project `docker-compose up -d`
3. [Open the app in your web browser](http://localhost:8080/app/)

## IDE Setup
IntelliJ:
* `File -> Open -> <select repo location>`

NetBeans:
* `File -> Open Project -> <select repo location>`
