package com.allianzbot.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.allianzbot.controller.AllianzBotController;
import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.model.AllianzBotDocument;
import com.allianzbot.service.interfaces.IAllianzBotOpenNlpService;
import com.allianzbot.service.interfaces.IAllianzBotTextExtractorService;

/**
 * Junit for service class AllianzBotTextExtractorService
 * 
 * @author eknath.take
 */
@Service(value = "allianzBotTextExtractorService")
public class AllianzBotTextExtractorServiceImpl implements IAllianzBotTextExtractorService {

	private static final Logger log = LoggerFactory.getLogger(AllianzBotController.class);

	private static final int MAX_VALUE = Integer.MAX_VALUE;

	@Inject
	@Named("allianzBotOpenNlpService")
	private IAllianzBotOpenNlpService allianzBotOpenNlpService;

	@Override
	public AllianzBotDocument documentTextExtractor(InputStream inputstream)
			throws IOException, SAXException, TikaException, AllianzBotException {
		// OOXml parser
		OOXMLParser ooXmlParser = new OOXMLParser();

		BodyContentHandler handler = new BodyContentHandler(MAX_VALUE);
		Metadata metadata = new Metadata();
		ParseContext pcontext = new ParseContext();

		ooXmlParser.parse(inputstream, handler, metadata, pcontext);

		AllianzBotDocument allianzBotServiceResponse = new AllianzBotDocument();
		log.info("Content: {}", handler.toString());
		allianzBotServiceResponse.setContent(handler.toString());
		return allianzBotServiceResponse;
	}

	@Override
	public AllianzBotDocument msOfficeDocumentTextExtractor(InputStream inputstream)
			throws IOException, SAXException, TikaException, AllianzBotException {
		// OOXml parser
		OfficeParser msofficeparser = new OfficeParser();

		BodyContentHandler handler = new BodyContentHandler(MAX_VALUE);
		Metadata metadata = new Metadata();
		ParseContext pcontext = new ParseContext();

		msofficeparser.parse(inputstream, handler, metadata, pcontext);

		AllianzBotDocument allianzBotServiceResponse = new AllianzBotDocument();
		log.info("Content: {}", handler.toString());
		allianzBotServiceResponse.setContent(handler.toString());
		return allianzBotServiceResponse;
	}

	@Override
	public AllianzBotDocument textFileExtractor(InputStream inputstream)
			throws IOException, SAXException, TikaException, AllianzBotException {

		// Text document parser
		TXTParser texTParser = new TXTParser();

		BodyContentHandler handler = new BodyContentHandler(MAX_VALUE);
		Metadata metadata = new Metadata();
		ParseContext pcontext = new ParseContext();

		texTParser.parse(inputstream, handler, metadata, pcontext);

		AllianzBotDocument allianzBotServiceResponse = new AllianzBotDocument();
		log.info("Content: {}", handler.toString());
		allianzBotServiceResponse.setContent(handler.toString());
		return allianzBotServiceResponse;
	}

	@Override
	public AllianzBotDocument pdfTextExtractor(InputStream inputstream)
			throws IOException, SAXException, TikaException, AllianzBotException {

		// pdf parser
		PDFParser pdfParser = new PDFParser();

		BodyContentHandler handler = new BodyContentHandler(MAX_VALUE);
		Metadata metadata = new Metadata();
		ParseContext pcontext = new ParseContext();

		pdfParser.parse(inputstream, handler, metadata, pcontext);

		AllianzBotDocument allianzBotServiceResponse = new AllianzBotDocument();
		allianzBotServiceResponse.setContent(handler.toString());
		return allianzBotServiceResponse;
	}

	@Override
	public AllianzBotDocument excelDataExtraction(InputStream fis) throws FileNotFoundException, IOException {
		// Used the LinkedHashMap and LikedList to maintain the order
		LinkedHashMap<Integer, List<String>> excelRows = new LinkedHashMap<>();
		try ( // Create an excel workbook from the file system
				XSSFWorkbook workBook = new XSSFWorkbook(fis);) {
			for (Sheet sheet : workBook) {
				for (Row row : sheet) {
					List<String> dataColumns = new LinkedList<>();
					for (Cell cell : row) {
						cell.setCellType(CellType.STRING);
						dataColumns.add(cell.getStringCellValue());
					}
					excelRows.put(row.getRowNum(), dataColumns);
				}
			}

		}

		AllianzBotDocument allianzBotServiceResponse = new AllianzBotDocument();
		allianzBotServiceResponse.setContent(excelRows);
		return allianzBotServiceResponse;
	}

}
