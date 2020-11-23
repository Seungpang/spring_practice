package com.victolee.board.controller;


import com.victolee.board.domain.repository.ChatRoomRepository;
import com.victolee.board.dto.ChatMessage;
import com.victolee.board.dto.ChatRoom;
import com.victolee.board.dto.UserInfoDto;
import com.victolee.board.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {


    private final ChatRoomRepository chatRoomRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 채팅 리스트 화면
    @GetMapping("/room")
    public String rooms(Model model) {
        return "/chat/room";
    }
    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> room() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllRoom();
        chatRooms.stream().forEach(room -> room.setUserCount(chatRoomRepository.getUserCount(room.getRoomId())));

        return chatRooms;
    }

    // 채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public ChatRoom createRoom(@RequestParam String name) {
        return chatRoomRepository.createChatRoom(name);
    }
    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "/chat/roomdetail";
    }
    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRoomRepository.findRoomById(roomId);
    }
    //로그인 유저 정보 조회
    @GetMapping("/user")
    @ResponseBody
    public UserInfoDto getUserInfo(Principal principal) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userid = principal.getName();
        return UserInfoDto.builder().id(userid).token(jwtTokenProvider.generateToken(userid)).build();
    }


}