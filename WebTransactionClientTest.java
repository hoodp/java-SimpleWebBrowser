import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;

// Note:  This isn't a traditional unit test.  (1) A traditional unit test wouldn't actually interact with a real 
// "live" server.  It would either (a) launch a temporary server specifically for these tests, or (2) mock out the server.
// (2) A traditional unit test wouldn't use the "real" MyURL class.  Instead, it would mock out the MyURL class.


public class WebTransactionClientTest {

	@Test
	public void fetchesHTML() throws IOException {

		MyURL url = new MyURL("cis.gvsu.edu/~kurmasz/Humor/stupid.html");
		WebTransactionClient client = new WebTransactionClient(url);

		Assert.assertEquals("Response differs", 200, client.responseCode());
		Assert.assertEquals("Reported length differs", 1780, Integer.parseInt(client.getHeader("content-length")));
		Assert.assertEquals("Type differs", "text/html", client.getHeader("content-type"));

		String payload = client.getText();
		Assert.assertTrue("Starts incorrectly", payload.startsWith("<HTML>"));
		Assert.assertTrue("Contents missing", payload.contains("<LI>Surfing in Nebraska.\n"));
		Assert.assertTrue("Ends incorrectly", payload.endsWith("</HTML>\n"));
	}

	@Test
	public void fetchesText() throws IOException {

		MyURL url = new MyURL("cis.gvsu.edu/~kurmasz/DistillerSetup/distillerSetup.txt");
		WebTransactionClient client = new WebTransactionClient(url);

		Assert.assertEquals("Response differs", 200, client.responseCode());
		Assert.assertEquals("Reported length differs", 2930, Integer.parseInt(client.getHeader("content-length")));
		Assert.assertEquals("Type differs", "text/plain", client.getHeader("content-type"));

		String payload = client.getText();
		Assert.assertTrue("Starts incorrectly", payload.startsWith("The software"));
		Assert.assertTrue("Contents missing", payload.contains("cd into $WS.\n\n"));
		Assert.assertTrue("Ends incorrectly", payload.endsWith("17). Build Utilities.\n"));
	}

	@Test
	public void fetchesJpeg() throws IOException {

		// MyURL url = new MyURL("cis.gvsu.edu/~kurmasz/buzz1.jpg");
		MyURL url = new MyURL("http://www.greenbuzz.gatech.edu/sites/greenbuzz.gatech.edu/files/editorial/gbawards.jpg");
		WebTransactionClient client = new WebTransactionClient(url);

		Assert.assertEquals("Response differs", 200, client.responseCode());
		Assert.assertEquals("Reported length differs", 196423, Integer.parseInt(client.getHeader("content-length")));
		Assert.assertEquals("Type differs", "image/jpeg", client.getHeader("content-type"));

		Image image = client.getImage();
		Assert.assertNotNull("Problem loading image", image);
		Assert.assertEquals("Incorrect height: ", 238, image.getHeight(null));
		Assert.assertEquals("Incorrect width: ", 489, image.getWidth(null));
	}

	@Test
	public void fetchesPng() throws IOException {

		MyURL url = new MyURL("http://www.unixstickers.com/image/data/stickers/emacs/emacs.sh.png");
		WebTransactionClient client = new WebTransactionClient(url);

		Assert.assertEquals("Response differs", 200, client.responseCode());
		Assert.assertEquals("Reported length differs", 81938, Integer.parseInt(client.getHeader("content-length")));
		Assert.assertEquals("Type differs", "image/png", client.getHeader("content-type"));

		Image image = client.getImage();
		Assert.assertNotNull("Problem loading image", image);
		Assert.assertEquals("Incorrect height: ", 650, image.getHeight(null));
		Assert.assertEquals("Incorrect width: ", 650, image.getWidth(null));
	}

	@Test
	public void fetchesGif() throws IOException {

		MyURL url = new MyURL("http://upload.wikimedia.org/wikipedia/commons/7/70/Logo_Apple.inc.gif");
		WebTransactionClient client = new WebTransactionClient(url);
		Assert.assertEquals("Response differs", 200, client.responseCode());
		Assert.assertEquals("Reported length differs", 8575, Integer.parseInt(client.getHeader("content-length")));
		Assert.assertEquals("Type differs", "image/gif", client.getHeader("content-type"));

		Image image = client.getImage();
		Assert.assertNotNull("Problem loading image", image);
		Assert.assertEquals("Incorrect height: ", 373, image.getHeight(null));
		Assert.assertEquals("Incorrect width: ", 320, image.getWidth(null));
	}


	@Test 
	public void handles404() throws IOException {
		MyURL url = new MyURL("http://www.gatech.edu/noSuchFile.wxyz");
		WebTransactionClient client = new WebTransactionClient(url);
		Assert.assertEquals("Response differs", 404, client.responseCode());
	}

	@Test
	public void handlesForward() throws IOException {
		MyURL url = new MyURL("http://www.cis.gvsu.edu/~kurmasz");
		WebTransactionClient client = new WebTransactionClient(url);

		Assert.assertEquals("Response differs: ", 301, client.responseCode());
		Assert.assertEquals("Location differs: ", "http://www.cis.gvsu.edu/~kurmasz/", client.getHeader("location"));
	}

}