package dev.mvc.midarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.mvc.botarea.BotArea;

@Service("dev.mvc.midarea.MidAreaProc")
public class MidAreaProc implements MidAreaProcInter {
	@Autowired
	MidAreaDAOInter midAreaDAO;
	
	@Override
	public int create(MidAreaVO midAreaVO) {
		int cnt = this.midAreaDAO.create(midAreaVO);
		return cnt;
	}

	@Override
	public MidAreaVO read(int midareano) {
		MidAreaVO midAreaVO = this.midAreaDAO.read(midareano);
		return midAreaVO;
	}

	@Override
	public ArrayList<MidAreaVO> list_all() {
		ArrayList<MidAreaVO> list = this.midAreaDAO.list_all();
		return list;
	}

	@Override
	public ArrayList<MidAreaVO> search_list(String word) {
		ArrayList<MidAreaVO> list = this.midAreaDAO.search_list(word);
		return list;
	}

	@Override
	public int update(MidAreaVO midAreaVO) {
		int cnt = this.midAreaDAO.update(midAreaVO);
		return cnt;
	}

	@Override
	public int delete(int midareano) {
		int cnt = this.midAreaDAO.delete(midareano);
		return cnt;
	}
	
	@Override
	public ArrayList<MidAreaVO> list_paging(HashMap<String, Object> map) {
		int begin_of_page = ((int) map.get("now_page") - 1) * MidArea.RECORD_PER_PAGE;

		
		int start_num = begin_of_page + 1;
		
	
		int end_num = begin_of_page + MidArea.RECORD_PER_PAGE;   
		
		System.out.println("start"+start_num);
		System.out.println("end:"+end_num);
		map.put("start_num", start_num);
		map.put("end_num", end_num);
		ArrayList<MidAreaVO> list = this.midAreaDAO.list_paging(map);
		return list;
	}

	@Override
	public int list_count() {
		int cnt = this.midAreaDAO.list_count();
		return cnt;
	}

	@Override
	public String pagingBox(int now_page, int search_count, int record_per_page, int page_per_block) {
		// 전체 페이지 수: (double)1/10 -> 0.1 -> 1 페이지, (double)12/10 -> 1.2 페이지 -> 2 페이지
	    int total_page = (int)(Math.ceil((double)search_count / record_per_page));
	    System.out.println("->total_page:" + record_per_page);
	    // 전체 그룹  수: (double)1/10 -> 0.1 -> 1 그룹, (double)12/10 -> 1.2 그룹-> 2 그룹
	    int total_grp = (int)(Math.ceil((double)total_page / page_per_block)); 
	    // 현재 그룹 번호: (double)13/10 -> 1.3 -> 2 그룹
	    int now_grp = (int)(Math.ceil((double)now_page / page_per_block));  
	    System.out.println(search_count);
	    // 1 group: 1, 2, 3 ... 9, 10
	    // 2 group: 11, 12 ... 19, 20
	    // 3 group: 21, 22 ... 29, 30
	    int start_page = ((now_grp - 1) * page_per_block) + 1; // 특정 그룹의 시작 페이지  
	    int end_page = (now_grp * page_per_block);               // 특정 그룹의 마지막 페이지   
	     
	    StringBuffer str = new StringBuffer(); // String class 보다 문자열 추가등의 편집시 속도가 빠름 
	    
	    // style이 java 파일에 명시되는 경우는 로직에 따라 css가 영향을 많이 받는 경우에 사용하는 방법
	    
	    str.append("<div id='paging'>"); 
//	    str.append("현재 페이지: " + nowPage + " / " + totalPage + "  "); 
	 
	    // 이전 10개 페이지로 이동
	    // now_grp: 1 (1 ~ 10 page)
	    // now_grp: 2 (11 ~ 20 page)
	    // now_grp: 3 (21 ~ 30 page) 
	    // 현재 2그룹일 경우: (2 - 1) * 10 = 1그룹의 마지막 페이지 10
	    // 현재 3그룹일 경우: (3 - 1) * 10 = 2그룹의 마지막 페이지 20
	    int _now_page = (now_grp - 1) * page_per_block;  
	    if (now_grp >= 2){ // 현재 그룹번호가 2이상이면 페이지수가 11페이지 이상임으로 이전 그룹으로 갈수 있는 링크 생성
	    	str.append("<span class='span_box_1' onclick='pageGrpMove(this)' data-page='"+_now_page+"'>이전</span>");
	    } 
	 
	    // 중앙의 페이지 목록
	    for(int i=start_page; i<=end_page; i++){ 
	      if (i > total_page){ // 마지막 페이지를 넘어갔다면 페이 출력 종료
	        break; 
	      } 
	  
	      if (now_page == i){ // 목록에 출력하는 페이지가 현재페이지와 같다면 CSS 강조(차별을 둠)
	        str.append("<span class='span_box_2' id='now_page'>"+i+"</span>"); // 현재 페이지, 강조 
	      }else{
	          str.append("<span class='span_box_1' onclick='pageMove(this)' data-page='"+i+"'>"+i+"</span>");
	      } 
	    } 
	 
	    // 10개 다음 페이지로 이동
	    // nowGrp: 1 (1 ~ 10 page),  nowGrp: 2 (11 ~ 20 page),  nowGrp: 3 (21 ~ 30 page) 
	    // 현재 페이지 5일경우 -> 현재 1그룹: (1 * 10) + 1 = 2그룹의 시작페이지 11
	    // 현재 페이지 15일경우 -> 현재 2그룹: (2 * 10) + 1 = 3그룹의 시작페이지 21
	    // 현재 페이지 25일경우 -> 현재 3그룹: (3 * 10) + 1 = 4그룹의 시작페이지 31
	    _now_page = (now_grp * page_per_block)+1; //  최대 페이지수 + 1 
	    if (now_grp < total_grp){ 
	        str.append("<span class='span_box_1' onclick='pageGrpMove(this)' data-page='"+_now_page+"' >다음</span>");
	    } 
	    str.append("</div>"); 
	     
	    return str.toString(); 
	}

}
