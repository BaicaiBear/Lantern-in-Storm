package dev.onyxstudios.foml.obj;

import de.javagl.obj.FloatTuple;
import de.javagl.obj.Mtl;
import de.javagl.obj.TextureOptions;

import java.util.List;

public class FOMLMaterial implements Mtl {
    private final Mtl material;
    private int tintIndex = -1;
    private boolean useDiffuseColor = false;

    public FOMLMaterial(Mtl material) {
        this.material = material;
    }

    @Override
    public String getName() {
        return material.getName();
    }

    @Override
    public Integer getIllum() {
        return 0;
    }

    @Override
    public void setIllum(Integer illum) {

    }

    @Override
    public Float getNi() {
        return 0f;
    }

    @Override
    public void setNi(Float ni) {

    }

    @Override
    public FloatTuple getTf() {
        return null;
    }

    @Override
    public void setTf(Float r, Float g, Float b) {

    }

    @Override
    public Float getSharpness() {
        return 0f;
    }

    @Override
    public void setSharpness(Float sharpness) {

    }

    @Override
    public FloatTuple getKa() {
        return material.getKa();
    }

    @Override
    public void setKa(Float r, Float g, Float b) {

    }

    @Override
    public String getMapKa() {
        return "";
    }

    @Override
    public void setMapKa(String mapKa) {

    }

    @Override
    public TextureOptions getMapKaOptions() {
        return null;
    }

    @Override
    public void setMapKaOptions(TextureOptions options) {

    }

    public void setKa(float ka0, float ka1, float ka2) {
        material.setKa(ka0, ka1, ka2);
    }

    @Override
    public FloatTuple getKs() {
        return material.getKs();
    }

    @Override
    public void setKs(Float r, Float g, Float b) {

    }

    @Override
    public String getMapKs() {
        return "";
    }

    @Override
    public void setMapKs(String mapKs) {

    }

    @Override
    public TextureOptions getMapKsOptions() {
        return null;
    }

    @Override
    public void setMapKsOptions(TextureOptions options) {

    }


    public void setKs(float ks0, float ks1, float ks2) {
        material.setKs(ks0, ks1, ks2);
    }

    @Override
    public FloatTuple getKd() {
        return material.getKd();
    }

    @Override
    public void setKd(Float r, Float g, Float b) {

    }


    public void setKd(float kd0, float kd1, float kd2) {
        material.setKd(kd0, kd1, kd2);
    }

    @Override
    public String getMapKd() {
        return material.getMapKd();
    }

    @Override
    public void setMapKd(String mapKd) {
        material.setMapKd(mapKd);
    }

    @Override
    public TextureOptions getMapKdOptions() {
        return null;
    }

    @Override
    public void setMapKdOptions(TextureOptions options) {

    }

    @Override
    public Float getNs() {
        return material.getNs();
    }

    @Override
    public void setNs(Float ns) {

    }

    @Override
    public String getMapNs() {
        return "";
    }

    @Override
    public void setMapNs(String mapNs) {

    }

    @Override
    public TextureOptions getMapNsOptions() {
        return null;
    }

    @Override
    public void setMapNsOptions(TextureOptions options) {

    }

    public void setNs(float ns) {
        material.setNs(ns);
    }

    @Override
    public Float getD() {
        return material.getD();
    }

    @Override
    public void setD(Float d) {

    }

    @Override
    public Boolean isHalo() {
        return null;
    }

    @Override
    public void setHalo(Boolean halo) {

    }

    @Override
    public String getMapD() {
        return "";
    }

    @Override
    public void setMapD(String mapD) {

    }

    @Override
    public TextureOptions getMapDOptions() {
        return null;
    }

    @Override
    public void setMapDOptions(TextureOptions options) {

    }

    @Override
    public String getBump() {
        return "";
    }

    @Override
    public void setBump(String bump) {

    }

    @Override
    public TextureOptions getBumpOptions() {
        return null;
    }

    @Override
    public void setBumpOptions(TextureOptions options) {

    }

    @Override
    public String getDisp() {
        return "";
    }

    @Override
    public void setDisp(String disp) {

    }

    @Override
    public TextureOptions getDispOptions() {
        return null;
    }

    @Override
    public void setDispOptions(TextureOptions options) {

    }

    @Override
    public String getDecal() {
        return "";
    }

    @Override
    public void setDecal(String decal) {

    }

    @Override
    public TextureOptions getDecalOptions() {
        return null;
    }

    @Override
    public void setDecalOptions(TextureOptions options) {

    }

    @Override
    public List<TextureOptions> getReflOptions() {
        return List.of();
    }

    @Override
    public Float getPr() {
        return 0f;
    }

    @Override
    public void setPr(Float pr) {

    }

    @Override
    public String getMapPr() {
        return "";
    }

    @Override
    public void setMapPr(String mapPr) {

    }

    @Override
    public TextureOptions getMapPrOptions() {
        return null;
    }

    @Override
    public void setMapPrOptions(TextureOptions options) {

    }

    @Override
    public Float getPm() {
        return 0f;
    }

    @Override
    public void setPm(Float pm) {

    }

    @Override
    public String getMapPm() {
        return "";
    }

    @Override
    public void setMapPm(String mapPm) {

    }

    @Override
    public TextureOptions getMapPmOptions() {
        return null;
    }

    @Override
    public void setMapPmOptions(TextureOptions options) {

    }

    @Override
    public Float getPs() {
        return 0f;
    }

    @Override
    public void setPs(Float ps) {

    }

    @Override
    public String getMapPs() {
        return "";
    }

    @Override
    public void setMapPs(String mapPs) {

    }

    @Override
    public TextureOptions getMapPsOptions() {
        return null;
    }

    @Override
    public void setMapPsOptions(TextureOptions options) {

    }

    @Override
    public Float getPc() {
        return 0f;
    }

    @Override
    public void setPc(Float pc) {

    }

    @Override
    public Float getPcr() {
        return 0f;
    }

    @Override
    public void setPcr(Float pcr) {

    }

    @Override
    public FloatTuple getKe() {
        return null;
    }

    @Override
    public void setKe(Float r, Float g, Float b) {

    }

    @Override
    public String getMapKe() {
        return "";
    }

    @Override
    public void setMapKe(String mapKe) {

    }

    @Override
    public TextureOptions getMapKeOptions() {
        return null;
    }

    @Override
    public void setMapKeOptions(TextureOptions options) {

    }

    @Override
    public Float getAniso() {
        return 0f;
    }

    @Override
    public void setAniso(Float aniso) {

    }

    @Override
    public Float getAnisor() {
        return 0f;
    }

    @Override
    public void setAnisor(Float anisor) {

    }

    @Override
    public String getNorm() {
        return "";
    }

    @Override
    public void setNorm(String norm) {

    }

    @Override
    public TextureOptions getNormOptions() {
        return null;
    }

    @Override
    public void setNormOptions(TextureOptions options) {

    }


    public void setD(float d) {
        material.setD(d);
    }

    public int getTintIndex() {
        return this.tintIndex;
    }

    public void setTintIndex(int tintIndex) {
        this.tintIndex = tintIndex;
    }

    public boolean useDiffuseColor() {
        return this.useDiffuseColor;
    }

    public void setUseDiffuseColor() {
        this.useDiffuseColor = true;
    }
}
