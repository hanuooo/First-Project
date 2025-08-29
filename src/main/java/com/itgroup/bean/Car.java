package com.itgroup.bean;

public class Car {
    // ✔ null 허용: 시퀀스/트리거, 입력 오류 대비
    private Integer id;

    private String  name;
    // ✔ null 허용: 가격 미정 가능
    private Integer price;

    private String  color;
    private String  company;

    // ✔ 간단히 String 유지(YYYY-MM-DD). 추후 LocalDate로 바꿔도 OK
    private String  release_date;

    private String  fuel;

    // === getter / setter ===
    public Integer getId() {return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getRelease_date() { return release_date; }
    public void setRelease_date(String release_date) { this.release_date = release_date; }

    public String getFuel() { return fuel; }
    public void setFuel(String fuel) { this.fuel = fuel; }
}
