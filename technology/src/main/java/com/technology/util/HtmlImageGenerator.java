package com.technology.util;

import gui.ava.html.image.util.FormatNameUtil;
import gui.ava.html.image.util.SynchronousHTMLEditorKit;
import gui.ava.html.link.LinkHarvester;
import gui.ava.html.link.LinkInfo;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JEditorPane;

public class HtmlImageGenerator {
	private JEditorPane editorPane;
	static final Dimension DEFAULT_SIZE = new Dimension(800, 800);

	public HtmlImageGenerator() {
		this.editorPane = createJEditorPane();
	}

	public ComponentOrientation getOrientation() {
		return this.editorPane.getComponentOrientation();
	}

	public void setOrientation(ComponentOrientation orientation) {
		this.editorPane.setComponentOrientation(orientation);
	}

	public Dimension getSize() {
		return this.editorPane.getSize();
	}

	public void setSize(Dimension dimension) {
		this.editorPane.setSize(dimension);
	}

	public void loadUrl(URL url) {
		try {
			this.editorPane.setPage(url);
		} catch (IOException e) {
			throw new RuntimeException(String.format(
					"Exception while loading %s", new Object[] { url }), e);
		}
	}

	public void loadUrl(String url) {
		try {
			this.editorPane.setPage(url);
		} catch (IOException e) {
			throw new RuntimeException(String.format(
					"Exception while loading %s", new Object[] { url }), e);
		}
	}

	public void loadHtml(String html) {
		this.editorPane.setText(html);
		onDocumentLoad();
	}

	public String getLinksMapMarkup(String mapName) {
		StringBuilder markup = new StringBuilder();
		markup.append("<map name=\"").append(mapName).append("\">\n");
		for (LinkInfo link : getLinks()) {
			List<Rectangle> bounds = link.getBounds();
			for (Rectangle bound : bounds) {
				int x1 = (int) bound.getX();
				int y1 = (int) bound.getY();
				int x2 = (int) (x1 + bound.getWidth());
				int y2 = (int) (y1 + bound.getHeight());
				markup.append(String.format(
						"<area coords=\"%s,%s,%s,%s\" shape=\"rect\"",
						new Object[] { Integer.valueOf(x1),
								Integer.valueOf(y1), Integer.valueOf(x2),
								Integer.valueOf(y2) }));
				for (Map.Entry entry : link.getAttributes().entrySet()) {
					String attName = (String) entry.getKey();
					String value = (String) entry.getValue();
					markup.append(" ").append(attName).append("=\"")
							.append(value.replace("\"", "&quot;")).append("\"");
				}
				markup.append(">\n");
			}
		}
		LinkInfo link;
		markup.append("</map>\n");
		return markup.toString();
	}

	public List<LinkInfo> getLinks() {
		LinkHarvester harvester = new LinkHarvester(this.editorPane);
		return harvester.getLinks();
	}

	public void saveAsHtmlWithMap(String file, String imageUrl) {
		saveAsHtmlWithMap(new File(file), imageUrl);
	}

	public void saveAsHtmlWithMap(File file, String imageUrl) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
			writer.append("<html>\n<head></head>\n");
			writer.append("<body style=\"margin: 0; padding: 0; text-align: center;\">\n");
			String htmlMap = getLinksMapMarkup("map");
			writer.write(htmlMap);
			writer.append("<img border=\"0\" usemap=\"#map\" src=\"");
			writer.append(imageUrl);
			writer.append("\"/>\n");
			writer.append("</body>\n</html>");
		} catch (IOException e) {
			throw new RuntimeException(String.format(
					"Exception while saving '%s' html file",
					new Object[] { file }), e);
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException ignore) {
				}
		}
	}

	public void saveAsImage(String file) {
		saveAsImage(new File(file));
	}

	public void saveAsImage(File file) {
		BufferedImage img = getBufferedImage();
		try {
			String formatName = FormatNameUtil
					.formatForFilename(file.getName());
			ImageIO.write(img, formatName, file);
		} catch (IOException e) {
			throw new RuntimeException(
					String.format("Exception while saving '%s' image",
							new Object[] { file }), e);
		}
	}

	protected void onDocumentLoad() {
	}

	public Dimension getDefaultSize() {
		return DEFAULT_SIZE;
	}

	public BufferedImage getBufferedImage() {
		
		editorPane.setMargin(new Insets(0, 0, 0, 0));
		Dimension prefSize = this.editorPane.getPreferredSize();
		BufferedImage img = new BufferedImage(prefSize.width,
				this.editorPane.getPreferredSize().height, 2);
		Graphics2D graphics = img.createGraphics();
		this.editorPane.setSize(prefSize);
		this.editorPane.paint(graphics);
		return img;
	}

	protected JEditorPane createJEditorPane() {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setSize(getDefaultSize());
		editorPane.setEditable(false);
		SynchronousHTMLEditorKit kit = new SynchronousHTMLEditorKit();
		editorPane.setEditorKitForContentType("text/html", kit);
		editorPane.setContentType("text/html");
		editorPane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("page"))
					HtmlImageGenerator.this.onDocumentLoad();
			}
		});
		return editorPane;
	}
}