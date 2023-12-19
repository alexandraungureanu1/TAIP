set MAIN_PATH=D:/Faculta/info_master/aset/TAIP/tests
set TEST_PLAN=nationality_verification_test_plan
set JMETER_PATH=D:/Faculta/info_master/aset/apache-jmeter-5.6.2/bin

for /l %%x in (1, 1, 4) do (
   %JMETER_PATH%/jmeter.bat -n -t %MAIN_PATH%/scenario/%TEST_PLAN%_%%x.jmx -l %MAIN_PATH%/logs/%TEST_PLAN%_%%x.txt -e -o %MAIN_PATH%/reports/%TEST_PLAN%_%%x/
)