# Test task

### Requirements:
sbt, docker, docker-compose
windows or linux

### Before you start:
You need to build one image on which all spark apps in this project are based on.
go to `docker/submit` and run `build.sh` (`build.bat` for windows env)
This step is required because image vendor has no docker images for spark 3.x.x, 
only dockerfile is provided

### Steps:
* First of all let's generate visit logs: 
run `sbt visitLogGenerator/docker:publishLocal`.
In docker dir run `docker-compose up visit-log-generator`.
Logs are generated.
All data will appear in `docker/app-data` directory

* Let's compose snapshots from generated logs:
run `sbt snapshotComposer/docker:publishLocal`.
In docker dir run `docker-compose up snapshot-composer`

* To generate more snapshots please open `docker/docker-compose.yaml`
and change arguments 
of `visit-log-generator` service:
`data/generatedVisitLogs` to `data/generatedVisitLogs2`
and `snapshot-composer` service: 
`data/generatedVisitLogs` to `data/generatedVisitLogs2 `
`data/snapshots` to `data/snapshots2`

* Next we will merge snapshots:
run `sbt snapshotMerger/docker:publishLocal`.
In docker dir run `docker-compose up snapshot-merger`

* Let's save merged logs to database.
Start container with mysql database by running
`docker-compose up -d mysql` command in docker dir.
Create initial structure:
In docker dir run `docker-compose exec mysql bash`
run `mysql -u root -p < create_structure.sql` (password is: rOOt). It will create working and test databases.
run `sbt snapshotToStorage/docker:publishLocal`
In docker dir run `docker-compose up snapshot-to-storage`

* Now we can see some stat:
run `sbt snapshotAnalyzer/run`

### Run tests:
To run tests use `sbt test` command.
If you have any problems with that use specialized docker image:
go to `docker/test` and run `build.sh` (`build.bat` for windows env).
in docker dir run `docker run --rm -i -t tt/test:latest bash`
and then `docker-compose exec test bash`.
Here you can run `sbt test`

### Time info:
In my env visit logs generation takes about 80 seconds for 1000000*10 logs.
Snapshot composing takes about 364 seconds.
Snapshot merging takes about 330 seconds

### Notes:
I've used kit for spark testing but I faced some issues, 
probably it's related to spark version (kit supports 2.x.x, 3.x.x support was not tested), so I have to compare all rdd's manually

If you have already existing cluster you may use sbt `assembly` command to generate runnable jobs and submit it.
Also if you want to yse docker based cluster you may use images 
`bde2020/spark-master:3.0.0-hadoop3.2` for master and `bde2020/spark-worker:3.0.0-hadoop3.2` for workers.
Check https://github.com/big-data-europe/docker-spark#using-docker-compose for more info.

If you have any problems please contact me: samtnt@yandex.ru