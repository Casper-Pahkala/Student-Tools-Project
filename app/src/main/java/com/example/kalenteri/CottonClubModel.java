package com.example.kalenteri;

public class CottonClubModel {
    String salad,lunch2,lunch3,lunch4;

    public CottonClubModel(String salad, String lunch2, String lunch3, String lunch4) {
        this.salad = salad;
        this.lunch2 = lunch2;
        this.lunch3 = lunch3;
        this.lunch4 = lunch4;
    }

    public String getSalad() {
        return salad;
    }

    public String getLunch2() {
        return lunch2;
    }

    public String getLunch3() {
        return lunch3;
    }

    public String getLunch4() {
        return lunch4;
    }

    public CottonClubModel() {
    }
}
