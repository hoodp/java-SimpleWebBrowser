
/**
 * @author Paul Hood
 * 
 * Represents a URL
 */
public class MyURL {

	private String scheme = "http";
	private String domainName = null;
	private int port = 80;
	private String path = "/";

	/**
	 * Split {@code url} into the various components of a URL
	 *
	 * @param url the {@code String} to parse
	 */
	public MyURL(String url) throws RuntimeException{
		splitUrl(url);
	}

	/**
	 * If {@code newURL} has a scheme (e.g., begins with "http://", "ftp://", etc), then parse {@code newURL} 
	 * and ignore {@code currentURL}.  If {@code newURL} does not have a scheme, then assume it is intended 
	 * to be a relative link and replace the file component of {@code currentURL}'s path with {@code newURL}.
	 *
	 * @param newURL     a {@code String} representing the new URL.
	 * @param currentURL the current URL
	 */
	public MyURL(String newURL, MyURL currentURL) {

		// check for scheme
		if (newURL.indexOf("://") != -1) {
			splitUrl(newURL);
		} else {
			
			// copy data
			scheme = currentURL.scheme;
			domainName = currentURL.domainName;
			port = currentURL.port;
			path = currentURL.path;

			// update file path
			path = path.substring(0, path.lastIndexOf("/") + 1) + newURL;
		}
	}

	private void splitUrl(String url) {
		
		// last index of prefix 
		int schemeIndex = url.indexOf("://");

		// index of port character
		int portIndex = url.lastIndexOf(":");

		// starting index of path character
		int pathIndex = url.indexOf("/",  schemeIndex + 
				new String("://").length());

		// set scheme if schemeIndex is > 1
		if (schemeIndex != -1)
			scheme = url.substring(0, schemeIndex);

		// check if port is set
		if (portIndex != schemeIndex) {
			
			// get port between domain name & path index
			if (pathIndex != -1)
				port = Integer.parseInt(url.substring(portIndex + 1, pathIndex));
			
			// port is the last part of the string
			else
				port = Integer.parseInt(url.substring(portIndex + 1));
		}

		// set file path
		if (pathIndex != -1)
			path = url.substring(pathIndex);

		// starting index for domain
		int domainStart = 0;

		// check for scheme
		if (schemeIndex != -1)
			domainStart = schemeIndex + new String("://").length();

		// get substring from scheme to port if set 
		if (portIndex != schemeIndex)
			domainName = url.substring(domainStart, portIndex);
		
		// get substring from scheme to path 
		else if (pathIndex != -1)
			domainName = url.substring(domainStart, pathIndex);
		
		// rest of string is the domain name
		else
			domainName = url.substring(domainStart);

		// throw error if domain name empty
		if (domainName.isEmpty())
			throw new RuntimeException();
	}

	public String scheme() {
		return scheme;
	}

	public String domainName() {
		return domainName;
	}

	public int port() {
		return port;
	}

	public String path() {
		return path;
	}

	/**
	 * Format this URL as a {@code String}
	 *
	 * @return this URL formatted as a string.
	 */
	public String toString() {
		return String.format("%s://%s:%d%s", scheme, domainName, port, path);
		//return String.format(scheme + "://" + domainName + ":" + port + path);
	}

	// Needed in order to use MyURL as a key to a HashMap
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	// Needed in order to use MyURL as a key to a HashMap
	@Override
	public boolean equals(Object other) {
		if (other instanceof MyURL) {
			MyURL otherURL = (MyURL) other;
			return this.scheme.equals(otherURL.scheme) &&
					this.domainName.equals(otherURL.domainName) &&
					this.port == otherURL.port() &&
					this.path.equals(otherURL.path);
		} else {
			return false;
		}
	}
} // end class