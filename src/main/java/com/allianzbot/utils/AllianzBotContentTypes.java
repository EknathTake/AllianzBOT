package com.allianzbot.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.tika.mime.MediaType;

/**
 * All the supported MIME types.
 * 
 * @author eknath.take
 *
 */
public class AllianzBotContentTypes {

	public static final Set<MediaType> MEDIA_TYPE_DOCUMENTS = Collections.unmodifiableSet(new HashSet<>(
			Arrays.asList(MediaType.application("vnd.openxmlformats-officedocument.presentationml.presentation"),
					MediaType.application("vnd.ms-powerpoint.presentation.macroenabled.12"),
					MediaType.application("vnd.openxmlformats-officedocument.presentationml.template"),
					MediaType.application("vnd.openxmlformats-officedocument.presentationml.slideshow"),
					MediaType.application("vnd.ms-powerpoint.slideshow.macroenabled.12"),
					MediaType.application("vnd.ms-powerpoint.addin.macroenabled.12"),
					MediaType.application("vnd.ms-powerpoint.template.macroenabled.12"),
					MediaType.application("vnd.ms-powerpoint.slide.macroenabled.12"),
					MediaType.application("vnd.openxmlformats-officedocument.presentationml.slide"),

					/*MediaType.application("vnd.openxmlformats-officedocument.spreadsheetml.sheet"),*/
					MediaType.application("vnd.ms-excel.sheet.macroenabled.12"),
					MediaType.application("vnd.openxmlformats-officedocument.spreadsheetml.template"),
					MediaType.application("vnd.ms-excel.template.macroenabled.12"),
					MediaType.application("vnd.ms-excel.addin.macroenabled.12"),
					MediaType.application("vnd.ms-excel.sheet.binary.macroenabled.12"),

					MediaType.application("vnd.openxmlformats-officedocument.wordprocessingml.document"),
					MediaType.application("vnd.ms-word.document.macroenabled.12"),
					MediaType.application("vnd.openxmlformats-officedocument.wordprocessingml.template"),
					MediaType.application("vnd.ms-word.template.macroenabled.12"),

					MediaType.application("vnd.ms-visio.drawing"),
					MediaType.application("vnd.ms-visio.drawing.macroenabled.12"),
					MediaType.application("vnd.ms-visio.stencil"),
					MediaType.application("vnd.ms-visio.stencil.macroenabled.12"),
					MediaType.application("vnd.ms-visio.template"),
					MediaType.application("vnd.ms-visio.template.macroenabled.12"),
					MediaType.application("vnd.ms-visio.drawing"), MediaType.application("vnd.ms-xpsdocument"),
					MediaType.parse("model/vnd.dwfx+xps"), MediaType.application("vnd.ms-xpsdocument"))));

	public static final Set<MediaType> MEDIA_TYPE_MS_OFFICE = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList(MediaType.application("vnd.ms-excel"),
					MediaType.application("msword"), MediaType.application("x-tika-msoffice"),
					MediaType.application("x-tika-ooxml-protected"), MediaType.application("vnd.ms-powerpoint"),
					MediaType.application("x-mspublisher"), MediaType.application("vnd.ms-project"),
					MediaType.application("vnd.visio"), MediaType.application("vnd.ms-works"),
					MediaType.application("x-tika-msworks-spreadsheet"), MediaType.application("vnd.ms-outlook"),
					MediaType.application("sldworks"), MediaType.application("vnd.ms-graph"),
					new MediaType(MediaType.application("x-tika-msoffice-embedded"), "format", "ole10_native"),
					new MediaType(MediaType.application("x-tika-msoffice-embedded"), "format", "comp_obj"))));

	public static final MediaType MEDIA_TYPE_PDF = MediaType.application("pdf");

	public static final MediaType MEDIA_TYPE_TEXT = MediaType.TEXT_PLAIN;

}
