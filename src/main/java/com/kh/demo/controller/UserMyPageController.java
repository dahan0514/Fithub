package com.kh.demo.controller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kh.demo.domain.dto.*;
import com.kh.demo.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/usermypage/*")
public class UserMyPageController {

    @Autowired
    @Qualifier("UserMyPageServiceImpl")
    private UserMyPageService service;

    @Autowired
    private CalorieService calorieService;

    @Autowired
    @Qualifier("UserServiceImpl")
    private UserService serviceUser;

    @Autowired
    private ChallengeService challService;

    @Autowired
    @Qualifier("TrainerServiceImpl")
    private TrainerService tservice;


    @GetMapping("user_myinfo")
    public String user_myinfo(HttpServletRequest req, HttpServletResponse response, Model model) throws IOException {
        HttpSession session = req.getSession();
        String loginUser = (String) session.getAttribute("loginUser");

        String alertScript = "<script>alert('유저만 접근 가능한 페이지입니다!!!');window.location.replace(\"/\");</script>";
        String alertScript2 = "<script>alert('로그인 후 이용 가능합니다!!');window.location.replace(\"/user/login\");</script>";

        if(loginUser != null){
            UserDTO user = service.getUserDetail(loginUser);
            if (user == null) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().write(alertScript);
                response.getWriter().flush();
                return null;
            }
            else{
                model.addAttribute("user", user);
                return "/usermypage/user_myinfo";
            }
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write(alertScript2);
        response.getWriter().flush();
        return null;
    }


    @GetMapping("user_myinfo_modify")
    public void user_myinfo_modify(HttpServletRequest req, Model model) {
        HttpSession session = req.getSession();
        String loginUser = (String) session.getAttribute("loginUser");
        UserDTO user = service.getUserDetail(loginUser);
        model.addAttribute("user", user);
    }

    @PostMapping("user_myinfo_modify")
    public String user_myinfo_modify(UserDTO userdto, Model model) {
        System.out.println(userdto);
        if (service.user_modify(userdto)) {
            UserDTO user = service.getUserDetail(userdto.getUserId());
            model.addAttribute("user", user);
            return "redirect:/usermypage/user_myinfo";
        } else {
            return "redirect:/";
        }
    }


    @GetMapping("user_challenge")
    public void replaceChallenge(String challCategory, String challTerm, Criteria cri, Model model, HttpServletRequest req) {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("loginUser");

        if (challCategory == null) {
            challCategory = "challAll";
        }
        if (challTerm == null) {
            challTerm = "challengeAll";
        }

        List<ChallNoticeBoardDTO> list = service.getMyChallenge(cri, userId, challCategory, challTerm);
        model.addAttribute("list", list);
        model.addAttribute("challCategory",challCategory);
        model.addAttribute("challTerm",challTerm);
        model.addAttribute("pageMaker", new PageDTO(service.getChallengeTotal(cri, userId, challCategory, challTerm), cri));
    }


    @GetMapping("user_subtrainer")
    public void replaceSubTrainer(Criteria cri, Model model, HttpServletRequest req) {
        cri.setAmount(12);
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("loginUser");
        List<TrainerDTO> list = service.getMyScribe(cri, userId);

        model.addAttribute("list", list);
        model.addAttribute("pageMaker", new PageDTO(service.getScribeTotal(cri, userId), cri));

    }


    //북마크 번호 담을려면 보드dto랑 북마크dto 합쳐진 dto 필요
    @GetMapping("user_subbookmark")
    public void user_subbookmark(Criteria cri, Model model, HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("loginUser");
        model.addAttribute("pageMaker", new PageDTO(service.getBookmarkTotal(cri, userId), cri));
    }

    @GetMapping("board_info")
    @ResponseBody
    public String board_info(@RequestParam("pageNum") int pageNum, @RequestParam("keyword") String keyword, @RequestParam("type") String type, HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("loginUser");

        ObjectNode json = JsonNodeFactory.instance.objectNode();
        Criteria cri = new Criteria(pageNum, 10);
        cri.setKeyword(keyword);
        cri.setType(type);

        List<BoardDTO> list = service.getMyBookmark(cri, userId);
        PageDTO pageDTO = new PageDTO(service.getBookmarkTotal(cri, userId), cri);
        json.putPOJO("list", list);
        json.putPOJO("pageDTO", pageDTO);

        return json.toString();
    }

    @GetMapping("board_product")
    @ResponseBody
    public String board_product(@RequestParam("pageNum") int pageNum, @RequestParam("keyword") String keyword, @RequestParam("type") String type, HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("loginUser");


        ObjectNode json = JsonNodeFactory.instance.objectNode();
        Criteria cri = new Criteria(pageNum, 10);
        cri.setKeyword(keyword);
        cri.setType(type);

        List<ProductBoardDTO> list = service.getMyBookmarkProduct(cri, userId);
        PageDTO pageDTO = new PageDTO(service.getBookmarkProductTotal(cri, userId), cri);
        json.putPOJO("list", list);
        json.putPOJO("pageDTO", pageDTO);

        return json.toString();
    }

    @GetMapping("user_boardlist")
    public void user_boardlist(Criteria cri, Model model, HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("loginUser");
        List<BoardDTO> list = service.getBoardMyList(cri, userId);
        /* List<BoardDTO> list = serviceBoard.getBoardList(cri);*/
        System.out.println("cri : " + cri);
        System.out.println("PageDTO : " + new PageDTO(service.getBoardTotal(cri, userId), cri));
        model.addAttribute("list", list);
        model.addAttribute("pageMaker", new PageDTO(service.getBoardTotal(cri, userId), cri));
        model.addAttribute("newly_board", service.getBoardNewlyList(list));
        model.addAttribute("reply_cnt_list", service.getBoardReplyCntList(list));
        model.addAttribute("recent_reply", service.getBoardRecentReplyList(list));
    }

    @GetMapping("user_messagelist")
    public void user_messagelist(String message, Criteria cri, Model model, HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("loginUser");
        if (message == null) {
            message = "messageAll";
        }
        List<MessageDTO> list = service.getMessageMyList(cri, userId, message);

        System.out.println(cri);
        System.out.println("list:" + list);
        model.addAttribute("message",message);
        model.addAttribute("list", list);
        model.addAttribute("pageMaker", new PageDTO(service.getMessageTotal(cri, userId, message), cri));
        model.addAttribute("newly_Message", service.getMessageNewlyList(list));
    }

    @GetMapping("u_t_matching")
    public void u_t_matching(Criteria cri, Model model, HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("loginUser");

        List<UTMatchingDTO> list = service.getMyMatchinglist(cri, userId);

        System.out.println(cri);
        System.out.println("list:" + list);
        model.addAttribute("list", list);
        model.addAttribute("pageMaker", new PageDTO(service.getMatchingTotal(cri, userId), cri));
        model.addAttribute("newly_Message", service.getMatchingNewlyList(list));
    }

    @PostMapping("u_t_matching")
    public String u_t_matching(UTMatchingDTO utMatching) {

        if (service.updateMatching(utMatching)) {
            return "redirect:/usermypage/u_t_matching";
        }
        return "redirect:/";
    }

//프로필
    @GetMapping(value = {"user_myprofile","user_profile","user_profile_modify"})
    public String user_profile( String userId , Model model, HttpServletRequest req) throws Exception {
        String requestURI = req.getRequestURI();
        System.out.println("requestURI: "+requestURI);
        if(!requestURI.equals("/trainermypage/trainer_profile")){
            HttpSession session = req.getSession();
            userId = (String) session.getAttribute("loginUser");
        }

        UserDTO id = service.getUserDetail(userId);

        model.addAttribute("user", id);
        model.addAttribute("profile",service.getProFileList(userId));
        if (requestURI.equals("/usermypage/user_myprofile")){
            return "/usermypage/user_profile";
        }

        return requestURI;
    }

        @PostMapping("user_profile_modify")
    public String trainer_myprofile_modify(UserDTO user,String updateCnt, MultipartFile profile) throws IOException {
        System.out.println(user);
        System.out.println("profile: "+profile);

        if (service.user_profile_modify(user,profile,updateCnt)){
            return "redirect:/usermypage/user_myprofile";
        }
        else {
            return "redirect:/";
        }
    }

    @GetMapping("thumbnail")
    public ResponseEntity<Resource> thumbnail(String sysName) throws Exception{
        return service.getThumbnailResource(sysName);
    }
    @GetMapping("thumbnail_id")
    public ResponseEntity<Resource> thumbnail_id(String id) throws Exception{
        return service.getThumbnailResource_id(id);
    }


    @GetMapping("user_applytrainer")
    public void user_applytrainer(HttpServletRequest req, Model model) {
        HttpSession session = req.getSession();
        String loginUser = (String) session.getAttribute("loginUser");
        UserDTO user = service.getUserDetail(loginUser);
        model.addAttribute("user", user);
    }

    @PostMapping("applytrainer")
    public String applytrainer(TrainerSignUpDTO user, MultipartFile[] files) throws Exception {
        if (service.insertApplytrainer(user, files)) {
            return "redirect:/usermypage/user_applytrainer";
        }
        return "redirect:/";
    }

    @GetMapping("user_diary")
    public void replaceDiary(HttpServletRequest req, Model model) {
        HttpSession session = req.getSession();
        String loginUser = (String)session.getAttribute("loginUser");
        // 트레이너 랭킹
        List<TrainerDTO> trainerTop5List= tservice.getTrainerTop5List();

//        List<DiaryDTO> diaryList = service.getDiaryList(loginUser);
//        ArrayList<String> dateList = new ArrayList<String>();
//            for (DiaryDTO dtoData : diaryList) {
//                dateList.add(dtoData.getRegdate());
//        }

//        System.out.println(dateList);
//        model.addAttribute("diaryList", diaryList);
        model.addAttribute("trainerTop5List",trainerTop5List);
//        model.addAttribute("dateList",dateList);
    }

    @GetMapping("checklist")
    public String checklist(String choicedate, HttpServletRequest req, Model model) {

        HttpSession session = req.getSession();
        String loginUser = (String) session.getAttribute("loginUser");

        //작성으로 이동
        if (service.checkList(choicedate, loginUser) == null) {
//            System.out.println(choicedate);

            UserDTO user = serviceUser.getDetail(loginUser);
            List<TrainerDTO> trainerTop5List= tservice.getTrainerTop5List();
            model.addAttribute("trainerTop5List",trainerTop5List);
            model.addAttribute("regdate", choicedate);
            model.addAttribute("user", user);
            return "/usermypage/diaryWrite";
        }
        //view로 이동
        else {
            UserDTO user = serviceUser.getDetail(loginUser);
            model.addAttribute("user", user);
            return "redirect:/usermypage/diaryView?choicedate=" + choicedate;
        }
    }

    @GetMapping("diaryWrite")
    public void replacediaryWrite() {

    }

    @GetMapping(value = {"diaryView", "diaryModify"})
    public void replacediaryView(String choicedate, HttpServletRequest req, Model model) {
        HttpSession session = req.getSession();
        String loginUser = (String) session.getAttribute("loginUser");
        UserDTO user = serviceUser.getDetail(loginUser);
        DiaryDTO diary = service.getDiaryDetail(choicedate, loginUser);
        // 트레이너 랭킹
        List<TrainerDTO> trainerTop5List= tservice.getTrainerTop5List();
        model.addAttribute("trainerTop5List",trainerTop5List);
//        System.out.println(diary);



        //오늘의 비교 계산
        double resultweight = Math.round((user.getWeightGoal() - diary.getTodayWeight()) * 100.0) / 100.0;
        //오늘 칼로리 계산
        double totalResult = Math.round(diary.getFoodCalories() - diary.getExerCalories()) * 100.0 / 100.0;

        //각 음식, 운동 num 번호가 있는 배열
        String bfNumlist = diary.getTodayBreakfast();
        String lunchNumlist = diary.getTodayLunch();
        String dinnerNumlist = diary.getTodayDinner();
        String snackNumlist = diary.getTodaySnack();
        String exerNumlist = diary.getTodayExer();

        String[] todayAlllist = {bfNumlist, lunchNumlist, dinnerNumlist, snackNumlist, exerNumlist};
        String breakfast = "";
        String lunch = "";
        String dinner = "";
        String snack = "";
        String exer = "";
        double bfCal = 0d;
        double lunchCal = 0d;
        double dinnerCal = 0d;
        double snackCal = 0d;



        for (int i = 0; i < todayAlllist.length; i++) {
//            System.out.println(todayAlllist[i]);
            if (todayAlllist[i].isEmpty()) {
//                System.out.println("빈문자열"+ i);
            } else {
                if (i == 0) {
                    String[] bfArr = todayAlllist[i].split(",");
                    ArrayList<String> bfnameList = new ArrayList<String>();
                    for (String data : bfArr) {
                        List<FoodDTO> bfDTOList = calorieService.findfoodName(data);
                        for (FoodDTO dtoData : bfDTOList) {
                            bfnameList.add(dtoData.getFoodName());
                            bfCal += Double.parseDouble(dtoData.getFoodCalories());

                        }
                    }
                    bfCal = Math.round(bfCal * 100.0) / 100.0;
                    for (int j = 0; j < bfnameList.size(); j++) {
                        String breakfast1 = "";
                        if (j == bfnameList.size() - 1) {
                            breakfast1 += bfnameList.get(j);
                        } else {
                            breakfast1 += (bfnameList.get(j) + ",");
                        }
                        breakfast += breakfast1;

                    }

                } else if (i == 1) {
                    String[] lunchArr = todayAlllist[i].split(",");
                    ArrayList<String> lunchnameList = new ArrayList<String>();
                    for (String data : lunchArr) {
                        List<FoodDTO> lunchDTOList = calorieService.findfoodName(data);
                        for (FoodDTO dtoData : lunchDTOList) {
                            lunchnameList.add(dtoData.getFoodName());
                            lunchCal += Double.parseDouble(dtoData.getFoodCalories());
                        }
                    }
                    lunchCal = Math.round(lunchCal * 100.0) / 100.0;
                    for (int j = 0; j < lunchnameList.size(); j++) {
                        String lunch1 = "";
                        if (j == lunchnameList.size() - 1) {
                            lunch1 += lunchnameList.get(j);
                        } else {
                            lunch1 += (lunchnameList.get(j) + ",");
                        }
                        lunch += lunch1;

                    }
                } else if (i == 2) {
                    String[] dinnerArr = todayAlllist[i].split(",");
                    ArrayList<String> dinnernameList = new ArrayList<String>();
                    for (String data : dinnerArr) {
                        List<FoodDTO> dinnerDTOList = calorieService.findfoodName(data);
                        for (FoodDTO dtoData : dinnerDTOList) {
                            dinnernameList.add(dtoData.getFoodName());
                            dinnerCal += Double.parseDouble(dtoData.getFoodCalories());
                        }
                    }
                    dinnerCal = Math.round(dinnerCal * 100.0) / 100.0;
                    for (int j = 0; j < dinnernameList.size(); j++) {
                        String dinner1 = "";
                        if (j == dinnernameList.size() - 1) {
                            dinner1 += dinnernameList.get(j);
                        } else {
                            dinner1 += (dinnernameList.get(j) + ",");
                        }
                        dinner += dinner1;

                    }
                } else if (i == 3) {
                    String[] snackArr = todayAlllist[i].split(",");
                    ArrayList<String> snacknameList = new ArrayList<String>();
                    for (String data : snackArr) {
                        List<FoodDTO> snackDTOList = calorieService.findfoodName(data);
                        for (FoodDTO dtoData : snackDTOList) {
                            snacknameList.add(dtoData.getFoodName());
                            snackCal += Double.parseDouble(dtoData.getFoodCalories());
                        }
                    }
                    snackCal = Math.round(snackCal * 100.0) / 100.0;
                    for (int j = 0; j < snacknameList.size(); j++) {
                        String snack1 = "";
                        if (j == snacknameList.size() - 1) {
                            snack1 += snacknameList.get(j);
                        } else {
                            snack1 += (snacknameList.get(j) + ",");
                        }
                        snack += snack1;

                    }
                } else if (i == 4) {
                    String[] exerArr = todayAlllist[i].split(",");
                    ArrayList<String> exernameList = new ArrayList<String>();
                    for (String data : exerArr) {
                        List<ExerciseDTO> exerDTOList = calorieService.findExecName(data);
                        for (ExerciseDTO dtoData : exerDTOList) {
                            exernameList.add(dtoData.getExecName());
                        }
                    }
                    for (int j = 0; j < exernameList.size(); j++) {
                        String exer1 = "";
                        if (j == exernameList.size() - 1) {
                            exer1 += exernameList.get(j);
                        } else {
                            exer1 += (exernameList.get(j) + ",");
                        }
                        exer += exer1;

                    }
                } else {
                    System.out.println("해당사항없음 오류");
                }

            }
        }
        double[] foodCalArr = {bfCal, lunchCal, dinnerCal, snackCal};
        String[] listNameArr = {breakfast, lunch, dinner, snack, exer};
        String[] sccChall;
        //진행중인 챌린지 내역
        List<MyChallengeDTO> myChallINGDTOList = challService.findMychall(diary.getUserId(), diary.getRegdate());
        //성공한 챌린지 내역
//        System.out.println("성공한 챌린지 번호 : "+diary.getMyChallNum());

        model.addAttribute("user", user);
        model.addAttribute("diary", diary);
        model.addAttribute("resultweight", resultweight);
        model.addAttribute("listNameArr", listNameArr);
        model.addAttribute("foodCalArr", foodCalArr);
        model.addAttribute("totalResult", totalResult);
        model.addAttribute("myChallINGDTOList", myChallINGDTOList);
        model.addAttribute("sccChallNum", diary.getMyChallNum());

    }

    @PostMapping("diaryWrite")
    public String diaryWrite(String choicedate, DiaryDTO diary) {
        int result = service.registDiary(diary);
        if (result == 1) {
            if (diary.getMyChallNum() == null) {
                return "redirect:/usermypage/diaryView?choicedate=" + choicedate;
            } else {
                String[] sccChallNumArr = (diary.getMyChallNum()).split(",");
                int sccChallNum = 0;
                HashMap<String, String> diaryInfo = new HashMap<String, String>();
                diaryInfo.put("diarydate", diary.getRegdate());
                diaryInfo.put("userid", diary.getUserId());

                for (int i = 0; i < sccChallNumArr.length; i++) {
                    sccChallNum = Integer.parseInt(sccChallNumArr[i]);
                    if (service.addStemp(sccChallNum, diaryInfo)) {
                    } else {
                        System.out.println("스탬프 적립 실패");
                    }
                }
            }
//            System.out.println("다이어리 저장 :" + diary);
            return "redirect:/usermypage/diaryView?choicedate=" + choicedate;
        }
        //실패시 다시 캘린더로
        //return "redirect:/usermypage/user_diary?choicedate"+choicedate;
        return "/usermypage/user_diary";
    }

    @PostMapping("diaryModify")
    public String diaryModify(DiaryDTO diary, String choicedate) {
        if (service.modifyDiary(diary)) {
            return "redirect:/usermypage/diaryView?choicedate=" + choicedate;
        }

        return "/usermypage/user_diary";
    }

    @PostMapping("diaryRemove")
    public String diaryRemove(Long diaryNum, String choicedate) {
        DiaryDTO diary = service.getDiaryByNum(diaryNum);
//        System.out.println("removediary :" + diary.getMyChallNum());

        if (diary.getMyChallNum() != null) {
            if (service.removeStamp(diary.getUserId(), diary.getRegdate())) {
                if (service.removeDiary(diaryNum)) {
                    return "/usermypage/user_diary";
                }
                return "redirect:/usermypage/diaryView?choicedate=" + choicedate;

            }
        } else {
            if (service.removeDiary(diaryNum)) {
                return "/usermypage/user_diary";
            }
        }
        return "redirect:/usermypage/diaryView?choicedate=" + choicedate;
    }

    @PostMapping("todaydiary")
    @ResponseBody
    public String todaydiary(@RequestParam("userid") String userid, @RequestParam("todaydate") String todaydate){
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        DiaryDTO diary = service.getsideDiaryWithUser(todaydate, userid);
        //오늘 총 칼로리 계산(음식-운동)
        if(diary != null) {
            double totalResult = Math.round(diary.getFoodCalories() - diary.getExerCalories()) * 100.0 / 100.0;
            diary.setNowCal(totalResult);

            //오늘의 체중비교 계산
            double resultweight = Math.round((diary.getWeightGoal() - diary.getTodayWeight()) * 100.0) / 100.0;
            diary.setTodayKGGap(resultweight);

            //목표 칼로리
            int caloriesGoal = diary.getCaloriesGoal();
            diary.setCaloriesGoal(caloriesGoal);
            //각 음식, 운동 num 번호가 있는 배열
            String bfNumlist = diary.getTodayBreakfast();
            String lunchNumlist = diary.getTodayLunch();
            String dinnerNumlist = diary.getTodayDinner();
            String snackNumlist = diary.getTodaySnack();

            String[] todayAlllist = {bfNumlist, lunchNumlist, dinnerNumlist, snackNumlist};

            double bfCal = 0d;
            double lunchCal = 0d;
            double dinnerCal = 0d;
            double snackCal = 0d;


            for (int i = 0; i < todayAlllist.length; i++) {
                if (todayAlllist[i].isEmpty()) {

                } else {
                    if (i == 0) {
                        String[] bfArr = todayAlllist[i].split(",");
                        for (String data : bfArr) {
                            List<FoodDTO> bfDTOList = calorieService.findfoodName(data);
                            for (FoodDTO dtoData : bfDTOList) {
                                bfCal += Double.parseDouble(dtoData.getFoodCalories());

                            }
                        }
                        bfCal = Math.round(bfCal * 100.0) / 100.0;
                        diary.setBfCal(bfCal);
                    } else if (i == 1) {
                        String[] lunchArr = todayAlllist[i].split(",");
                        for (String data : lunchArr) {
                            List<FoodDTO> lunchDTOList = calorieService.findfoodName(data);
                            for (FoodDTO dtoData : lunchDTOList) {
                                lunchCal += Double.parseDouble(dtoData.getFoodCalories());
                            }
                        }
                        lunchCal = Math.round(lunchCal * 100.0) / 100.0;
                        diary.setLunchCal(lunchCal);

                    } else if (i == 2) {
                        String[] dinnerArr = todayAlllist[i].split(",");
                        for (String data : dinnerArr) {
                            List<FoodDTO> dinnerDTOList = calorieService.findfoodName(data);
                            for (FoodDTO dtoData : dinnerDTOList) {
                                dinnerCal += Double.parseDouble(dtoData.getFoodCalories());
                            }
                        }
                        dinnerCal = Math.round(dinnerCal * 100.0) / 100.0;
                        diary.setDinnerCal(dinnerCal);
                    } else if (i == 3) {
                        String[] snackArr = todayAlllist[i].split(",");
                        for (String data : snackArr) {
                            List<FoodDTO> snackDTOList = calorieService.findfoodName(data);
                            for (FoodDTO dtoData : snackDTOList) {
                                snackCal += Double.parseDouble(dtoData.getFoodCalories());
                            }
                        }
                        snackCal = Math.round(snackCal * 100.0) / 100.0;
                        diary.setSnackCal(snackCal);
                    } else {
                        System.out.println("해당사항없음 오류");
                    }

                }
            }
            json.putPOJO("diaryDTO", diary);
            json.putPOJO("todaydate", todaydate);
        }else {
            json.putPOJO("diaryDTO", diary);
            json.putPOJO("todaydate", todaydate);
        }
        return json.toString();
    }


}


