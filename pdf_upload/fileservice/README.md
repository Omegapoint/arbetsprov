# Work Test for Omegapoint Application
- Candidate: Lovisa Landgren
- The project is a solution of the task "pdf_upload" and solves the following tasks:
  - Provide an API to which PDF files can be uploaded.
  - Validate that an uploaded file is a PDF file.
  - Validate that the uploaded file is max 2MB in size.
  - Persist uploaded PDF-files in a simple manner. In-memory storage is OK.
  - Validate that a file has not already been uploaded. Files are considered as duplicates if they have the same name and same checksum.
  - Allow uploaded PDF-files to be downloaded through another endpoint.

## Getting started
- The program runs as a RESTful API on default port 4000. This can be changed in resources/application.properties.
- Driven with Java 17, Spring boot, Gradle
- Uploaded pdf files are by default stored in a local folder "uploads" created in the project directory. Name and location is changeable in resources/application.properties.
- The controller is mapped to "/v1/api/files". A POST request to this endpoint with request parameter "file" as a form-data media type will upload your pdf file.
- A GET request to the same endpoint will retrieve a list of uploaded files.
- A GET request to "/v1/api/files/serve" with request parameter "name" as text input will retrieve the pdf file with the given name.
- Run the service with "sh ./gradlew bootRun" in terminal.