// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// [START drive_quickstart]
package projet;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate Drive's upload to folder use-case. */
public class UploadToFolder {
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);
	
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
		      throws IOException {
		    // Load client secrets.
		    InputStream in = UploadToFolder.class.getResourceAsStream("/credentials.json");
		    if (in == null) {
		      throw new FileNotFoundException("Resource not found: " + "/credentials.json");
		    }
		    GoogleClientSecrets clientSecrets =
		        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		    // Build flow and trigger user authorization request.
		    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
		        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
		        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("/tokens")))
		        .setAccessType("offline")
		        .build();
		    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		    Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
		    //returns an authorized Credential object.
		    return credential;
		  }
	
  /**
   * Upload a file to the specified folder.
   *
   * @return Inserted file metadata if successful, {@code null} otherwise.
   * @throws IOException if service account credentials file not found.
   */
  public static File uploadToFolder() throws IOException {
    // Load pre-authorized user credentials from the environment.
    // TODO(developer) - See https://developers.google.com/identity for
    // guides on implementing OAuth2 for your application.
	NetHttpTransport HTTP_TRANSPORT = null;
	try {
		HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	} catch (GeneralSecurityException e1) {
		e1.printStackTrace();
	} catch (IOException e1) {
		e1.printStackTrace();
	}
	Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
	        .setApplicationName("wordleHumainFrancais")
	        .build();

    // File's metadata.
    File fileMetadata = new File();
    fileMetadata.setName("test.json");
    fileMetadata.setParents(Collections.singletonList("15MLy0lQuwdBZXOt9dzCzeb4oFRLsEawi"));
    java.io.File filePath = new java.io.File("/test.json");
    FileContent mediaContent = new FileContent("json", filePath);
    try {
      File file = service.files().create(fileMetadata, mediaContent)
          .setFields("id, parents")
          .execute();
      System.out.println("File ID: " + file.getId());
      return file;
    } catch (GoogleJsonResponseException e) {
      // TODO(developer) - handle error appropriately
      System.err.println("Unable to upload file: " + e.getDetails());
      throw e;
    }
  }
  public static void main(String[] args) {
	try {
		uploadToFolder();
	} catch (IOException e) {
		e.printStackTrace();
	}
  }
}