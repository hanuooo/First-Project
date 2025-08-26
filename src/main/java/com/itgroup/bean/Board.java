package com.itgroup.bean;

public class Board { // 챗Gpt
    private Integer no;         // PK
    private String  writer;
    private String  subject;
    private String  content;
    private Integer carId;      // cars.id (옵션)
    private String  createdAt;  // "YYYY-MM-DD" 문자열로 단순 관리

    public Integer getNo() { return no; }
    public void setNo(Integer no) { this.no = no; }

    public String getWriter() { return writer; }
    public void setWriter(String writer) { this.writer = writer; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getCarId() { return carId; }
    public void setCarId(Integer carId) { this.carId = carId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}