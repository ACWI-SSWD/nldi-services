package gov.usgs.owi.nldi.services;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;

import gov.usgs.owi.nldi.dao.NavigationDao;
import gov.usgs.owi.nldi.transform.FeatureTransformer;

@Service
public class Navigation {
	private static final Logger LOG = LoggerFactory.getLogger(Navigation.class);

	public static final String COMID = FeatureTransformer.COMID;
	public static final String DD = "DD";
	public static final String DISTANCE = "distance";
	public static final String DM = "DM";
	public static final String DOWNSTREAM_DIVERSIONS = "downstreamDiversions";
	public static final String DOWNSTREAM_MAIN = "downstreamMain";
	public static final String NAVIGATION_MODE = "navigationMode";
	public static final String UM = "UM";
	public static final String UT = "UT";
	public static final String UPSTREAM_MAIN = "upstreamMain";
	public static final String UPSTREAM_TRIBUTARIES = "upstreamTributaries";
	public static final String STOP_COMID = "stopComid";

	protected final NavigationDao navigationDao;

	@Autowired
	public Navigation(NavigationDao inNavigationDao) {
		navigationDao = inNavigationDao;
	}

	public String navigate(OutputStream responseStream, final String comid, final String navigationMode,
			final String distance, final String stopComid) {
		LOG.trace("entering navigation");

		Map<String, Object> parameterMap = processParameters(comid, navigationMode, distance, stopComid);
		LOG.trace("parameters processed");

		String sessionId = navigationDao.getCache(parameterMap);

		if (null == sessionId) {
			Map<?,?> navigationResult = navigationDao.navigate(parameterMap);
			LOG.trace("navigation built");

			sessionId = interpretResult(responseStream, navigationResult);

		}
		LOG.trace("leaving navigation");

		return sessionId;
	}

	protected Map<String, Object> processParameters(final String comid, final String navigationMode,
			final String distance, final String stopComid) {
		Map<String, Object> parameterMap = new HashMap<> ();

		if (StringUtils.isNotBlank(comid)) {
			LOG.debug("comid:" + comid);
			parameterMap.put(COMID, NumberUtils.parseNumber(comid, Integer.class));
		}
		if (StringUtils.isNotBlank(navigationMode)) {
			LOG.debug("navigationMode:" + navigationMode);
			parameterMap.put(NAVIGATION_MODE, navigationMode);
		}
		if (StringUtils.isNotBlank(distance)) {
			LOG.debug("distance:" + distance);
			parameterMap.put(DISTANCE, NumberUtils.parseNumber(distance, BigDecimal.class));
		}
		if (StringUtils.isNotBlank(stopComid)) {
			LOG.debug("stopComid:" + stopComid);
			parameterMap.put(STOP_COMID, NumberUtils.parseNumber(stopComid, Integer.class));
		}

		LOG.debug("Request Parameters:" + parameterMap.toString());

		return parameterMap;
	}

	protected String interpretResult(OutputStream responseStream, Map<?,?> navigationResult) {
		//An Error Result - {navigate=(,,,,-1,"Valid navigation type codes are UM, UT, DM, DD and PP.",)}
		//Another Error - {navigate=(13297246,1.1545800000,13297198,48.5846800000,310,"Start ComID must have a hydroseq greater than the hydroseq for stop ComID.",{f170f490-00ad-11e6-8f62-0242ac110003})}
		//A Good Result - {navigate=(13297246,0.0000000000,,,0,,{4d06cca2-001e-11e6-b9d0-0242ac110003})}
		LOG.debug("return from navigate:" + navigationResult.get(NavigationDao.NAVIGATE_CACHED).toString());

		String sessionId = null;
		String resultCode = null;
		String statusMessage = null;

		try {
			String resultCsv = navigationResult.get(NavigationDao.NAVIGATE_CACHED).toString().replace("(", "").replace(")", "");
			CsvMapper mapper = new CsvMapper();
			mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
			MappingIterator<String[]> mi = mapper.readerFor(String[].class).readValues(resultCsv);
			while (mi.hasNext()) {
				String[] result = mi.next();

				resultCode = result[4];
				statusMessage = result[5];

				if ("0".equals(resultCode)) {
					sessionId = result[6];
				} else {
					String msg = "{\"errorCode\":" + resultCode + ", \"errorMessage\":\"" + statusMessage + "\"}";
					LOG.debug(msg);
					responseStream.write(msg.getBytes());
				}
			}
		} catch (Throwable e) {
			LOG.error("Unable to stream error message", e);
		}

		return sessionId;
	}

}