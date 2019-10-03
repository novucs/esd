# Enterprise Systems Development Project
*We're not entirely sure what it is yet. Though we do know we need Glassfish,
Derby, and JDK8. This project currently provides tooling for the ESD tech stack
that is compatible with Docker, IntelliJ, and NetBeans.*

## Quickstart
Requirements:
* [jdk8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [gradle](https://gradle.org/install/)
* [git](https://git-scm.com/)
* [docker](https://docs.docker.com/get-started/)
* [docker-compose](https://docs.docker.com/compose/)

Steps:
1. Clone the repo `git clone https://github.com/novucs/esd.git && cd esd`
2. Run the project `docker-compose up -d`
3. [Open the app in your web browser](http://localhost:8080/app/)

To automate local deployments while editing code: `./scripts/autodeploy.sh`

## IDE Setup
IntelliJ:
* `File -> Open -> <select repo location>`

NetBeans:
* `File -> Open Project -> <select repo location>`
