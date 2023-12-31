package com.kh.demo.mapper;


import com.kh.demo.domain.dto.TrainerDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TrainerMapper {

    TrainerDTO findById(String userid);

    int updateInfo(TrainerDTO user);


    TrainerDTO getTrainerInfo(String trainerId);

    List<TrainerDTO> getTrainerTop1();

    List<TrainerDTO> getTrainerTop5();

    List<TrainerDTO> getTrainerNickname(String trainerId);

    List<TrainerDTO> getTrainerTopNumList(int i);

    List<TrainerDTO> getTrainerBoardTotalTop5List();

    List<TrainerDTO> getTrainerReplyTotalTop5List();
}
