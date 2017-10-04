package com.ibm.is.sappack.cw.app.data.bdr.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.ibm.is.sappack.cw.app.data.Resources;

@Embeddable
public class Transaction implements Serializable, Comparable<Transaction> {

	private static final long serialVersionUID = 1L;

	@Column(name = "ID", nullable = false)
	private Double transactionId;

	@Column(name = "NAME", length = Resources.BDR_LENGTH_TRANSACTION_NAME * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String name;

	@Column(name = "COMMENT", length = Resources.BDR_LENGTH_COMMENT * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String comment;

	// TODO make the constructor require a name
	
	public Double getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Double transactionId) {
		this.transactionId = transactionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public int compareTo(Transaction ta) {
		if (name != null && ta.getName() != null) {
			return name.compareTo(ta.getName());
		}

		return 0;
	}
}
