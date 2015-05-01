package org.sathyasai.ssbcsj;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

public class Test {

	public static void main(String[] args) throws Throwable {
		final XMLSlideShow presentation = new XMLSlideShow(
				new FileInputStream(
						"/Users/rkanadam/workspace/saibhajanmaker/WebContent/WEB-INF/divineCodeOfConduct.pptx"));
		final XSLFSlide slides = presentation.getSlides()[0];
		final Iterator<XSLFShape> shapes = slides.iterator();
		while (shapes.hasNext()) {
			System.out.println("New Shape");
			XSLFAutoShape next = (XSLFAutoShape) shapes.next();
			final List<XSLFTextParagraph> paragraphs = next.getTextParagraphs();
			for (XSLFTextParagraph paragraph : paragraphs) {
				System.out.println("New Paragraph");
				final List<XSLFTextRun> runs = paragraph.getTextRuns();
				for (XSLFTextRun run : runs) {
					System.out.println("New Run");
					System.out.println(run.getText());
				}
			}
		}
	}

}
