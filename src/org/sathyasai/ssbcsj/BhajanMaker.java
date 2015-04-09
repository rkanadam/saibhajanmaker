package org.sathyasai.ssbcsj;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class BhajanMaker extends HttpServlet {
	private static final long serialVersionUID = -1936457346720697934L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String bhajanString = request.getParameter("bhajans");
		final Object json = JSONValue.parse(bhajanString);

		final JSONArray array;
		if (json instanceof JSONArray) {
			array = (JSONArray) json;
		} else {
			array = new JSONArray();
			array.add(json);
		}

		final XMLSlideShow templatePresentation = new XMLSlideShow(request
				.getSession().getServletContext()
				.getResourceAsStream("/WEB-INF/poi.pptx"));
		final XSLFSlide template = templatePresentation.getSlides()[0];
		final XMLSlideShow newPresentation = new XMLSlideShow();

		for (final Object o : array) {
			JSONObject bhajan = (JSONObject) o;

			String lyric = StringUtils
					.trimToEmpty((String) bhajan.get("lyric"));
			String meaning = StringUtils.trimToEmpty((String) bhajan
					.get("meaning"));
			String scale = StringUtils
					.trimToEmpty((String) bhajan.get("scale"));

			XSLFSlide slide = newPresentation.createSlide();
			slide.importContent(template);

			final Iterator<XSLFShape> shapes = slide.iterator();

			((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
					.getTextRuns().get(0).setText(lyric);

			((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
					.getTextRuns().get(0).setText(meaning);

			((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
					.getTextRuns().get(0).setText(scale);
		}

		response.setContentType("application/vnd.ms-ppt");
		response.setHeader("Content-Disposition",
				"inline; filename=\"bhajans.pptx\"");
		newPresentation.write(response.getOutputStream());
	}
}
