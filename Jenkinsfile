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
        copyScript = "sudo cp target/${processName} ${folderDeploy}"
        permsScript = "sudo chown -R ${appUser}. ${folderDeploy}"
        killScript = "sudo kill -9 \$(ps -ef| grep ${processName}| grep -v grep| awk '{print \$2}')"
        runScript = 'sudo su ${appUser} -c "cd ${folderDeploy}; java -jar ${processName} > nohup.out 2>&1 &"'
    }

    stages {
        stage('info') {
          steps {
              sh(script: """ whoami;pwd;ls -la """, label: "first time so give me your info")
          }
        }
        stage('build') {
            steps {
                sh(script: """ ${buildScript} """, label: "maven is building")
            }
        }
    }
}

  
    
