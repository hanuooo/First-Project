package com.itgroup.dao;

import com.itgroup.bean.Car;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CarDao extends SuperDao {

    public CarDao() {
        super();
        // ❗ DAO 생성자에서 Scanner 사용/로직 실행 절대 금지 (NPE 원인)
    }

    /* -------------------- 유틸리티 -------------------- */

    private static java.sql.Date toSqlDate(String ymdOrNull) {
        if (ymdOrNull == null) return null;
        String s = ymdOrNull.trim();
        if (s.isEmpty()) return null;
        s = s.replace('/', '-'); // "YYYY/MM/DD" 허용
        try {
            LocalDate ld = LocalDate.parse(s);
            return java.sql.Date.valueOf(ld);
        } catch (Exception e) {
            return null; // 형식 틀리면 NULL 저장
        }
    }

    private static String dateToString(java.sql.Date d) {
        if (d == null) return null;
        return d.toLocalDate().format(DateTimeFormatter.ISO_DATE); // "YYYY-MM-DD"
    }

    private Car makeBean(ResultSet rs) throws SQLException {
        Car c = new Car();
        c.setId(rs.getInt("id")); // PK는 NOT NULL이면 그대로 OK

        c.setName(rs.getString("name"));

        // ✔ BigDecimal→Integer 캐스팅 금지! getInt + wasNull 패턴
        int p = rs.getInt("price");
        c.setPrice(rs.wasNull() ? null : p);

        c.setColor(rs.getString("color"));
        c.setCompany(rs.getString("company"));

        java.sql.Date d = rs.getDate("release_date");
        c.setRelease_date(d == null ? null : d.toLocalDate().toString()); // "YYYY-MM-DD"

        c.setFuel(rs.getString("fuel"));
        return c;
    }

    /* -------------------- CRUD -------------------- */

    // INSERT (id 직접 입력 버전; 시퀀스/트리거 쓰면 id 제외하는 메소드도 아래에 제공)
    public int insertData(Car bean) {
        int cnt = -1;
        final String sql =
                "INSERT INTO cars (id, name, price, color, company, release_date, fuel) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = super.getConnection();
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);

            // 1) id (NUMBER) — Car.id가 null이면 NULL로
            if (bean.getId() == null) pstmt.setNull(1, Types.NUMERIC);
            else pstmt.setInt(1, bean.getId());

            // 2) name
            if (bean.getName() == null) pstmt.setNull(2, Types.VARCHAR);
            else pstmt.setString(2, bean.getName());

            // 3) price (NUMBER)
            if (bean.getPrice() == null) pstmt.setNull(3, Types.NUMERIC);
            else pstmt.setInt(3, bean.getPrice());

            // 4) color
            if (bean.getColor() == null) pstmt.setNull(4, Types.VARCHAR);
            else pstmt.setString(4, bean.getColor());

            // 5) company
            if (bean.getCompany() == null) pstmt.setNull(5, Types.VARCHAR);
            else pstmt.setString(5, bean.getCompany());

            // 6) release_date (DATE) — Car는 String 보유
            java.sql.Date d = toSqlDate(bean.getRelease_date());
            if (d == null) pstmt.setNull(6, Types.DATE);
            else pstmt.setDate(6, d);

            // 7) fuel
            if (bean.getFuel() == null) pstmt.setNull(7, Types.VARCHAR);
            else pstmt.setString(7, bean.getFuel());

            cnt = pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (Exception ignored) {}
            throw new RuntimeException("INSERT 실패: ORA-" + e.getErrorCode() + " " + e.getMessage(), e);
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if (conn != null)  conn.close();  } catch (Exception ignored) {}
        }
        return cnt;
    }

    // INSERT (id 자동발급 버전: 시퀀스+트리거 사용 시)
    public int insertDataAutoId(Car bean) {
        int cnt = -1;
        final String sql =
                "INSERT INTO cars (name, price, color, company, release_date, fuel) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            ps.setString(1, bean.getName());
            if (bean.getPrice() == null) ps.setNull(2, Types.NUMERIC);
            else ps.setInt(2, bean.getPrice());
            ps.setString(3, bean.getColor());
            ps.setString(4, bean.getCompany());
            java.sql.Date d = toSqlDate(bean.getRelease_date());
            if (d == null) ps.setNull(5, Types.DATE);
            else ps.setDate(5, d);
            ps.setString(6, bean.getFuel());

            cnt = ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("INSERT(자동 ID) 실패: ORA-" + e.getErrorCode() + " " + e.getMessage(), e);
        }
        return cnt;
    }

    // UPDATE
    public int updateData(Car bean) {
        int cnt = -1;
        final String sql =
                "UPDATE cars " +
                        "   SET name = ?, price = ?, color = ?, company = ?, release_date = ?, fuel = ? " +
                        " WHERE id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = super.getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, bean.getName());
            if (bean.getPrice() == null) pstmt.setNull(2, Types.NUMERIC);
            else pstmt.setInt(2, bean.getPrice());
            pstmt.setString(3, bean.getColor());
            pstmt.setString(4, bean.getCompany());

            java.sql.Date d = toSqlDate(bean.getRelease_date());
            if (d == null) pstmt.setNull(5, Types.DATE);
            else pstmt.setDate(5, d);

            pstmt.setString(6, bean.getFuel());

            if (bean.getId() == null) throw new IllegalArgumentException("id가 필요합니다.");
            pstmt.setInt(7, bean.getId());

            cnt = pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (Exception ignored) {}
            throw new RuntimeException("UPDATE 실패: ORA-" + e.getErrorCode() + " " + e.getMessage(), e);
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if (conn != null)  conn.close();  } catch (Exception ignored) {}
        }
        return cnt;
    }

    // DELETE
    public int deleteData(int id) {
        final String sql = "DELETE FROM cars WHERE id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate(); // 0 또는 1
        } catch (SQLException e) {
            throw new RuntimeException("DELETE 실패: ORA-" + e.getErrorCode() + " " + e.getMessage(), e);
        }
    }

    // 단건 조회 (PK)
    public Car findOne(int id) {
        final String sql =
                "SELECT id, name, price, color, company, release_date, fuel " +
                        "  FROM cars WHERE id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return makeBean(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("단건 조회 실패: ORA-" + e.getErrorCode() + " " + e.getMessage(), e);
        }
        return null;
    }

    // 이름 기반 단건 조회가 필요하면 이렇게 (선택)
    public Car getCarOne(String idText) {
        // 문자열 id를 받아도 NUMBER 컬럼이므로 안전하게 파싱
        int id;
        try {
            id = Integer.parseInt(idText.trim().replace(",", ""));
        } catch (Exception e) {
            return null;
        }
        return findOne(id);
    }

    // 총 개수
    public int getSize() {
        final String sql = "SELECT COUNT(*) AS cnt FROM cars";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("cnt") : 0;
        } catch (SQLException e) {
            throw new RuntimeException("COUNT 실패: ORA-" + e.getErrorCode() + " " + e.getMessage(), e);
        }
    }

    // 전체 목록
    public List<Car> selectAll() {
        final String sql =
                "SELECT id, name, price, color, company, release_date, fuel " +
                        "  FROM cars ORDER BY name ASC";
        List<Car> list = new ArrayList<>();
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(makeBean(rs));
        } catch (SQLException e) {
            throw new RuntimeException("목록 조회 실패: ORA-" + e.getErrorCode() + " " + e.getMessage(), e);
        }
        return list;
    }

    // 색상으로 검색
    public List<Car> findByColor(String color) {
        final String sql =
                "SELECT id, name, price, color, company, release_date, fuel " +
                        "  FROM cars WHERE color = ?";
        List<Car> list = new ArrayList<>();
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, color);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(makeBean(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("색상 조회 실패: ORA-" + e.getErrorCode() + " " + e.getMessage(), e);
        }
        return list;
    }
}
