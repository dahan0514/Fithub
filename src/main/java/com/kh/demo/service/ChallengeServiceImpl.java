package com.kh.demo.service;

import com.kh.demo.domain.dto.*;

import com.kh.demo.domain.dto.Criteria;
import com.kh.demo.domain.dto.FileDTO;
import com.kh.demo.domain.dto.MyChallengeDTO;
import com.kh.demo.mapper.ChallengeMapper;
import com.kh.demo.mapper.FileMapper;
import com.kh.demo.mapper.ReplyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Qualifier("ChallengeServiceImpl")
public class ChallengeServiceImpl implements ChallengeService{

    @Autowired
    private ReplyMapper rmapper;
    @Autowired
    private FileMapper fmapper;
    @Value("${file.dir}")
    private String saveFolder;


    @Autowired
    private ChallengeMapper challMapper;

    @Override
    public List<MyChallengeDTO> findMychall(String userid,String choicedate) {
        return challMapper.getIngMychall(userid,choicedate);
    }

    @Override
    public ChallNoticeBoardDTO getChallenge(String userId, String mychallNum) {
        return challMapper.getChallenge(userId,mychallNum);
    }


    public ChallNoticeBoardDTO findchall(String challNum) {
        return challMapper.getFindchall(challNum);
    }

    @Override
    public List<ChallNoticeBoardDTO> getChallSearchList(String keyword) {
        return challMapper.getChallSearchList(keyword);
    }

    @Override
    public boolean insertChallNotice(ChallNoticeBoardDTO chall) {
        int row = challMapper.insertChallNotice(chall);
        return row == 1;
    }

    @Override
    public Long getNoticeTotal(Criteria noticeCri, String challCategory, String challTerm) {
        if(challCategory.equals("challAll")){
            if (challTerm.equals("challengeAll")){
                return challMapper.getChallengeAllAllTotal(noticeCri);

            }else {
                return challMapper.getChallengeAllTermTotal(noticeCri,challTerm);

            }
        } else{
            if (challTerm.equals("challengeAll")){
                return challMapper.getChallengeCategoryAllTotal(noticeCri,challCategory);

            } else {
                return challMapper.getChallengeCategoryTermTotal(noticeCri,challCategory,challTerm);

            }
        }
    }

    @Override
    public List<ChallNoticeBoardDTO> getChallNoticeList(Criteria noticeCri, String challCategory, String challTerm) {
        if(challCategory.equals("challAll")){
            if (challTerm.equals("challengeAll")){
                return challMapper.getChallengeAllAll(noticeCri);
            }else {
                return challMapper.getChallengeAllTerm(noticeCri, challTerm);
            }
        } else{
            if (challTerm.equals("challengeAll")){
                return challMapper.getChallengeCategoryAll(noticeCri, challCategory);
            } else {
                return challMapper.getChallengeCategoryTerm(noticeCri, challCategory,challTerm);
            }
        }
    }

    @Override
    public ChallNoticeBoardDTO getChallNoticeDetail(Long challNum) {
        return challMapper.getChallNoticeDetail(challNum);
    }

    @Override
    public boolean insertMyChall(long challNum, String id) {
        return challMapper.insertMyChall(challNum, id);
    }

    @Override
    public void deleteChallNotice(long challNum) {
        challMapper.deleteChallNotice(challNum);
    }

    public List<ChallNoticeBoardDTO> get12challSearchList(Criteria cri) {
        return challMapper.get12challSearchList(cri);
    }

    @Override
    public Long getchallTotal(Criteria cri) {
        return challMapper.getchallTotal(cri);
    }

    @Override
    public boolean checkChall(long challNum, String id) {
        return challMapper.checkChall(challNum, id);
    }

    @Override
    public void deleteMyChall(Long mychallNum) {
        challMapper.deleteMyChall(mychallNum);
    }

    @Override
    public List<ChallNoticeBoardDTO> getChallNoticeBoardList(int i) {
        return challMapper.getChallNoticeBoardList(i);
    }


    /*재우*/


    @Override
    public boolean regist(ChallCertBoardDTO chall, MultipartFile[] files) throws Exception {
        int row = challMapper.insertBoard(chall);
        if(row != 1) {
            return false;
        }
        if(files == null || files.length == 0) {
            return true;
        }
        else {
            //방금 등록한 게시글 번호
            Long boardnum = challMapper.getLastNum(chall.getUserId());
            boolean flag = false;
            for(int i=0;i<files.length-1;i++) {
                MultipartFile file = files[i];
                //apple.png
                String orgname = file.getOriginalFilename();
                //5
                int lastIdx = orgname.lastIndexOf(".");
                //.png
                String extension = orgname.substring(lastIdx);

                LocalDateTime now = LocalDateTime.now();
                String time = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

                //20231005103911237랜덤문자열.png
                String systemname = time+ UUID.randomUUID().toString()+extension;
                System.out.println(systemname);

                //실제 저장될 파일의 경로
                String path = saveFolder+systemname;

                FileDTO fdto = new FileDTO();
                fdto.setBoardNum(boardnum);
                fdto.setSysName(systemname);
                fdto.setOrgName(orgname);
                fdto.setBoardCategory("challCert");
                //실제 파일 업로드
                file.transferTo(new File(path));

                flag = fmapper.insertFile(fdto) == 1;

                if(!flag) {
                    //업로드 했던 파일 삭제, 게시글 데이터 삭제
                    return flag;
                }
            }
        }
        return true;
    }

    @Override
    public boolean modify(ChallCertBoardDTO chall, MultipartFile[] files, String updateCnt, String boardCategory) throws Exception {
        int row = challMapper.updateBoard(chall);
        if(row != 1) {
            return false;
        }
        List<FileDTO> org_file_list = fmapper.getFiles(chall.getBoardNum(), boardCategory);
        if(org_file_list.size()==0 && (files == null || files.length == 0)) {
            return true;
        }
        else {
            if(files != null) {
                boolean flag = false;
                //후에 비즈니스 로직 실패 시 원래대로 복구하기 위해 업로드 성공했던 파일들도 삭제해주어야 한다.
                //업로드 성공한 파일들의 이름을 해당 리스트에 추가하면서 로직을 진행한다.
                ArrayList<String> sysnames = new ArrayList<>();
                System.out.println("service : "+files.length);
                for(int i=0;i<files.length-1;i++) {
                    MultipartFile file = files[i];
                    String orgname = file.getOriginalFilename();
                    //수정의 경우 중간에 있는 파일은 수정이 되지 않은 경우도 있다.
                    //그런 경우의 file의 orgname은 null 이거나 "" 이다.
                    //따라서 업로드가 될 필요가 없으므로 continue로 다음 파일로 넘어간다.
                    if(orgname == null || orgname.equals("")) {
                        continue;
                    }
                    int lastIdx = orgname.lastIndexOf(".");
                    String extension = orgname.substring(lastIdx);
                    LocalDateTime now = LocalDateTime.now();
                    String time = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
                    String systemname = time+UUID.randomUUID().toString()+extension;
                    sysnames.add(systemname);

                    String path = saveFolder+systemname;

                    FileDTO fdto = new FileDTO();
                    fdto.setBoardNum(chall.getBoardNum());
                    fdto.setOrgName(orgname);
                    fdto.setSysName(systemname);
                    fdto.setBoardCategory(boardCategory);


                    file.transferTo(new File(path));

                    flag = fmapper.insertFile(fdto) == 1;
                    if(!flag) {
                        break;
                    }
                }
                //강제탈출(실패)
                if(!flag) {
                    //아까 추가했던 systemname들(업로드 성공한 파일의 systemname)을 꺼내오면서
                    //실제 파일이 존재한다면 삭제 진행
                    for(String systemname : sysnames) {
                        File file = new File(saveFolder,systemname);
                        if(file.exists()) {
                            file.delete();
                        }
                        fmapper.deleteBySystemname(systemname);
                    }
                }
            }
            //지워져야 할 파일(기존에 있었던 파일들 중 수정된 파일)들의 이름 추출
            String[] deleteNames = updateCnt.split("\\\\");
            for(int i=1;i<deleteNames.length;i++) {
                File file = new File(saveFolder,deleteNames[i]);
                //해당 파일 삭제
                if(file.exists()) {
                    file.delete();
                    //DB상에서도 삭제
                    fmapper.deleteBySystemname(deleteNames[i]);
                }
            }
            return true;
        }
    }

    @Override
    public void updateReadCount(Long boardnum) {
        challMapper.updateReadCount(boardnum);
    }

    @Override
    public boolean remove(String loginUser, Long boardnum, String boardCategory) {
        ChallCertBoardDTO board = challMapper.findByNum(boardnum);
        if(board.getUserId().equals(loginUser)) {
            List<FileDTO> files = fmapper.getFiles(boardnum, boardCategory);
            for(FileDTO fdto : files) {
                File file = new File(saveFolder,fdto.getSysName());
                if(file.exists()) {
                    file.delete();
                    fmapper.deleteBySystemname(fdto.getSysName());
                }
            }
            return challMapper.deleteBoard(boardnum) == 1;
        }
        return false;
    }

    @Override
    public Long getTotal(Criteria cri) {
        return challMapper.getTotal(cri);
    }

    @Override
    public List<ChallCertBoardDTO> getChallList(Criteria cri) {
        return challMapper.getList(cri);
    }

    @Override
    public ChallCertBoardDTO getDetail(Long boardnum) {
        return challMapper.findByNum(boardnum);
    }

    @Override
    public Long getLastNum(String userid) {
        return challMapper.getLastNum(userid);
    }

    @Override
    public ArrayList<String> getNewlyBoardList(List<ChallCertBoardDTO> list) throws Exception {
        ArrayList<String> newly_board = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        for(ChallCertBoardDTO board : list) {
            Date regdate = df.parse(board.getRegdate());
            if(now.getTime() - regdate.getTime() < 1000*60*60*2) {
                newly_board.add("O");
            }
            else {
                newly_board.add("X");
            }
        }
        return newly_board;
    }

    @Override
    public ArrayList<Integer> getReplyCntList(List<ChallCertBoardDTO> list) {
        ArrayList<Integer> reply_cnt_list = new ArrayList<>();
        for(ChallCertBoardDTO board : list) {
            reply_cnt_list.add(rmapper.getTotal(board.getBoardNum()));
        }
        return reply_cnt_list;
    }

    @Override
    public ArrayList<String> getRecentReplyList(List<ChallCertBoardDTO> list) {
        ArrayList<String> recent_reply = new ArrayList<>();
        for(ChallCertBoardDTO board : list) {
            if(rmapper.getRecentReply(board.getBoardNum()) >= 5) {
                recent_reply.add("O");
            }
            else {
                recent_reply.add("X");
            }
        }
        return recent_reply;
    }

    @Override
    public List<FileDTO> getFileList(Long boardnum, String boardCategory) {
        return fmapper.getFiles(boardnum,boardCategory);
    }

    @Override
    public ResponseEntity<Resource> getThumbnailResource(String sysName) throws Exception{
        //경로에 관련된 객체(자원으로 가지고 와야 하는 파일에 대한 경로)
        Path path = Paths.get(saveFolder+sysName);
        //경로에 있는 파일의 MIME타입을 조사해서 그대로 담기
        String contentType = Files.probeContentType(path);
        //응답 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        //해당 경로(path)에 있는 파일에서부터 뻗어나오는 InputStream(Files.newInputStream)을 통해 자원화(InputStreamResource)
        Resource resource = new InputStreamResource(Files.newInputStream(path));
        return new ResponseEntity<>(resource,headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> downloadFile(String sysName, String orgName) throws Exception{
        //경로에 관련된 객체(자원으로 가지고 와야 하는 파일에 대한 경로)
        Path path = Paths.get(saveFolder+sysName);
        //해당 경로(path)에 있는 파일에서부터 뻗어나오는 InputStream(Files.newInputStream)을 통해 자원화(InputStreamResource)
        Resource resource = new InputStreamResource(Files.newInputStream(path));

        File file = new File(saveFolder,sysName);

        HttpHeaders headers = new HttpHeaders();
        String dwName = "";

        try {
            dwName = URLEncoder.encode(orgName,"UTF-8").replaceAll("\\+","%20");
        } catch (UnsupportedEncodingException e) {
            dwName = URLEncoder.encode(file.getName(),"UTF-8").replaceAll("\\+","%20");
        }

        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(dwName).build());
        return new ResponseEntity<Object>(resource,headers,HttpStatus.OK);
    }
}
