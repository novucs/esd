# ESD - CI/CD Pipeline
Here has all the necessary components for setting up the Jenkins CI/CD pipeline for the 
ESD project.

## Requirements
* [docker-compose](https://docs.docker.com/compose/)

## Usage
1. Create a user account for jenkins. All files created should be in this directory.

    Create a file named `jenkins_user.txt`, with one line containing your desired jenkins username.

    Create a file named `jenkins_pass.txt`, with one line containing your desired jenkins password.

2. Provide Jenkins with a GitHub bot account credentials. It must have access to the repo.

    Create a file named `github_user.txt`, with one line containing the GitHub bot account username.

    Create a file named `github_pass.txt`, with one line containing the GitHub bot account password.

3. Start the services.

    Execute the following: `docker-compose up -d`

    The services should be successfully built and running, to double check execute:
    `docker-compose ps`

4. Add a Webhook to the GitHub repo.

    Go to the GitHub repository -> Settings -> Webhooks -> Add webhook

    Set the Payload URL to wherever your CI/CD is deployed.

    Click `Let me select individual events`

    Select:
    * Branch or tag creation
    * Branch or tag deletion
    * Pull requests
    * Pushes

    Click `Add webhook`

5. Accessing the services:

    Jenkins should be found at: `hostname:8081`

    Application deployments should be found at: `hostname:8080`
