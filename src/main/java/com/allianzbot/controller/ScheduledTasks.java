package com.allianzbot.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Function;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.exception.AllianzBotRunTimeException;
import com.allianzbot.functions.AllianzBotCheckedExceptionHandlerFunction;
import com.allianzbot.process.interfaces.IAllianzBotProcess;
import com.allianzbot.response.AllianzBotSolrCreateDocumentResponse;

@Component
public class ScheduledTasks {

	@Inject
	@Named("botTextExtractorProcess")
	private IAllianzBotProcess botTextExtractorProcess;

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	/**
	 * This job will executed 12PM everyday. See the following expresstion for the
	 * more details.
	 *
	 * @see following cron expression -> [Seconds] [Minutes] [Hours] [Day of month]
	 *      [Month] [Day of week] [Year]
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SolrServerException
	 * @throws AllianzBotException
	 * @throws TikaException
	 * @throws SAXException
	 */
	//@Scheduled(cron = "0 0 12 * * ?")
	//@Scheduled(fixedDelay=10000)
	public void batchJobToStoreFiles() throws FileNotFoundException, IOException, SAXException, TikaException,
			AllianzBotException, SolrServerException {
		File[] listOfFiles = new File("C:/dev/Docs").listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				final String fileName = file.getName();
				MultipartFile multipartFile = new MockMultipartFile(fileName, fileName,
						Files.probeContentType(file.toPath()), new FileInputStream(file));

				AllianzBotSolrCreateDocumentResponse response = botTextExtractorProcess.storeDocument(multipartFile);
				
				log.info("AllianzBotSolrCreateDocumentResponse response: {}", response);
			}
		}
		
		/*AllianzBotCheckedExceptionHandlerFunction<File, MockMultipartFile, IOException> mapFileToMultipart = file -> {
		return new MockMultipartFile(file.getName(), file.getName(), Files.probeContentType(file.toPath()),
				new FileInputStream(file));
	};
	
	boolean isDocumentSaved = Stream.of(listOfFiles)
		.filter(Objects::nonNull)
		.filter(file -> file.isFile())
		.peek(file-> log.info("Processing on file: {}", file.getName()))
		.map(wrapper(mapFileToMultipart))
		.map(wrapper(multiPartFile -> botTextExtractorProcess.storeDocument(multiPartFile)))
		.peek(response-> log.info("AllianzBotSolrCreateDocumentResponse response: {}", response))
		.anyMatch(response -> response.getStatus().getStatusCode()==0);
	
	log.info((isDocumentSaved)? "Stored the document": "Documents are already exist");*/

	}

	private <T, R, E extends Exception> Function<T, R> wrapper(
			AllianzBotCheckedExceptionHandlerFunction<T, R, E> allianzBotFunctionalException) {
		return args -> {
			try {
				return allianzBotFunctionalException.apply(args);
			} catch (Exception e) {
				throw new AllianzBotRunTimeException(000, e.getMessage());
			}
		};

	}

}