(defproject com.hello/messeji "0.1.3"
  :description "Async messaging service for communicating with Sense"
  :url "https://github.com/hello/messeji"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [aleph "0.4.1-beta2"]
                 [compojure "1.4.0"]
                 [com.amazonaws/aws-java-sdk-dynamodb "1.10.49"]
                 [com.google.guava/guava "18.0"]
                 [com.google.protobuf/protobuf-java "2.6.1"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]
                 [prismatic/schema "1.0.4"]]
  :plugins [[s3-wagon-private "1.2.0"]
            [lein-pprint "1.1.1"]
            [ystad/lein-deb "1.0.0"]]
  :source-paths ["src" "src/main/clojure"]
  :java-source-paths ["src/main/java"]  ; Java source is stored separately.
  :resource-paths ["resources"]
  :main com.hello.messeji.server
  :aot [com.hello.messeji.server]
  :jvm-opts ["-server", "-Dlogfile.path=./log"]
  :profiles {:dev {:plugins [[com.hello/lein-deploy-uberjar "0.1.0"]]}}
  :test-selectors {:default (complement :integration)
                   :integration :integration
                   :all (constantly true)}
  :aliases {"package-deb" ["do" ["clean"] ["uberjar"] ["deb"]]}
  :release-tasks [["clean"]
                  ["test"]
                  ["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["pprint" ":version"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "v" "--no-sign"]
                  ["package-deb"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]]
  :deploy-branches ["master"]
  :deb {
    :filesets [{:file "target/messeji-*-standalone.jar"
                :fullpath "/opt/hello/messeji.jar"}
               {:file "resources/config/prod.edn"
                :fullpath "/etc/hello/messeji.prod.edn"}
               {:file "init/messeji.conf"
                :fullpath "/etc/init/messeji.conf"}]}
  :repositories [["releases" {:url "s3p://hello-maven/release/"
                              :username :env/aws_access_key_id
                              :passphrase :env/aws_secret_key
                              :sign-releases false}]
                 ["snapshots" {:url "s3p://hello-maven/snapshot/"
                               :username :env/aws_access_key_id
                               :passphrase :env/aws_secret_key}]
                 ["packages" {:url "s3p://hello-deploy/packages/"
                              :username :env/aws_access_key_id
                              :passphrase :env/aws_secret_key}]])
