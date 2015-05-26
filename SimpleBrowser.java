import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * This class can serve as starter code for a simple web browser.
 * It provides a basic GUI setup:  and address bar, and a scrollable panel on which to draw.
 * It loads text from a local file and uses {@link Display} to render it. 
 * <p/>
 * Created by kurmasz on 12/17/14.
 */
public class SimpleBrowser {
	private JFrame frame;
	protected JTextField addressBar;
	private JScrollPane scrollPane;
	private Display display;
	private String homeLoc;

	// Caching images prevents the browser from repeatedly fetching the same image from the server
	// (This repeated fetching is especially annoying when scrolling.)
	protected ImageCache cache = new ImageCache();

	// The URL of the currently displayed document;
	protected MyURL currentURL = null;

	protected SimpleBrowser(String frameName, String initialLocation, JPanel displayPanel) {
		homeLoc = initialLocation;

		frame = new JFrame(frameName);
		frame.setSize(500, 500);
		addressBar = new JTextField(initialLocation);

		JPanel barPanel = new JPanel();
		barPanel.setLayout(new BorderLayout());
		JButton home = new JButton("Home");
		barPanel.add(home, BorderLayout.WEST);
		barPanel.add(addressBar, BorderLayout.CENTER);

		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.width /= 2;
		screenSize.height /= 2;

		displayPanel.setPreferredSize(screenSize);
		scrollPane = new JScrollPane(displayPanel);


		frame.getContentPane().add(barPanel, BorderLayout.NORTH);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();

		// Respond to the user pressing <enter> in the address bar.
		addressBar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String textInBar = addressBar.getText();

				// Replace this with the code that loads
				// text from a web server
				loadPage(textInBar);
			}
		});

		home.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadPage(homeLoc);
			}
		});


		displayPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				clicked(e.getPoint());
			}
		});
	}

	public SimpleBrowser(String frameName, String initialLocation, Display display_in) {
		this(frameName, initialLocation, (JPanel) display_in);
		display = display_in;
		loadPage(initialLocation);
	}

	protected void clicked(Point point) {

		// get url from mouse click
		String newUrl = display.getUrl(point);

		// check for non url link page to open
		if (newUrl != null)
			loadPage(newUrl);
	}

	protected void loadPage(String textInBar) {
		try {

			// check if url should be updated or created
			if (currentURL == null)
				currentURL = new MyURL(textInBar);
			else
				currentURL = new MyURL(textInBar, currentURL);

			// new web transaction client object
			WebTransactionClient client = new WebTransactionClient(currentURL);

			// create list of lines to display
			List<String> lines = new ArrayList<String>();

			// split page text by newline character
			for (String s : client.getText().split("\n"))
				lines.add(s);

			display.setBrowser(this);
			display.setText(lines);
			frame.repaint();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Fetch an image from from the server, or return null if 
	// the image isn't available.
	protected Image fetchImage(MyURL url) {
		try {
			WebTransactionClient client = new WebTransactionClient(url);
			return client.getImage();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Return the image at the given url.
	 *
	 * @param urlString the URL of the image to load.
	 * @return The desired image, or {@code null} if the image isn't available.
	 */
	public Image getCachedImage(String urlString) {
		MyURL url = new MyURL(urlString, currentURL);

		// This unusual syntax (the "new ImageCache.ImageLoader" stuff) is an "anonymous inner class.  It is Java's way
		// of allowing us to pass the fetchImage method as a parameter to the ImageCache.getImage.  You may have seen this
		// syntax before with ActionListeners.  If not, I will be happy to explain it to you.
		return cache.getImage(url, new ImageCache.ImageLoader() {
			@Override
			public Image loadImage(MyURL url) {
				return fetchImage(url);
			}
		});
	}


	public static void main(String[] args) {

		// Notice that the display object (the Display) is created *outside* of the 
		// SimpleBrowser object.  This is an example of "dependency injection" (also called 
		// "inversion of control").  In general, dependency injection simplifies unit testing.
		// I this case, I used dependency injection so that I could more easily write a subclass
		// of this browser that uses a completely different display class.
		String initial = args.length > 0 ? args[0]  : "http://www.cis.gvsu.edu/~kurmasz/Teaching/Courses/S15/CS371/Assignments/WebBrowser/sampleInput/subdirImages.txt";
		new SimpleBrowser("CIS 371 Starter Browser", initial, new Display());
	}


}