pipeline {
    agent any

    stages {
        stage("Code") {
            steps {
                git url: "https://github.com/Akartal03/garage-api.git", branch: "main"
            }
        }


        stage("Build") {
           agent {
                  docker { image 'maven:3-alpine'
                          }
                   }
            steps {
                sh "whoami"
                sh "mvn clean install -DskipTests"
                sh "docker build -t garage-api:1.0.0 ."
            }
        }
        stage("Docker push") {
            steps {
                withCredentials([usernamePassword(credentialsId: 'DockerCreds', usernameVariable: '2974', passwordVariable: 'Tensai10*')]) {
                    sh "docker login -u $DockerUsername -p $DockerPassword"
                    sh "docker image tag garage-api:1.0.0 $DockerUsername/garage-api:1.0.0"
                    sh "docker push $DockerUsername/garage-api:1.0.0"
                }
            }
        }
        stage("Docker Compose") {
            steps {
                sh "docker compose down && docker compose up -d"
            }
        }
    }
}     