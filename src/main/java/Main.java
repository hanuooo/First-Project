import com.DataManager;
import com.itgroup.bean.Car;
import com.itgroup.dao.BoardDao;

import java.util.Scanner;

public class Main {// 챗Gpt
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        DataManager manager = new DataManager();
        BoardDao boardDao = new BoardDao();
        while (true){
            System.out.println("차량 선택");
            System.out.println("0:종료, 1:목록조회, 2:차량 구매, 3:수정, 4:총 차량 수, 5:환불, 6:차량 정보, 7:색상 조회");
            System.out.println("11:차량 전체, 12:등록, 13:수정, 14:전체 건수, 15:삭제, 16:1건정보, 17:짝수만 조회");
            int menu = scan.nextInt(); // 선택한 메뉴

            switch (menu){
                case 0 :
                System.out.println("프로그램을 종료합니다.");
                System.exit(0);
                case 1 :
                    manager.selectAll();
                    break;
                case 2 :
                    manager.insertData();
                    break;
                case 3 :
                    manager.updateData();
                    break;
                case 4 :
                    manager.getSize();
                    break;
                case 5 :
                    manager.deleteData();
                    break;
                case 6 :
                    manager.getCarOne();
                    break;
                case 7 :
                    manager.findByColor();
                    break;
                case 11 :
                    manager.selectAllBroad();
                    break;
                case 12 :

                    break;
                case 13 :
                    break;
                case 14 :
                    break;
                case 15 :
                    break;
                case 16 :
                    break;
                case 17 :manager.selectEvenData();
                    break;


            }
        }
    }
}