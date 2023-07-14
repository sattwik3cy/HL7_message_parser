package ehospital.lis.hl7reportconverter.service;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.util.Terser;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;


@Service
public class ReportGeneratorService {
	private final HashMap<String,String> data;
	private final Logger logger = LoggerFactory.getLogger(ReportGeneratorService.class);

	public ReportGeneratorService(HashMap<String, String> data) {
		this.data = data;
	}

	public ByteArrayInputStream stringToPDF(String msg) throws IOException {
		logger.info("PDF Report Generation Initialized");

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, out);
		document.open();

		Font fontTitle = FontFactory.getFont(FontFactory.TIMES_BOLD);
		fontTitle.setSize(24);

		Paragraph paragraph = new Paragraph("REPORT", fontTitle);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);

		Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
		fontParagraph.setSize(12);

		Paragraph paragraph2 = new Paragraph(msg, fontParagraph);
		paragraph2.setAlignment(Paragraph.ALIGN_LEFT);

		document.add(paragraph);
		document.add(paragraph2);
		document.close();
		return new ByteArrayInputStream(out.toByteArray());
	}
	public ByteArrayInputStream txtFileToPDF(String msg) throws IOException, HL7Exception {
		logger.info("PDF Report Generation Initialized");
		parseHL7(msg);
		// PDF Instance opened
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, out);
		document.open();

		// PDF TITLE: REPORT
		Font fontTitle = FontFactory.getFont(FontFactory.TIMES_BOLD);
		fontTitle.setSize(24);
		Paragraph paragraph = new Paragraph("REPORT", fontTitle);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);


		// CREATING TABLE
		Table table = new Table(2);
		table.setBorderWidth(0);
		table.setPadding(5);
		table.setSpacing(0);
		table.addCell("Name: " + data.get("patientName"));
		table.addCell("Collected: ");
		table.addCell("DOB: " + data.get("DOB").substring(6,8)+ "/" +data.get("DOB").substring(4,6)+"/"+data.get("DOB").substring(0,4));
		table.addCell("Received: ");
		table.addCell("Gender: "+ data.get("gender"));
		table.addCell("Reported: ");
		table.addCell("Patient ID: "+ data.get("patientID"));
		table.addCell("Admit Date: "+ data.get("admitTime").substring(6,8)+ "/" +data.get("admitTime").substring(4,6)+"/"+data.get("admitTime").substring(0,4));
		table.addCell("Contact: "+ data.get("contact"));




		// WRITING PARAGRAPH
/*		Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
		fontParagraph.setSize(12);
		Paragraph paragraph2 = new Paragraph(msg, fontParagraph);
		paragraph2.setAlignment(Paragraph.ALIGN_LEFT);*/

		// CLOSING FILE
		document.add(paragraph);
		document.add(table);
//		document.add(paragraph2);
		document.close();

		return new ByteArrayInputStream(out.toByteArray());
	}

	public void parseHL7(String msg) throws HL7Exception {
		Parser hapiParser;
		Message hapiMsg;
		Terser hapiTerser;
		try(HapiContext context = new DefaultHapiContext()) {
			hapiParser = context.getPipeParser();
		}catch (IOException e){
			throw new RuntimeException(e);
		}

		try{
			hapiMsg = hapiParser.parse(msg);
		} catch (HL7Exception e) {
			e.printStackTrace();
			return;
		}
		hapiTerser = new Terser(hapiMsg);
		data.put("patientName",hapiTerser.get("/.PID-5-1")+" "+hapiTerser.get("/.PID-5-2"));
		data.put("gender", hapiTerser.get("/.PID-8"));
		data.put("DOB", hapiTerser.get("/.PID-7-1"));
		data.put("patientID", hapiTerser.get("/.PID-2-1"));
		data.put("contact", hapiTerser.get("/.PID-13"));
		data.put("admitTime", hapiTerser.get("/.PV1-44"));


	}

}
