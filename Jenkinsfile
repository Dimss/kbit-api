def getGitCommitShortHash() {
    return checkout(scm).GIT_COMMIT.substring(0, 7)
}

pipeline {
    agent {
        node {
            label 'maven'
        }
    }
    stages {
        stage("Run unit tests") {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject() {
                            echo "Hello from ${openshift.cluster()}'s default project: ${openshift.project()}"

                        }
                    }
                }
            }
        }
        stage("Run static code analysis") {
            steps {
                script {
                    echo "Running static code analysis"
                    withSonarQubeEnv('SonarQube1') {
//                        sh 'mvn clean package sonar:sonar'
                    }
                }
            }

        }
//        stage("Deploy integration tests dependencies") {
//            steps {
//                script {
//                    openshift.withCluster() {
//                        openshift.withProject() {
//                            echo "Deploying integration tests dependencies"
//
//                        }
//                    }
//                }
//            }
//        }
        stage("Build & push docker image ") {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject() {
                            def scmUrl = scm.getUserRemoteConfigs()[0].getUrl()
                            echo "${scmUrl}"
//                            def crTemplate = readFile('ocp/tmpl/app-image-build.yaml')
//                            def models = openshift.process(crTemplate,
//                                    "-p=IS_NAME=${size}",
//                                    "-p=IMAGE_NAME=${appName}",
//                                    "-p=IMAGE_TAG=${namespace}",
//                                    "-p=GIT_REPO=${image}")
//                            echo "${JsonOutput.prettyPrint(JsonOutput.toJson(models))}"
//                            openshift.create(models)
                        }
                    }
                }
            }
        }
//
//        stage("Executing integration tests") {
//            steps {
//                script {
//                    openshift.withCluster() {
//                        openshift.withProject() {
//                            echo "Deploying integration tests dependencies"
//
//                        }
//                    }
//                }
//            }
//        }
    }

    post {
        failure {
            script {
                openshift.withCluster() {
                    openshift.withProject() {
                        sh "echo 'this is failure catch'"
                    }
                }
            }
        }
    }
}