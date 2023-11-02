package com.kh.demo.service;

import com.kh.demo.domain.dto.*;

import java.util.List;

public interface AdminMyPageService {
//    Report
    List<ReportDTO> getReportList(Criteria cri);

    List<ReportDTO> getReportListByUser(Criteria cri);

    List<ReportDTO> getReportListByTrainer(Criteria cri);

    Long getReportTotal(Criteria cri);

    Long getReportTotalByUser(Criteria cri);

    Long getReportTotalByTrainer(Criteria cri);

//    SignUp
    List<TrainerSignUpDTO> getSignUpList(Criteria cri);

    Long getSignUpTotal(Criteria cri);

//    AdminBoard
    List<BoardDTO> getBoardList(Criteria cri);

    Long getBoardTotal(Criteria cri);

    List<BoardDTO> getAdminExerBoard(Criteria cri);

    List<BoardDTO> getAdminNewsBoard(Criteria cri);

    List<BoardDTO> getAdminRecipeBoard(Criteria cri);

    List<BoardDTO> getAdminTipBoard(Criteria cri);

    Long getExerBoardTotal(Criteria cri);

    Long getNewsBoardTotal(Criteria cri);

    Long getTipBoardTotal(Criteria cri);

    Long getRecipeBoardTotal(Criteria cri);

    Object getUser(String keyword);

}
