package com.allianzbot.functions;

import org.springframework.mock.web.MockMultipartFile;

@FunctionalInterface
public interface AllianzBotMockMultipartFileFunction {

	MockMultipartFile apply();
}
