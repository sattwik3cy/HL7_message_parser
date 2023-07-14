package ehospital.lis.hl7reportconverter.controller;

import ca.uhn.hl7v2.HL7Exception;
import ehospital.lis.hl7reportconverter.model.HL7Message;
import ehospital.lis.hl7reportconverter.service.ReportGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
@RestController
@RequestMapping("/pdf")
public class ReportGeneratorController {
	@Autowired
	private ReportGeneratorService reportGeneratorService;

	@PostMapping(value = "/generate")
	public ResponseEntity<InputStreamResource> generatePDF(@RequestBody HL7Message msg) throws IOException {
		ByteArrayInputStream pdf = reportGeneratorService.stringToPDF(msg.getMsg());
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-Disposition", "inline;file=report.pdf");
		return ResponseEntity.ok().headers(httpHeaders).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(pdf));
	}
	@PostMapping(value = "/report")
	public ResponseEntity<InputStreamResource> generateReport(@RequestParam("file") MultipartFile file) throws IOException, HL7Exception {
		String msg = "Hello! Error";
		if(!file.isEmpty()){
			try {
				byte[] bytes = file.getBytes();
				msg = new String(bytes);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		ByteArrayInputStream pdf = reportGeneratorService.txtFileToPDF(msg);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-Disposition", "inline;file=report.pdf");
		return ResponseEntity.ok().headers(httpHeaders).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(pdf));
	}
}
