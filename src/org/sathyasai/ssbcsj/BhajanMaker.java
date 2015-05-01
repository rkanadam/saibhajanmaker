package org.sathyasai.ssbcsj;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BhajanMaker extends HttpServlet {
	private static final long serialVersionUID = -1936457346720697934L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse httpResponse) throws ServletException,
			IOException {

		final String json = request.getParameter("bhajans");

		final Response response = new ObjectMapper().reader(Response.class)
				.readValue(json);

		final XMLSlideShow templatePresentation = new XMLSlideShow(request
				.getSession().getServletContext()
				.getResourceAsStream("/WEB-INF/master.pptx"));
		final XSLFSlide template = templatePresentation.getSlides()[0];

		final XMLSlideShow newPresentation = new XMLSlideShow();

		final XMLSlideShow prefix = new XMLSlideShow(request.getSession()
				.getServletContext()
				.getResourceAsStream("/WEB-INF/prefix.pptx"));
		for (final XSLFSlide slide : prefix.getSlides()) {
			newPresentation.createSlide().importContent(slide);
		}

		final List<Bhajan> bhajans = response.getBhajans();

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

		if (!StringUtils.isEmpty(response.getDivineCodeOfConduct())) {
			final XMLSlideShow divineCodeOfConductPresentation = new XMLSlideShow(
					request.getSession()
							.getServletContext()
							.getResourceAsStream(
									"/WEB-INF/divineCodeOfConduct.pptx"));
			for (XSLFSlide slide : divineCodeOfConductPresentation.getSlides()) {
				final XSLFSlide importedSlide = newPresentation.createSlide()
						.importContent(slide);
				((XSLFAutoShape) importedSlide.getShapes()[1])
						.getTextParagraphs().get(0).getTextRuns().get(0)
						.setText(response.getDivineCodeOfConduct());
			}
		}

		if (!StringUtils.isEmpty(response.getThoughtForTheWeek())) {
			final XMLSlideShow thoughtForTheWeekPresentation = new XMLSlideShow(
					request.getSession()
							.getServletContext()
							.getResourceAsStream(
									"/WEB-INF/thoughtForTheWeek.pptx"));
			for (final XSLFSlide slide : thoughtForTheWeekPresentation
					.getSlides()) {
				final XSLFSlide importedSlide = newPresentation.createSlide()
						.importContent(slide);
				((XSLFAutoShape) importedSlide.getShapes()[1])
						.getTextParagraphs().get(0).getTextRuns().get(0)
						.setText(response.getThoughtForTheWeek());
			}
		}

		final XMLSlideShow closingPrayersPresentation = new XMLSlideShow(
				request.getSession().getServletContext()
						.getResourceAsStream("/WEB-INF/closingPrayers.pptx"));
		for (final XSLFSlide slide : closingPrayersPresentation.getSlides()) {
			newPresentation.createSlide().importContent(slide);
		}

		httpResponse.setContentType("application/vnd.ms-ppt");
		httpResponse.setHeader("Content-Disposition",
				"inline; filename=\"bhajans.pptx\"");
		newPresentation.write(httpResponse.getOutputStream());
	}
}
