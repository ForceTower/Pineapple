package com.forcetower.uefs.ru;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
@SuppressWarnings("SpellCheckingInspection")
public class RUData {
    private String time;
    private boolean aberto;
    private List<String> cotas;

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

    public List<String> getCotas() {
        return cotas;
    }

    public void setCotas(List<String> cotas) {
        this.cotas = cotas;
    }
}
