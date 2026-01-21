/*
 * Jenkins Pipeline for RestAssured API Testing with Allure Reporting
 * 
 * Prerequisites:
 * 1. Install Allure Jenkins Plugin from Jenkins Plugin Manager
 * 2. Configure Global Tool Configuration -> Allure Commandline
 * 3. Maven and JDK tools configured as 'Maven3' and 'JDK11'
 * 
 * Features:
 * - Parameterized test suite selection
 * - TestNG and Allure report generation
 * - Automatic report publishing
 * - Test artifacts archiving
 */
pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK11'
    }

    parameters {
        choice(name: 'TEST_SUITE', choices: ['testng-objectMock.xml', 'testng-prescriptionMock.xml', 'testng-realApis.xml'], description: 'Select TestNG suite to run')
    }

    environment {
        MAVEN_OPTS = '-Xmx1024m'
        ALLURE_RESULTS_DIRECTORY = 'target/allure-results'
        JAVA_TOOL_OPTIONS = '-Dfile.encoding=UTF-8'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building the project...'
                bat 'mvn clean compile'
            }
        }

        stage('Run Tests') {
            steps {
                echo "Running tests using ${params.TEST_SUITE}..."
                bat "mvn test \"-DsuiteXmlFile=${params.TEST_SUITE}\""
            }
            post {
                always {
                    // Publish TestNG results
                    testNG testResultsPattern: 'target/surefire-reports/testng-results.xml'
                    
                    // Archive Allure results
                    archiveArtifacts artifacts: 'target/allure-results/**/*', allowEmptyArchive: true
                }
            }
        }

        stage('Generate Allure Report') {
            steps {
                echo 'Generating Allure report...'
                bat 'mvn allure:report'
            }
            post {
                always {
                    // Publish Allure report
                    allure([
                        includeProperties: false,
                        jdk: '',
                        properties: [],
                        reportBuildPolicy: 'ALWAYS',
                        results: [[path: 'target/allure-results']]
                    ])
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed - reports available'
            echo 'TestNG reports: target/surefire-reports/'
            echo 'Allure reports: Published via Allure Jenkins plugin'
            
            // Clean up workspace but keep reports
            cleanWs(patterns: [[pattern: 'target/allure-results/**', type: 'EXCLUDE']])
        }
        success {
            echo 'Pipeline completed successfully!'
            echo 'All tests passed - check Allure report for details'
        }
        failure {
            echo 'Pipeline failed!'
            echo 'Check Allure report for failure details and screenshots'
        }
    }
}
