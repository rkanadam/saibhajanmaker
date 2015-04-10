package org.sathyasai.ssbcsj;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

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

		final String json = request.getParameter("bhajans");

		final XMLSlideShow templatePresentation = new XMLSlideShow(request
				.getSession().getServletContext()
				.getResourceAsStream("/WEB-INF/poi.pptx"));
		final XSLFSlide template = templatePresentation.getSlides()[0];
		final XMLSlideShow newPresentation = new XMLSlideShow();

		final MappingIterator<Bhajan> iterator = new ObjectMapper().reader(
				Bhajan.class).readValues(json);
		while (iterator.hasNext()) {
			final Bhajan bhajan = iterator.next();
			XSLFSlide slide = newPresentation.createSlide();
			slide.importContent(template);

			final Iterator<XSLFShape> shapes = slide.iterator();

			((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
					.getTextRuns().get(0).setText(bhajan.getLyrics());

			((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
					.getTextRuns().get(0).setText(bhajan.getMeaning());

			((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
					.getTextRuns().get(0).setText(bhajan.getScale());

		}

		response.setContentType("application/vnd.ms-ppt");
		response.setHeader("Content-Disposition",
				"inline; filename=\"bhajans.pptx\"");
		newPresentation.write(response.getOutputStream());
	}
}
