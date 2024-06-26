package dev.mvc.owner;

import dev.mvc.customer.CustomerVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface OwnerDAOInter {

  /**
   * 사업자 중복이름 체크
   * @param nickname
   * @return 추가한 레코드 갯수
   */
  public int checkNickName(String nickname);

  /**
   * 중복아이디 체크
   * @param id
   * @return 추가한 레코드 갯수
   */
  public int checkID(String id);

  /**
   * 사업자 가입
   * @param ownerVO
   * @return 추가한 레코드 갯수
   */
  public int create(OwnerVO ownerVO);

  /**
   * 사업자 전체 목록
   * @return
   */

  /**
   * 사업자 등록정보 기입
   * @param ownerVO
   * @return 추가한 레코드 갯수
   */
  public int createCertifi(OwnerVO ownerVO);
  public ArrayList<OwnerVO> list();

  public int update_grade(HashMap<String,Object> map);

  /**
   * ownerno로 사업자 정보 조회
   * @param ownerno
   * @return
   */
  public OwnerVO read(int ownerno);

  /**
   * id로 사업자 정보 조회
   * @param id
   * @return
   */
  public OwnerVO readById(String id);

  /**
   * 수정 처리
   * @param ownerVO
   * @return
   */
  public int update(OwnerVO ownerVO);
  /**
   * 사업자 삭제 처리
   * @param ownerno
   * @return
   */
  public int delete(int ownerno);

  /**
   * 현재 패스워드 검사
   * @param map
   * @return 0: 일치하지 않음, 1: 일치함
   */
  public int passwd_check(HashMap<String, Object> map);

  /**
   * 패스워드 변경
   * @param map
   * @return 변경된 패스워드 갯수
   */
  public int passwd_update(HashMap<String, Object> map);

  /**
   * 로그인 처리
   */
  public int login(HashMap<String, Object> map);

  /**
   * 인증정보 업데이트
   * @param ownerVO
   * @return
   */
  public int updateCertifi(OwnerVO ownerVO);

  /**
   * 프로필 업데이트
   * @param ownerVO
   * @return
   */
  public int updateProfile(OwnerVO ownerVO);


  public ArrayList<OwnerVO> list_search_paging(Map<String,Object> map);

  public int list_search_count(HashMap<String,Object> map);


  public String findNamePhone(HashMap<String , Object> map) ;

  public String findNameEmail(HashMap<String , Object> map) ;

  public int checkNamePhone(HashMap<String , Object> map) ;

  public int checkNameEmail(HashMap<String , Object> map) ;

  public int passwd_updates(HashMap<String, Object> map);
}
