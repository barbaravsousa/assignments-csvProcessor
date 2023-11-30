# Csv Processor

### About the project 
This is a simple command-line application that does the following:
   - Receives a path to a CSV file as a command-line argument.
   - Verifies if the file exists.
   - Checks if the CSV matches a predefined set of headers.
   - For each row, makes a POST request to my HTTP server (javaApplication).
   - Logs any errors encountered during these processes. 

In order to make the code easier to maintain, I chose Apache Commons CSV (for parsing the CSV file) and 
Apache HttpClient(for HTTP requests) libraries. 

#### How to run it locally
- Make sure that the server ([JavaApplication](https://github.com/barbaravsousa/assignments-javaApplication)) is running in order to get a successful response.
1) Run the following commands(replace the path to csv file placeholder with the absolute path to your csv file):
```
mvn clean compile 
mvn exec:java -Dexec.mainClass=CsvProcessor -Dexec.args="path_to_csv_file"
```
- You can define the server to which you make the requests to, in the config.properties. 
- If everything is working as expected the message *'Number_of_Customers_Created customer(s) created'* can be seen in the console. 
In case you try to create a customer that already exists the request will fail and the message *'Customer_Ref customer already exists.'* will be logged. 
If the message that appears in the console is *'Could not create customer.'* is because something went wrong when saving the costumer in the database. It's an
uncontrolled error (I hope it does not happy). 
