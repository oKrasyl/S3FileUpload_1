package net.codejava.aws;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;


public class S3Util {

	
	public static void uploadFile(String fileName, InputStream inputStream, S3Client client, String bucket)
			throws AwsServiceException, SdkClientException, IOException {

		
		PutObjectRequest request = PutObjectRequest.builder()
										.bucket(bucket)
										.key(fileName)
										.acl("public-read")
										.build();
		
		client.putObject(request, 
				RequestBody.fromInputStream(inputStream, inputStream.available()));


	}

	public static void downloadFile(String file, S3Client client, String bucket)
			throws AwsServiceException, SdkClientException, IOException {


		GetObjectRequest request = GetObjectRequest.builder()
				.bucket(bucket)
				.key(file)
				.build();

		ResponseInputStream<GetObjectResponse> response = client.getObject(request);

		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));

		byte[] buffer = new byte[4096];
		int bytesRead = -1;

		while ((bytesRead = response.read(buffer)) !=  -1) {
			outputStream.write(buffer, 0, bytesRead);
		}

		response.close();
		outputStream.close();
	}

}


