/**
 * @author Paul Hood
 */
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class WebTransactionClient {

	private PrintWriter out;
	private DataInputStream in;
	private Socket socket;
	private String response;   // The entire response string (e.g., "HTTP/1.1 200 Ok")
	private HashMap<String, String> headers = new HashMap<String, String>();

	@SuppressWarnings("deprecation")
	public WebTransactionClient(MyURL url) throws IOException {
		socket = new Socket(url.domainName(), url.port());
		in = new DataInputStream(socket.getInputStream());
		out = new PrintWriter(socket.getOutputStream(), true);
		
		// send get request
		out.printf("GET %s HTTP/1.1\nHost: %s\n\n", url.path(),
				url.domainName());
		out.flush();
		
		// first line is response string
		response = in.readLine();
		
		// get header information 
		String line;
		while ((line = in.readLine()).trim().length() > 0) {
			
			// index of semicolon that separates key & value for hash map
			int index = line.indexOf(":");
			
			// get key & value, increment index to avoid semi colon
			String key = line.substring(0, index).trim().toLowerCase();
			String value = line.substring(index + 1).trim();
			headers.put(key,  value);
		}
	}

	@SuppressWarnings("deprecation")
	public String getText() throws IOException {
		StringBuffer result = new StringBuffer();

		// get lines until readline is null
		String line;
		while ((line = in.readLine()) != null)
			result.append(line + "\n");

		return result.toString();
	} // end getText

	public BufferedImage getImage() throws IOException {
		return ImageIO.read(in);
	}


	public String response() {
		return response;
	}

	public int responseCode() {
		
		// split response string by spaces
		String[] responses = response.split(" ");
		
		// return second index of responses array with response value
		return Integer.parseInt(responses[1]);
	}

	public Map<String, String> responseHeaders() {
		return headers;
	}

	public String getHeader(String key) {
		return headers.get(key.toLowerCase());
	}

	@Override
	protected void finalize() throws Throwable {

		// This method is complete.
		super.finalize();
		in.close();
		out.close();
		socket.close();
	}
} // end WebTransactionClient