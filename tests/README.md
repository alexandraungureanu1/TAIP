## Performance tests

### Info

- performed with jmeter tool
- test scenarios are saved in the `/scenario` folder
- html reports can be found under `/reports` for each scenario
- to run this tests:
  - download and extract jmeter  
  - go in `/scripts` and change paths in `main_scripts.bat`
  - delete old files and folders under `/logs` and `/reports`
  - run `main_scripts.bat` from a cmd
- Before each test, a new token is automatically generated

### Scenarios

1. Performance test valid data
   - simultaneous users in a span of 10 seconds
   - Looped 50 times --> 100 requests
   - Verify nationality request
   - Request should always return true (user's data is correct)  

2. Performance test invalid data
   - 2 simultaneous users in a span of 10 seconds
   - Looped 50 times --> 100 requests
   - Verify nationality request
   - Request should always return false (user's data is not correct)  

3. Performance test random data
   - 2 simultaneous users in a span of 10 seconds
   - Looped 50 times --> 100 requests
   - Verify nationality request
   - Request result is randomized   

4. Stress test
   - 5 simultaneous users in a span of 5 seconds
   - Looped 10 times --> 100 requests
   - Verify nationality request
   - Request should always return true (user's data is correct)  
