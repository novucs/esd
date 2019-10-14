pipeline {
    agent {
        label 'master'
    }
    options {
        disableConcurrentBuilds()
    }
    stages {
        stage('build') {
            steps {
                sh './gradlew autodeploy'
            }
        }
        stage('deploy') {
            steps {
                sh "ssh billy@esd.novucs.net bash -c 'cd  ; docker cp \$(docker-compose ps -c ~/esd/cicd/docker-compose.yml -q jenkins):${pwd()}/build/libs/*.war /tmp/app.war && docker cp /tmp/app.war \$(docker-compose ps -c ~/esd/cicd/docker-compose.yml -q app):/app/glassfish/domains/domain1/autodeploy/app.war' "
            }
        }
    }
}
