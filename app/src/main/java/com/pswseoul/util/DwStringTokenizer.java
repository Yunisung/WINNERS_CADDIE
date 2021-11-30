package com.pswseoul.util;

public class DwStringTokenizer {
//	private	String	org_data	=	null;
	/**
	 * @uml.property  name="data"
	 */
	private	String	data		=	null;
	/**
	 * @uml.property  name="deli"
	 */
	private	String	deli		=	null;

	public DwStringTokenizer(String data, String deli) {
//		this.org_data	=	data;
		this.data		=	data;
		this.deli		=	deli;
	}

	public int countTokens() {
		if(data == null || deli == null) return 0;

		int idx		=	0;
		int	count	=	0;

		boolean	stop	=	false;
		while(!stop) {
			idx		=	data.indexOf(deli, idx);
			count++;

			if(idx < 0) break;
			idx		+=	deli.length();
		}

		return count;
	}

	public boolean hasMoreTokens() {
		if(data == null || deli == null) return false;
		else return true;
	}

	public String nextToken() {
		if(data == null || deli == null) return null;

		String	buf	=	null;
		int idx		=	data.indexOf(deli);

		if(idx < 0) {
			buf		=	data;
			data	=	null;

			return buf;
		}

		buf		=	data.substring(0, idx);
		data	=	data.substring(idx + deli.length());

		return buf;
	}
        public String nextData() {
                if(data == null) return null;
                return data;
        }
	
	
}

