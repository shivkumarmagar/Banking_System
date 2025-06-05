@echo off

:: Download SQLite JDBC driver
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.42.0.0/sqlite-jdbc-3.42.0.0.jar' -OutFile 'lib/sqlite-jdbc-3.42.0.0.jar'"

:: Compile and run the application
.\compile_run.bat
