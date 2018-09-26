package com.allianzbot.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.model.AllianzBotDocument;
import com.allianzbot.service.impl.AllianzBotTextExtractorServiceImpl;
import com.allianzbot.service.interfaces.IAllianzBotTextExtractorService;

/**
 * Testcases for the servoce layer {@link AllianzBotTextExtractorServiceImpl}
 * TODO: negatove scenarios needs to be tested....
 * @author eknath.take
 *
 */
@Ignore
public class AllianzBotTextExtractorServiceImplTest {

	private IAllianzBotTextExtractorService classUnderTest;

	@Before
	public void setUp() throws Exception {
		classUnderTest = new AllianzBotTextExtractorServiceImpl();
	}

	@Test
	public void testDocumentTextExtractor() throws IOException, SAXException, TikaException, AllianzBotException {

		File file = new File("src/test/resources/docs/Document.docx");
		InputStream inputstream = new FileInputStream(file);
		AllianzBotDocument actualOutput = classUnderTest.documentTextExtractor(inputstream);
		//assertThat(actualOutput.getContent(), is(containsString("Demo text for Apache Tika")));
		//assertThat(actualOutput.getMetadata(), is(containsString("dc:creator=Take, Eknath")));
	}

	@Test
	public void testMsOfficeDocumentTextExtractor() throws IOException, SAXException, TikaException, AllianzBotException {
		File file = new File("src/test/resources/docs/excel.xls");
		InputStream inputstream = new FileInputStream(file);
		AllianzBotDocument actualOutput = classUnderTest.msOfficeDocumentTextExtractor(inputstream);
		//assertThat(actualOutput.getContent(), is(containsString("Stationery Requisition Form")));
	//	assertThat(actualOutput.getMetadata(), is(containsString("Application=Microsoft Excel")));
	}

	@Test
	public void testTextFileExtractor() throws IOException, SAXException, TikaException, AllianzBotException {

		File file = new File("src/test/resources/docs/ex1.txt");
		InputStream inputstream = new FileInputStream(file);
		AllianzBotDocument actualOutput = classUnderTest.textFileExtractor(inputstream);
		//assertThat(actualOutput.getContent(), is(equalTo("Text extraction Example..\n\n")));
		/*assertThat(actualOutput.getMetadata(),
				is(equalTo("Content-Encoding=ISO-8859-1 Content-Type=text/plain; charset=ISO-8859-1")));*/
	}

	@Test
	public void testPdfTextExtractor() throws IOException, SAXException, TikaException, AllianzBotException {
		File file = new File("src/test/resources/docs/PdfDocument.pdf");
		InputStream inputstream = new FileInputStream(file);
		AllianzBotDocument actualOutput = classUnderTest.pdfTextExtractor(inputstream);
		//assertThat(actualOutput.getContent(), is(containsString("Reverse Polish notation (RPN)")));
		//assertThat(actualOutput.getMetadata(), is(containsString("format=application/pdf")));
	}
	
	@Test
	public void testExcelDataExtraction() throws FileNotFoundException, IOException {
		/*String filePath = "/documents/AF-TT Integration.xlsx";
		AllianzBotDocument actualOutput = classUnderTest.excelDataExtraction(filePath);
		
		
		HashMap<String, LinkedHashMap<Integer, List<String>>> result = (HashMap<String, LinkedHashMap<Integer, List<String>>>) actualOutput.getContent();
		
		
		result.forEach((sheetName, data)->{
			data.forEach((rowNum, colmns)->{
				
				if(rowNum!=0)
				System.out.println(rowNum+"=="+colmns);
			});
		});*/
		
		
	}

}
