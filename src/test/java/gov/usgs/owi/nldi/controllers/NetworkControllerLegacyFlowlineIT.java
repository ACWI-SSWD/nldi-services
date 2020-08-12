package gov.usgs.owi.nldi.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.owi.nldi.BaseIT;
import gov.usgs.owi.nldi.transform.FlowLineTransformer;

@EnableWebMvc
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@DatabaseSetup("classpath:/testData/crawlerSource.xml")
public class NetworkControllerLegacyFlowlineIT extends BaseIT {

	@Value("${serverContextPath}")
	private String context;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private static final String RESULT_FOLDER  = "network/flowline/";

	@BeforeEach
	public void setUp() {
		urlRoot = "http://localhost:" + port + context;
	}

	//UT Testing
	@Test
	public void getComidUtTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13293474/navigation/UT/flowlines?distance=9999&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"7",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13293474_UT.json"),
				true,
				false);
	}

	@Test
	public void getComidUtDistanceTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13297246/navigation/UT/flowlines?distance=10&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"9",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13297246_UT_distance_10.json"),
				true,
				false);
	}

	@Test
	public void getComidUtDiversionTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13294158/navigation/UT/flowlines?distance=9999&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"15",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13294158_UT.json"),
				true,
				false);
	}

	//UM Testing
	@Test
	public void getComidUmTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13293474/navigation/UM/flowlines?distance=9999&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"4",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13293474_UM.json"),
				true,
				false);
	}

	@Test
	public void getComidUmDistanceTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13297246/navigation/UM/flowlines?distance=10&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"6",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13297246_UM_distance_10.json"),
				true,
				false);
	}

	//DM Testing
	@Test
	public void getComidDmTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13296790/navigation/DM/flowlines?distance=9999&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"5",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13296790_DM.json"),
				true,
				false);
	}

	@Test
	public void getComidDmDiversionsNotIncludedTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13294310/navigation/DM/flowlines?distance=9999&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"42",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13294310_DM.json"),
				true,
				false);
	}

	@Test
	public void getComidDmDistanceTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13293474/navigation/DM/flowlines?distance=10&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"8",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13293474_DM_distance_10.json"),
				true,
				false);
	}

	//DD Testing
	@Test
	public void getComidDdTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13294310/navigation/DD/flowlines?distance=9999&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"49",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13294310_DD.json"),
				true,
				false);
	}

	@Test
	public void getComidDdDistanceTest() throws Exception {
		//We are going to sacrifice a little accuracy for performance, so this does not match the old way...
		assertEntity(restTemplate,
				"/linked-data/comid/13294310/navigation/DD/flowlines?distance=11&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"11",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13294310_DD_distance_11_legacy.json"),
				true,
				false);
	}

	//PP Testing
	@Test
	public void getComidPpStopComidInvalidTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13297246/navigation/PP/flowlines?distance=9999&stopComid=13297198&legacy=true",
				HttpStatus.BAD_REQUEST.value(),
				null,
				null,
				MediaType.APPLICATION_JSON_VALUE,
				getCompareFile(RESULT_FOLDER, "comid_13297246_PP_stop_13297198.json"),
				true,
				true);
	}

	@Test
	public void getComidPpStopComidTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13297198/navigation/PP/flowlines?distance=9999&stopComid=13297246&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"12",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13297198_PP_stop_13297246.json"),
				true,
				false);
	}

	//Interesting diversion/tributary
	//There is another simple diversion between 13294248 and 13294242
	@Test
	public void interestingTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13293844/navigation/DM/flowlines?distance=5&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"7",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13293844_DM_distance_5.json"),
				true,
				false);

		assertEntity(restTemplate,
				"/linked-data/comid/13293844/navigation/DD/flowlines?distance=5&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"14",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13293844_DD_distance_5.json"),
				true,
				false);

		assertEntity(restTemplate,
				"/linked-data/comid/13294328/navigation/DM/flowlines?distance=5&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"6",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13294328_DM_distance_5.json"),
				true,
				false);

		assertEntity(restTemplate,
				"/linked-data/comid/13294328/navigation/DD/flowlines?distance=5&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"10",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13294328_DD_distance_5.json"),
				true,
				false);

		assertEntity(restTemplate,
				"/linked-data/comid/13294390/navigation/UM/flowlines?distance=5&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"6",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13294390_UM_distance_5.json"),
				true,
				false);

		assertEntity(restTemplate,
				"/linked-data/comid/13294390/navigation/UT/flowlines?distance=5&legacy=true",
				HttpStatus.OK.value(),
				FlowLineTransformer.FLOW_LINES_COUNT_HEADER,
				"22",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13294390_UT_distance_5.json"),
				true,
				false);
	}

}
