package net.codejava.aws;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import io.findify.s3mock.S3Mock;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import software.amazon.awssdk.services.s3.model.*;

import java.io.BufferedOutputStream;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import software.amazon.awssdk.regions.Region;

@SpringBootTest
class S3FileUploadApplicationTests {

	@Test
	void contextLoads() {
	}

	private final S3Client s3Client = createS3Client();
	private final S3Mock api = createApi();
	String bucketName = "my-test-bucket";
	String objectKey = "my-object.txt";

	private static S3Mock createApi() {
		return new S3Mock.Builder().withPort(8001).withInMemoryBackend().build();
	}

	private static S3Client createS3Client() {
		return S3Client.builder()
				.region(Region.US_EAST_1)
				.endpointOverride(URI.create("http://localhost:8001"))
				.build();
	}
	@BeforeEach
	public void setup() {
		api.start();
		EndpointConfiguration endpoint = new EndpointConfiguration("http://localhost:8001", "us-west-2");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

		CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
				.bucket(bucketName)
				.build();

		CreateBucketResponse createBucketResponse = s3Client.createBucket(createBucketRequest);

		System.out.println("Bucket created: " + createBucketResponse.location());

	}

	@Test
	public void testDownloadFile() throws IOException {
		Path filePath = Paths.get("src/main/resources/my-object.txt");

		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(objectKey)
				.build();

		s3Client.putObject(putObjectRequest, filePath);

		System.out.println("Object uploaded successfully");

		S3Util.downloadFile("my-object.txt", s3Client,"my-test-bucket");
		Path downloadedFilePath = Paths.get("my-object.txt");
		assertTrue(Files.exists(downloadedFilePath), "Downloaded file exists");

		byte[] originalFileBytes = Files.readAllBytes(filePath);
		byte[] downloadedFileBytes = Files.readAllBytes(downloadedFilePath);
		assertArrayEquals(originalFileBytes, downloadedFileBytes, "Downloaded file contents match the original file");

		Files.delete(downloadedFilePath);
		assertFalse(Files.exists(downloadedFilePath), "Downloaded file has been deleted");
	}

	@AfterEach
	public void cleanup() {
		api.stop();
	}
}





