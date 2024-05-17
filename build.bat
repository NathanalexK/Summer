set project_name=summer

set server_dir=D:\NATHANALEX\Tools\tomcat\webapps
set classes_dir=D:\Toutes\ITU\S4\INF209 Web Dyn\Sprint\Summer\out\production\Summer
set jar_dir=D:\Toutes\Java Library

xcopy "%classes_dir%" "build\" /Y /E /I
@REM xcopy "%web_dir%" "build\" /Y /E /I
@REM copy "web\WEB-INF" "build\WEB-INF\"
@REM xcopy "lib" "build\" /Y /E /I

jar cf %project_name%.jar -C build .
copy %project_name%.jar "%jar_dir%\%project_name%"
copy "%project_name%".jar "D:\Toutes\ITU\S4\INF209 Web Dyn\Sprint\Biblio\lib"
@REM copy %project_name%.war "%server_dir%"