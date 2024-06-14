package dev.mvc.notice;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import dev.mvc.restaurant.RestaurantProInter;
import dev.mvc.restaurant.RestaurantVO;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;

@RequestMapping("/notice")
@Controller
public class NoticeController {
	@Qualifier("dev.mvc.notice.NoticeProc")
	@Autowired
	private NoticeProcInter noticeProc;
	
	@Qualifier("dev.mvc.restaurant.RestaurantProc")
	@Autowired
	private RestaurantProInter restaurantPro;
	
	@GetMapping("/create")
	public String create(Model model, HttpSession session) {
		String accessType = (String) session.getAttribute("type");
		System.out.println("type:" + accessType);
		if(accessType!=null && accessType.equals("owner")) {	// 사업자 접속
			int ownerno = (int) session.getAttribute("ownerno");
			ArrayList<RestaurantVO> restList = this.restaurantPro.findByOwnerR(ownerno);
			int restno;
			System.out.println("size=>" + restList.size());
			if(restList.size() >= 1) {
				restno = restList.get(0).getRestno();
				model.addAttribute("restno", restno);
			}else {
				model.addAttribute("code", "not_created_restaurant");
				return "/notice/msg";
			}
		}else {	//관리자 접속
			
		}
		return "/notice/create";
	}
	
	@PostMapping("/create")
	public String createProc(Model model, NoticeVO noticeVO) {
		int cnt = this.noticeProc.create(noticeVO);
		if(cnt > 0) {
			return "redirect:/notice/list";
		}else {
			return "redirect:/notice/msg";
		}
		
	}
	
	@GetMapping("/list")
	public String list(Model model, HttpSession session) {
		String accessType = (String) session.getAttribute("type");
		System.out.println("type=>" + accessType);
		ArrayList<NoticeVO> noticeList;
		if(accessType!=null && accessType.equals("owner")) {
			int ownerno = (int) session.getAttribute("ownerno");
			ArrayList<RestaurantVO> restList = this.restaurantPro.findByOwnerR(ownerno);
			if(restList.size() >= 1) {
				int restno = restList.get(0).getRestno();
				noticeList = this.noticeProc.list_by_restno(restno);
				model.addAttribute("restVO", restList.get(0));
			}else {
				model.addAttribute("code", "not_created_restaurant");
				return "/notice/msg";
			}
		}else {
			noticeList = this.noticeProc.list_all();
		}
		
		model.addAttribute("noticeList", noticeList);
		
		return "/notice/list";
	}
	
	@GetMapping("/read")
	public String read(Model model, int noticeno) {
		NoticeVO noticeVO = this.noticeProc.read(noticeno);
		model.addAttribute("noticeVO", noticeVO);
		return "/notice/read";
	}
	
	@GetMapping("/update")
	public String update(Model model, int noticeno) {
		System.out.println(noticeno);
		NoticeVO noticeVO = this.noticeProc.read(noticeno);
		model.addAttribute("noticeVO", noticeVO);
		return "/notice/update";
	}
	
	
	@PostMapping("/update")
	public String updateProc(Model model, NoticeVO noticeVO){
		
		int cnt = this.noticeProc.update(noticeVO);
		return "redirect:/notice/list";
	}
	@PostMapping("/delete")
	public String delete(Model model, int noticeno) {
		int cnt = this.noticeProc.delete(noticeno);
		return "redirect:/notice/list";
	}
}