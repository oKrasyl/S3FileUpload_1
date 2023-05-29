package net.codejava.aws;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Controller
public class MainController {

	static S3Client client = createS3Client();
	public static final String bucket = "s3-vip";

	@GetMapping("")
	public String viewHomePage() {
		return "upload";
	}
	
	@PostMapping("/upload")
	public String handleUploadForm(Model model, String description,
			@RequestParam("file") MultipartFile multipart) {
		String fileName = multipart.getOriginalFilename();
		
		System.out.println("Description: " + description);
		System.out.println("filename: " + fileName);
		
		String message = "";
		
		try {
			S3Util.uploadFile(fileName, multipart.getInputStream(), client, bucket);
			message = "Your file has been uploaded successfully!";
		} catch (Exception ex) {
			message = "Error uploading file: " + ex.getMessage();
		}
		
		model.addAttribute("message", message);
		
		return "message";				
	}
	@GetMapping("/download")
	public static String handleDownloadForm(Model model,
											String file){

		System.out.println("filename: " + file);

		String message = "";

		try {
			S3Util.downloadFile(file, client, bucket);
			message = "Your file has been downloaded successfully!";
		} catch (Exception ex) {
			message = "Error downloading file: " + ex.getMessage();
		}

		model.addAttribute("message", message);

		return "message";
	}

	private static S3Client createS3Client() {
		return S3Client.builder().region(Region.EU_WEST_1).build();
	}


}
