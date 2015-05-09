import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MyURLTest {

	private void verifyParts(MyURL url, String scheme, String domain, int port, String path) {
		Assert.assertEquals("Scheme differs:", scheme, url.scheme());
		Assert.assertEquals("Domain differs: ", domain, url.domainName());
		Assert.assertEquals("Port differs: ", port, url.port());
		Assert.assertEquals("Path differs: ", path, url.path());
	}

	private void verifyParts(String urlString, String scheme, String domain, int port, String path) {
		verifyParts(new MyURL(urlString), scheme, domain, port, path);
	}

	private void verifyParts(String urlString, MyURL current, String scheme, String domain, int port, String path) {
		verifyParts(new MyURL(urlString, current), scheme, domain, port, path);
	}


	private MyURL current1;

	@Before
	public void setupCurrent() {
		current1 = new MyURL("http://www.google.com/place1/place2/george.html");
	}


	////////////////////////////////////
	//
	// Test constructor MyURL(String)
	//
	////////////////////////////////////

	@Test
	public void parsesURLWithAllComponents() {
		verifyParts("ftp://www.cis.gvsu.edu:73/dir1/dir2/file.html", "ftp", "www.cis.gvsu.edu", 73, "/dir1/dir2/file.html");
	}

	@Test
	public void parsesURLWithAllComponentsExceptPort() {
		verifyParts("ftp://www.cis.gvsu.edu/dir1/dir2/file.html", "ftp", "www.cis.gvsu.edu", 80, "/dir1/dir2/file.html");
	}

	@Test
	public void parsesURLWithNoScheme() {
		verifyParts("www.cis.gvsu.edu:73/dir1/dir2/file.html", "http", "www.cis.gvsu.edu", 73, "/dir1/dir2/file.html");
	}

	@Test
	public void parsesURLWithDomainOnly() {
		verifyParts("www.cis.gvsu.edu", "http", "www.cis.gvsu.edu", 80, "/");
	}

	@Test
	public void parsesURLWithDomainOnly_slash() {
		verifyParts("www.cis.gvsu.edu/", "http", "www.cis.gvsu.edu", 80, "/");
	}

	@Test
	public void parsesURLWithDomainAndPortOnly() {
		verifyParts("www.cis.gvsu.edu:73", "http", "www.cis.gvsu.edu", 73, "/");
	}

	@Test
	public void parsesURLWithDomainAndPortOnly_slash() {
		verifyParts("www.cis.gvsu.edu:73/", "http", "www.cis.gvsu.edu", 73, "/");
	}

	@Test
	public void parsesURLWithNoFile() {
		verifyParts("ssh://www.cis.gvsu.edu:133/dir1/dir2/", "ssh", "www.cis.gvsu.edu", 133, "/dir1/dir2/");
	}

	@Test
	public void parsesURLWithNoExtension() {
		verifyParts("ssh://www.cis.gvsu.edu:133/dir1/dir2/file", "ssh", "www.cis.gvsu.edu", 133, "/dir1/dir2/file");
	}

	@Test
	public void parsesDomainOnly() {
		verifyParts("www.google.com", "http", "www.google.com", 80, "/");
	}

	@Test
	public void parsesDomainAndDirOnly() {
		verifyParts("www.google.com/dir1/dir2", "http", "www.google.com", 80, "/dir1/dir2");
	}

	@Test
	public void parsesDomainPortAndDirOnly() {
		verifyParts("www.google.com:66/dir1/dir2", "http", "www.google.com", 66, "/dir1/dir2");
	}

	@Test(expected = NumberFormatException.class)
	public void complainsIfPortNotInteger() {
		new MyURL("www.google.com:fourteen/dir1/dir2");
	}

	@Test(expected = RuntimeException.class)
	public void complainsIfStringEmpty() {
		new MyURL("");
	}

	@Test(expected = RuntimeException.class)
	public void complainsIfDomainMissing() {
		new MyURL("http://");
	}

	@Test(expected = RuntimeException.class)
	public void complainsIfPortOnly() {
		new MyURL(":98");
	}


	@Test(expected = RuntimeException.class)
	public void complainsIfSchemeAndPortOnly() {
		new MyURL("http://:98");
	}


	////////////////////////////////////////////
	//
	// Test constructor MyURL(String, MyURL)
	//
	/////////////////////////////////////////////

	@Test
	public void ignoresCurrentIfStringHasScheme() {
		verifyParts("ftp://www.cis.gvsu.edu:73/dir1/dir2/file.html", current1, "ftp", "www.cis.gvsu.edu", 73, "/dir1/dir2/file.html");
	}

	@Test
	public void ignoresCurrentIfStringHasSchemeButNoPort() {
		verifyParts("ftp://www.cis.gvsu.edu/dir1/dir2/file.html", current1, "ftp", "www.cis.gvsu.edu", 80, "/dir1/dir2/file.html");
	}

	@Test
	public void ignoresCurrentIfStringHasSchemeButNoPath() {
		verifyParts("ssh://www.cis.gvsu.edu:133/dir1/dir2/", current1, "ssh", "www.cis.gvsu.edu", 133, "/dir1/dir2/");
	}

	@Test
	public void appliesRelative_singleFile() {
		MyURL url = new MyURL("www.cis.gvsu.edu/dir1/dir2/dir3/file.html");
		verifyParts("newFile.xls", url, "http", "www.cis.gvsu.edu", 80, "/dir1/dir2/dir3/newFile.xls");
	}

	@Test
	public void appliesRelative_subdir() {
		MyURL url = new MyURL("www.cis.gvsu.edu/dir1/dir2/dir3/file.html");
		verifyParts("fred/barney/wilma.html", url, "http", "www.cis.gvsu.edu", 80, "/dir1/dir2/dir3/fred/barney/wilma.html");
	}

	@Test
	public void appliesRelative_singleFileToDirectory() {
		MyURL url = new MyURL("www.cis.gvsu.edu/dir1/dir2/dir3/");
		verifyParts("newFile.xls", url, "http", "www.cis.gvsu.edu", 80, "/dir1/dir2/dir3/newFile.xls");
	}

	@Test
	public void appliesRelative_subdirToDirectory() {
		MyURL url = new MyURL("www.cis.gvsu.edu/dir1/dir2/dir3/");
		verifyParts("fred/barney/wilma.html", url, "http", "www.cis.gvsu.edu", 80, "/dir1/dir2/dir3/fred/barney/wilma.html");
	}

	@Test
	public void appliesRelative_replacesFileWithNoExtension() {
		MyURL url = new MyURL("www.cis.gvsu.edu/dir1/dir2/dir3/file");
		verifyParts("newFile.xls", url, "http", "www.cis.gvsu.edu", 80, "/dir1/dir2/dir3/newFile.xls");
	}

	@Test
	public void appliesRelative_copiesScheme() {
		MyURL url = new MyURL("ftp://www.cis.gvsu.edu/dir1/dir2/dir3/file.html");
		verifyParts("newFile.xls", url, "ftp", "www.cis.gvsu.edu", 80, "/dir1/dir2/dir3/newFile.xls");
	}

	@Test
	public void appliesRelative_copiesPort() {
		MyURL url = new MyURL("ssh://www.cis.gvsu.edu:6676/dir1/dir2/dir3/file.html");
		verifyParts("newFile.xls", url, "ssh", "www.cis.gvsu.edu", 6676, "/dir1/dir2/dir3/newFile.xls");
	}

	////////////////////////////////////////////
	//
	// Test toString
	//
	/////////////////////////////////////////////

	@Test
	public void fullURLtoString() {
		String urlString = "http://www.yahoo.com:8088/p1/d2/v3/mommy.html";
		MyURL url = new MyURL(urlString);
		Assert.assertEquals(urlString, url.toString());
	}

	@Test
	public void noPathToString() {
		String urlString = "http://www.yahoo.com:8088";
		MyURL url = new MyURL(urlString);
		// Note:  The '/' at the end isn't necessary.  Feel free to change this test if
		// you implemented toString differently.
		Assert.assertEquals(urlString + '/', url.toString());
	}


	////////////////////////////////////////////
	//
	// Test equals
	//
	/////////////////////////////////////////////

	@Test
	public void URLEqualToSelf() {
		MyURL url1 = new MyURL("http://fred.com/blah/blah/blah.html");
		Assert.assertEquals(url1, url1);
	}

	@Test
	public void identicalURLsAreEqual() {
		MyURL url1 = new MyURL("http://fred.com/blah/blah/blah.html");
		MyURL url2 = new MyURL("http://fred.com/blah/blah/blah.html");
		Assert.assertEquals(url1, url2);
		Assert.assertEquals(url2, url1);
	}

	@Test
	public void differentSchemesNotEqual() {
		MyURL url1 = new MyURL("http://fred.com/blah/blah/blah.html");
		MyURL url2 = new MyURL("https://fred.com/blah/blah/blah.html");
		Assert.assertNotEquals(url1, url2);
		Assert.assertNotEquals(url2, url1);
	}

	@Test
	public void differentDomainssNotEqual() {
		MyURL url1 = new MyURL("http://fred.com/blah/blah/blah.html");
		MyURL url2 = new MyURL("http://ferd.com/blah/blah/blah.html");
		Assert.assertNotEquals(url1, url2);
		Assert.assertNotEquals(url2, url1);
	}

	@Test
	public void differentPortsNotEqual() {
		MyURL url1 = new MyURL("http://fred.com:19/blah/blah/blah.html");
		MyURL url2 = new MyURL("http://fred.com:22/blah/blah/blah.html");
		Assert.assertNotEquals(url1, url2);
		Assert.assertNotEquals(url2, url1);
	}

	@Test
	public void differentPathsNotEqual() {
		MyURL url1 = new MyURL("http://fred.com/blah/blah/blah.html");
		MyURL url2 = new MyURL("http://fred.com/blah/blah.html");
		Assert.assertNotEquals(url1, url2);
		Assert.assertNotEquals(url2, url1);
	}

	////////////////////////////////////////////
	//
	// Test hashCode
	//
	/////////////////////////////////////////////

	@Test
	public void identicalURLsHaveSameHashCode() {
		MyURL url1 = new MyURL(new String("http://fred.com/blah/blah/blah.html"));
		MyURL url2 = new MyURL(new String("http://fred.com/blah/blah/blah.html"));
		Assert.assertEquals(url1.hashCode(), url2.hashCode());

	}

}