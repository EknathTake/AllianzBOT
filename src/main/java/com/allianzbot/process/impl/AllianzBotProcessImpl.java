package com.allianzbot.process.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.tika.exception.TikaException;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.allianzbot.exception.AllianzBotException;
import com.allianzbot.model.AllianzBotDocument;
import com.allianzbot.model.AllianzBotResponseStatus;
import com.allianzbot.model.AllianzBotSearchResponse;
import com.allianzbot.process.interfaces.IAllianzBotProcess;
import com.allianzbot.response.AllianzBotSolrCreateDocumentResponse;
import com.allianzbot.response.AllianzBotSolrSearchDocumentResponse;
import com.allianzbot.service.interfaces.IAllianzBotOpenNlpService;
import com.allianzbot.service.interfaces.IAllianzBotSolrService;
import com.allianzbot.service.interfaces.IAllianzBotTextExtractorService;
import com.allianzbot.service.interfaces.IAllianzBotTokenizerService;
import com.allianzbot.utils.AllianzBotContentTypes;
import com.allianzbot.utils.FileUtils;

import opennlp.tools.util.InvalidFormatException;

/**
 * Process layer implementation. Contents extracted from documents based on the
 * content/type of the document
 * 
 * @author eknath.take
 *
 */
@Component(value = "botTextExtractorProcess")
public class AllianzBotProcessImpl implements IAllianzBotProcess {

	@Inject
	@Named("allianzBotTextExtractorService")
	private IAllianzBotTextExtractorService botTextExtractorService;

	@Inject
	@Named("allianzBotTokenizerService")
	private IAllianzBotTokenizerService allianzBotTokenizerService;

	@Inject
	@Named("allianzBotSolrService")
	private IAllianzBotSolrService allianzBotSolrService;

	@Inject
	@Named("allianzBotOpenNlpService")
	private IAllianzBotOpenNlpService allianzBotOpenNlpService;

	private Logger log = LoggerFactory.getLogger(AllianzBotProcessImpl.class);

	@Override
	public AllianzBotSolrCreateDocumentResponse storeDocument(MultipartFile file)
			throws IOException, SAXException, TikaException, AllianzBotException, SolrServerException {
		// extract content from file
		AllianzBotDocument allianzBotServiceResponse = extractContent(file);

		// indexing document
		UpdateResponse updateResponse = allianzBotSolrService.storeDocument(allianzBotServiceResponse);

		return mapServiceToProcessResponse(allianzBotServiceResponse, updateResponse);
	}

	/**
	 * Extractis the content from document file.
	 * 
	 * @param file
	 * @return {@link AllianzBotServiceResponse}
	 * @throws IOException
	 * @throws SAXException
	 * @throws TikaException
	 */
	@Override
	public AllianzBotDocument extractContent(MultipartFile file)
			throws IOException, SAXException, TikaException, AllianzBotException {

		final InputStream inputstream = file.getInputStream();
		final MediaType contentType = MediaType.parse(file.getContentType());

		AllianzBotDocument allianzBotServiceResponse = new AllianzBotDocument();
		if (isMsExcel(contentType))
			allianzBotServiceResponse = botTextExtractorService.excelDataExtraction(inputstream);
		else if (isDocument(contentType))
			allianzBotServiceResponse = botTextExtractorService.documentTextExtractor(inputstream);
		else if (isMsOfficeDocument(contentType))
			allianzBotServiceResponse = botTextExtractorService.msOfficeDocumentTextExtractor(inputstream);
		else if (isPdf(contentType))
			allianzBotServiceResponse = botTextExtractorService.pdfTextExtractor(inputstream);
		else if (isTextFile(contentType))
			allianzBotServiceResponse = botTextExtractorService.textFileExtractor(inputstream);
		else
			throw new AllianzBotException(415,
					"The request entity has a media type which the server or resource does not support. Please upload pdf, txt, docx, doc, xls, xlsx");
		return allianzBotServiceResponse;
	}

	private boolean isMsExcel(MediaType contentType) {
		return MediaType.application("vnd.openxmlformats-officedocument.spreadsheetml.sheet").equals(contentType);
	}

	/**
	 * Map response from service to process layer.
	 * 
	 * @param allianzBotServiceResponse
	 * @param updateResponse
	 * @return {@link AllianzBotSolrCreateDocumentResponse}
	 * @throws IOException
	 */
	private AllianzBotSolrCreateDocumentResponse mapServiceToProcessResponse(
			AllianzBotDocument allianzBotServiceResponse, UpdateResponse updateResponse) throws IOException {

		AllianzBotSolrCreateDocumentResponse allianzBotProcessResponse = new AllianzBotSolrCreateDocumentResponse();
		if (null != allianzBotServiceResponse) {
			AllianzBotDocument allianzBotDocument = new AllianzBotDocument();
			allianzBotDocument.setId(allianzBotServiceResponse.getId());
			allianzBotDocument.setContent(allianzBotServiceResponse.getContent());
			allianzBotProcessResponse.setAllianzBotDocument(allianzBotDocument);
		}

		if (null != updateResponse) {
			AllianzBotResponseStatus status = new AllianzBotResponseStatus();
			status.setMessage(updateResponse.toString());
			status.setStatusCode(updateResponse.getStatus());
			allianzBotProcessResponse.setStatus(status);
		}
		return allianzBotProcessResponse;
	}

	/**
	 * Checks the {@link MediaType} is type of MsOffice
	 * 
	 * @param contentType
	 * @return
	 */
	private boolean isMsOfficeDocument(MediaType contentType) {
		Set<MediaType> docs = AllianzBotContentTypes.MEDIA_TYPE_MS_OFFICE;
		if (docs.contains(contentType))
			return true;
		return false;
	}

	/**
	 * Cheks particular content type for the text.
	 * 
	 * @param contentType
	 * @return boolean value.
	 */
	private boolean isTextFile(MediaType contentType) {
		if (AllianzBotContentTypes.MEDIA_TYPE_TEXT.equals(contentType))
			return true;
		return false;
	}

	/**
	 * Cheks particular content type for the pdf.
	 * 
	 * @param contentType
	 * @return boolean value.
	 */
	private boolean isPdf(MediaType contentType) {
		if (AllianzBotContentTypes.MEDIA_TYPE_PDF.equals(contentType))
			return true;
		return false;
	}

	/**
	 * Cheks particular content type for the document.
	 * 
	 * @param contentType
	 * @return boolean value
	 */
	private boolean isDocument(MediaType contentType) {
		Set<MediaType> docs = AllianzBotContentTypes.MEDIA_TYPE_DOCUMENTS;
		if (docs.contains(contentType))
			return true;
		return false;
	}

	@Override
	public AllianzBotSolrSearchDocumentResponse searchDocument(@Valid @NotNull String query, boolean isSearch)
			throws SolrServerException, InvalidFormatException, IOException, AllianzBotException {
		if (StringUtils.isNotEmpty(query)) {
			// get all the lemmas for user query
			String lemmas = Stream.of(allianzBotOpenNlpService
					.lemmatization(query)).filter(Objects::nonNull)
					.filter(lemma -> !StringUtils.equalsIgnoreCase(lemma, "O"))
					.filter(lemma -> !StringUtils.equalsIgnoreCase(lemma, "be")).distinct().map(lemma -> lemma)
					.collect(Collectors.joining(" "));
			query = query + " "+ lemmas;

			// removing duplicates keywords from the query...
			Set<String> seen = new LinkedHashSet<>();
			String[] splitedQuery = query.split("\\s");
			for (String string : splitedQuery) {
				seen.add(string);
			}
			query = FileUtils.removeStopWords(String.join(" ", seen));
			return allianzBotSolrService.searchDocuments(query, isSearch);
		} else {
			throw new AllianzBotException(HttpStatus.BAD_REQUEST.value(), "Please enter your question.");
		}
	}

	@Override
	public AllianzBotSolrCreateDocumentResponse updateScore(AllianzBotSearchResponse document)
			throws SolrServerException, IOException, AllianzBotException {

		// get the all the documents with the same answer to update the likes
		Function<? super AllianzBotSearchResponse, ? extends AllianzBotSearchResponse> functionLikes = statement -> {
			return new AllianzBotSearchResponse.AllianzBotSearchResponseBuilder()
					.setLikes(document.getLikes())
					.setAllianzBotTestCenterData(statement.getAllianzBotTestCenterData())
					.setAnswer(statement.getAnswer())
					.setId(statement.getId())
					.setQuestion(statement.getQuestion())
					.setScore(statement.getScore())
					.build();
		};

		List<AllianzBotSearchResponse> documents = searchDocument(document.getQuestion(), false).getDocuments();
		List<AllianzBotSearchResponse> allianzBotSearchResponseList = documents
					.parallelStream()
					.filter(Objects::nonNull)
					.filter(x-> null != x.getAnswer())
					.filter(allianzBotSearchResponse -> allianzBotSearchResponse
							.getAnswer()
							.equalsIgnoreCase(document.getAnswer()))
					.map(functionLikes)
					.collect(Collectors.toList());
		
		List<AllianzBotSearchResponse> duplicateTestCenterDataList = documents.parallelStream()
					.filter(Objects::nonNull)
					.filter(allianzBotSearchResponse -> null == allianzBotSearchResponse.getAnswer()
													&& null != allianzBotSearchResponse.getAllianzBotTestCenterData())
					.filter(allianzBotSearchResponse -> StringUtils.equalsIgnoreCase(
							allianzBotSearchResponse.getAllianzBotTestCenterData().getTeam(),
							document.getAllianzBotTestCenterData().getTeam())
							&& StringUtils.equalsIgnoreCase(allianzBotSearchResponse.getAllianzBotTestCenterData().getTestCaseId(),
									document.getAllianzBotTestCenterData().getTestCaseId())
							&& StringUtils.equalsIgnoreCase(allianzBotSearchResponse.getAllianzBotTestCenterData().getExecutionDate(),
									document.getAllianzBotTestCenterData().getExecutionDate())
							&& StringUtils.equalsIgnoreCase(allianzBotSearchResponse.getAllianzBotTestCenterData().getFailedRunCount(),
									document.getAllianzBotTestCenterData().getFailedRunCount())
							&& StringUtils.equalsIgnoreCase(allianzBotSearchResponse.getAllianzBotTestCenterData().getDefectIdStr(),
									document.getAllianzBotTestCenterData().getDefectIdStr())
							&& StringUtils.equalsIgnoreCase(allianzBotSearchResponse.getAllianzBotTestCenterData().getFailureCategory(),
									document.getAllianzBotTestCenterData().getFailureCategory())
							&& StringUtils.equalsIgnoreCase(allianzBotSearchResponse.getAllianzBotTestCenterData().getFailedLog(),
									document.getAllianzBotTestCenterData().getFailedLog())
							&& StringUtils.equalsIgnoreCase(allianzBotSearchResponse.getAllianzBotTestCenterData().getDefectId(),
									document.getAllianzBotTestCenterData().getDefectId()))
					.map(functionLikes)
					.collect(Collectors.toList());
		
		allianzBotSearchResponseList.addAll(duplicateTestCenterDataList);
		if(allianzBotSearchResponseList.size()==0)
			allianzBotSolrService.updateScore(document);
		else {
			for (AllianzBotSearchResponse allianzBotSentence : allianzBotSearchResponseList) {
				if(null != allianzBotSentence)
					allianzBotSolrService.updateScore(allianzBotSentence);
			}
		}
		
		AllianzBotDocument allianzBotDocument = new AllianzBotDocument();
		allianzBotDocument.setContent(document);
		allianzBotDocument.setId(document.getId());
		AllianzBotSolrCreateDocumentResponse mapServiceToProcessResponse = mapServiceToProcessResponse(
				allianzBotDocument, null);
		AllianzBotResponseStatus status = new AllianzBotResponseStatus(new Date(), 200,
				"Thanks for your Contribution!");
		mapServiceToProcessResponse.setStatus(status);
		return mapServiceToProcessResponse;
	}

}