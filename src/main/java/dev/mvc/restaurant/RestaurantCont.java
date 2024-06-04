package dev.mvc.restaurant;

import dev.mvc.botarea.BotAreaVO;
import dev.mvc.category.CategoryProcInter;
import dev.mvc.category.CategoryVO;

import dev.mvc.dto.RestDTO;
import dev.mvc.midarea.MidAreaProcInter;
import dev.mvc.midarea.MidAreaVO;
import dev.mvc.restimg.RestImgProInter;
import dev.mvc.restimg.RestimgVO;
import dev.mvc.tool.Security;
import dev.mvc.tool.Tool;
import dev.mvc.tool.Upload;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/restaurant")
public class RestaurantCont {


  @Autowired
  @Qualifier("dev.mvc.restaurant.RestaurantProc")
  private RestaurantProInter restaurantProc;


  @Autowired
  @Qualifier("dev.mvc.restimg.RestImgProC")
  private RestImgProInter restimgproc;
  
  @Autowired
  @Qualifier("dev.mvc.midarea.MidAreaProc")
  private MidAreaProcInter midAreaProc;
  
  @Autowired
  @Qualifier("dev.mvc.category.CategoryProc")
  private CategoryProcInter categoryProc;

  @Autowired
  private Security security;

  public RestaurantCont() {
//    System.out.println("RestaurantCont created");
  }






  /*
   * 식당 등록 폼 메서드
   *
   *
   */
  @GetMapping("/create")
  public String create(Model model,RestaurantVO restaurantVO,HttpSession session) {
    String type =  "s";

    if (type == null) {
      return "redirect:/";
    } else {
      return "restaurant/create";
    }

  }

  /*
   * 식당 등록 처리 메서드
   *
   *
   */

  @PostMapping("/create")
  public String restaurant(Model model, RedirectAttributes redirectAttributes, RestaurantVO restaurantVO, RestimgVO restimgVO, RedirectAttributes ra) {

    restaurantVO.setOwnerno(8);







      int count = this.restaurantProc.create(restaurantVO);

      int restno = this.restaurantProc.foreign(restaurantVO.getOwnerno());

      if (count == 1) {

        String upDir = Restaurant.getUploadDir(); // 파일을 업로드할 폴더 준비
        System.out.println("-> upDir: " + upDir);

        MultipartFile mf1 = restimgVO.getFile1MF();
        MultipartFile mf2 = restimgVO.getFile2MF();
        MultipartFile mf3 = restimgVO.getFile3MF(); // 파일 3 추가

        String[] fileNames = {mf1.getOriginalFilename(), mf2.getOriginalFilename(), mf3.getOriginalFilename()};
        MultipartFile[] files = {mf1, mf2, mf3};

        for (int i = 0; i < files.length; i++) {
          if (!fileNames[i].isEmpty()) {
            if (Tool.checkUploadFile(fileNames[i])) {
              long size = files[i].getSize();

              if (size > 0) {
                String exe = fileNames[i].split("\\.")[1];
                String newFileName = "owner_" + (i+1)  + "." + exe;
                String fileSaved = Upload.saveFileSpring(files[i], upDir, newFileName);

                if (Tool.isImage(fileSaved)) {
                  String thumb = Tool.preview(upDir, fileSaved, 200, 150);

                  restimgVO.setRestno(restno);
                  restimgVO.setImagefile(fileSaved); // 저장된 파일명 설정
                  restimgVO.setThumbfile(thumb); // 저장된 썸네일 파일명 설정

                  int saved = this.restimgproc.create(restimgVO);
                  if (saved != 1) {
                    return "redirect:/category/list";
                  }
                } else {
                  return "redirect:/restaurant/list"; // 파일이 이미지가 아닐 경우 리다이렉트
                }
              } else {
                return "redirect:/category/list"; // 파일 크기가 0일 경우 리다이렉트
              }
            } else {
              ra.addFlashAttribute("cnt", 0);
              ra.addFlashAttribute("code", "check_upload_file_fail");
              ra.addFlashAttribute("url", "/contents/msg"); // 메시지 페이지 URL 설정
              return "redirect:/contents/msg";
            }
          }
        }

        return "redirect:/category/list";
      } else {
        return "redirect:/restaurant/create";
      }




  }




  @GetMapping("/search_b")
  public String searchownerno(HttpSession session, Model model, @RequestParam(name = "type", defaultValue = "100") String type, String word, CategoryVO
    categoryVO, @RequestParam(name = "now_page", defaultValue = "1") int now_page) {


      String id = (String) session.getAttribute("id");
      String grade = (String) session.getAttribute("grade");

      if (now_page < 1) {
        now_page = 1;
      }


      word = Tool.wordcheckNull(word);
      type = Tool.wordcheckNull(type);
      String types = "";


      if (type.equals("100")) {
        types = "중분류";
      } else if (type.equals("200")) {
        types = "소분류";
      } else {
        types = "중분류 + 소분류";
      }


      int count = this.restaurantProc.list_search_count(word, type);
      // 일련 번호 생성
      int num = count - ((now_page - 1) * Restaurant.RECORD_PER_PAGE);
      ArrayList<RestDTO> restlist = this.restaurantProc.list_search_paging(word, type, now_page, Restaurant.RECORD_PER_PAGE);
      String paging = this.restaurantProc.pagingBox(now_page, word, type, "/restaurant/search_b", count, Restaurant.RECORD_PER_PAGE, Restaurant.PAGE_PER_BLOCK);
      model.addAttribute("paging", paging);
      model.addAttribute("now_page", now_page);
      model.addAttribute("count", count);

      model.addAttribute("num", num);
      if (restlist.isEmpty()) {
        model.addAttribute("word", word);
        model.addAttribute("type", type);
        model.addAttribute("nulllist", "결과가 없습니다.");
      } else if (!restlist.isEmpty() && !word.equals("")) {
        model.addAttribute("word", word);
        model.addAttribute("type", type);
        model.addAttribute("search", types + ":" + word + "에  대한 검색 결과 : 총 " + restlist.size() + "건");
      }

      model.addAttribute("word", word);
      model.addAttribute("type", type);
      model.addAttribute("searchlist", restlist);
      model.addAttribute("restDTO", restlist);

      return "restaurant/search_all"; // Assuming "search_result" is the name of the view to display the search results


    }
  
  @GetMapping("/search")
  public String search(Model model) {
	  ArrayList<MidAreaVO> midAreaList = this.midAreaProc.list_all();
	  model.addAttribute("midAreaList", midAreaList);
	  ArrayList<CategoryVO> cateList = this.categoryProc.list();
	  model.addAttribute("cateList", cateList);
	  return "/search";  
  }
  
  @GetMapping("/search_list")
  public String search_list(Model model, 
		  					int person, 
		  					String date, 
		  					int time,
		  					@RequestParam(defaultValue="0") int categoryno,
		  					@RequestParam(defaultValue="") String botarea,
		  					@RequestParam(name = "min_price", defaultValue = "0")int minPrice, 
		  					@RequestParam(name = "max_price", defaultValue = "40")int maxPrice) {
	  int[] botareanos = null;
	  if(!botarea.equals("")) {
		  String[] splitedBotareas = botarea.split("_");
		  botareanos = new int[splitedBotareas.length];
		  for(int i = 0;i < splitedBotareas.length; i++) {
			  botareanos[i] = Integer.parseInt(splitedBotareas[i]);
		  }
	  }else {
		  botareanos = new int[0];
	  }
	 
	  
	  Map<String, Object> map = new HashMap<String, Object>();
	  map.put("person", person);
	  map.put("date", date + " 00:00:00");
	  map.put("time", time);
	  map.put("categoryno", categoryno);
	  map.put("botareanos", botareanos);
	  map.put("min_price", minPrice);
	  map.put("max_price", maxPrice);
	  System.out.println("person:"+person);
	  System.out.println("date:"+date);
	  System.out.println("time:"+time);
	  System.out.println("categoryno:"+categoryno);
	  System.out.println("min_price:"+minPrice);
	  System.out.println("max_price:"+maxPrice);
	  
	  ArrayList<RestaurantVO> list = this.restaurantProc.condition_search_list(map);
	  
	  String date1 = this.restaurantProc.test(date);
	  model.addAttribute("list", list);
	  
	  model.addAttribute("person", person);
	  model.addAttribute("date", date);
	  model.addAttribute("time", time);
	  return "/search_list";
  }
  
  @GetMapping("/main_page")
  public String main_page(Model model, int restno, int person, String date, int time) {
	  RestaurantVO restaurantVO = this.restaurantProc.read(restno);
	  System.out.println(restaurantVO.getName());
	  model.addAttribute("restrauntVO", restaurantVO);
	  
	  model.addAttribute("person", person);
	  model.addAttribute("date", date);
	  model.addAttribute("time", time);
	  return "/restaurant_page";
  }
  
}



