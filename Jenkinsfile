pipeline {
    agent {
        label 'lab-server'
    }
     environment {
        appUser = "bookinghotel"
        appName = "booking-hotel"
        appVersion = "0.0.1-SNAPSHOT"
        appType = "jar"
        processName = "${appName}-${appVersion}.${appType}"
        folderDeploy = "/datas/${appUser}"
        buildScript = "mvn clean install -DskipTests=true"
        copyScript = "cp target/${processName} ${folderDeploy}"
        // killScript = "kill -9 \$(ps -ef| grep ${processName}| grep -v grep| awk '{print \$2}')"
        //runScript = 'jenkins bash -c "cd ${folderDeploy} && java -jar ${processName} &"'
        runScript = 'bash -c "cd /datas/bookinghotel && java -jar -Dspring.profiles.active=pro booking-hotel-0.0.1-SNAPSHOT.jar > nohup.out 2>&1 &"'

    }

    stages {
        stage('info') {
          steps {
              sh(script: """ whoami;pwd;ls -la; """, label: "first time so give me your info")
          }
        }
        stage('build') {
            steps {
                sh(script: """ echo "Running build script..." """)
                sh(script: """ ${buildScript} """, label: "Building")
                sh(script: """ echo "Build script completed." """)
            }
        }
        stage('deploy') {
            steps {
                sh(script: """ whoami;pwd; """, label: "second time so give me your info")
                sh(script: """ ${copyScript} """, label: "copy the .jar file into deploy folder")
                // sh(script: """ ${killScript} """, label: "terminate the running process")
                sh(script: """ ${runScript} """, label: "run the project")
            }
        }

    }
}

  
    
