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
        killScript = "kill -9 \$(ps -ef| grep ${processName}| grep -v grep| awk '{print \$2}')"
        pro_properties = "-Dspring.profiles.active=pro"
        updateChown = "chmod 777 ${folderDeploy}/${processName}"
        runScript = 'bash -c "java -jar -Dspring.profiles.active=pro /datas/bookinghotel/booking-hotel-0.0.1-SNAPSHOT.jar > /datas/bookinghotel/nohup.out &"'
    }

    stages {
        stage('info') {
          steps {
              sh(script: """ whoami;pwd;ls -la; """, label: "first time so give me your info")
          }
        }
        stage('build') {
            steps {
                sh(script: """ echo "Stop old process..." """)
                sh(script: """ ${killScript} """, label: "terminate the running process")
                sh(script: """ echo "Running build script..." """)
                sh(script: """ ${buildScript} """, label: "Building")
                sh(script: """ echo "Build script completed." """)
            }
        }
        stage('deploy') {
            steps {
                sh(script: """ echo "Running deploy script..." """)
                sh(script: """ whoami;pwd; """, label: "second time so give me your info")
                sh(script: """ ${copyScript} """, label: "copy the .jar file into deploy folder")
                sh(script: """ ${updateChown} """, label: "update chown")
                sh(script: """ cd ${folderDeploy}; """, label: "run the project")
                sh(script: """ bash -c "java -jar ${pro_properties} ${folderDeploy}/${processName} | tee ${folderDeploy}/nohup.out"; """, label: "run the project")
                sh(script: """ ls -ld; """, label: "")
            }
        }

    }
}

  
    
