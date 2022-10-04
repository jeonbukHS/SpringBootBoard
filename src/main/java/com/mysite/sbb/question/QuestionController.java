package com.mysite.sbb.question;

import java.security.Principal;
import java.util.List;

import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
@RequestMapping("/question")   // 컨트롤러 안에 있는 모든 함수에 /question 으로 먼저 매핑
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;

    @RequestMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<Question> paging = this.questionService.getList(page);
        model.addAttribute("paging", paging);
        return "question_list";
    }
    @RequestMapping(value = "/detail/{id}") // 검증 과정 때문에 answerForm 파라미터 추가, 값을 넣지 않아서 무작위 값이 들어가있을 것이며 null 체크 회피
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm){
        Question question =  this.questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question_detail";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")   //원래는 파라미터 없었지만, form 검증 기능 도입 후 questionForm 추가, 그냥 객체만 생성한 상태이므로 랜덤한 값 들어가 있을 듯 -> null 체크 회피
    public String qustionCreate(QuestionForm questionForm){
        return "question_form";
    }
    // 원래는 @RequestParam String subject, @RequestParam String content 로 받아와서 바로 입력하는 방식
    // 제목과 내용의 null값을 체크하기 위해 @Valid 사용하여 QuestionForm에서 검증
    // question_form에서 넘어온 subject, content 값이 QuestionForm 객체에 담겨서 전송됨
    // 전송된 subject, content 값은 스프링 프레임워크의 바인딩 기능을 통해 QuestionForm의 subject, content 값에 자동으로 바인딩된다.
    //  BindingResult 매개변수는 @Valid 애너테이션으로 인해 검증이 수행된 결과를 의미하는 객체이다.

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String qustionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal){
        if (bindingResult.hasErrors()){
            return "question_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
        return "redirect:/question/list"; //질문 저장 후 목록으로 이동
    }
}