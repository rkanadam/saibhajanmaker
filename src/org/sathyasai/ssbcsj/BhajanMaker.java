package org.sathyasai.ssbcsj;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

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

		final XMLSlideShow newPresentation;
		if ("GAB2015".equals(response.getTemplate())) {
			newPresentation = renderGABBhajans(request, response);
		} else if ("GAB2016".equals(response.getTemplate())) {
			newPresentation = renderTemplate(request, response, "/WEB-INF/templates/GAB2016/master.pptx");
		} else if ("Peninsula".equalsIgnoreCase(response.getTemplate())) {
			newPresentation = renderPeninsulaTemplate(request, response);
		} else if ("Shivaratri2016".equalsIgnoreCase(response.getTemplate())) {
			newPresentation = renderShivaratri2016Bhajans(request, response);
		} else if ("Regional Retreat 2016".equalsIgnoreCase(response.getTemplate())) {
			newPresentation = renderRegionalRetreatBhajans(request, response);
		} else if ("25th Anniversary".equalsIgnoreCase(response.getTemplate())) {
			newPresentation = renderCSJBhajanTemplate(request, response, "25th Anniversary");
		} else {
			newPresentation = renderRegularBhajans(request, response);
		}

		httpResponse.setContentType("application/vnd.ms-ppt");
		httpResponse.setHeader("Content-Disposition",
				"inline; filename=\"bhajans.pptx\"");
		newPresentation.write(httpResponse.getOutputStream());
	}


	private XMLSlideShow renderGABBhajans(final HttpServletRequest request,
			final Response response) throws IOException {
		String x = "GAB2015";
		final XMLSlideShow templatePresentation = new XMLSlideShow(
				request				.getSession().getServletContext()
				.getResourceAsStream("/WEB-INF/templates/" + x + "/master.pptx"));
		final XSLFSlide template = templatePresentation.getSlides().get(0);

		final XMLSlideShow newPresentation = new XMLSlideShow();

		final List<Bhajan> bhajans = response.getBhajans();

		for (int i = 0, len = bhajans.size(); i < len; ++i) {

			final Bhajan bhajan = bhajans.get(i);
			String firstLineOfNextBhajan = "", scaleOfNextBhajan = "";

			if (i + 1 < len) {
				final Bhajan nextBhajan = bhajans.get(i + 1);
				firstLineOfNextBhajan = getFirstLineForSlideBottom(nextBhajan
						.getLyrics());
				scaleOfNextBhajan = nextBhajan.getScale();
			}

			final String[] parts = Pattern.compile("^\\s*$", Pattern.MULTILINE)
					.split(bhajan.getLyrics());
			for (int j = 0, jlen = parts.length; j < jlen; ++j) {
				final XSLFSlide slide = templatePresentation.createSlide();
				slide.importContent(template);

				final Iterator<XSLFShape> shapes = slide.iterator();
				shapes.next();

				if (j < parts.length - 1) {
					((XSLFAutoShape) shapes.next())
							.getTextParagraphs()
							.get(0)
							.getTextRuns()
							.get(0)
							.setText(
									getFirstLineForSlideBottom("Continued: "
											+ StringUtils
													.trimToEmpty(parts[j + 1])));
					(((XSLFAutoShape) shapes.next()).getTextParagraphs())
							.get(0).getTextRuns().get(0)
							.setText(StringUtils.trimToEmpty(parts[j]));

					((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
							.getTextRuns().get(0).setText(bhajan.getMeaning());

					((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
							.getTextRuns().get(0).setText(bhajan.getScale());

					((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
							.getTextRuns().get(0).setText(bhajan.getScale());
				} else {
					((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
							.getTextRuns().get(0)
							.setText(firstLineOfNextBhajan);

					(((XSLFAutoShape) shapes.next()).getTextParagraphs())
							.get(0).getTextRuns().get(0)
							.setText(StringUtils.trimToEmpty(parts[j]));

					((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
							.getTextRuns().get(0).setText(bhajan.getMeaning());

					((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
							.getTextRuns().get(0).setText(scaleOfNextBhajan);

					((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
							.getTextRuns().get(0).setText(bhajan.getScale());
				}
			}
		}

		return templatePresentation;
	}

	private XMLSlideShow renderShivaratri2016Bhajans(
			final HttpServletRequest request, final Response response)
			throws IOException {
		return renderTemplate(request, response, "/WEB-INF/templates/Shivaratri2016/master.pptx");
	}
	
	private XMLSlideShow renderRegionalRetreatBhajans(
			final HttpServletRequest request, final Response response)
			throws IOException {
		return renderTemplate(request, response, "/WEB-INF/templates/Regional Retreat 2016/master.pptx");
	}

	private XMLSlideShow renderTemplate(final HttpServletRequest request,
			final Response response, String templateFilePath) throws IOException {
		return renderTemplate(request, response, templateFilePath, null);		
	}
	
	private XMLSlideShow renderTemplate(final HttpServletRequest request,
			final Response response, String templateFilePath, XMLSlideShow newPresentation)
			throws IOException {
		if (newPresentation == null) {
			newPresentation = new XMLSlideShow();	
		}
		
		final XMLSlideShow templatePresentation = new XMLSlideShow(request
				.getSession()
				.getServletContext()
				.getResourceAsStream(
						templateFilePath));
		final XSLFSlide template = templatePresentation.getSlides().get(0);


		final List<Bhajan> bhajans = response.getBhajans();

		for (int i = 0, len = bhajans.size(); i < len; ++i) {

			final Bhajan bhajan = bhajans.get(i);
			String firstLineOfNextBhajan = "", scaleOfNextBhajan = "";

			if (i + 1 < len) {
				final Bhajan nextBhajan = bhajans.get(i + 1);
				firstLineOfNextBhajan = getFirstLineForSlideBottom(nextBhajan
						.getLyrics());
				scaleOfNextBhajan = nextBhajan.getScale();
			}

			final String[] parts = Pattern.compile("^\\s*$", Pattern.MULTILINE)
					.split(bhajan.getLyrics());
			for (int j = 0, jlen = parts.length; j < jlen; ++j) {
				final XSLFSlide slide = newPresentation.createSlide();
				slide.importContent(template);

				final Iterator<XSLFShape> shapes = slide.iterator();
				shapes.next();

				if (j < parts.length - 1) {
					setValueIntoShape(slide, "NextBhajan",
							getFirstLineForSlideBottom("Continued: "
									+ StringUtils.trimToEmpty(parts[j + 1])));
					setValueIntoShape(slide, "NextScale", bhajan.getScale());
					setValueIntoShape(slide, "NextBhajanNextScale", "Next: " + "Continued: "
							+ StringUtils.trimToEmpty(parts[j + 1]) + ", " + bhajan.getScale());
				} else {
					setValueIntoShape(slide, "NextBhajan",
							firstLineOfNextBhajan);
					setValueIntoShape(slide, "NextScale", scaleOfNextBhajan);
					setValueIntoShape(slide, "NextBhajanNextScale", "Next: " + firstLineOfNextBhajan + ", " + scaleOfNextBhajan);
				}

				setValueIntoShape(slide, "Bhajan", parts[j]);

				setValueIntoShape(slide, "Meaning", bhajan.getMeaning());

				setValueIntoShape(slide, "Scale", bhajan.getScale());
			}
		}

		return newPresentation;
	}

	private XSLFAutoShape findShape(final XSLFSlide slide, final String string) {
		for (XSLFShape shape : slide) {
			if (shape instanceof XSLFAutoShape) {
				final XSLFAutoShape textShape = XSLFAutoShape.class.cast(shape);
				if (textShape.getText().trim().equalsIgnoreCase(string)) {
					return textShape;
				}
			}
		}
		return null;
	}

	private boolean setValueIntoShape(final XSLFSlide slide,
			final String shapePlaceHolderToLookFor, final String newValue) {
		final XSLFAutoShape shape = findShape(slide, shapePlaceHolderToLookFor);
		if (shape != null) {
			shape.getTextParagraphs().get(0).getTextRuns().get(0)
					.setText(newValue);
			return true;
		}
		return false;
	}

	private XMLSlideShow renderPeninsulaTemplate(
			final HttpServletRequest request, final Response response)
			throws IOException {

		final XMLSlideShow newPresentation = new XMLSlideShow();

		final XMLSlideShow thoughtForTheDay = new XMLSlideShow(
				request.getSession()
						.getServletContext()
						.getResourceAsStream(
								"/WEB-INF/templates/Peninsula/thought_for_the_day.pptx"));

		final XSLFSlide thoughtForTheDaySlide = newPresentation.createSlide();
		thoughtForTheDaySlide.importContent(thoughtForTheDay.getSlides().get(0));
		final Iterator<XSLFShape> thoughtForTheDayIterator = thoughtForTheDaySlide
				.iterator();
		thoughtForTheDayIterator.next();
		thoughtForTheDayIterator.next();
		thoughtForTheDayIterator.next();
		thoughtForTheDayIterator.next();
		((XSLFAutoShape) thoughtForTheDayIterator.next()).getTextParagraphs()
				.get(0).getTextRuns().get(0)
				.setText(response.getThoughtForTheWeek());

		final XMLSlideShow after_thought_for_the_day = new XMLSlideShow(
				request.getSession()
						.getServletContext()
						.getResourceAsStream(
								"/WEB-INF/templates/Peninsula/after_thought_for_the_day.pptx"));
		for (final XSLFSlide slide : after_thought_for_the_day.getSlides()) {
			newPresentation.createSlide().importContent(slide);
		}

		final XMLSlideShow templatePresentation = new XMLSlideShow(request
				.getSession()
				.getServletContext()
				.getResourceAsStream(
						"/WEB-INF/templates/Peninsula/bhajans.pptx"));
		final XSLFSlide template = templatePresentation.getSlides().get(0);

		final List<Bhajan> bhajans = response.getBhajans();

		for (int i = 0, len = bhajans.size(); i < len; ++i) {

			final Bhajan bhajan = bhajans.get(i);
			String firstLineOfNextBhajan = "", scaleOfNextBhajan = "";

			if (i + 1 < len) {
				final Bhajan nextBhajan = bhajans.get(i + 1);
				firstLineOfNextBhajan = getFirstLineForSlideBottom(nextBhajan
						.getLyrics());
				scaleOfNextBhajan = nextBhajan.getScale();
			}

			final String[] parts = Pattern.compile("^\\s*$", Pattern.MULTILINE)
					.split(bhajan.getLyrics());
			for (int j = 0, jlen = parts.length; j < jlen; ++j) {
				final XSLFSlide slide = newPresentation.createSlide();
				slide.importContent(template);

				final Iterator<XSLFShape> shapes = slide.iterator();
				shapes.next();

				final XSLFAutoShape nextBhajanFirstLineShape = ((XSLFAutoShape) shapes
						.next());

				final XSLFAutoShape currentBhajanScale = (XSLFAutoShape) shapes
						.next();

				shapes.next();

				if (j < parts.length - 1) {
					shapes.next();
					(((XSLFAutoShape) shapes.next()).getTextParagraphs())
							.get(0).getTextRuns().get(0)
							.setText(StringUtils.trimToEmpty(parts[j]));

					((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
							.getTextRuns().get(0).setText(bhajan.getMeaning());

					nextBhajanFirstLineShape.getTextParagraphs().get(0)
							.getTextRuns().get(0).setText(bhajan.getScale());

					currentBhajanScale.getTextParagraphs().get(0).getTextRuns()
							.get(0).setText(bhajan.getScale() + ": Continued");
				} else {
					System.out.println(nextBhajanFirstLineShape.getText());
					System.out.println(currentBhajanScale.getText());
					(((XSLFAutoShape) shapes.next()).getTextParagraphs())
							.get(0).getTextRuns().get(0)
							.setText(StringUtils.trimToEmpty(parts[j]));

					((XSLFAutoShape) shapes.next()).getTextParagraphs().get(0)
							.getTextRuns().get(0).setText(bhajan.getMeaning());

					nextBhajanFirstLineShape
							.getTextParagraphs()
							.get(0)
							.getTextRuns()
							.get(0)
							.setText(
									firstLineOfNextBhajan + "-"
											+ scaleOfNextBhajan);

					currentBhajanScale.getTextParagraphs().get(0).getTextRuns()
							.get(0).setText(bhajan.getScale());
				}
			}
		}

		final XMLSlideShow postfix = new XMLSlideShow(request
				.getSession()
				.getServletContext()
				.getResourceAsStream(
						"/WEB-INF/templates/Peninsula/postfix.pptx"));
		for (final XSLFSlide slide : postfix.getSlides()) {
			newPresentation.createSlide().importContent(slide);
		}

		return newPresentation;
	}

	private String getFirstLineForSlideBottom(final String lyrics) {
		String firstLine = lyrics.split("\n")[0];
		firstLine = firstLine.substring(0, Math.min(firstLine.length(), 35));
		if (!firstLine.endsWith(" ")) {
			int lastIndex = firstLine.lastIndexOf(' ');
			if (lastIndex != -1) {
				firstLine = firstLine.substring(0, lastIndex);
			}
		}
		return firstLine;
	}
	
	private XMLSlideShow renderRegularBhajans(HttpServletRequest request,
			Response response) throws IOException {

		final XMLSlideShow templatePresentation = new XMLSlideShow(request
				.getSession().getServletContext()
				.getResourceAsStream("/WEB-INF/master.pptx"));
		final XSLFSlide template = templatePresentation.getSlides().get(0);

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

				((XSLFAutoShape) shapes.next())
						.getTextParagraphs()
						.get(0)
						.getTextRuns()
						.get(0)
						.setText(
								getFirstLineForSlideBottom(nextBhajan
										.getLyrics()));
			}
		}

		final XMLSlideShow postUnisonPresentation = new XMLSlideShow(request
				.getSession().getServletContext()
				.getResourceAsStream("/WEB-INF/postUnison.pptx"));
		for (XSLFSlide slide : postUnisonPresentation.getSlides()) {
			newPresentation.createSlide().importContent(slide);
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
				((XSLFAutoShape) importedSlide.getShapes().get(0))
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
				((XSLFAutoShape) importedSlide.getShapes().get(0))
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
		return newPresentation;

	}


	private XMLSlideShow renderCSJBhajanTemplate(HttpServletRequest request,
			Response response, String templateName) throws IOException {
		
		final XMLSlideShow newPresentation = new XMLSlideShow();

		final XMLSlideShow prefix = new XMLSlideShow(request.getSession()
				.getServletContext()
				.getResourceAsStream("/WEB-INF/templates/" + templateName + "/prefix.pptx"));
		for (final XSLFSlide slide : prefix.getSlides()) {
			newPresentation.createSlide().importContent(slide);
		}

		renderTemplate(request, response, "/WEB-INF/templates/" + templateName + "/master.pptx", newPresentation);

		final XMLSlideShow postUnisonPresentation = new XMLSlideShow(request
				.getSession().getServletContext()
				.getResourceAsStream("/WEB-INF/templates/" + templateName + "/postUnison.pptx"));
		for (final XSLFSlide slide : postUnisonPresentation.getSlides()) {
			newPresentation.createSlide().importContent(slide);
		}

		if (!StringUtils.isEmpty(response.getDivineCodeOfConduct())) {
			final XMLSlideShow divineCodeOfConductPresentation = new XMLSlideShow(
					request.getSession()
							.getServletContext()
							.getResourceAsStream(
									"/WEB-INF/templates/" + templateName + "/divineCodeOfConduct.pptx"));
			for (XSLFSlide slide : divineCodeOfConductPresentation.getSlides()) {
				final XSLFSlide importedSlide = newPresentation.createSlide()
						.importContent(slide);
				((XSLFAutoShape) importedSlide.getShapes().get(0))
						.getTextParagraphs().get(0).getTextRuns().get(0)
						.setText(response.getDivineCodeOfConduct());
			}
		}

		if (!StringUtils.isEmpty(response.getThoughtForTheWeek())) {
			final XMLSlideShow thoughtForTheWeekPresentation = new XMLSlideShow(
					request.getSession()
							.getServletContext()
							.getResourceAsStream(
									"/WEB-INF/templates/" + templateName + "/thoughtForTheWeek.pptx"));
			for (final XSLFSlide slide : thoughtForTheWeekPresentation
					.getSlides()) {
				final XSLFSlide importedSlide = newPresentation.createSlide()
						.importContent(slide);
				((XSLFAutoShape) importedSlide.getShapes().get(0))
						.getTextParagraphs().get(0).getTextRuns().get(0)
						.setText(response.getThoughtForTheWeek());
			}
		}

		final XMLSlideShow closingPrayersPresentation = new XMLSlideShow(
				request.getSession().getServletContext()
						.getResourceAsStream("/WEB-INF/templates/" + templateName + "/closingPrayers.pptx"));
		for (final XSLFSlide slide : closingPrayersPresentation.getSlides()) {
			newPresentation.createSlide().importContent(slide);
		}
		return newPresentation;
	}
}
