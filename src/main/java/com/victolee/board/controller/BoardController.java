package com.victolee.board.controller;

import com.victolee.board.domain.entity.UserEntity;
import com.victolee.board.dto.BoardDto;
import com.victolee.board.dto.CartDto;
import com.victolee.board.dto.UserInfoDto;
import com.victolee.board.service.BoardService;
import com.victolee.board.service.CartService;
import com.victolee.board.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
public class BoardController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private CartService cartService;


    //-----------------------------------jpa로만든 컨트롤러-------------------------------------------------
    /* 메인 화면 */
    @GetMapping("/")
    public String list(@AuthenticationPrincipal UserEntity userEntity,Model model) {
        String role = userEntity.getRole();

        model.addAttribute("role",role);
        return "/index";
    }


    /* 게시글 전체 목록 */ /* 페이지 수를 담는 배열과  그 페이지에 따라 게시글 목록들을 담은 리스트 */
    @GetMapping("/managerlist")
    public String list(Model model, @RequestParam(value="page", defaultValue = "1") Integer pageNum) {
        List<BoardDto> boardList = boardService.getBoardlist(pageNum);
        Integer[] pageList = boardService.getPageList(pageNum);

        model.addAttribute("boardList", boardList);
        model.addAttribute("pageList", pageList);

        return "/managerlist";
    }

    /* 게시글 상세 목록*/
    @RequestMapping("/post/{no}")
    public String detail(@PathVariable("no") Long no, Model model
            ,Principal principal) {

        BoardDto boardDto = boardService.getPost(no);
//        CartDto cartDto = cartService.getCartlist();

        if(!boardDto.getWriter().equals(principal.getName())) {
            int count = boardDto.getBcount();
            count = count + 1;
            boardDto.setBcount(count);
            boardService.savePost(boardDto);
        }

        model.addAttribute("userId",principal.getName());
        model.addAttribute("boardDto", boardDto);
        return "board/detail";
    }


    /* 게시글 쓰기 폼으로 이동*/
    @GetMapping("/post")
    public String write() {

        return "board/write";
    }


    /* 게시글 쓰기 */ /* 로그인한 유저가 작성자가 되도록 해줌.*/

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public String write(@RequestParam("img") MultipartFile files, BoardDto boardDto, Principal principal) {
        System.out.println("넘어오나용");
        try {
            String baseDir = "C:\\JAVA_Spring\\캡스톤 프로젝트\\spring_practice\\media";//파일 저장 코드
            String filePath = baseDir + "\\" + files.getOriginalFilename();
            files.transferTo(new File(filePath));//해당 위치에 저장 형준 수정

            String userid = principal.getName();
            boardDto.setImgname(filePath);
            boardDto.setWriter(userid);
            boardService.savePost(boardDto);

            return "redirect:/managerlist";
        } catch(Exception e) {
            e.printStackTrace();
        }
//        String filePath = files.getOriginalFilename();
//        files.transferTo(new File("\\static\\images\\media\\"+filePath));

        return "redirect:/managerlist";
    }

    /* 게시글 수정 */
    @GetMapping("/post/edit/{no}")
    public String edit(@PathVariable("no") Long no, Model model) {
        BoardDto boardDto = boardService.getPost(no);

        model.addAttribute("boardDto", boardDto);
        return "board/update";
    }
    /* 수정 폼에서 수정 완료*/
    @PutMapping("/post/edit/{no}")
    public String update(BoardDto boardDto,Principal principal) {
        String userid = principal.getName();

        boardDto.setWriter(userid);
        boardService.savePost(boardDto);

        return "redirect:/managerlist";
    }

    /* 게시글 삭제 */
    @DeleteMapping("/post/{no}")
    public String delete(@PathVariable("no") Long no) {
        boardService.deletePost(no);

        return "redirect:/managerlist";
    }

    /* 게시글 검색 */ //키워드를 받아서 검색.
    @GetMapping("/board/search")
    public String search(@RequestParam(value="keyword") String keyword, Model model) {
        List<BoardDto> boardDtoList = boardService.searchPosts(keyword);

        model.addAttribute("boardList", boardDtoList);

        return "/managerlist";
    }

    /* 게시글 목록 */
    @GetMapping("/map")
    public String map(){

        return "/map";
    }


}