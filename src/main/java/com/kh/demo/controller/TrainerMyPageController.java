package com.kh.demo.controller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kh.demo.domain.dto.*;
import com.kh.demo.service.BoardService;
import com.kh.demo.service.TrainerMyPageService;
import com.kh.demo.service.TrainerService;
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
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/trainermypage/*")
public class TrainerMyPageController {

    @Autowired
    @Qualifier("TrainerMyPageServiceImpl")
    private TrainerMyPageService service;

    @Autowired
    @Qualifier("TrainerServiceImpl")
    private TrainerService tservice;

    @Autowired @Qualifier("BoardServiceImpl")
    private BoardService boardservice;


    @GetMapping(value = {"trainer_myprofile","trainer_profile","trainer_profile_modify"})
    public String trainer_profile(Criteria cri, String trainerId , Model model, HttpServletRequest req) throws Exception {
        cri = new Criteria(cri.getPagenum(), 4);
        String requestURI = req.getRequestURI();

        if(!requestURI.equals("/trainermypage/trainer_profile")){
            HttpSession session = req.getSession();
            trainerId = (String) session.getAttribute("loginUser");
        }

        TrainerDTO id = service.getUserDetail(trainerId);
        //인기게시글 띄우기
        List<BoardDTO> boardTop5List = boardservice.getBoardTop5List();

        // 트레이너 랭킹
        List<TrainerDTO> trainerTop5List= tservice.getTrainerTop5List();

        model.addAttribute("trainerTop5List",trainerTop5List);
        model.addAttribute("boardTop5List",boardTop5List);
        List<BoardDTO>  list = service.getBoardMyList(cri, trainerId);
        model.addAttribute("list", list);
        model.addAttribute("trainer", id);

        model.addAttribute("subPageMaker", new PageDTO(service.getScribeTotal(cri, trainerId), cri));

        model.addAttribute("profile",service.getProFileList(trainerId));

        model.addAttribute("files",service.getFileList(trainerId));

        model.addAttribute("pageMaker", new PageDTO(service.getBoardTotal(cri, trainerId), cri));
        model.addAttribute("newly_board", service.getBoardNewlyList(list));
        model.addAttribute("reply_cnt_list", service.getBoardReplyCntList(list));
        model.addAttribute("recent_reply", service.getBoardRecentReplyList(list));

        if (requestURI.equals("/trainermypage/trainer_myprofile")){
            return "/trainermypage/trainer_profile";
        }

        return requestURI;
    }


    @PostMapping("trainer_myprofile_modify")
    public String trainer_myprofile_modify(TrainerDTO trainerdto,String updateCnt , Model model , MultipartFile[] files,MultipartFile profile) throws IOException {
        if(files != null){
            for (int i = 0; i < files.length; i++) {
                System.out.println("controller : "+files[i].getOriginalFilename());
            }
        }
        if (service.trainer_modify(trainerdto,files,profile,updateCnt)){
            return "redirect:/trainermypage/trainer_myprofile";
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

    @GetMapping("trainer_challenge")
    public void trainer_challenge(String challCategory ,String challTerm ,Criteria cri, Model model, HttpServletRequest req) {
        HttpSession session = req.getSession();
        String TrainerId = (String) session.getAttribute("loginUser");

        TrainerDTO id = service.getUserDetail(TrainerId);

        if(challCategory == null){
            challCategory = "challAll";
        }
        if(challTerm == null){
            challTerm = "challengeAll";
        }

        List<ChallNoticeBoardDTO> list = service.getMyChallenge(cri, id.getTrainerId(),challCategory,challTerm);
        model.addAttribute("challCategory",challCategory);
        model.addAttribute("challTerm",challTerm);
        model.addAttribute("list",list);
        model.addAttribute("pageMaker", new PageDTO(service.getChallengeTotal(cri,id.getTrainerId(),challCategory,challTerm), cri));
    }


    @GetMapping("trainer_mysubscribeuser")
    public void trainer_mysubscribeuser(Criteria cri, Model model, HttpServletRequest req){
        cri.setAmount(12);
        HttpSession session = req.getSession();
        String TrainerId = (String) session.getAttribute("loginUser");

        TrainerDTO id = service.getUserDetail(TrainerId);

        List<UserDTO> list = service.getMyScribe(cri, id.getTrainerId());
        model.addAttribute("list",list);
        model.addAttribute("pageMaker", new PageDTO(service.getScribeTotal(cri,id.getTrainerId()), cri));
    }


    //북마크 번호 담을려면 보드dto랑 북마크dto 합쳐진 dto 필요
    @GetMapping("trainer_subbookmark")
    public void trainer_subbookmark(Criteria cri, Model model, HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession();
        String TrainerId = (String) session.getAttribute("loginUser");

        TrainerDTO id = service.getUserDetail(TrainerId);

        List<BoardDTO> list = service.getMyBookmark(cri,id.getTrainerId());
        List<ProductBoardDTO> listProduct = service.getMyBookmarkProduct(cri,id.getTrainerId());
        model.addAttribute("list",list);
        model.addAttribute("listProduct",listProduct);
        model.addAttribute("pageMaker", new PageDTO(service.getBookmarkTotal(cri,id.getTrainerId()), cri));
    }

    @GetMapping("board_info")
    @ResponseBody
    public String board_info(@RequestParam("pageNum") int pageNum, @RequestParam("keyword") String keyword, @RequestParam("type") String type,HttpServletRequest req) throws Exception{
        HttpSession session = req.getSession();
        String TrainerId = (String) session.getAttribute("loginUser");

        TrainerDTO id = service.getUserDetail(TrainerId);

        ObjectNode json = JsonNodeFactory.instance.objectNode();
        Criteria cri = new Criteria(pageNum, 10);
        cri.setKeyword(keyword);
        cri.setType(type);

        List<BoardDTO> list = service.getMyBookmark(cri,id.getTrainerId());
        PageDTO pageDTO = new PageDTO(service.getBookmarkTotal(cri,id.getTrainerId()), cri);
        json.putPOJO("list", list);
        json.putPOJO("pageDTO", pageDTO);

        return json.toString();
    }

    @GetMapping("board_product")
    @ResponseBody
    public String board_product(@RequestParam("pageNum") int pageNum, @RequestParam("keyword") String keyword, @RequestParam("type") String type, HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession();
        String TrainerId = (String) session.getAttribute("loginUser");

        TrainerDTO id = service.getUserDetail(TrainerId);

        ObjectNode json = JsonNodeFactory.instance.objectNode();
        Criteria cri = new Criteria(pageNum, 10);
        cri.setKeyword(keyword);
        cri.setType(type);

        List<ProductBoardDTO> list = service.getMyBookmarkProduct(cri,id.getTrainerId());
        PageDTO pageDTO = new PageDTO(service.getBookmarkProductTotal(cri,id.getTrainerId()), cri);
        json.putPOJO("list", list);
        json.putPOJO("pageDTO", pageDTO);

        return json.toString();
    }




    @GetMapping("trainer_boardlist")
    public void trainer_boardlist(Criteria cri, Model model,HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession();
        String TrainerId = (String) session.getAttribute("loginUser");

        TrainerDTO id = service.getUserDetail(TrainerId);

        List<BoardDTO> list = service.getBoardMyList(cri,id.getTrainerId());
        /* List<BoardDTO> list = serviceBoard.getBoardList(cri);*/
        model.addAttribute("list",list);
        model.addAttribute("pageMaker",new PageDTO(service.getBoardTotal(cri, id.getTrainerId()), cri));
        model.addAttribute("newly_board",service.getBoardNewlyList(list));
        model.addAttribute("reply_cnt_list",service.getBoardReplyCntList(list));
        model.addAttribute("recent_reply",service.getBoardRecentReplyList(list));
    }

    @GetMapping("trainer_messagelist")
    public void trainer_messagelist(String message ,Criteria cri,Model model,HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession();
        String TrainerId = (String) session.getAttribute("loginUser");

        TrainerDTO id = service.getUserDetail(TrainerId);

        if(message == null){
            message = "messageAll";
        }

        List<MessageDTO> list = service.getMessageMyList(cri,id.getTrainerId(),message);
        model.addAttribute("message",message);
        model.addAttribute("list",list);
        model.addAttribute("pageMaker",new PageDTO(service.getMessageTotal(cri,id.getTrainerId(),message), cri));
        model.addAttribute("newly_Message",service.getMessageNewlyList(list));
    }

    @GetMapping("u_t_matching")
    public void u_t_matching( Criteria cri, Model model, HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession();
        String TrainerId = (String) session.getAttribute("loginUser");

        TrainerDTO id = service.getUserDetail(TrainerId);

        List<UTMatchingDTO> list = service.getMyMatchinglist(cri, id.getTrainerId());

        model.addAttribute("list", list);
        model.addAttribute("pageMaker", new PageDTO(service.getMatchingTotal(cri, id.getTrainerId()), cri));
        model.addAttribute("newly_Message", service.getMatchingNewlyList(list));
    }

    @PostMapping("u_t_matching")
    public String u_t_matching(UTMatchingDTO utMatching, String SC){
        String trainer_check = null;
        if(SC.equals("success")){
             trainer_check = "O";
        } else{
             trainer_check = "X";
        }

        if(service.updateMatching(utMatching, trainer_check)){
            return "redirect:/trainermypage/u_t_matching";
        }
        return "redirect:/";
    }



    @GetMapping("trainer_modify")
    public String trainer_modify(HttpServletRequest req, HttpServletResponse response, Model model) throws IOException {
        HttpSession session = req.getSession();
        String loginUser = (String) session.getAttribute("loginUser");

        String alertScript = "<script>alert('트레이너만 접근 가능한 페이지입니다!!!');window.location.replace(\"/\");</script>";
        String alertScript2 = "<script>alert('로그인 후 이용 가능합니다!!');window.location.replace(\"/user/login\");</script>";

        if(loginUser != null){
            TrainerDTO user = service.getUserDetail(loginUser);
            if (user == null) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().write(alertScript);
                response.getWriter().flush();
                return null;
            }
            model.addAttribute("user", user);
            return "/trainermypage/trainer_modify";
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write(alertScript2);
        response.getWriter().flush();
        return null;
    }

    @GetMapping("trainer_myinfo_modify")
    public void trainer_myinfo_modify(HttpServletRequest req, Model model){
        HttpSession session = req.getSession();
        String loginUser = (String) session.getAttribute("loginUser");
        TrainerDTO user = service.getUserDetail(loginUser);
        model.addAttribute("user", user);
    }


    @PostMapping("trainer_myinfo_modify")
    public String trainer_myinfo_modify(TrainerDTO trainerdto, Model model) {
        if (service.updateInfo(trainerdto)){
            TrainerDTO user = service.getUserDetail(trainerdto.getTrainerId());
            model.addAttribute("user", user);
            return "redirect:/trainermypage/trainer_modify";
        }
        else {
            return "redirect:/";
        }
    }


}
