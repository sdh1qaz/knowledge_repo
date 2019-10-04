package com.my.blog.website.exception;

public class KnowledgeRepoException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1442716615880348761L;

	public KnowledgeRepoException() {
	}

	public KnowledgeRepoException(String message) {
		super(message);
	}

	public KnowledgeRepoException(String message, Throwable cause) {
		super(message, cause);
	}

	public KnowledgeRepoException(Throwable cause) {
		super(cause);
	}

}
