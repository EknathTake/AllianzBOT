package com.allianzbot.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.SAXException;

import com.allianzbot.dto.AllianzBotSentence;
import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.process.interfaces.IAllianzBotProcess;
import com.allianzbot.response.AllianzBotSolrCreateDocumentResponse;
import com.allianzbot.response.AllianzBotSolrSearchDocumentResponse;

/**
 * Handles requests for the application home page.
 * 
 * @author eknath.take
 *
 */
@Controller
@Validated
public class AllianzBotController {

	@Inject
	@Named("botTextExtractorProcess")
	private IAllianzBotProcess botTextExtractorProcess;

	private static final Logger log = LoggerFactory.getLogger(AllianzBotController.class);

	@Value("${file.path.documents}")
	private String documents;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@CrossOrigin(origins = { "*" })
	@GetMapping(value = "/")
	public String home(Model model) {
		log.info("Welcome home!");
		return "home";
	}

	/**
	 * Upload single file using Spring Controller
	 * 
	 * @throws TikaException,      SAXException, IOException
	 * @throws SolrServerException
	 * @throws AllianzBotException
	 */
	@CrossOrigin(origins = { "*" })
	@PostMapping(value = "/extract/document")
	public ModelAndView storeSolrDocuments()
			throws IOException, SAXException, TikaException, SolrServerException, AllianzBotException {

		batchJobToStoreFiles();
		ModelAndView modelAndView = new ModelAndView("home");
		modelAndView.addObject("message", "All Documents stored. Successfully");
		// AllianzBotSolrCreateDocumentResponse storeDocument =
		// botTextExtractorProcess.storeDocument(file);
		return modelAndView;

	}

	/**
	 * Search the document in Solr search engine
	 * 
	 * @param userQuery
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws AllianzBotException
	 */

	@CrossOrigin(origins = { "*" })
	@GetMapping(value = "/search/document")
	public @ResponseBody AllianzBotSolrSearchDocumentResponse searchSolrDocument(
			@RequestParam(name = "q") @Valid String q) throws SolrServerException, IOException, AllianzBotException {

		AllianzBotSolrSearchDocumentResponse allianzBotControllerResponse = botTextExtractorProcess.searchDocument(q);
		log.debug("Inside AllianzBotController.searchSolr AllianzBotControllerResponse is {}",
				allianzBotControllerResponse);

		return allianzBotControllerResponse;
	}

	/**
	 * Handles the 404 page not found error.
	 * 
	 * @return 404 Resource not found error
	 */
	@CrossOrigin(origins = { "*" })
	@GetMapping(value = "*")
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleResourceNotFoundException() {
		return "404";
	}

	@CrossOrigin(origins = { "*" })
	@PutMapping(value = "/update/document")
	public ResponseEntity<AllianzBotSolrCreateDocumentResponse> updateSolrDocument(
			@RequestBody @Valid AllianzBotSentence document, BindingResult bindingResult)
			throws SolrServerException, IOException, AllianzBotException, SAXException, TikaException {

		log.info("Inside AllianzBotController.updateSolrDocument Document is :{}", document);

		if (bindingResult.hasErrors()) {
			log.info("Inside AllianzBotController.updateSolrDocument validation fail");
			throw new AllianzBotException(400, bindingResult.getAllErrors().toString());
		}
		AllianzBotSolrCreateDocumentResponse allianzBotControllerResponse = botTextExtractorProcess
				.updateScore(document);
		log.debug("Inside AllianzBotController.updateSolrDocument AllianzBotSolrCreateDocumentResponse is {}",
				allianzBotControllerResponse);
		return new ResponseEntity<>(allianzBotControllerResponse, HttpStatus.OK);
	}

	public void batchJobToStoreFiles() throws FileNotFoundException, IOException, SAXException, TikaException,
			AllianzBotException, SolrServerException {
		File[] listOfFiles = new File(documents).listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				final String fileName = file.getName();
				MultipartFile multipartFile = new MockMultipartFile(fileName, fileName,
						Files.probeContentType(file.toPath()), new FileInputStream(file));

				botTextExtractorProcess.storeDocument(multipartFile);
				log.info("All new documents are stored successfully");
			}
		}
	}

}
