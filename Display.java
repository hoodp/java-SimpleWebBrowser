import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * This class demonstrates a simple technique of laying out text "by hand"
 * <p/>
 * Also demonstrates how to change fonts and colors.
 * <p/>
 * Created by kurmasz on 12/17/14.
 * 
 * @author Paul Hood
 * @version 05-14-2015
 */
public class Display extends JPanel {

	private static final int MARGIN = 10; // the margin around the edge of the window.
	private List<String> content;  // the text that is to be displayed.


	// This Map is what makes links:  Each Rectangle is a link --- an area on the screen that can be clicked.
	// The rectangle is the key.  The value, in this case, is the color that should be used when the link is clicked.
	// When building a "real" browser, the links are also areas on the screen, but the corresponding value is the URL
	// that should be loaded when the link is clicked.
	private Map<Rectangle, String> links = new HashMap<Rectangle, String>();
	private SimpleBrowser browser;

	/**
	 * set the browser variable for retreived image cache
	 */
	public void setBrowser(SimpleBrowser browser) {
		this.browser = browser;
	}

	/**
	 * Set the text that is to be displayed.
	 *
	 * @param text_in the text that is to be dis played
	 */
	public void setText(List<String> text_in) {
		content = text_in;
	}

	/**
	 * Actually "draws" the text on the window.
	 *
	 * @param g
	 */
	@Override
	public void paintComponent(Graphics g) {

        // reset links if the map is not empty
        if (links.size() != 0)
            links.clear();

		// prevents display from messing up while scrolling
		super.paintComponent(g);

		// If no file has been loaded yet, then do nothing.
		if (content == null) {
			return;
		}

		// The FontMetrics object can compute the size of text in the window.
		// You must get a new FontMetrics object every time you change or modify the font (e.g., use bold or italics).
		FontMetrics metrics = g.getFontMetrics();
		int line_height = metrics.getHeight();
		int panel_width = getWidth() - MARGIN * 2;
		int x = MARGIN;
		int y = line_height;

		// save the original font in case we change it.
		Font originalFont = g.getFont();

		// booleans for italics, bold, and links set to false
		boolean bold, italic, link;
		bold = italic = link = false;

		// Iterate over each line.
		for (String line : content) {
			Scanner words = new Scanner(line);
			String url = null;

			// iterate over each word
			while (words.hasNext()) {
				String nextWord = words.next().trim();

				// check for an image
				if (hasMarkup(nextWord, "<<", true)
						&& hasMarkup(nextWord, ">>", false)) {
					nextWord = nextWord.substring(2, nextWord.length() - 2);
					Image image = browser.getCachedImage(nextWord);
					x = MARGIN;
					y += line_height + image.getHeight(null);
					g.drawImage(image, x, y, image.getWidth(null), image.getHeight(null), null);
					y += line_height;
					continue;
				}

				// original style for word
				int style = Font.PLAIN;

				// start checking for adding style markup
				// check for start of bold markup 
				if (hasMarkup(nextWord, "*", true)) {
					bold = true;

					// remove character at beginning of string
					nextWord = nextWord.substring(1);
				}

				// check for start of italic markup
				if (hasMarkup(nextWord, "_", true)) {
					italic = true;

					// remove markup from beginning of string
					nextWord = nextWord.substring(1);
				}

				if (hasMarkup(nextWord, "[[", true)) {
					link = true;
					
					// remove starting brackets
					nextWord = nextWord.substring(2);
					
					// set url 
					url = nextWord;
							
					// check for closing markup bracket in url
					if (url.contains("]]"))
						url = url.substring(0, url.length() - 2);
					
					// skip to next word if more than one word in link
					if (line.split(" ").length != 1 && words.hasNext())
						nextWord = words.next();
				}

				// remove plain style and add bold
				if (bold)
					style = Font.BOLD;

				// add italic font to style
				if (italic)
					style += Font.ITALIC;
				
				// update color
				g.setColor(link ? Color.BLUE : Color.BLACK);
				
				// check for link
				if (link) {
					
					// create new rectangle
					Rectangle rect = new Rectangle(x, y - line_height,
							metrics.stringWidth(nextWord), line_height);
					
					// add rectangle to screen
					g.clearRect(rect.x, rect.y, rect.width, rect.height);
					
					// add rectangle to hash map
					links.put(rect, url);
				}

				// start checking for end of markup
				// check for end of bold line
				if (hasMarkup(nextWord, "*", false)) {
					bold = false;

					// remove char at end of string
					nextWord = nextWord.substring(0, nextWord.length() - 1);
				}

				// check for end of italic line
				if (hasMarkup(nextWord, "_", false)) {
					italic = false;

					// remove char at end of string
					nextWord = nextWord.substring(0, nextWord.length() - 1);
				}

				if (hasMarkup(nextWord, "]]", false)) {
					link = false;		
					
					// remove brackets at end of string
					nextWord = nextWord.substring(0, nextWord.length() - 2);
				}

				String wordAndSpace = nextWord + " ";
                g.setFont(originalFont.deriveFont(style));
                metrics = g.getFontMetrics();
				int word_width = metrics.stringWidth(wordAndSpace);

				// If there isn't room for this word, go to the next line
				if (x + word_width > panel_width) {
					x = MARGIN;
					y += line_height;
				}
								
				// draw the word

				g.drawString(wordAndSpace, x, y);
				
				x += word_width;

			} // end of the line

			// move to the next line
			x = MARGIN;
			y += line_height;
		} // end of all ines

		// make this JPanel bigger if necessary.
		// Calling re-validate causes the scroll bars to adjust, if necessary.
		if (y > getHeight()) {
			setPreferredSize(new Dimension(x, y + line_height + 2 * MARGIN));
			revalidate();
		}
	}

	/**
	 * Return the color value of the color link at {@code point}, or
	 * return {@code null} if {@code point} doesn't point to a color link.

	 * @param point the {@code Point} that was clicked.
	 *
	 * @return the color value of the color link at {@code point}, or
	 * return {@code null} if {@code point} doesn't point to a color link.
	 */
	// 
	public String getUrl(Point point) {
		for (Rectangle rect : links.keySet()) {
			if (rect.contains(point))
				return links.get(rect);
		}
		return null;
	}

	/**
	 * Determine if string starts or ends with specific character
	 * 
	 * @param word to look for
	 * @param start check beginning or false to check end
	 * 
	 * @return true if character is found and starts or ends with value
	 * 
	 */
	private boolean hasMarkup(String word, String markup, boolean start) {
		// TODO: edit this method to return starts with and ends with one string
		if (start)
			return word.startsWith(markup);
		else
			return word.endsWith(markup);
	}

}