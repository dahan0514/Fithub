package com.kh.demo.service;

import com.kh.demo.domain.dto.*;
import com.kh.demo.mapper.AdminMyPageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Qualifier("AdminMyPageServiceImpl")
public class AdminMyPageServiceImpl implements AdminMyPageService {
    @Autowired
    private AdminMyPageMapper adminMyPageMapper;

//    Report
    @Override
    public List<ReportDTO> getReportList(Criteria cri) {
        return adminMyPageMapper.getReportList(cri);
    }

    @Override
    public List<ReportDTO> getReportListByUser(Criteria cri) {
        return adminMyPageMapper.getReportListByUser(cri);
    }

    @Override
    public List<ReportDTO> getReportListByTrainer(Criteria cri) {
        return adminMyPageMapper.getReportListByTrainer(cri);
    }

    @Override
    public Long getReportTotal(Criteria cri) {
        return adminMyPageMapper.getReportTotal(cri);
    }

    @Override
    public Long getReportTotalByUser(Criteria cri) {
        return adminMyPageMapper.getReportTotalByUser(cri);
    }

    @Override
    public Long getReportTotalByTrainer(Criteria cri) {
        return adminMyPageMapper.getReportTotalByTrainer(cri);
    }

    @Override
    public List<ReportDTO> getDoneReportList(Criteria cri) {
        return adminMyPageMapper.getDoneReportList(cri);
    }

    @Override
    public Long getDoneReportTotal(Criteria cri) {
        return adminMyPageMapper.getDoneReportTotal(cri);
    }

    @Override
    public Object getUserById(String userId) {
        if (adminMyPageMapper.getTrainerByIdBoolean(userId)) {
            return adminMyPageMapper.getTrainerById(userId);
        }
        else if (adminMyPageMapper.getUserByIdBoolean(userId)) {
            return adminMyPageMapper.getUserById(userId);
        }
        return null;
    }

    //    SignUp
    @Override
    public List<TrainerSignUpDTO> getSignUpList(Criteria cri) {
        return adminMyPageMapper.getSignUpList(cri);
    }

    @Override
    public Long getSignUpTotal(Criteria cri) {
        return adminMyPageMapper.getSignUpTotal(cri);
    }

//    AdminBoard
    @Override
    public List<BoardDTO> getBoardList(Criteria cri) {
        return adminMyPageMapper.getBoardList(cri);
    }

    @Override
    public Long getBoardTotal(Criteria cri) {
        return adminMyPageMapper.getBoardTotal(cri);
    }

    @Override
    public List<BoardDTO> getAdminExerBoard(Criteria cri) {
        return adminMyPageMapper.getAdminExerBoardList(cri);
    }

    @Override
    public List<BoardDTO> getAdminNewsBoard(Criteria cri) {
        return adminMyPageMapper.getAdminNewsBoardList(cri);
    }

    @Override
    public List<BoardDTO> getAdminRecipeBoard(Criteria cri) {
        return adminMyPageMapper.getAdminRecipeBoardList(cri);
    }

    @Override
    public List<BoardDTO> getAdminTipBoard(Criteria cri) {
        return adminMyPageMapper.getAdminTipBoardList(cri);
    }

    @Override
    public Long getExerBoardTotal(Criteria cri) {
        return adminMyPageMapper.getExerBoardTotal(cri);
    }

    @Override
    public Long getNewsBoardTotal(Criteria cri) {
        return adminMyPageMapper.getNewsBoardTotal(cri);
    }

    @Override
    public Long getTipBoardTotal(Criteria cri) {
        return adminMyPageMapper.getTipBoardTotal(cri);
    }

    @Override
    public Long getRecipeBoardTotal(Criteria cri) {
        return adminMyPageMapper.getRecipeBoardTotal(cri);
    }

//    SearchUser
    @Override
    public TrainerDTO getTrainer(String keyword) {
        return adminMyPageMapper.getTrainer(keyword);
    }

    @Override
    public UserDTO getUser(String keyword) {
        UserDTO userDTO = adminMyPageMapper.getUser(keyword);

        if(userDTO != null) {
            String birthDateWithHyphen = userDTO.getUserBirth();
            String[] parts = birthDateWithHyphen.split("-");
            String birthDateWithoutHyphen = String.join("", parts);

            if (birthDateWithoutHyphen != null && birthDateWithoutHyphen.length() == 8) {
                // 현재 날짜 가져오기
                LocalDate currentDate = LocalDate.now();
                int birthYear = Integer.parseInt(birthDateWithoutHyphen.substring(0, 4));
                int birthMonth = Integer.parseInt(birthDateWithoutHyphen.substring(4, 6));
                int birthDay = Integer.parseInt(birthDateWithoutHyphen.substring(6, 8));
                LocalDate birthLocalDate = LocalDate.of(birthYear, birthMonth, birthDay);

                // 나이 계산
                Period period = Period.between(birthLocalDate, currentDate);
                userDTO.setUserAge(period.getYears());
            }
            else {
                userDTO.setUserAge(-1); // 또는 다른 값을 사용하여 오류를 표시
            }
        }
        return userDTO;
    }

    @Override
    public List<UserDTO> getUserList(Criteria cri) {
        return adminMyPageMapper.getUserList(cri);
    }

    @Override
    public List<TrainerDTO> getTrainerList(Criteria cri) {
        return adminMyPageMapper.getTrainerList(cri);
    }

    @Override
    public Long getUserTotal(Criteria cri) {
        return adminMyPageMapper.getUserTotal(cri);
    }

    @Override
    public Long getTrainerTotal(Criteria cri) {
        return adminMyPageMapper.getTrainerTotal(cri);
    }

//    message
    @Override
    public List<MessageDTO> getMessageList(Criteria cri) {
        return adminMyPageMapper.getMessageList(cri);
    }

    @Override
    public Long getMessageTotal(Criteria cri) {
        return adminMyPageMapper.getMessageTotal(cri);
    }

    @Override
    public List<MessageDTO> getMessageByUser(Criteria cri) {
        return adminMyPageMapper.getMessageByUser(cri);
    }

    @Override
    public List<MessageDTO> getMessageByTrainer(Criteria cri) {
        return adminMyPageMapper.getMessageByTrainer(cri);
    }

    @Override
    public Long getMessageTotalByUser(Criteria cri) {
        return adminMyPageMapper.getMessageTotalByUser(cri);
    }

    @Override
    public Long getMessageTotalByTrainer(Criteria cri) {
        return adminMyPageMapper.getMessageTotalByTrainer(cri);
    }

//    Modal
    @Override
    public ReportDTO getReportDTO(Long reportNum) {
        ReportDTO reportDTO =  adminMyPageMapper.getReportDTO(reportNum);
        return reportDTO;
    }

    @Override
    public void updateReportYn(Long reportNum) {
        adminMyPageMapper.updateReportYn(reportNum);
    }

    @Override
    public void updateReportedUser(String reportedUser, Long boardNum, String boardCategory) {
//        신고 당한 횟수 업데이트
        if (adminMyPageMapper.getTrainerByIdBoolean(reportedUser)) {
            adminMyPageMapper.updateReportedTrainer(reportedUser);
        }
        else if (adminMyPageMapper.getUserByIdBoolean(reportedUser)){
            adminMyPageMapper.updateReportedUser(reportedUser);
        }
        adminMyPageMapper.insertMessageReportedUser(reportedUser);

//        게시글 삭제
        if(adminMyPageMapper.selectBoard(boardNum)){
            adminMyPageMapper.deleteReportedBoard(boardNum, boardCategory);
        }
        else if (adminMyPageMapper.selectReview(boardNum)){
            adminMyPageMapper.deleteReportedReview(boardNum);
        }
        else if (adminMyPageMapper.selectMessage(boardNum)){
            adminMyPageMapper.deleteReportedMessage(boardNum, boardCategory);
        }
        else if (adminMyPageMapper.selectChallCert(boardNum)){
            adminMyPageMapper.deleteReportedChallCert(boardNum);
        }
    }

    @Override
    public void insertMessageDoneReport(String userId) {
        adminMyPageMapper.insertMessageDoneReport(userId);
    }

    @Override
    public void insertMessageCancelReport(String userId) {
        adminMyPageMapper.insertMessageCancelReport(userId);
    }

    @Override
    public TrainerSignUpDTO getSignUpDTO(Long signupNum) {
        return adminMyPageMapper.getSignUpDTO(signupNum);
    }

    @Override
    public List<ProfileDTO> getSignUpFile(String userId) {
        return adminMyPageMapper.getSignUpFile(userId);
    }

    @Override
    public void signUpConfirm(TrainerSignUpDTO signUpDTO, UserDTO userDTO) {
        adminMyPageMapper.updateUserCategory(userDTO.getUserId());

        Map<String, Object> map = new HashMap<>();
        map.put("signUpDTO", signUpDTO);
        map.put("userDTO", userDTO);

        adminMyPageMapper.insertTrainer(map);
        adminMyPageMapper.insertMessageConfirmSignUp(userDTO.getUserId());
        adminMyPageMapper.deleteSignUp(signUpDTO.getSignupNum());
    }

    @Override
    public void signUpCancel(Long signupNum, String userId) {
        System.out.println(userId);
        adminMyPageMapper.insertMessageCancelSignUp(userId);
        adminMyPageMapper.deleteSignUp(signupNum);
    }

    @Override
    public MessageDTO getMessage(Long messageNum) {
        return adminMyPageMapper.getMessage(messageNum);
    }

    @Override
    public void returnMessage(String messageContent, String receiveId, Long messageNum) {
        adminMyPageMapper.returnMessage(messageContent, receiveId);
        adminMyPageMapper.updateMessageCategory(messageNum);
    }


    @Override
    public void saveMessage(MessageDTO newMessage){
        adminMyPageMapper.saveMatching(newMessage);
    }

    @Override
    public void messageToAdmin(String messageContent, String userId) {
        adminMyPageMapper.insertMessageToAdmin(messageContent, userId);
    }

    @Override
    public void messageWrite(String messageContent, String userId, String receiveId) {
        adminMyPageMapper.messageWrite(messageContent, userId, receiveId);
    }
}
