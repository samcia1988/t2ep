package org.theta.maven.plugins.t2ep.server;

/**
 *
 * @author Ranger 2015骞�6鏈�11鏃�
 */
public class HttpConsts {

	public static class Headers {

		public static final String SET_COOKIE = "Set-Cookie";

		public static final String CONTENT_TYPE = "Content-Type";

		public static final String COOKIE = "Cookie";

		public static final String REFERER = "Referer";

		public static final String X_FORWARDED_FOR = "X-Forwarded-For";
		
		public static final String USER_AGENT="User-agent";

	}
	
	public static class Headers_Default_Value{
		public static final String CONTENT_TYPE="text/html; charset=UTF8";
		public static final String USER_AGENT="T2EP-PROXY";
	}

	public static class Methods {
		public static final String GET = "GET";
		public static final String POST = "POST";
	}

}
