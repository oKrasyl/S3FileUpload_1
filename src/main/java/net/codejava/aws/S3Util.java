package net.codejava.aws;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

public class S3Util {
	public static final String BUCKET = "s3-vip";
	
	public static void uploadFile(String fileName, InputStream inputStream) 
			throws S3Exception, AwsServiceException, SdkClientException, IOException {
		S3Client client = createS3Client();
		
		PutObjectRequest request = PutObjectRequest.builder()
										.bucket(BUCKET)
										.key(fileName)
										.acl("public-read")
										.build();
		
		client.putObject(request, 
				RequestBody.fromInputStream(inputStream, inputStream.available()));
		
		S3Waiter waiter = client.waiter();
		HeadObjectRequest waitRequest = HeadObjectRequest.builder()
											.bucket(BUCKET)
											.key(fileName)
											.build();
		
		WaiterResponse<HeadObjectResponse> waitResponse = waiter.waitUntilObjectExists(waitRequest);
		
		waitResponse.matched().response().ifPresent(x -> {
			// run custom code that should be executed after the upload file exists
		});
	}

	public static void downloadFile(String file)
			throws S3Exception, AwsServiceException, SdkClientException, IOException {

		S3Client client = createS3Client();

		GetObjectRequest request = GetObjectRequest.builder()
				.bucket(BUCKET)
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

	private static S3Client createS3Client() {
		return S3Client.builder().region(Region.EU_WEST_1).build();
	}
}


