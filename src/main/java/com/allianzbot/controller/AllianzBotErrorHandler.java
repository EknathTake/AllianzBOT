package com.allianzbot.controller;

import java.io.IOException;
import java.util.Date;
import javax.validation.ConstraintViolationException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.xml.sax.SAXException;

import com.allianzbot.dto.AllianzBotResponseStatus;
import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.response.AllianzBotSolrSearchDocumentResponse;

/**
 * Exception handler for AllianzBot
 * 
 * @author eknath.take
 *
 */
@ControllerAdvice
public class AllianzBotErrorHandler {

	private Logger log = LoggerFactory.getLogger(AllianzBotErrorHandler.class);

	/**
	 * Handles TikaException in AllianzBotExceptionHandler
	 * 
	 * @param te
	 * @return
	 */
	@ExceptionHandler(value = TikaException.class)
	public ResponseEntity<AllianzBotSolrSearchDocumentResponse> badRequest(TikaException te) {

		log.error("TikaException occured in AllianzBOT: ", te);
		AllianzBotResponseStatus allianzBotExceptionInfo = new AllianzBotResponseStatus();
		allianzBotExceptionInfo.setMessage(te.getLocalizedMessage());
		allianzBotExceptionInfo.setStatusCode(HttpStatus.BAD_REQUEST.value());

		AllianzBotSolrSearchDocumentResponse allianzBotControllerResponse = new AllianzBotSolrSearchDocumentResponse();
		allianzBotControllerResponse.setError(allianzBotExceptionInfo);
		return new ResponseEntity<AllianzBotSolrSearchDocumentResponse>(allianzBotControllerResponse,
				HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles SAXException in AllianzBotExceptionHandler
	 * 
	 * @param se
	 * @return
	 */
	@ExceptionHandler(value = SAXException.class)
	public ResponseEntity<AllianzBotSolrSearchDocumentResponse> badRequest(SAXException se) {

		log.error("SAXException occured in AllianzBOT: ", se);
		AllianzBotResponseStatus allianzBotExceptionInfo = new AllianzBotResponseStatus();
		allianzBotExceptionInfo.setMessage(se.getLocalizedMessage());
		allianzBotExceptionInfo.setStatusCode(HttpStatus.BAD_REQUEST.value());

		AllianzBotSolrSearchDocumentResponse allianzBotControllerResponse = new AllianzBotSolrSearchDocumentResponse();
		allianzBotControllerResponse.setError(allianzBotExceptionInfo);
		return new ResponseEntity<AllianzBotSolrSearchDocumentResponse>(allianzBotControllerResponse,
				HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles IOException in AllianzBotExceptionHandler
	 * 
	 * @param ioe
	 * @return
	 */
	@ExceptionHandler(value = IOException.class)
	public ResponseEntity<AllianzBotSolrSearchDocumentResponse> handleIOException(IOException ioe) {

		log.error("IOException occured in AllianzBOT: ", ioe);
		AllianzBotResponseStatus allianzBotExceptionInfo = new AllianzBotResponseStatus();
		allianzBotExceptionInfo.setMessage(ioe.getLocalizedMessage());
		allianzBotExceptionInfo.setStatusCode(HttpStatus.BAD_REQUEST.value());

		AllianzBotSolrSearchDocumentResponse allianzBotControllerResponse = new AllianzBotSolrSearchDocumentResponse();
		allianzBotControllerResponse.setError(allianzBotExceptionInfo);
		return new ResponseEntity<AllianzBotSolrSearchDocumentResponse>(allianzBotControllerResponse,
				HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles AllianzBotException in AllianzBotExceptionHandler
	 * 
	 * @param ioe
	 * @return
	 */
	@ExceptionHandler(value = AllianzBotException.class)
	public ResponseEntity<AllianzBotSolrSearchDocumentResponse> handleAllianzBotException(AllianzBotException abe) {

		log.error("AllianzBotException occured in AllianzBOT: ", abe);
		AllianzBotResponseStatus allianzBotExceptionInfo = new AllianzBotResponseStatus();
		allianzBotExceptionInfo.setMessage(abe.getStatusMessage());
		allianzBotExceptionInfo.setStatusCode(abe.getStatusCode());

		AllianzBotSolrSearchDocumentResponse allianzBotControllerResponse = new AllianzBotSolrSearchDocumentResponse();
		allianzBotControllerResponse.setError(allianzBotExceptionInfo);
		return new ResponseEntity<AllianzBotSolrSearchDocumentResponse>(allianzBotControllerResponse,
				HttpStatus.valueOf(abe.getStatusCode()));
	}

	/**
	 * Handles SolrServerException in AllianzBotExceptionHandler
	 * 
	 * @param ioe
	 * @return
	 */
	@ExceptionHandler(value = SolrServerException.class)
	public ResponseEntity<AllianzBotSolrSearchDocumentResponse> handleSolrServerException(SolrServerException abe) {

		log.error("SolrServerException occured in AllianzBOT: ", abe);
		AllianzBotResponseStatus allianzBotExceptionInfo = new AllianzBotResponseStatus();
		allianzBotExceptionInfo.setMessage(abe.getLocalizedMessage());
		allianzBotExceptionInfo.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

		AllianzBotSolrSearchDocumentResponse allianzBotControllerResponse = new AllianzBotSolrSearchDocumentResponse();
		allianzBotControllerResponse.setError(allianzBotExceptionInfo);
		return new ResponseEntity<AllianzBotSolrSearchDocumentResponse>(allianzBotControllerResponse,
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handles NullPointerException in AllianzBotExceptionHandler
	 * 
	 * @param ioe
	 * @return
	 */
	@ExceptionHandler(value = NullPointerException.class)
	public ResponseEntity<AllianzBotSolrSearchDocumentResponse> handleNullPointerException(NullPointerException abe) {

		log.error("NullPointerException occured in AllianzBOT: ", abe);
		AllianzBotResponseStatus allianzBotExceptionInfo = new AllianzBotResponseStatus();
		allianzBotExceptionInfo.setMessage(abe.getLocalizedMessage());
		allianzBotExceptionInfo.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

		AllianzBotSolrSearchDocumentResponse allianzBotControllerResponse = new AllianzBotSolrSearchDocumentResponse();
		allianzBotControllerResponse.setError(allianzBotExceptionInfo);
		return new ResponseEntity<AllianzBotSolrSearchDocumentResponse>(allianzBotControllerResponse,
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handles RemoteSolrException in AllianzBotExceptionHandler
	 * 
	 * @param ioe
	 * @return
	 */
	@ExceptionHandler(value = RemoteSolrException.class)
	public ResponseEntity<AllianzBotSolrSearchDocumentResponse> handleRemoteSolrException(RemoteSolrException abe) {

		log.error("RemoteSolrException occured in AllianzBOT: ", abe);
		AllianzBotResponseStatus allianzBotExceptionInfo = new AllianzBotResponseStatus();
		allianzBotExceptionInfo.setMessage(abe.getLocalizedMessage());
		allianzBotExceptionInfo.setStatusCode(HttpStatus.valueOf(523).value());

		AllianzBotSolrSearchDocumentResponse allianzBotControllerResponse = new AllianzBotSolrSearchDocumentResponse();
		allianzBotControllerResponse.setError(allianzBotExceptionInfo);
		return new ResponseEntity<AllianzBotSolrSearchDocumentResponse>(allianzBotControllerResponse,
				HttpStatus.valueOf(523));
	}

	@ExceptionHandler
	public ResponseEntity<AllianzBotSolrSearchDocumentResponse> handle(MethodArgumentNotValidException exception) {
		AllianzBotSolrSearchDocumentResponse body = new AllianzBotSolrSearchDocumentResponse();
		AllianzBotResponseStatus error = new AllianzBotResponseStatus(new Date(), HttpStatus.BAD_REQUEST.value(),
				exception.getMessage());
		body.setError(error);
		ResponseEntity<AllianzBotSolrSearchDocumentResponse> responseEntity = new ResponseEntity<AllianzBotSolrSearchDocumentResponse>(
				body, HttpStatus.BAD_REQUEST);
		return responseEntity;
	}

	@ExceptionHandler
	public ResponseEntity<AllianzBotSolrSearchDocumentResponse> handle(ConstraintViolationException exception) {
		AllianzBotSolrSearchDocumentResponse body = new AllianzBotSolrSearchDocumentResponse();
		AllianzBotResponseStatus error = new AllianzBotResponseStatus(new Date(), HttpStatus.BAD_REQUEST.value(),
				exception.getMessage());
		body.setError(error);
		ResponseEntity<AllianzBotSolrSearchDocumentResponse> responseEntity = new ResponseEntity<AllianzBotSolrSearchDocumentResponse>(
				body, HttpStatus.BAD_REQUEST);
		return responseEntity;
	}

	/**
	 * Handles NullPointerException in AllianzBotExceptionHandler
	 * 
	 * @param ioe
	 * @return
	 */
	/*@ExceptionHandler(value = Exception.class)
	public ResponseEntity<AllianzBotSolrSearchDocumentResponse> handleException(Exception abe) {

		log.error("Unknown Exception occured in AllianzBOT: ", abe);
		AllianzBotResponseStatus allianzBotExceptionInfo = new AllianzBotResponseStatus();
		allianzBotExceptionInfo.setMessage(abe.getLocalizedMessage());
		allianzBotExceptionInfo.setStatusCode(520);

		AllianzBotSolrSearchDocumentResponse allianzBotControllerResponse = new AllianzBotSolrSearchDocumentResponse();
		allianzBotControllerResponse.setError(allianzBotExceptionInfo);
		return new ResponseEntity<AllianzBotSolrSearchDocumentResponse>(allianzBotControllerResponse,
				HttpStatus.valueOf(520));
	}
*/
}
