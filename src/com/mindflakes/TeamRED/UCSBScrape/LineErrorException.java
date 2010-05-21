package com.mindflakes.TeamRED.UCSBScrape;

/**
 * Exception classes used to handle unexpected line errors in the UCSBJMenuScraper class.
 * @author Johan Henkens
 *
 */
@SuppressWarnings("serial")
public class LineErrorException extends RuntimeException {

	/**
	 * Constructs a new <code>LineErrorException</code> with the specified description.
	 * @param throwError String containing description of the LineErrorException that occurred
	 */
	public LineErrorException(String throwError){
		super(throwError);
	}
	
	
	/**
	 * Constructs a new <code>LineErrorException</code> with the specified description and cause.
	 * @param throwError description of the <code>LineErrorException</code> that occurred
	 * @param cause the cause of the <code>Exception</code>
	 */
	public LineErrorException(String throwError, Throwable cause){
		super(throwError,cause);
	}
	
	
}
