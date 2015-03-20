package org.sathyasai.ssbcsj;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;

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
		XMLSlideShow ppt = new XMLSlideShow(request.getSession()
				.getServletContext().getResourceAsStream("/WEB-INF/poi.pptx"));
		XMLSlideShow newPresentation = new XMLSlideShow();
		final XSLFSlide[] slides = ppt.getSlides();
		for (int i = 0; i < 1; ++i) {
			for (XSLFSlide slide : slides) {
				XSLFSlide newSlide = newPresentation.createSlide();
				newSlide.importContent(slide);
				XSLFAutoShape shape = (XSLFAutoShape) newSlide.iterator()
						.next();
				shape.getTextParagraphs().get(0).getTextRuns().get(0)
						.setText("Hello World!");

			}
		}
		response.setContentType("application/vnd.ms-ppt");
		response.setHeader("Content-Disposition",
				"inline; filename=\"bhajans.pptx\"");
		newPresentation.write(response.getOutputStream());
	}
}
