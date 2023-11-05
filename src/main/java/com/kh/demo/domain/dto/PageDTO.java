package com.kh.demo.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PageDTO {
	private int startPage;
	private int endPage;
	private int realEnd;
	private Long total;
	private boolean prev,next;
	private Criteria cri;
	private CriteriaTrainerProfile criTP;
	
	public PageDTO(Long total, Criteria cri) {
		int pagenum = cri.getPagenum();
		this.cri = cri;
		this.total = total;
		
		this.endPage = (int)Math.ceil(pagenum/10.0)*10;
		this.startPage = this.endPage - 9;
		this.realEnd = (int)Math.ceil(total*1.0/10);
		this.realEnd = this.realEnd == 0?1:this.realEnd;
		
		this.endPage = Math.min(endPage, realEnd);
		
		this.prev = this.startPage > 1;
		this.next = this.endPage < this.realEnd;
	}

	public PageDTO(Long total, CriteriaTrainerProfile cri) {
		int pagenum = cri.getPagenum();
		this.criTP = cri;
		this.total = total;

		this.endPage = (int)Math.ceil(pagenum/10.0)*10;
		this.startPage = this.endPage - 9;
		this.realEnd = (int)Math.ceil(total*1.0/10);
		this.realEnd = this.realEnd == 0?1:this.realEnd;

		this.endPage = Math.min(endPage, realEnd);

		this.prev = this.startPage > 1;
		this.next = this.endPage < this.realEnd;
	}
}