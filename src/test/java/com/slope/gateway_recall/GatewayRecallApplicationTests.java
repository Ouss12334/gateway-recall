package com.slope.gateway_recall;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.web.reactive.server.WebTestClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
	,properties = {"httpbinUrl=http://localhost:${wiremock.server.port}"} // name of var in conf class
)
@AutoConfigureWireMock(port = 0) // start wiremock to mock httpbin api
class GatewayRecallApplicationTests {

	@Autowired
	private WebTestClient webClient;

	@Test // 1 test
	void contextLoads() {
		log.info("mocking gateway redirection");
		
		// mock httpbin api response
		stubFor(
			get(urlEqualTo("/get"))
        	.willReturn(
				aResponse()
				.withBody("{\"headers\":{\"hello\":\"test\"}}")
				.withHeader("Content-Type", "application/json")));
		
		stubFor(
			get("/delay/5")
			.willReturn(
				aResponse()
				.withBody("""
					{
						args: { },
						data: "",
						files: { },
						form: { },
						headers: {
						Accept: "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
						Accept-Encoding: "gzip, deflate",
						Accept-Language: "fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7",
						Dnt: "1",
						Host: "httpbin.org",
						Sec-Gpc: "1",
						Upgrade-Insecure-Requests: "1",
						User-Agent: "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36",
						X-Amzn-Trace-Id: "Root=1-67f67e5a-0067b6c2741b3aa902864dd3"
						},
						origin: "185.169.159.20",
						url: "http://httpbin.org/delay/5"
					}""")
				.withFixedDelay(5000)
				));

		log.info("running test /get");
		webClient
		.get()
		.uri("/get")
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.jsonPath("$.headers.hello").isEqualTo("test"); // headers in body like the api

		log.info("running test /delay");
		webClient.get()
		.uri("/delay/5")
		.header("host", "www.somereal.site")
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo("fallback".getBytes()));
	}

}
