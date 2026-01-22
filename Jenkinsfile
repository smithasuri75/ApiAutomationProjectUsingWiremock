/*
 * Jenkins Pipeline for RestAssured API Testing with Allure Reporting
 * UPDATED: 2026-01-21 - Regular Jenkins agent without Docker dependency
 * 
 * Features:
 * - Regular Jenkins agent execution
 * - Parameterized test suite selection
 * - Direct GitHub repository cloning
 * - TestNG and Allure report generation
 * - HTML report publishing
 * - Test artifacts archiving
 */
pipeline {
    agent any

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
        stage('Environment Check') {
            steps {
                echo 'Checking available tools...'
                sh 'echo "PATH: $PATH"'
                sh 'java -version'
                sh 'which mvn || echo "Maven not found in PATH"'
                sh 'which git || echo "Git not found in PATH"'
            }
        }

        stage('Checkout') {
            steps {
                echo "Cloning repo ${env.REPO_URL}"
                git branch: "${env.BRANCH}", url: "${env.REPO_URL}"
                sh 'git status'
            }
        }

        stage('Build') {
            steps {
                echo 'Building project...'
                sh 'mvn clean compile'
            }
            }
        }

        stage('Run Tests') {
            steps {
                echo "Running TestNG suite: ${params.TEST_SUITE}"
                sh "mvn test -DsuiteXmlFile=${params.TEST_SUITE}"
            }
            post {
                always {
                    echo 'Archiving TestNG reports and Allure results...'
                    archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
                    archiveArtifacts artifacts: 'target/allure-results/**/*', allowEmptyArchive: true
                }
            }
        }

        stage('Generate Allure Report') {
            steps {
                echo 'Generating Allure report...'
                sh 'mvn allure:report'
            }
            post {
                always {
                    publishHTML([
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/allure-maven-plugin',
                        reportFiles: 'index.html',
                        reportName: 'Allure Report'
                    ])
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
