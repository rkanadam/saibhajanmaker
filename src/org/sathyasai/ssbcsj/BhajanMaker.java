package org.sathyasai.ssbcsj;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

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

		final MappingIterator<Bhajan> iterator = new ObjectMapper().reader(
				Bhajan.class).readValues(json);
		final List<Bhajan> bhajans = iterator.readAll();

		final XMLSlideShow templatePresentation = new XMLSlideShow(request
				.getSession().getServletContext()
				.getResourceAsStream("/WEB-INF/master.pptx"));
		final XSLFSlide template = templatePresentation.getSlides()[0];

		final XMLSlideShow newPresentation = new XMLSlideShow();

		final XMLSlideShow prefix = new XMLSlideShow(request.getSession()
				.getServletContext()
				.getResourceAsStream("/WEB-INF/prefix.pptx"));
		for (XSLFSlide slide : prefix.getSlides()) {
			newPresentation.createSlide().importContent(slide);
		}

		for (int i = 0, len = bhajans.size(); i < len; ++i) {

			final Bhajan bhajan = bhajans.get(i);
			final XSLFSlide slide = newPresentation.createSlide();
			slide.importContent(template);

			final Iterator<XSLFShape> shapes = slide.iterator();

			((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
					.getTextRuns().get(0).setText(bhajan.getLyrics());

			((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
					.getTextRuns().get(0).setText(bhajan.getMeaning());

			((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
					.getTextRuns().get(0).setText(bhajan.getScale());

			if (i + 1 == len) {
				((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
						.getTextRuns().get(0).setText("");
				((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
						.getTextRuns().get(0).setText("");
			} else {
				final Bhajan nextBhajan = bhajans.get(i + 1);
				((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
						.getTextRuns().get(0).setText(nextBhajan.getScale());

				String firstLineOfNextBhajan = nextBhajan.getLyrics().split(
						"\n")[0];
				firstLineOfNextBhajan = firstLineOfNextBhajan.substring(0,
						Math.min(firstLineOfNextBhajan.length(), 35));
				if (!firstLineOfNextBhajan.endsWith(" ")) {
					int lastIndex = firstLineOfNextBhajan.lastIndexOf(' ');
					if (lastIndex != -1) {
						firstLineOfNextBhajan = firstLineOfNextBhajan
								.substring(0, lastIndex);
					}
				}
				((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
						.getTextRuns().get(0).setText(firstLineOfNextBhajan);
			}
		}

		final XMLSlideShow suffix = new XMLSlideShow(request.getSession()
				.getServletContext()
				.getResourceAsStream("/WEB-INF/suffix.pptx"));
		for (XSLFSlide slide : suffix.getSlides()) {
			newPresentation.createSlide().importContent(slide);
		}


		response.setContentType("application/vnd.ms-ppt");
		response.setHeader("Content-Disposition",
				"inline; filename=\"bhajans.pptx\"");
		newPresentation.write(response.getOutputStream());
	}
}
