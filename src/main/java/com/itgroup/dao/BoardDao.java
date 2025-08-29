package com.itgroup.dao;

import com.itgroup.bean.Board;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BoardDao extends SuperDao { // 챗Gpt

    private final Scanner scan;

    public BoardDao() {
        super();
        this.scan = new Scanner(System.in); //
    }



    private static String dateToString(java.sql.Date d) {
        if (d == null)
        return null;

        return d.toLocalDate().format(DateTimeFormatter.ISO_DATE);
    }

    private static java.sql.Date toSqlDate(String ymdOrNull) {
        if (ymdOrNull == null) return null;
        String s = ymdOrNull.trim();
        if (s.isEmpty()) return null;
        s = s.replace('/', '-');
        try {
            LocalDate ld = LocalDate.parse(s);
            return java.sql.Date.valueOf(ld);
        } catch (Exception e) {
            return null;
        }
    }

    private Board makeBean(ResultSet rs) throws SQLException {
        Board b = new Board();
        b.setNo(rs.getInt("no"));
        b.setWriter(rs.getString("writer"));
        b.setSubject(rs.getString("subject"));
        b.setContent(rs.getString("content"));

        int car = rs.getInt("car_id");
        b.setCarId(rs.wasNull() ? null : car);

        b.setCreatedAt(dateToString(rs.getDate("created_at")));
        return b;
    }


    private String readLine(String prompt) {
        System.out.print(prompt);
        return scan.nextLine().trim();
    }

    private Integer readIntOrNull(String prompt) {
        String s = readLine(prompt);
        if (s.isEmpty()) return null;
        try { return Integer.parseInt(s.replace(",", "")); }
        catch (NumberFormatException e) { System.out.println("⚠ 숫자 형식이 아닙니다. 빈값으로 처리합니다."); return null; }
    }



    public List<Board> selectAll() {
        String sql = "SELECT no, writer, subject, content, car_id, created_at FROM boards ORDER BY no DESC";
        List<Board> list = new ArrayList<>();
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(makeBean(rs));
        } catch (SQLException e) {
            throw new RuntimeException("boards 전체 조회 실패: ORA-" + e.getErrorCode() + " " + e.getMessage(), e);
        }
        return list;
    }

    public List<Board> selectEvenData() {
        String sql = "SELECT no, writer, subject, content, car_id, created_at " +
                "FROM boards WHERE MOD(no,2)=0 ORDER BY no DESC";
        List<Board> list = new ArrayList<>();
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(makeBean(rs));
        } catch (SQLException e) {
            throw new RuntimeException("boards 짝수 조회 실패: ORA-" + e.getErrorCode() + " " + e.getMessage(), e);
        }
        return list;
    }

    public Board findOne(int no) {
        String sql = "SELECT no, writer, subject, content, car_id, created_at FROM boards WHERE no = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, no);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return makeBean(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("boards 단건 조회 실패: ORA-" + e.getErrorCode() + " " + e.getMessage(), e);
        }
        return null;
    }

    public int insert(Board b) {
        String sql = "INSERT INTO boards (no, writer, subject, content, car_id, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            if (b.getNo() == null) ps.setNull(1, Types.NUMERIC); else ps.setInt(1, b.getNo());
            if (b.getWriter() == null)  ps.setNull(2, Types.VARCHAR); else ps.setString(2, b.getWriter());
            if (b.getSubject() == null) ps.setNull(3, Types.VARCHAR); else ps.setString(3, b.getSubject());
            if (b.getContent() == null) ps.setNull(4, Types.VARCHAR); else ps.setString(4, b.getContent());
            if (b.getCarId() == null)   ps.setNull(5, Types.NUMERIC); else ps.setInt(5, b.getCarId());

            java.sql.Date d = toSqlDate(b.getCreatedAt());
            if (d == null) ps.setNull(6, Types.DATE); else ps.setDate(6, d);

            int cnt = ps.executeUpdate();
            conn.commit();
            return cnt;
        } catch (SQLException e) {
            throw new RuntimeException("boards INSERT 실패: ORA-" + e.getErrorCode() + " " + e.getMessage(), e);
        }
    }

    public int insertAutoNo(Board b) {
        String sql = "INSERT INTO boards (writer, subject, content, car_id, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            if (b.getWriter() == null)  ps.setNull(1, Types.VARCHAR); else ps.setString(1, b.getWriter());
            if (b.getSubject() == null) ps.setNull(2, Types.VARCHAR); else ps.setString(2, b.getSubject());
            if (b.getContent() == null) ps.setNull(3, Types.VARCHAR); else ps.setString(3, b.getContent());
            if (b.getCarId() == null)   ps.setNull(4, Types.NUMERIC); else ps.setInt(4, b.getCarId());

            java.sql.Date d = toSqlDate(b.getCreatedAt());
            if (d == null) ps.setNull(5, Types.DATE); else ps.setDate(5, d);

            int cnt = ps.executeUpdate();
            conn.commit();
            return cnt;
        } catch (SQLException e) {
            throw new RuntimeException("boards INSERT(자동 no) 실패: ORA-" + e.getErrorCode() + " " + e.getMessage(), e);
        }
    }

    public int update(Board b) {
        String sql = "UPDATE boards SET writer=?, subject=?, content=?, car_id=?, created_at=? WHERE no = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            if (b.getWriter() == null)  ps.setNull(1, Types.VARCHAR); else ps.setString(1, b.getWriter());
            if (b.getSubject() == null) ps.setNull(2, Types.VARCHAR); else ps.setString(2, b.getSubject());
            if (b.getContent() == null) ps.setNull(3, Types.VARCHAR); else ps.setString(3, b.getContent());
            if (b.getCarId() == null)   ps.setNull(4, Types.NUMERIC); else ps.setInt(4, b.getCarId());

            java.sql.Date d = toSqlDate(b.getCreatedAt());
            if (d == null) ps.setNull(5, Types.DATE); else ps.setDate(5, d);

            if (b.getNo() == null) throw new IllegalArgumentException("no가 필요합니다.");
            ps.setInt(6, b.getNo());

            int cnt = ps.executeUpdate();
            conn.commit();
            return cnt;
        } catch (SQLException e) {
            throw new RuntimeException("boards UPDATE 실패: ORA-" + e.getErrorCode() + " " + e.getMessage(), e);
        }
    }

    public int delete(int no) {
        String sql = "DELETE FROM boards WHERE no = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, no);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("boards DELETE 실패: ORA-" + e.getErrorCode() + " " + e.getMessage(), e);
        }
    }

    public int getSize() {
        String sql = "SELECT COUNT(*) AS cnt FROM boards";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("cnt") : 0;
        } catch (SQLException e) {
            throw new RuntimeException("boards COUNT 실패: ORA-" + e.getErrorCode() + " " + e.getMessage(), e);
        }
    }




    public void insertInteractive(boolean manualNo) {
        System.out.println("=== 게시글 등록 ===");
        Board b = new Board();

        if (manualNo) {
            b.setNo(readIntOrNull("글번호(no, 빈값=NULL): "));
        }
        b.setWriter(readLine("작성자: "));
        b.setSubject(readLine("제목: "));
        b.setContent(readLine("내용: "));
        b.setCarId(readIntOrNull("차량 ID(car_id, 빈값=NULL): "));
        String created = readLine("작성일(YYYY-MM-DD, 빈값=NULL): ");
        b.setCreatedAt(created.isEmpty() ? null : created);

        int cnt = manualNo ? insert(b) : insertAutoNo(b);
        System.out.println(cnt == 1 ? "등록 완료" : "등록 실패");
    }


    public void updateInteractive() {
        System.out.println("=== 게시글 수정 ===");
        Integer no = readIntOrNull("수정할 글번호: ");
        if (no == null) { System.out.println("취소/무효 입력"); return; }

        Board b = findOne(no);
        if (b == null) { System.out.println("해당 글이 없습니다."); return; }

        String writer = readLine("작성자(빈값=유지): ");
        String subject = readLine("제목(빈값=유지): ");
        String content = readLine("내용(빈값=유지): ");
        Integer carId  = readIntOrNull("차량 ID(빈값=유지): ");
        String created = readLine("작성일 YYYY-MM-DD(빈값=유지): ");

        if (!writer.isEmpty())  b.setWriter(writer);
        if (!subject.isEmpty()) b.setSubject(subject);
        if (!content.isEmpty()) b.setContent(content);
        if (carId != null)      b.setCarId(carId);
        if (!created.isEmpty()) b.setCreatedAt(created);

        int cnt = update(b);
        System.out.println(cnt == 1 ? "수정 완료" : (cnt == 0 ? "변경 없음" : "수정 실패"));
    }


    public void deleteInteractive() {
        System.out.println("=== 게시글 삭제 ===");
        Integer no = readIntOrNull("삭제할 글번호: ");
        if (no == null) { System.out.println("취소/무효 입력"); return; }
        int cnt = delete(no);
        System.out.println(cnt > 0 ? "삭제 완료" : (cnt == 0 ? "대상 없음" : "삭제 실패"));
    }


    public void findOneInteractive() {
        System.out.println("=== 게시글 조회 ===");
        Integer no = readIntOrNull("조회할 글번호: ");
        if (no == null) { System.out.println("취소/무효 입력"); return; }
        Board b = findOne(no);
        if (b == null) {
            System.out.println("해당 글이 없습니다.");
            return;
        }
        System.out.printf("no:%d, writer:%s, subject:%s, car_id:%s, created:%s%ncontent:%s%n",
                b.getNo(),
                safe(b.getWriter()),
                safe(b.getSubject()),
                b.getCarId() == null ? "-" : b.getCarId().toString(),
                safe(b.getCreatedAt()),
                safe(b.getContent()));
    }


    public void selectAllInteractive() {
        System.out.println("=== 게시글 전체 ===");
        List<Board> list = selectAll();
        for (Board b : list) {
            System.out.printf("[%d] %s / %s / car:%s / %s%n",
                    b.getNo(),
                    safe(b.getWriter()),
                    safe(b.getSubject()),
                    b.getCarId() == null ? "-" : b.getCarId(),
                    safe(b.getCreatedAt()));
        }
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
}
