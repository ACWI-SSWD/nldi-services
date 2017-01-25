package gov.usgs.owi.nldi.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.springinit.TestSpringConfig;

public class FeatureTransformerTest {

	protected FeatureTransformer transformer;
	protected MockHttpServletResponse response;

	@Before
	public void beforeTest() throws IOException {
		response = new MockHttpServletResponse();
		transformer = new FeatureTransformer(response, TestSpringConfig.TEST_ROOT_URL);
	}

	@After
	public void afterTest() throws Exception {
		transformer.close();
	}

	@Test
	public void writePropertiesTest() {
		Map<String, Object> map = new HashMap<>();
		map.put(FlowLineTransformer.NHDPLUS_COMID, "13293474");
		map.put(FeatureTransformer.COMID, "47439231");
		map.put(FeatureTransformer.IDENTIFIER, "identifierValue");
		map.put(FeatureTransformer.NAME, "nameValue");
		map.put(FeatureTransformer.URI, "uriValue");
		map.put(LookupDao.SOURCE, "sourceValue");
		map.put(FeatureTransformer.SOURCE_NAME_DB, "sourceNameValue");
		map.put(FeatureTransformer.REACHCODE, "05020002004263");
		map.put(FeatureTransformer.MEASURE, 1.3823300000);
		try {
			transformer.g.writeStartObject();
			transformer.writeProperties(transformer.g, map);
			transformer.g.writeEndObject();
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals("{\"source\":\"sourceValue\",\"sourceName\":\"sourceNameValue\",\"identifier\":\"identifierValue\",\"name\":\"nameValue\","
					+ "\"uri\":\"uriValue\",\"comid\":\"47439231\",\"reachcode\":\"05020002004263\",\"measure\":\"1.38233\","
					+ "\"navigation\":\"" + TestSpringConfig.TEST_ROOT_URL + "/sourcevalue/identifierValue/navigate\"}",
					response.getContentAsString());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testWritePropertiesNoReachNoMeasure() {
		Map<String, Object> map = new HashMap<>();
		map.put(FlowLineTransformer.NHDPLUS_COMID, "13294118");
		map.put(FeatureTransformer.COMID, "81149213");
		map.put(FeatureTransformer.IDENTIFIER, "identifier2Value");
		map.put(FeatureTransformer.NAME, "name2Value");
		map.put(FeatureTransformer.URI, "uri2Value");
		map.put(LookupDao.SOURCE, "source2Value");
		map.put(FeatureTransformer.SOURCE_NAME_DB, "sourceName2Value");

		try {
			transformer.g.writeStartObject();
			transformer.writeProperties(transformer.g, map);
			transformer.g.writeEndObject();
			//need to flush the JsonGenerator to get at output. 
			transformer.g.flush();
			assertEquals("{\"source\":\"source2Value\",\"sourceName\":\"sourceName2Value\",\"identifier\":\"identifier2Value\",\"name\":\"name2Value\","
						+ "\"uri\":\"uri2Value\",\"comid\":\"81149213\","
						+ "\"navigation\":\"" + TestSpringConfig.TEST_ROOT_URL + "/source2value/identifier2Value/navigate\"}",
						response.getContentAsString());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}
}
