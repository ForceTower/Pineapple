package com.forcetower.uefs.ru;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class RUData {
    private String time;
    private boolean aberto;
    private List<Integer> cotas;

    public RUData() {}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isAberto() {
        return aberto;
    }

    public void setAberto(boolean aberto) {
        this.aberto = aberto;
    }

    public List<Integer> getCotas() {
        return cotas;
    }

    public void setCotas(List<Integer> cotas) {
        this.cotas = cotas;
    }
}
