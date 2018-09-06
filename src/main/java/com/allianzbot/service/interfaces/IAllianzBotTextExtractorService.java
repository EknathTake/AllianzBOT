package com.allianzbot.service.interfaces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import com.allianzbot.dto.AllianzBotDocument;
import com.allianzbot.exception.AllianzBotException;

/**
 * Each method extracts the contents from different document. see the javadocs
 * for the supported method.
 * 
 * @author eknath.take
 *
 */
public interface IAllianzBotTextExtractorService {

	/**
	 * <b>All the documents will be parsed for below media types</b><br>
	 * vnd.openxmlformats-officedocument.presentationml.presentation,<br>
	 * vnd.ms-powerpoint.presentation.macroenabled.12,<br>
	 * vnd.openxmlformats-officedocument.presentationml.template,<br>
	 * vnd.openxmlformats-officedocument.presentationml.slideshow,<br>
	 * vnd.ms-powerpoint.slideshow.macroenabled.12,<br>
	 * vnd.ms-powerpoint.addin.macroenabled.12,<br>
	 * vnd.ms-powerpoint.template.macroenabled.12,<br>
	 * vnd.ms-powerpoint.slide.macroenabled.12,<br>
	 * vnd.openxmlformats-officedocument.presentationml.slide,<br>
	 * <br>
	 * 
	 * vnd.openxmlformats-officedocument.spreadsheetml.sheet,<br>
	 * vnd.ms-excel.sheet.macroenabled.12,<br>
	 * vnd.openxmlformats-officedocument.spreadsheetml.template,<br>
	 * vnd.ms-excel.template.macroenabled.12,
	 * vnd.ms-excel.addin.macroenabled.12,<br>
	 * vnd.ms-excel.sheet.binary.macroenabled.12,<br>
	 * 
	 * vnd.openxmlformats-officedocument.wordprocessingml.document,<br>
	 * vnd.ms-word.document.macroenabled.12,<br>
	 * vnd.openxmlformats-officedocument.wordprocessingml.template,<br>
	 * vnd.ms-word.template.macroenabled.12,<br>
	 * <br>
	 * 
	 * vnd.ms-visio.drawing, vnd.ms-visio.drawing.macroenabled.12,<br>
	 * vnd.ms-visio.stencil, vnd.ms-visio.stencil.macroenabled.12,<br>
	 * vnd.ms-visio.template, vnd.ms-visio.template.macroenabled.12,<br>
	 * vnd.ms-visio.drawing, vnd.ms-xpsdocument
	 * 
	 * @param file
	 * @return extracted content
	 */
	AllianzBotDocument documentTextExtractor(InputStream inputstream)
			throws IOException, SAXException, TikaException, AllianzBotException;

	/**
	 * <b>All the documents will be parsed for below media types </b><br>
	 * vnd.ms-excel, msword, x-tika-msoffice, x-tika-ooxml-protected,
	 * vnd.ms-powerpoint, x-mspublisher, vnd.ms-project, vnd.visio, vnd.ms-works,
	 * x-tika-msworks-spreadsheet, vnd.ms-outlook, sldworks, vnd.ms-graph,
	 * x-tika-msoffice-embedded
	 * 
	 * @param inputstream
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws TikaException
	 */
	AllianzBotDocument msOfficeDocumentTextExtractor(InputStream inputstream)
			throws IOException, SAXException, TikaException, AllianzBotException;

	/**
	 * All the documents will be parsed only of .txt files
	 * 
	 * @param file
	 * @return extracted content
	 */
	AllianzBotDocument textFileExtractor(InputStream inputstream)
			throws IOException, SAXException, TikaException, AllianzBotException;

	/**
	 * All the documents will be parsed only of .pdf files
	 * 
	 * @param file
	 * @return extracted content
	 */
	AllianzBotDocument pdfTextExtractor(InputStream inputstream)
			throws IOException, SAXException, TikaException, AllianzBotException;

	/**
	 * Extract the Information from the Allianz Excelsheet. The excel sheet contains
	 * the auestions-answers.
	 * 
	 * @param filePath
	 * @return HashMap<String, LinkedHashMap<Integer, List<String>>>
	 */
	AllianzBotDocument excelDataExtraction(InputStream fis)
			throws FileNotFoundException, IOException;

}
