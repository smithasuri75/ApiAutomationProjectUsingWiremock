/*
 * Jenkins Pipeline for RestAssured API Testing with Allure Reporting
 * UPDATED: 2026-01-21 - Docker-based pipeline without old Jenkins tool dependencies
 * 
 * Features:
 * - Docker-based execution with Maven and OpenJDK 11
 * - Parameterized test suite selection
 * - Direct GitHub repository cloning
 * - TestNG and Allure report generation
 * - HTML report publishing
 * - Test artifacts archiving
 */
pipeline {
    agent {
        docker {
            image 'maven:3.9.2-openjdk-11'
            args '-v $HOME/.m2:/root/.m2'
        }
    }

    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['testng-objectMock.xml', 'testng-prescriptionMock.xml', 'testng-realApis.xml'],
            description: 'Select TestNG suite to run'
        )
    }

    environment {
        MAVEN_OPTS = '-Xmx1024m'
        ALLURE_RESULTS_DIRECTORY = 'target/allure-results'
        JAVA_TOOL_OPTIONS = '-Dfile.encoding=UTF-8'
        REPO_URL = 'https://github.com/smithasuri75/ApiAutomationProjectUsingWiremock.git'
        BRANCH = 'main'
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Cloning repo ${env.REPO_URL}"
                sh "git clone -b ${env.BRANCH} ${env.REPO_URL} repo"
                dir('repo') {
                    sh 'git status'
                }
            }
        }

        stage('Build') {
            steps {
                dir('repo') {
                    echo 'Building project...'
                    sh 'mvn clean compile'
                }
            }
        }

        stage('Run Tests') {
            steps {
                dir('repo') {
                    echo "Running TestNG suite: ${params.TEST_SUITE}"
                    sh "mvn test -DsuiteXmlFile=${params.TEST_SUITE}"
                }
            }
            post {
                always {
                    dir('repo') {
                        echo 'Archiving TestNG reports and Allure results...'
                        archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
                        archiveArtifacts artifacts: 'target/allure-results/**/*', allowEmptyArchive: true
                    }
                }
            }
        }

        stage('Generate Allure Report') {
            steps {
                dir('repo') {
                    echo 'Generating Allure report...'
                    sh '''
                        if ! command -v allure &> /dev/null; then
                            curl -o /tmp/allure.tgz -L https://github.com/allure-framework/allure2/releases/download/2.22.2/allure-2.22.2.tgz
                            tar -zxvf /tmp/allure.tgz -C /opt
                            ln -s /opt/allure-2.22.2/bin/allure /usr/bin/allure
                            rm /tmp/allure.tgz
                        fi
                        allure generate target/allure-results -o target/allure-report --clean
                    '''
                }
            }
            post {
                always {
                    dir('repo') {
                        publishHTML([
                            allowMissing: true,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: 'target/allure-report',
                            reportFiles: 'index.html',
                            reportName: 'Allure Report'
                        ])
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished. Check TestNG and Allure reports.'
        }
        success {
            echo 'All tests passed successfully!'
        }
        failure {
            echo 'Some tests failed. Check reports for details.'
        }
    }
}
