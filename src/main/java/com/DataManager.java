package com;

import com.itgroup.bean.Board;
import com.itgroup.bean.Car;
import com.itgroup.dao.BoardDao;
import com.itgroup.dao.CarDao;

import java.util.List;
import java.util.Scanner;

public class DataManager {// 챗Gpt

    private final Scanner scan;
    private final CarDao cdao;
    private final BoardDao bdao;

    public DataManager() {
        this.scan = new Scanner(System.in);
        this.cdao = new CarDao();
        this.bdao = new BoardDao();
    }

    /* ================= 유틸: 안전 입력 ================= */

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scan.nextLine().trim();
    }

    private Integer readIntOrNull(String prompt) {
        String s = readLine(prompt);
        if (s.isEmpty()) return null;
        try {
            return Integer.parseInt(s.replace(",", ""));
        } catch (NumberFormatException e) {
            System.out.println("⚠ 숫자를 입력하세요.");
            return null;
        }
    }

    private int readIntRequired(String prompt) {
        while (true) {
            Integer v = readIntOrNull(prompt);
            if (v != null) return v;
            // 다시 입력
        }
    }

    /* ================= Car 도메인 ================= */

    // 차량 등록
    public void insertData() {
        Car bean = new Car();

        // next()/nextInt() 혼용을 피하고 nextLine()만 사용
        Integer id = readIntOrNull("id 입력(빈값=수동 NULL): ");
        String name = readLine("이름 입력: ");

        // 데모용 기본값 (원하면 모두 입력받도록 확장하세요)
        bean.setId(id);
        bean.setName(name.isEmpty() ? "noname" : name);
        bean.setPrice(2000);
        bean.setColor("blue");
        bean.setCompany("kia");
        bean.setRelease_date("2025-08-20"); // YYYY-MM-DD 권장
        bean.setFuel("gasoline");

        int cnt = cdao.insertData(bean);
        if (cnt == 1) {
            System.out.println("차량 ID " + (id == null ? "(auto)" : id) + " 등록 성공");
        } else if (cnt == 0) {
            System.out.println("등록되지 않았습니다.");
        } else {
            System.out.println("등록 실패");
        }
    }

    // 차량 수정(이름만 예시)
    public void updateData() {
        int id = readIntRequired("수정할 차량 id 입력: ");
        Car bean = cdao.findOne(id);
        if (bean == null) {
            System.out.println("해당 차량이 존재하지 않습니다.");
            return;
        }

        String name = readLine("새 이름 입력(빈값=변경 안 함): ");
        if (!name.isEmpty()) bean.setName(name);

        // 필요한 항목 더 받으려면 여기에 추가 (color/company/price/release_date/fuel)
        int cnt = cdao.updateData(bean);
        System.out.println(cnt == 1 ? "업데이트 성공" : (cnt == 0 ? "변경 없음" : "업데이트 실패"));
    }

    // 차량 1건 조회
    public void getCarOne() {
        int id = readIntRequired("조회할 차량 ID: ");
        Car car = cdao.findOne(id);
        if (car == null) {
            System.out.println("해당 차량이 존재하지 않습니다.");
        } else {
            System.out.printf("ID:%d, 이름:%s, 가격:%s, 색상:%s, 회사:%s, 출시일:%s, 연료:%s%n",
                    car.getId(),
                    nullToDash(car.getName()),
                    car.getPrice() == null ? "-" : car.getPrice(),
                    nullToDash(car.getColor()),
                    nullToDash(car.getCompany()),
                    nullToDash(car.getRelease_date()),
                    nullToDash(car.getFuel()));
        }
    }

    // 전체 목록
    public void selectAll() {
        List<Car> cars = cdao.selectAll();
        System.out.println("이름\t가격\t색상\t회사");
        for (Car bean : cars) {
            String name = nullToDash(bean.getName());
            String price = bean.getPrice() == null ? "-" : String.valueOf(bean.getPrice());
            String color = nullToDash(bean.getColor());
            String company = nullToDash(bean.getCompany());
            System.out.println(name + "\t" + price + "\t" + color + "\t" + company);
        }
    }

    // 총 차량 수
    public void getSize() {
        int cnt = cdao.getSize();
        String message = (cnt == 0) ? "검색된 차량이 존재하지 않습니다."
                : "검색된 차량은 총 " + cnt + "대 입니다.";
        System.out.println(message);
    }

    // 색상으로 조회
    public void findByColor() {
        String color = readLine("조회할 색상 입력: ");
        List<Car> mydata = cdao.findByColor(color);
        System.out.println("이름\t가격\t색상");
        for (Car bean : mydata) {
            String name = nullToDash(bean.getName());
            String price = bean.getPrice() == null ? "-" : String.valueOf(bean.getPrice());
            String c = nullToDash(bean.getColor());
            System.out.println(name + "\t" + price + "\t" + c);
        }
    }

    // 차량 삭제
    public void deleteData() {
        int id = readIntRequired("삭제할 차량 ID: ");
        int cnt = cdao.deleteData(id);
        System.out.println(cnt > 0 ? "차량 환불 완료"
                : (cnt == 0 ? "차량이 존재하지 않습니다." : "차량 환불 실패"));
    }

    private String nullToDash(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    /* ================= Board 도메인 ================= */

    public void selectAllBroad() {
        List<Board> boardList = bdao.selectAll();
        System.out.println("글번호\t작성자\t글제목\t글내용");
        for (Board bean : boardList) {
            System.out.println(bean.getNo() + "\t" + bean.getWriter() + "\t"
                    + bean.getSubject() + "\t" + bean.getContent());
        }
    }

    public void selectEvenData() {
        List<Board> boardList = bdao.selectEvenData();
        System.out.println("글번호\t작성자\t글제목\t글내용");
        for (Board bean : boardList) {
            System.out.println(bean.getNo() + "\t" + bean.getWriter() + "\t"
                    + bean.getSubject() + "\t" + bean.getContent());
        }
    }
}
